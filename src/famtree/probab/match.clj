(ns famtree.probab.match

;; Match pairs of records probabilistically, or at least calculate a match
;; score between two records. This approach generates more flexibility in
;; matching vs the previous (determinstic) approach, and should also be easier
;; to extend)

(defn year-of-birth-from-census
  "Calculate approx. year of birth from census record"
  [c-rec]
  (- (:year c-rec) (:age-at-census c-rec)))

(defn age-matches?
  "Does age match for two census records
  Matches when year-of-birth difference is 0 or 1 years"
  [c-rec1 c-rec2]
  (let [year-of-birth1 (year-of-birth-from-census c-rec1)
        year-of-birth2 (year-of-birth-from-census c-rec2)
        year-abs-diff (abs (- year-of-birth1 year-of-birth2))]
    (<= year-abs-diff 1)))

(defn match-field
  "Does a given field match for two records?"
  [field rec1 rec2]
  (= (field rec1) (field rec2)))

(def field-probs
  [{:match-fn (partial match-field :forename)
    :match-prob 0.95 
    :unmatch-prob 0.89} ;; TODO: Dynamic unmatch prob based on the name itself?
   ; {:match-fn age-matches?
   ;  :match-prob 0.8
   ;  :unmatch-prob 0.4}
   {:match-fn (partial match-field :gender)
    :match-prob 0.99
    :unmatch-prob 0.5}
   {:match-fn (partial match-field :county-city)
    :match-prob 0.3
    :unmatch-prob 0.1}
   {:match-fn (partial match-field :rd-name)
    :match-prob 0.4
    :unmatch-prob 0.05}])

;; Uses Felligi-Sunter method to match records.
;; Details well-explained in talk and linked resources at:
;; https://github.com/oakmac/record-linking-talk/tree/master
;; And more about the method in particular, and the maths at:
;; https://www.robinlinacre.com/probabilistic_linkage/

(defn weight-for-match
  "Updated weight following a match"
  [initial-weight match-prob unmatch-prob]
  (+ initial-weight (Math/log (/ match-prob unmatch-prob))))

(defn weight-for-unmatch
  "Updated weight following an unmatch"
  [initial-weight match-prob unmatch-prob]
  (+ initial-weight (Math/log (/ (- 1 match-prob) (- 1 unmatch-prob)))))

(defn match-score-for-field
  "Takes a set of field probs & match fn and two records and updates the weight
  for those"
  [rec1 rec2 weight field-info]
  (let [{match-fn :match-fn
         match-prob :match-prob
         unmatch-prob :unmatch-prob} field-info]
    (if (match-fn rec1 rec2)
      (weight-for-match weight match-prob unmatch-prob)
      (weight-for-unmatch weight match-prob unmatch-prob))))

(defn match-score
  "Match `rec1` against `rec2` and determine a match score"
  [rec1 rec2]
  (reduce
    (partial match-score-for-field rec1 rec2)
    0
    field-probs))

(defn match-result
  "Returns a tuple of:
  [match-score matched-record]"
  [source-rec target-rec]
  [(match-score source-rec target-rec) target-rec])

(defn match-against
  "Match `source-rec` against records in `target-coll`.
  Returns a list of tuples with key of the match score and value of the
  matched record. These are sorted, so highest weights are first"
  [source-rec target-coll]
  (let [targets-with-weights (map (partial match-result source-rec)
                                  target-coll)]
    (sort-by first > targets-with-weights)))


