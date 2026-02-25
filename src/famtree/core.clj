(ns famtree.core
  (:require [famtree.records.collections :as rec-colls]
            [famtree.printing :as p]
            [famtree.utils :as utils]
            [famtree.match.core :as m])
  (:gen-class))

(defn assoc-new-match-backref
  "Update coll to add an entry for the reverse-direction match reference"
  [coll ref-pair source-rec match-rec]
  (assoc-in coll [ref-pair match-rec] source-rec))

(defn assoc-new-match
  "Update coll to add an entry for a new match reference and the reverse reference"
  [coll ref-pair source-rec match-rec]
  (-> coll
      (assoc-in [ref-pair source-rec] match-rec)
      (assoc-new-match-backref (reverse ref-pair) source-rec match-rec)))

(defn match-records
  "Match records from source-seq"
  ([source-seq] (match-records {} 0 source-seq))
  ([matches-by-type match-count source-seq]
   (if-let [[ref-pair source-rec] (first source-seq)]
     (if-let [existing-match (get-in matches-by-type [ref-pair source-rec])]
       (recur matches-by-type match-count (rest source-seq))
       (if-let [matching-rec (m/find-single-match source-rec ref-pair)]
         (let [updated-matches-by-type (assoc-new-match matches-by-type
                                                        ref-pair
                                                        source-rec matching-rec)
               new-match-count (inc match-count)]
           (p/print-match-success source-rec matching-rec)
           (println new-match-count "matches")
           (recur updated-matches-by-type new-match-count (rest source-seq)))
         (recur matches-by-type match-count (rest source-seq)))))))

(defn -main
  [& args]
  (p/print-record-summary)
  (match-records (rec-colls/all-source-recs-with-types)))

