(ns famtree.record-colls.main-records
  "Lists of records, organised by record type
  There is some initial filtering done on some of the collections"
  (:require [famtree.record-colls.raw-records :as raw-colls]
            [famtree.fields.marriage :as marriage]))

(def births (var-get #'raw-colls/births))
(def deaths (var-get #'raw-colls/deaths))

(def marriages
  "Records for marriages, focusing on the person recorded as surname and
  forename"
  (->> (var-get #'raw-colls/marriages)
       (filter marriage/possible-core-person)))

(def marriages-spouse 
  "Records for marriages, focusing on the person originally recorded as
  spouse-surname and spouse-forename. These fields are flipped around (when
  they are defined in raw-colls/marriages-spouse so that
  data is in the usual surname/forename fields"
  (->> (var-get #'raw-colls/marriages-spouse)
       (filter marriage/possible-core-person)))

(defn create-census-def-for-year
  "Create a ref for a year's worth of census records
  Census records for a given year are essentially a standalone dataset"
  [[year recs]]
  (intern 'famtree.record-colls.main-records
          (symbol (str "census-" year)) recs))

;; Declared here to avoid lint warnings in some hard-coded references
;; to these collections. Actual data is defined dynamically below.
(declare census-1911)
(declare census-1921)

(def census-by-year-syms
  "List of the dynamically-created record collections for census.
  One collection per-census-year."
  (->> (var-get #'raw-colls/all-census)
       (group-by :year)
       (mapv create-census-def-for-year)))

(def all-collection-refs-except-census
  "A collection of all record lists except the census ones"
  [#'births #'deaths #'marriages #'marriages-spouse])

(def all-collection-refs
  "A collection of all record lists"
  (concat all-collection-refs-except-census
          census-by-year-syms))

(def all-records-except-census
  "All records of all types (except census) in one bit flat collection"
  (apply concat (map var-get all-collection-refs-except-census)))

(def all-records
  "All records of all types in one big flat collection"
  (apply concat (map var-get all-collection-refs)))

(defn rand-rec-from-coll-ref
  "Returns a random record from the collection referenced"
  [record-coll-ref]
  (rand-nth (var-get record-coll-ref)))

(defn all-source-recs-with-types
  "All permutations of each record type pairing with the records.
  These are intended to be the starting point for a search.
  Should not include: matching a collection against itself
  or: matching marriage records against marriage-spouse records
  Returns elements like:
  [['births 'marriages] BirthRec<blahblah>]"
  []
  (for [elem1 all-collection-refs
        elem2 all-collection-refs
        rec (var-get elem1)
        :when (and (not= elem1 elem2)
                   (not= #{#'marriages #'marriages-spouse}
                         (hash-set elem1 elem2)))]
    [[elem1 elem2] rec]))

