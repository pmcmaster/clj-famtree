(ns famtree.data-gen.synthetic-records-store)

;; TODO Actual functions for writing

; Keyed by type of record, then with a collection of people/years for each
; record which is going to be written

(def record-store (atom {}))

(defn missing-record-chance
  "Chance that a record is missing for a given `year`"
  [year]
  (cond (< year 1800) 0.5
        (< year 1850) 0.4
        (< year 1900) 0.3
        (< year 1950) 0.1
        :else 0.01))

(defn miss-record
  "Should an event occurring in `year` not be recorded?"
  [year]
  (<= (rand) (missing-record-chance year)))

(defn add-record-to-store
  "Function to perform the update on `existing-store`"
  [existing-store rec-type year data]
  (let [records-for-type (get existing-store rec-type [])
        updated-records (conj records-for-type [year data])]
    (assoc existing-store rec-type updated-records)))

(defn add-record
  "Add a new record of type `rec-type` to the record store
  `year` should be the year the record relates to and `data` is relevant
  data, probably a person or two"
  [rec-type year data]
  (swap! record-store add-record-to-store rec-type year data))

(defn print-record-summary
  []
  (doseq [[k coll] @record-store]
    (println k (count coll) "records")))
