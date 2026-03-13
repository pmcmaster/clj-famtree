(ns famtree.query
  "Query functions for records"
  (:require [famtree.records.collections :as rec-colls]
            [famtree.utils :as utils]))

(defn census-counts-by-region
  "Count of census records for each region"
  []
  (utils/counts-by-grouping (group-by :county-city rec-colls/census)))

