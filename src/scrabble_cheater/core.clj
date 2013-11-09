(ns scrabble-cheater.core
  (:gen-class)
  (:require [clojure.string :as string]))

(def wildcard \@)
(def path-to-dict "dictionaries/sowpods.txt")
(def max-num-chars 9)
(def scores {\A 1, \B 3, \C 3, \D 2, \E 1, \F 4, \G 2, \H 4,
             \I 1, \J 10, \K 5, \L 1, \M 3, \N 1, \O 1, \P 3,
             \Q 10, \R 1, \S 1, \T 1, \U 1, \V 4, \W 4, \X 8,
             \Y 4, \Z 10})

(def valid-characters (set (cons wildcard 
                                 (keys scores))))

(defn remove-first [x coll]
  "Remove the first instance of a value from a collection. If the value
  isn't present, do nothing. Copped from
  https://groups.google.com/d/msg/clojure/nkANE4GxFmU/7YgW6UMA560J"  
  (let [[pre post] (split-with #(not= x %)
                               coll)]
    (concat pre (rest post))))

(defn match? [rack wcs word]
  "Tail recursive function taking a rack sequence, a word sequence, and the 
  number of wildcards to allow. Returns truthy value." 
  (let [enough-wildcards (>= wcs 0) 
        character (first word)] 
    (if (or (nil? character) 
            (not enough-wildcards))
        enough-wildcards
        (recur (remove-first character rack) ;; Only changes if present 
               (rest word)
               (if (some #{character} rack)
                   wcs
                   (dec wcs))))))

(defn score-word [word]
  (reduce + (map scores word)))

(defn open-dictionary [dictionary-path]
  (with-open [rdr (clojure.java.io/reader dictionary-path)]
    (line-seq rdr)))

(defn get-match-list [list-of-words rack]
  (let [num-wcs (count (filter #(= wildcard %) 
                               rack))]
      (sort-by second 
               >
               (map #(list % (score-word %))
                    (filter (partial match? rack num-wcs)
                            list-of-words)))))


#_(defn -main
  "I don't do a whole lot ... yet."
  [& args]
    (doseq [[i [word score]] (map-indexed #(list (inc %1) %2)
                                          (get-match-list dictionary-path
                                                          rack))]
          (println (format "%d. '%s' = %d points" i word score))))
