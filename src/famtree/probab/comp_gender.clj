(ns famtree.probab.comp-gender
  (:require [famtree.probab.comp-basic :as comparisons]))

(defn gender-exact-match?
  [rec1 rec2]
  (comparisons/exact-matches-field? :gender rec1 rec2))

(defn gender-no-match?
  [_rec1 _rec2]
  true)

(def fns-and-weights
  [[gender-exact-match? {:match-prob 0.995 :unmatch-prob 0.6}]
   [gender-no-match? {:match-prob 0.005 :unmatch-prob 0.4}]])
