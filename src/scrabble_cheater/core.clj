(ns scrabble-cheater.core
  (:gen-class)
  (:require [clojure.string :as string]))

;; global values
(def wildcard \@)
(def path-to-dict "dictionaries/sowpods.txt")
(def max-num-chars 9)
(def scores {\A 1, \B 3, \C 3, \D 2, \E 1, \F 4, \G 2, \H 4,
             \I 1, \J 10, \K 5, \L 1, \M 3, \N 1, \O 1, \P 3,
             \Q 10, \R 1, \S 1, \T 1, \U 1, \V 4, \W 4, \X 8,
             \Y 4, \Z 10})
(def valid-characters (set (cons wildcard 
                                 (keys scores))))

(defn print-func [i word score]
  (println (format "%d. '%s' = %d"  
                   i
                   word
                   score)))

;; main functions
(defn remove-first
  "Remove the first instance of a value from a collection. If the value
  isn't present, do nothing. Copped from
  https://groups.google.com/d/msg/clojure/nkANE4GxFmU/7YgW6UMA560J"  
  [x coll]
  (let [[pre post] (split-with #(not= x %)
                               coll)]
    (concat pre (rest post))))

(defn match? 
  "Tail recursive function taking a rack sequence, the number of 
  wildcards to allow, and a word sequence. Returns truthy value." 
  [rack wcs word]
  (let [enough-wildcards (>= wcs 0) 
        character (first word)] 
    (if (or (nil? character) 
            (not enough-wildcards))
        enough-wildcards ;; enough-wildcards always holds the return value
        (recur (remove-first character rack) ;; Only changes if present 
               (if (some #{character} rack)
                   wcs
                   (dec wcs))
               (rest word)))))

(defn letter-intersection
  "Takes the rack, the word, and the number of wildcards. Returns
  nil if unable to use wildcards, else returns list of present letters
  ready for scoring."
  [rack num-wcs word]
  (loop [word-remaining word
         rack-remaining rack 
         remaining-wcs num-wcs
         intersected []]
    (let [letter (first word-remaining)]
      (cond (nil? letter) intersected
            ;; if letter in rack
            (some #{letter}
                  rack-remaining) (recur (rest word-remaining)
                                         (remove-first letter 
                                                       rack-remaining)
                                         remaining-wcs
                                         (conj intersected 
                                               letter))
            ;; letter not in rack, so check wcs
            (pos? remaining-wcs) (recur (rest word-remaining)
                                        rack-remaining
                                        (dec remaining-wcs)
                                        intersected)
            ;; no letter or wcs so
            :else nil))))

(defn score-letters [letters]
  (reduce + 
          (map scores 
               letters)))

(defn open-dictionary [dictionary-path]
  (with-open [rdr (clojure.java.io/reader dictionary-path)]
    (line-seq rdr)))

(defn get-match-list 
  "Takes the list of words and the rack, filters using match? function,
  pairs words in tuples with their scores, and then sorts on those scores 
  in descending order."
  [list-of-words rack]
  (let [[num-wcs tame-rack] ((juxt #(count (filter %1 %2))
                                   remove) 
                             #{wildcard} rack)
        matches (filter (partial match? 
                                 rack 
                                 num-wcs)
                        list-of-words)]
      (sort-by second 
               >
               (map #(vector % 
                             (score-letters %))
                    matches))))


#_(defn -main
  "I don't do a whole lot ... yet."
  [& args]
    (doseq [[i [word score]] (map-indexed #(vector (inc %1) %2)
                                          (get-match-list dictionary-path
                                                          rack))]
          (println (format print-string i word score))))
