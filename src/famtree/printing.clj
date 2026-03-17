(ns famtree.printing
  "Functions for outputting progress at various stages of matching")

(defn print-stats
  "Print out count of records referenced by data-symbol"
  [data-symbol]
  (println data-symbol (count (var-get data-symbol)) "records"))

(defn print-record-summary
  "Prints out counts of the loaded records"
  [record-collection-refs]
  (println "Loaded:")
  (doseq [record-ref record-collection-refs] (print-stats record-ref)))

;; General display of records

(defn print-match-success
  "Info on two matching records"
  [source-rec matched-rec match-count]
  (println "==== Matched 1-1" match-count " matches====")
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
  (doseq [record records] (println record)))


(defn print-details-for-person-set
  "Print details for a person-set, sorted by year"
  [records-for-person]
 (doseq [each-rec (sort-by :year records-for-person)]
      (println each-rec)))

;; Collated results (a set containing a set of records per-person)

(defn print-collated-results
  "Print the results out in a readable format"
  [set-of-sets-per-person]
  (doseq [records-for-person set-of-sets-per-person]
    (println)
    (print-details-for-person-set records-for-person))
  (println "Details for" (count set-of-sets-per-person) "people")
  set-of-sets-per-person)

;; Specific for fields

(defn print-grouped-by-year
  "Prints a collection of records (likely census records), grouped by year
  with headings for each year, then each record on a line"
  [records]
  (print-grouped-data (group-by :year records)))


