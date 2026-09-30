(ns famtree.data-gen.deaths
  (:require [famtree.data-gen.synthetic-records-store :as store]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

; Simulate people dying. Values used are quite sensitive in relation to those
; use for birth rates. If one is changed, likely need to change the other to
; avoid population explosion or collapse

(defn chance-of-death
  "What is the annual chance that someone dies at a given age?"
  [age]
  (cond (< age 2) 0.2
        (< age 55) 0.02
        (< age 75) 0.3
        (< age 90) 0.8
        :else 1))

(defn simulate-deaths
  "Run through the population and take out people who die"
  [year population]
  (filter
    #(if (< (rand) (chance-of-death (:age %)))
       (do (println % "dies")
           (store/add-record :death year %)
           false)
       true)
    population))

(def header-row
  ["Surname" "Forename" "Age at death" "Mother's Maiden Name" "Gender"
   "Year" "Ref" "RD Name"])

(defn record-to-csv-order
  "Convert the info stored for an event into the right shape for writing to 
  CSV file for this record type."
  [[year record]]
  [(:sname record) (:fname record) (:age record) "-----" (:gender record) year
   (:id record) (:location record)])

(def filename "data/deaths.csv")

(defn write-csv
  "Write header row and data for death records"
  []
  (when (.exists (io/file filename))
    (println "Exiting as" filename "file exists")
    (System/exit 0))
  (io/make-parents filename)
  (let [death-records (get @store/record-store :death)]
    (with-open [writer (io/writer filename)]
     (csv/write-csv writer [header-row] :separator \tab)
     (let [records-for-csv (map record-to-csv-order death-records)]
       (csv/write-csv writer records-for-csv :separator \tab)))))
