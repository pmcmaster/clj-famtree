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
  values are a collection of matched records"
  [rec-source]
  (reduce match-record-into-results [{} 0] rec-source))

(defn -main
  [& args]
  (p/print-record-summary)
  (let [[matches-by-type _] (match-records (rec-colls/all-source-recs-with-types))]
    (doseq [[match-keys matches] matches-by-type]
      (let [match-count (count matches)]
        (println match-keys match-count)))))

