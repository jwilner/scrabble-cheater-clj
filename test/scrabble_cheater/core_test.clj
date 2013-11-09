(ns scrabble-cheater.core-test
  (:require [clojure.test :refer :all]
            [scrabble-cheater.core :refer :all]))

(def match-triples [[true? "WANDER" 0 "WANDER"]
                    [true? "A" 2 "BE"]
                    [true? "WANDER" 3 "WANDERING" ]
                    [true? "ASODIJ" 0 "SODA" ]
                    [true? "A" "CATERING" 8]
                    [true? "CATSASDF" "CATS" 0]
                    [false? "A" "GERMAN" 0]
                    [false? "C" "LITERALLY" 2])

(deftest match-testing
  (testing
    "The main match function. Too bad doing it this way 
    you can't see the literal values."
    (dorun (map (partial apply #(is (%1 (match? (seq %2)
                                                (seq %3)
                                                %4))))
                 match-triples))))
