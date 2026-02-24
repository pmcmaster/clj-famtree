(ns famtree.fields
  (:require [clojure.string :as str]
            [clojure.set :as set]))

;; Computed record queries

(defn no-data?
  "Returns field-content if it matches empty field marker which is something like '-----'"
  [field-content]
  (if (string? field-content)
    (re-matches #"-+" field-content)))

(defn first-forename-from-rec
  "Get the first forename from a record {:forename 'Bob David' would return 'Bob'}"
  [rec]
  (first (str/split (:forename rec) #" ")))

(defn =-and-has-data?
  "Two values are considered equal only if they are not the 'no data' '-----' value,
  and they match"
  [val1 val2]
  (and (not (no-data? val1)) (= val1 val2)))

(defn =-or-no-data?
  "Two values are considered possibly equal if either one of them is the 'no data' '-----' value,
  or if they match"
  [val1 val2]
  (or (no-data? val1) (no-data? val2) (= val1 val2)))

(defn between-years?
  "Is year between first and second elements of start-end-year (inclusive)
  start-end-year may be nil"
  [start-end-year year]
  (when-not (nil? start-end-year)
    (let [[start-year end-year] start-end-year
          search-set (set(range start-year (inc end-year)))]
      (contains? search-set year))))

(defn ranges-overlap?
  "Takes two pairs of numbers and checks if they overlap (inclusive)
  Returns nil if either are nil"
  [pair1 pair2]
  (when-not (some nil? [pair1 pair2])
    (let [[pair1-1 pair1-2] (sort pair1)
          [pair2-1 pair2-2] (sort pair2)
          set1 (set (range pair1-1 (inc pair1-2)))
          set2 (set (range pair2-1 (inc pair2-2)))]
      (not (empty? (set/intersection set1 set2))))))

(defn est-birth-range-from-age
  "Estimates a birth year range from an age and a record year
  Birth year may be one year further behind than the simple subtraction"
  [age record-year]
  (if-not (some nil? [age record-year])
    (let [calc-birth-year (- record-year age)]
      [(dec calc-birth-year) calc-birth-year])))

(defn est-age-at-year
  "Estimate the age range from a record in a given year"
  [birth-year-range year]
  (when-not (nil? birth-year-range)
    (sort (map #(- year %) birth-year-range))))

