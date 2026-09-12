(ns famtree.probab.comp-forename
  (:require [famtree.probab.comp-basic :as comparisons]
            [clj-fuzzy.metrics :refer [jaro-winkler]]))

(defn forename-jaro-match-score
  "Return Jaro-Winkler match score for :forename from `rec1` and `rec2`"
  [rec1 rec2]
  (let [fn1 (:forename rec1)
        fn2 (:forename rec2)
        jaro-score (jaro-winkler fn1 fn2)]
    jaro-score))

(defn forename-exact-match?
  "Is forename an exact match for two records?"
  [rec1 rec2]
  (comparisons/exact-matches-field? :forename rec1 rec2))

(defn forename-v-close-match?
  "Is forename a very close match?"
  [rec1 rec2]
  (> (forename-jaro-match-score rec1 rec2)
     0.8))

(defn forename-vague-match?
  "Is forename a vague match"
  [rec1 rec2]
  (> (forename-jaro-match-score rec1 rec2)
     0.65))

(defn forename-no-match?
  "'Comparison' that always returns true"
  [_rec1 _rec2]
  true)

(def fns-and-weights
  "List of comparisons to work through for matching on forename and the
  relevant weights to use in the case that each match fn succeeds."
  [[forename-exact-match? {:match-prob 0.9 :unmatch-prob 0.6}]
   [forename-v-close-match? {:match-prob 0.95 :unmatch-prob 0.89}]
   [forename-vague-match? {:match-prob 0.55 :unmatch-prob 0.6}]
   [forename-no-match? {:match-prob 0.1 :unmatch-prob 0.99}]])

