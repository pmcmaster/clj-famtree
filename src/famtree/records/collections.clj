(ns famtree.records.collections
  (:require [famtree.records.core :use [map->BirthRec
                                        map->DeathRec
                                        map->MarriageRec
                                        map->CensusRec]]
    [famtree.load :as load]))

(def births (map map->BirthRec (load/data-for-type "births")))
(def deaths (map map->DeathRec (load/data-for-type "deaths")))
(def marriages (map map->MarriageRec (load/data-for-type "marriages")))
(def all-census (map map->CensusRec (load/data-for-type "census")))

(defn create-census-def-for-year
  "Create a ref for a year's worth of census records
  Census records for a given year are essentially a standalone dataset"
  [[year recs]]
  (intern 'famtree.records.collections (symbol (str "census-" year)) recs))

(def census-by-year-syms
  (->> all-census
       (group-by :year)
       (map create-census-def-for-year)))

(def all-collection-refs (concat [#'births #'deaths #'marriages] census-by-year-syms))

(defn rand-rec-from-coll-ref
  "Returns a random record from the collection referenced"
  [record-coll-ref]
  (rand-nth (var-get record-coll-ref)))

(defn all-source-recs-with-types
  "All permutations of each record type pairing with the records
  These are intended to be the starting point for a search
  Returns elements like:
  [['births 'marriages] BirthRec<blahblah>]"
  []
  (for [elem1 all-collection-refs
        elem2 all-collection-refs
        rec (var-get elem1)
        :when (not= elem1 elem2)]
    [[elem1 elem2] rec]))

