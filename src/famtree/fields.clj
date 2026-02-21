(ns famtree.fields
  (:require [clojure.string :as str]))

;; Computed record queries

(defn est-birth-from-death-rec
  "Estimated year of birth based on year of death and age.
  May be nil if either of those values are also nil"
  [death-record]
    ; TODO: Work in the difference in ages of a year-or-so depending on time-of-year of death vs birth
    (let [death-year (:year death-record)
          age-at-death (:age-at-death death-record)]
      (if-not (some nil? [death-year age-at-death])
        (- death-year age-at-death))))

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
