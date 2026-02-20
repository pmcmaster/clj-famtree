(ns famtree.utils
  (:require [famtree.records :as recs]))

;; Utility functions for working with records

(defn counts-by-grouping
  "Count of records for each group"
  [grouped-records]
  (map
    (fn [record]
      (let [region-name (first record)
            record-count (-> record second count)]
        (vector region-name record-count)))
    grouped-records))
    
(defn random-record-type-pair
  "Return a pair of record types to try to match"
  []
  (let [type-list (vec recs/all-types)
        type1 (rand-nth type-list)]
    (if (= type1 :census) ; Census is only type one person can show up in multiple times
      [type1 (rand-nth type-list)]
      [type1 (rand-nth (vec (disj recs/all-types type1)))])))