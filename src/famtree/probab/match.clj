(ns famtree.probab.match
  (:require [famtree.probab.comp-forename :as comp-forename]
            [famtree.probab.felligi-sunter :as fel-sun]))

;; Match pairs of records probabilistically, or at least calculate a match
;; score between two records. This approach generates more flexibility in
;; matching vs the previous (determinstic) approach, and should also be easier
;; to extend)

(def compare-fns-and-weights
  "A list of functions which in-turn yield a list of functions and weights
  which may match against specific fields. If any of the functions match
  (when passed two records to compare) then the weights relating to that
  function are used (later) to calculate a match score for that scenario"
  [comp-forename/fns-and-weights
; ... other fields ...
   ])

(defn match-scores
  "Individual match scores for field matches defined in field-comparison-fns.
  Summing these will give the overall match score for the two records.
  Returns a list of maps, each with keys :label :score"
  ([rec1 rec2]
   (match-scores rec1 rec2 compare-fns-and-weights))
  ([rec1 rec2 comp-fns-and-weights]
   (map #(fel-sun/match-score-for-field rec1 rec2 %)
        comp-fns-and-weights)))

(defn overall-match-score
  "Calculate match weights for each of the fields being compared
  then sum then to derive an overall match score.
  Returns a single numerical score."
  ;; TODO: Initial weight may not necessarily always be 0. Should represent
  ;; probabability of two randomly-selected records being a match, which varies
  ;; depending on what the types of rec1/rec2 are
  [rec1 rec2]
  (let [results (match-scores
                  rec1 rec2)
        scores-only (map :score results)]
    (reduce + 0 scores-only)))

(defn match-result
  "Returns a tuple of:
  [overall-match-score matched-record]"
  [source-rec target-rec]
  [(overall-match-score source-rec target-rec) target-rec])

(defn match-against
  "Match `source-rec` against records in `target-coll`.
  Returns a list of tuples with key of the match score and value of the
  matched record. These are sorted, so highest weights are first"
  [source-rec target-coll]
  (let [targets-with-weights (map (partial match-result source-rec)
                                  target-coll)]
    (sort-by first > targets-with-weights)))


