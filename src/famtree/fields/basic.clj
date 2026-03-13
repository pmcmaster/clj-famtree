(ns famtree.fields.basic
  "Functions to test and compare fields in records"
  (:require [clojure.string :as str]))

;; Computed record queries

(defn no-data?
  "Returns field-content if it matches empty field marker which is something
  like '-----'"
  [field-content]
  (if (string? field-content)
    (re-matches #"-+" field-content)))

(defn first-word-from-field
  "Get the first word (splitting on spaces only) from a field in a record"
  [field rec]
  (first (str/split (field rec) #" ")))

(defn =-and-has-data?
  "Two values are considered equal only if they are not the no data ('-----')
  value, and they match"
  [val1 val2]
  (and (not (no-data? val1)) (= val1 val2)))

(defn =-or-no-data?
  "Two values are considered possibly equal if either one of them is the no
  data ('-----') value, OR if they match"
  [val1 val2]
  (or (no-data? val1) (no-data? val2) (= val1 val2)))
