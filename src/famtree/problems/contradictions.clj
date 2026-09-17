(ns famtree.problems.contradictions
 (:require [famtree.printing :as p]
           [famtree.records :as rec])
 (:import [famtree.records BirthRec DeathRec CensusRec]))

;; Coded detail of situations where contradictory information is held about two
;; supposedly-matching records. For example, if there is a census record for
;; someone which is dated 25 years prior to their birth record.

;; TODO: Tests.
;; TODO: Revisit this module to see what is still useful with probablistic
;; matching approach

(defn multiple-recs
  "Do multiple recs in `rec-coll` pass the filter `filter-fn`"
  [filter-fn rec-coll]
  (let [filtered-recs (filter filter-fn rec-coll)]
    (> (count filtered-recs) 1)))

(defn multiple-recs-of-type
  "Are there multiple records of class `rec-type` in `rec-coll`?"
  [rec-type rec-coll]
  (multiple-recs #(= (class %) rec-type) rec-coll))

(defn multiple-entries-for-one-census-year
  "Are there multiple records for one person for one cencus year"
  [rec-coll]
  (let [census-recs (filter #(= (class %) CensusRec) rec-coll)
        grouped-census-recs (group-by :year census-recs)]
    (some #(> (count %) 1) (vals grouped-census-recs))))

(defn one-record-of-type-in
  "Get single record of `rec-type` from `rec-coll`.
  Returns nil if there are 0 or many."
  [rec-type rec-coll]
  (let [filtered-recs (filter #(= (class %) rec-type) rec-coll)]
    (when (= 1 (count filtered-recs))
      (first filtered-recs))))

(defn anything-before-birth-record
  "Are there any records before the birth record in `rec-col`"
  [rec-coll]
  (when-let [birth-rec (one-record-of-type-in BirthRec rec-coll)]
    (some #(< (:year %) (:year birth-rec)) rec-coll)))

(defn anything-after-death-record
  "Are there any records after the death record in `rec-col`"
  [rec-coll]
  (when-let [death-rec (one-record-of-type-in DeathRec rec-coll)]
    (some #(> (:year %) (:year death-rec)) rec-coll)))

;; TODO: Non-sequential age for census
;; TODO: Married name before marriage (after is fine))
;; TODO: Unexpected flip-flopping of middle names

(def problem-count
 "How many problems have been encountered?"
 (atom 0))

(defn check-problem
  "Check for problems in `rec-set` using `check-fn` with `label`"
  [check-fn label rec-set]
  (when (check-fn rec-set)
    (swap! problem-count inc)
    (println label)))

(defn check-set
  "Check a set of records supposedly for one person for contradictions"
  [rec-set]
  (println)
  (println "==========")
  (p/print-details-for-person-set rec-set)
  (check-problem (partial multiple-recs-of-type BirthRec)
                    "Multple birth recs"
                    rec-set)
  (check-problem (partial multiple-recs-of-type DeathRec)
                 "Multiple death recs"
                 rec-set)
  (check-problem anything-before-birth-record
                 "Records before birth rec"
                 rec-set)
  (check-problem anything-after-death-record
                 "Records after death-rec"
                 rec-set)
  (check-problem multiple-entries-for-one-census-year
                 "Same census year"
                 rec-set))
 
