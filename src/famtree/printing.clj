(ns famtree.printing 
  (:require [famtree.records :as recs]))

;; Print helpers for displaying records

(defn print-stats
  "Print out details of info referenced by data-symbol"
  [data-symbol]
  (let [data (var-get data-symbol)]
    (println data-symbol (count data) "records")))

(defn print-record-summary
  "Prints out counts of the loaded records"
  []
  (println "Loaded:")
  (print-stats #'recs/births)
  (print-stats #'recs/deaths)
  (print-stats #'recs/marriages)
  (print-stats #'recs/census))

;; General display of records

(defn print-match-success
  "Info on two matching records"
  [source-rec matched-rec]
  (println "==== Matched 1-1 ====")
  (println source-rec)
  (println matched-rec)
  (println))

(defn print-grouped-data
  "Print out grouped data
  Grouping is printed first, then records in that grouping line-by-line"
  [grouped-data]
  (doseq [[grouping-key coll] grouped-data]
    (println "==" grouping-key "==")
    (doseq [data-row coll]
      (println "  " data-row))))

(defn print-records
  "Prints out records, one per-line"
  [records]
  (doseq [record records]
    (println record)))

;; Specific for fields

(defn print-grouped-by-year
  "Prints a collection of records (likely census records), grouped by year
  with headings for each year, then each record on a line"
  [records]
  (print-grouped-data (group-by :year records)))

