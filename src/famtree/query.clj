(ns famtree.query
  "Query functions for records"
  (:require [famtree.records.collections :as rec-colls]))

(defn counts-by-grouping
  "Count of records for each group"
  [grouped-records]
  (map
    (fn [record]
      (let [grouping-key (first record)
            record-count (-> record second count)]
        (vector grouping-key record-count)))
    grouped-records))

(defn census-counts-by-region
  "Count of census records for each region"
  []
  (counts-by-grouping (group-by :county-city rec-colls/census)))

