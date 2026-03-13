(ns famtree.records.collections
  "Lists of records, organised by record type
  There is some initial filtering done on some of the collections"
  (:require [famtree.records.raw-collections :as raw-colls]
            [famtree.records.names :as names]))

(def births (var-get #'raw-colls/births))
(def deaths (var-get #'raw-colls/deaths))

(def marriages
  "Records for marriages, focussing on the person recorded as surname and
  forename"
  (->> (var-get #'raw-colls/marriages)
       ;; If a person is a 'JONES', and male, and the core surename is 'SMITH'
       ;; then this person is going to stay a 'JONES' after marriage, and not
       ;; become a 'SMITH'. Filter these records out here.
       (filter (fn [rec] (let [is-female (names/female-forename?
                                           (:forename rec))
                               partner-is-male (names/male-forename?
                                                 (:spouse-forename rec))]
                           (or (or is-female partner-is-male)
                               (names/is-core-surname (:surname rec))))))))

(def marriages-spouse 
  "Records for marriages, focussing on the person recorded as spouse-surname
  and spouse-forename"
  (->> (var-get #'raw-colls/marriages-spouse)
       ;; If a person is a 'JONES', and male, and the core surename is 'SMITH'
       ;; then this person is going to stay a 'JONES' after marriage, and not
       ;; become a 'SMITH'. Filter these records out here.
       (filter (fn [rec] (let [is-female (names/female-forename?
                                           (:spouse-forename rec))
                               partner-is-male (names/male-forename?
                                                 (:forename rec))]
                           (or (or is-female partner-is-male)
                               (names/is-core-surname
                                 (:spouse-surname rec))))))))

(defn create-census-def-for-year
  "Create a ref for a year's worth of census records
  Census records for a given year are essentially a standalone dataset"
  [[year recs]]
  (intern 'famtree.records.collections (symbol (str "census-" year)) recs))

(def census-by-year-syms
  "List of the dynamically-created record collections for census.
  One collection per-census-year."
  (->> (var-get #'raw-colls/all-census)
       (group-by :year)
       (map create-census-def-for-year)))

(def all-collection-refs
  "A collection of all record lists"
  (concat [#'births #'deaths #'marriages #'marriages-spouse]
          census-by-year-syms))

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

