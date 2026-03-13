(ns famtree.utils
  "Utility functions for working with records")

;; TODO: Move these to where they are used and delete this module

(defn counts-by-grouping
  "Count of records for each group"
  [grouped-records]
  (map
    (fn [record]
      (let [grouping-key (first record)
            record-count (-> record second count)]
        (vector grouping-key record-count)))
    grouped-records))
    
(defn if-1-only
  "Return the element in `coll` if there is only one
  Returns nil if there are zero or > 1 items"
  [coll]
  (if (= 1 (count coll))
    (first coll)))

