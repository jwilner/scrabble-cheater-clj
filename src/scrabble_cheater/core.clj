(ns scrabble-cheater.core
  (:gen-class)
  (:require [clojure.string :as string]))

(def wildcard "@")
(def path-to-dict "dictionaries/sowpods.txt")
(def valid-chars (into (vector wildcard)
                       (map #(str (char %)) (range 90 64 -1))))
(def max-num-chars 9)
(def scores {\A 1, \B 3, \C 3, \D 2, \E 1, \F 4, \G 2, \H 4,
             \I 1, \J 10, \K 5, \L 1, \M 3, \N 1, \O 1, \P 3,
             \Q 10, \R 1, \S 1, \T 1, \U 1, \V 4, \W 4, \X 8,
             \Y 4, \Z 10})

(def match-test [rack word wcs]
  (loop [wcs-available wcs
         letters-available (seq rack)
         unhandled-letters (seq word)]
    (let [enough-wildcards (neg? wcs-available) 
          c (first unhandled-letters)] 
        (if (or (nil? c) 
                (not (enough-wildcards)))
          enough-wildcards
          (if (contains? letters-available c)
            (recur 
              wcs-necessary
              (remove letters-available c)
              (rest unhandled-letters))
            (recur 
              (inc wcs-necessary)
              letters-available
              (rest unhandled-letters)))))))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (doseq [a args]
    (println a)))
