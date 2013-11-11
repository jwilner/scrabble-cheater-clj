(ns scrabble-cheater.core
  (:gen-class)
  (:require [clojure.string :as string])
  (:use [clojure.tools.cli :only [cli]]
        [clojure.java.io :only [as-file]]))

;; global values
(def wildcard \@)
(def path-to-dict "resources/sowpods.txt")
(def max-num-chars 9)
(def scores {\A 1, \B 3, \C 3, \D 2, \E 1, \F 4, \G 2, \H 4,
             \I 1, \J 10, \K 5, \L 1, \M 3, \N 1, \O 1, \P 3,
             \Q 10, \R 1, \S 1, \T 1, \U 1, \V 4, \W 4, \X 8,
             \Y 4, \Z 10})
(def valid-characters (set (cons wildcard 
                                 (keys scores))))
(def default-limit 100)

;; helper functions

(defn limited-list-prepper 
  "Changes to 1-indexed"
  [limited-list]
  (map-indexed #(conj %2 (inc %1))
               limited-list))

(defn print-func [word score i]
  (println (format "%d. '%s' = %d"  
                   i
                   word
                   score)))

(defn remove-first
  "Remove the first instance of a value from a collection. If the value
  isn't present, do nothing. Copped from
  https://groups.google.com/d/msg/clojure/nkANE4GxFmU/7YgW6UMA560J"  
  [x coll]
  (let [[pre post] (split-with #(not= x %)
                               coll)]
    (concat pre (rest post))))

(defn open-dictionary 
  "Lazily read file"
  [dictionary-path proc]
  (with-open [rdr (clojure.java.io/reader dictionary-path)]
    (proc (line-seq rdr))))

;; main logic

(defn letter-intersection
  "Takes the rack, the number of wildcards, and a word. Returns
  nil if unable to use wildcards, else returns list of present letters
  ready for scoring."
  [rack num-wcs word]
  (loop [word-remaining word
         rack-remaining rack 
         remaining-wcs num-wcs
         intersected []]
    (let [letter (first word-remaining)]
      (cond (nil? letter) intersected ;; word exhausted
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

(defn get-match-list 
  "Takes the list of words and the rack, filters using match? function,
  pairs words in tuples with their scores, and then sorts on those scores 
  in descending order."
  [rack list-of-words]
  (time 
    (let [max-length (count rack)
          [num-wcs tame-rack] ((juxt #(count (filter %1 %2))
                                    remove) 
                                #{wildcard} rack) ;; because I can...
          matches-scores (for [word (filter #(<= (count %) max-length)
                                            list-of-words)
                                    ;; don't bother looking at longer words
                                :let [matched-letters (letter-intersection tame-rack
                                                                          num-wcs
                                                                          (seq word))]
                                :when matched-letters] ;; returns nil when no match
                              [word (score-letters matched-letters)])]
        (sort-by second 
                >
                matches-scores))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
    (let [[options args banner] 
               (cli args
                    "This program takes a rack of letters and a dictionary,
                     and returns a list of your best scoring options."
                    ["-r" "--rack" "Your rack" 
                     :parse-fn #(seq (string/upper-case %))]
                    ["-d" "--dict" "A list of words to check against."
                     :default path-to-dict]
                    ["-l" "--limit" "The number of word to print"
                     :parse-fn #(Integer. %)
                     :default default-limit]
                    ["-h" "--help" "Show help."
                     :default false 
                     :flag true]
                    )]
      ;; check necessary conditions met
      (doseq [[required-predicate format-args]
              [[#(:help options)
                [banner]]
               [#(not (:rack options))
                ["The program can't do anything if you don't give it a rack
                  with -r"]]
               [#(not-every? valid-characters (:rack options))
                ["Invalid Scrabble characters passed: %s"
                 (:rack options)]]
               [#(not (.exists (as-file (:dict options))))
                ["Cannot find the dictionary at %s"
                  (:dict options)]]]]
        (when (required-predicate)
          (println (apply format
                          format-args))
          (System/exit 0)))
      ;; the fun begins...
      (let [match-list (open-dictionary (:dict options) 
                                        (partial get-match-list 
                                                 (:rack options)))
            limited-list (limited-list-prepper (take (:limit options) 
                                                     match-list))]
        ;; print all dem words
        (doseq [word-info limited-list]
          (apply print-func word-info))
        (System/exit 1))))
