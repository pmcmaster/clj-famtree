(ns famtree.probab.comp-location
  (:require [famtree.probab.comp-basic :as comparisons]))

(defn location-rd-name-exact-match?
  "Is :rd-name field of `rec1` and `rec2` an exact match?"
  [rec1 rec2]
  (comparisons/exact-matches-field? :rd-name rec1 rec2))

(defn location-no-match?
  "'Comparison' that always returns true"
  [_rec1 _rec2]
  true)

(def fns-and-weights
  "List of comparions to work through for matching on location, and the
  relevant weights to use in the case that each match fn succeeds."
  [[location-rd-name-exact-match? {:match-prob 0.5 :unmatch-prob 0.3}]
   ;; No-match weights are just 1 - w of the corresponding exact match weights
   [location-no-match? {:match-prob 0.995 :unmatch-prob 1}]])
