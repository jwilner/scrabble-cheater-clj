(ns scrabble-cheater.core
  (:gen-class)
  (:require [clojure.string :as string]))

(def wildcard "@")
(def path-to-dict "dictionaries/sowpods.txt")
(def valid-chars (into (vector wildcard)
                       (map #(str (char %)) (range 90 64 -1))))
(def max-num-chars 9)

(def char-map {(apply concat (for [[n vec-letters]  
                                     [[2 [\D \G]]
                                      [3 [\B \C \M \P]]
                                      [4 [\F \H \V \W \Y]]
                                      [5 [\K]]
                                      [8 [\J \X]]
                                      [10 [\Q \Z]]]]
                                    (map #(vector (str n) %) 
                                       vec-letters)))})


(def special-letters 
            {\C 3, \B 3, \D 2, \G 2, \F 4, \H 4, \K 5, \J 8, \M 3,
             \Q 10, \P 3, \W 4, \V 4, \Y 4, \X 8, \Z 10})
(def scores (into {} (concat  
                        (for [c valid-chars
                              :when (and (not (contains? special-letters c))
                                         (not= c wildcard))] 
                              [c 1])
                        special-letters)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (doseq [a args]
    (println a)))
