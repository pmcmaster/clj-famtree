(ns famtree.fields.age-year)

;; Common utility functions for dealing with ages and years in records

(defn between-years?
  "Is year between first and second elements of start-end-year (inclusive)
  start-end-year may be nil, in which case nil is returned"
  [start-end-year year]
  (when start-end-year
    (let [[start-year end-year] (sort start-end-year)]
      (<= start-year year end-year))))

(defn ranges-overlap?
  "Takes two pairs of numbers and checks if they overlap (inclusive)
  Returns nil if either pair are nil"
  [pair1 pair2]
  (when-not (some nil? [pair1 pair2])
    (let [[pair1-1 pair1-2] (sort pair1)
          [pair2-1 pair2-2] (sort pair2)]
      (or (<= pair1-1 pair2-1 pair1-2)
          (<= pair2-1 pair1-1 pair2-2)))))

(defn est-birth-range-from-age
  "Estimates a birth year range from an `age` and a `record-year`
  Birth year may be one year further behind than the simple subtraction"
  [age record-year]
  (if-not (some nil? [age record-year])
    (let [calc-birth-year (- record-year age)]
      [(dec calc-birth-year) calc-birth-year])))

(defn est-age-at-year
  "Estimate the age range from a record in a given year"
  [birth-year-range year]
  (when birth-year-range
    (sort (map #(- year %) birth-year-range))))

