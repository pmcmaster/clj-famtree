(ns famtree.core
  (:require [famtree.records.collections :as rec-colls]
            [famtree.printing :as p]
            [famtree.utils :as utils]
            [famtree.match.core :as m])
  (:gen-class))

(defn assoc-new-match-backref
  "Update coll to add an entry for the reverse-direction match reference
  Order if ref-pair and source-rec/match-rec is reversed"
  [coll ref-pair source-rec match-rec]
  (assoc-in coll [(reverse ref-pair) match-rec] source-rec))

(defn assoc-new-match
  "Update coll to add an entry for a new match reference and the reverse reference"
  [coll ref-pair source-rec match-rec]
  (-> coll
      (assoc-in [ref-pair source-rec] match-rec)
      (assoc-new-match-backref ref-pair source-rec match-rec)))

(defn match-record-into-results
  "Matches single record and adds to matches-by-type, updates match-count"
  [[matches-by-type match-count] [ref-pair source-rec]]  
  (if-not (get-in matches-by-type [ref-pair source-rec]) ; Check for existing matches
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

(defn relates-to-either
  "Returns a-set if it contains either rec1 or rec2"
  [a-set rec1 rec2]
  (if (or (a-set rec1)
          (a-set rec2))
    a-set))

(defn update-and-link
  "Add source-rec and dest-rec to the set of sets of existing records
  They should both be added to sets which already contain one of the records"
  [set-of-record-sets [source-rec dest-rec]]
  (if-let [existing-set (first (filter ; TODO This first filter is not nice
                                 (fn [e-set] (relates-to-either e-set source-rec dest-rec))
                                 set-of-record-sets))]
    (let [updated-set-for-person (conj existing-set source-rec dest-rec)]
      (-> set-of-record-sets
         (disj existing-set)
         (conj updated-set-for-person)))
    (conj set-of-record-sets #{source-rec dest-rec})))

(defn update-link-marriage
  "Add marriage record to existing records for a person
  These need to be handled separately because they relate to two people"
  [set-of-record-sets [marriage-rec other-rec]]
  (if-let [existing-set (first (filter ; TODO This first filter is not nice
                                 (fn [e-set] (e-set other-rec))
                                 set-of-record-sets))]
    (let [updated-set-for-person (conj existing-set marriage-rec)]
      (-> set-of-record-sets
          (disj existing-set)
          (conj updated-set-for-person)))
    (conj set-of-record-sets #{marriage-rec other-rec})))

(defn process-non-marriage-results
  "Build up a set of sets, where each contained set is entirely records relating
  to the same person
  Input is a map with keys being a source and destination reference for the record collection
  values are a pair of records (source-rec and dest-rec)"
  [matches-by-type]
  (->> matches-by-type
       (filter (fn [[match-types _]] (not-any? (partial = #'rec-colls/marriages)
                                               match-types)))
       (into {})
       vals
       (reduce concat [])
       (reduce update-and-link #{})))

(defn add-marriage-records-to-results
  "Add marriage records in to other records per-person
  Need to be handled separately as can refer to two people"
  [matches-by-type set-of-record-sets]
  (->> matches-by-type
       (filter (fn [[[source-rec-coll-ref _] _]] (= #'rec-colls/marriages
                                                     source-rec-coll-ref)))
       (into {})
       vals
       (reduce concat [])
       (reduce update-link-marriage set-of-record-sets)))

(defn collate-results
  "Collate output from the matching results
  matches-by-type is a map
  keys are a pair of references (source dest) to record collections
  values are a pair of matched records (source and dest)"
  [matches-by-type]
  (->> (process-non-marriage-results matches-by-type)
       (add-marriage-records-to-results matches-by-type)))

(defn -main
  [& args]
  (p/print-record-summary)
  (let [[matches-by-type _] (match-records (rec-colls/all-source-recs-with-types))
        matches-grouped-by-person (collate-results matches-by-type)]
    (p/print-collated-results matches-grouped-by-person)))

