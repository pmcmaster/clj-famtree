(ns famtree.probab.comp-age-year)

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
    

