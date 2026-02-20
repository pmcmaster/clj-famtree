(ns famtree.query
  (:require [famtree.records :as recs]
            [famtree.utils :as utils]))

;; General functions for querying against records

(defn census-counts-by-region
  "Count of census records for each region"
  []
  (utils/counts-by-grouping (group-by :county-city recs/census)))