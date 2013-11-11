(ns scrabble-cheater.core-test
  (:require [clojure.test :refer :all]
            [scrabble-cheater.core :refer :all]))

(def match-triples [[true? "WANDER" 0 "WANDER"]
                    [true? "A" 2 "BE"]
                    [true? "WANDER" 3 "WANDERING" ]
                    [true? "ASODIJ" 0 "SODA" ]
                    [true? "A" 8 "CATERING"]
                    [true? "CATSASDF" 0 "CATS"]
                    [false? "A" 0 "GERMAN"]
                    [false? "C" 2 "LITERALLY"]])

(def letters-scores [["CAB" 7]
                     ["CHEATER" 12]])

(def letter-intersections [["" 0 "A" nil]
                          ["" 1 "A" []]
                          ["A" 0 "B" nil]
                          ["A" 0 "" []]
                          ["A" 1 "B" []]
                          ["A" 0 "A" [\A]]
                          ["AA" 0 "A" [\A]]
                          ["A" 1 "AA" [\A]]
                          ["ABC" 1 "AB" [\A \B]]
                          ["A" 8 "LITERALLY" [\A]]
                          ["A" 7 "LITERALLY" nil]
                          ["ACHIR" 1 "CHAIR" [\C \H \A \I \R]]
                          ["ACHIS" 1 "CHAIRS" [\C \H \A \I \S]]
                          ["ACHIS" 0 "CHAIRS" nil]])

(deftest letter-intersection-test
  (testing
    ""
    (dorun (map (partial apply 
                         #(is (= (letter-intersection (seq %1) 
                                                      %2 
                                                      (seq %3))
                                  %4)))
                letter-intersections))))


(deftest match-test
  (testing
    "The main match function. Too bad doing it this way 
    you can't see the literal values."
    (dorun (map (partial apply #(is (%1 (match? (seq %2)
                                                %3 
                                                (seq %4)))))
                 match-triples))))

(deftest score-letters-test
  (testing
    "Simple function that scores word using HO funcs"
    (dorun (map (partial apply #(is (= (score-letters %1)
                                       %2)))
                letters-scores))))


