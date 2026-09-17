(ns famtree.determ.match.same-person
  (:require [famtree.determ.match.core :as match]
            [famtree.printing :as p]))

;; Match records together for the same person.
;; e.g., birth and death records for one person.

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
  "Matches single record and adds to matches-by-type, updates match-count.
  Prints out (to stdout) progress as it finds matches"
  [[matches-by-type match-count] [ref-pair source-rec]]  
  ;; Check for existing match first
  (if-not (get-in matches-by-type [ref-pair source-rec])
    (if-let [matching-rec (match/find-single-match source-rec ref-pair)]
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
  (let [[matches-by-type _match_count] (reduce match-record-into-results
                                               [{} 0]
                                               rec-source)]
    matches-by-type))
