(ns famtree.fields
  (:require [clojure.string :as str]))

;; Computed record queries

(defn est-birth-from-death-rec
  [death-record]
  (try
    (let [death-year (Integer/parseInt (:year death-record))
          age-at-death (Integer/parseInt (:age-at-death death-record))]
      ; TODO: Work in the difference in ages of a year depending on time-of-year
      (- death-year age-at-death))
  (catch Exception ex ; TODO: Specific exception type and clean up the records!!
    nil)))

(defn first-forename-from-rec
  [rec]
  (first (str/split (:forename rec) #" ")))
  