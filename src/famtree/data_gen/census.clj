(ns famtree.data-gen.census
    (:require [famtree.data-gen.synthetic-records-store :as store]
              [clojure.data.csv :as csv]
              [clojure.java.io :as io] ))


;; Years when a census was completed in Scotland, and for which
;; (as of 2026) the data is available)
(def census-years #{1841 1851 1861 1871 1881 1891 1901 1911 1921})

(defn simulate-census
  "Create census records if it is a census year. Does not actually
  affect the population at all, so just returns that unchanged"
  [year population]
  (when (contains? census-years year)
    (println year "is a census year")
    (doseq [person population]
      (store/add-record :census year person)))
  population)

(def header-row
  ["Surname" "Forename" "Year" "Gender" "Age at Census"
   "Ref" "RD Name" "County / City"])

(defn record-to-csv-order
  "Convert the info stored for an event into the right shape for writing to 
  CSV file for this record type."
  [[year record]]
  [(:sname record) (:fname record) year (:gender record) (:age record)
   (:id record) (:location record) "-----"])

(def filename "data/census.csv")

(defn write-csv
  "Write header row and data for death records"
  []
  (when (.exists (io/file filename))
    (println "Exiting as" filename "file exists")
    (System/exit 0))
  (io/make-parents filename)
  (let [census-records (get @store/record-store :census)]
    (with-open [writer (io/writer filename)]
     (csv/write-csv writer [header-row] :separator \tab)
     (let [records-for-csv (map record-to-csv-order census-records)]
       (csv/write-csv writer records-for-csv :separator \tab)))))
