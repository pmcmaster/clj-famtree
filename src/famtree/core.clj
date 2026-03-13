(ns famtree.core
  "Main entry point for project
  Runs the main matching process and triggers output of results"
  (:require [famtree.records.collections :as rec-colls]
            [famtree.printing :as p]
            [famtree.utils :as utils]
            [famtree.match.core :as m])
  (:gen-class))

(defn assoc-new-match-backref
  "Update `coll` to add an entry for the reverse-direction match reference
  Order of `ref-pair` and `source-rec`/`match-rec` is reversed"
  [coll ref-pair source-rec match-rec]
  (assoc-in coll [(reverse ref-pair) match-rec] source-rec))

(defn assoc-new-match
  "Update `coll` to add an entry for a new match reference and the reverse
  reference"
  [coll ref-pair source-rec match-rec]
  (-> coll
      (assoc-in [ref-pair source-rec] match-rec)
      (assoc-new-match-backref ref-pair source-rec match-rec)))

(defn match-record-into-results
  "Matches single record and adds to matches-by-type, updates match-count"
  [[matches-by-type match-count] [ref-pair source-rec]]  
  ;; Check for existing match first
  (if-not (get-in matches-by-type [ref-pair source-rec])
    (if-let [matching-rec (m/find-single-match source-rec ref-pair)]
      (let [updated-matches-by-type (assoc-new-match matches-by-type
                                                     ref-pair
                                                     source-rec matching-rec)
            new-match-count (inc match-count)]
        (p/print-match-success source-rec matching-rec new-match-count)
        [updated-matches-by-type new-match-count])
      [matches-by-type match-count])
    [matches-by-type match-count]))

(defn match-records
  "Match records from the given source rec-source
  Returns a pair of values [map-of-results result-count]
  map-of-results: keys are a pair of record types which have been matched,
  values are a sub-map of matched records (source-rec to matched-rec)"
  [rec-source]
  (reduce match-record-into-results [{} 0] rec-source))

(defn find-subset-containing-either
  "Find the first set in seq `xs` containing either `val1` or `val2`"
  [val1 val2 xs]
  (let [target-set (hash-set val1 val2)]
    (first (filter #(some target-set %) xs))))

(defn update-results-set
  "Remove `old-set` and add `new-set` to enclosing set `containing-set`"
  [containing-set old-set new-set]
  (-> containing-set
      (disj old-set)
      (conj new-set)))

(defn update-and-link
  "Add source-rec and dest-rec to the set of sets of existing records
  They should both be added to the set which already contain one of the
  records"
  [set-of-record-sets [source-rec dest-rec]]
  (if-let [existing-set (find-subset-containing-either
                          source-rec dest-rec
                          set-of-record-sets)]
    (->> (conj existing-set source-rec dest-rec)
     (update-results-set set-of-record-sets existing-set)) 
    (conj set-of-record-sets (hash-set source-rec dest-rec))))

(defn collate-results
  "Build up a set of sets, where each contained set is entirely records relating
  to the same person
  `matches-by-type` is a map with keys being a source and destination reference
  for the record collection values are a pair of records
  (source-rec and dest-rec)"
  [matches-by-type]
  (->> matches-by-type
       vals
       (reduce concat [])
       (reduce update-and-link #{})))

(defn -main
  [& args]
  (p/print-record-summary rec-colls/all-collection-refs)
  (let [[matches-by-type _match_count] (match-records
                                         (rec-colls/all-source-recs-with-types))
        matches-grouped-by-person (collate-results matches-by-type)]
    (p/print-collated-results matches-grouped-by-person)))

