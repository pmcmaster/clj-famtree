(ns famtree.match
  (:import [famtree.records BirthRec DeathRec MarriageRec CensusRec])
  (:require [famtree.fields :as fields]
            [famtree.match-proto :as match-p]
            [famtree.records :as recs]
            [famtree.consts :as consts]
            [famtree.utils :as utils]))

; Matching funcs for each type of record

(defn match-against-birth
  "Find records in other-rec-coll which could be matches against birth-rec"
  [birth-rec other-rec-coll]
  (let [birth-year (:year birth-rec)
        birth-gender (:gender birth-rec)
        birth-fname (fields/first-forename-from-rec birth-rec)
        birth-mm-name (:mm-name birth-rec)]
    (->>
      other-rec-coll
      (filter #(= (fields/first-forename-from-rec %) birth-fname))
      (filter #(fields/between-years?
                 (match-p/est-birth-year-range %)
                 birth-year))
      (filter #(match-p/match-on-gender % birth-gender))
      (filter #(match-p/match-on-mm-name % birth-mm-name)))))

(defn match-against-death
  "Find records in other-rec-coll which could be matches against death-rec"
  [death-rec other-record-coll]
  (let [est-birth-year-range-from-death (match-p/est-birth-year-range death-rec)
        death-gender (:gender death-rec)
        death-fname (fields/first-forename-from-rec death-rec)
        death-mm-name (:mm-name death-rec)]
    (->>
      other-record-coll
      (filter #(= (fields/first-forename-from-rec %) death-fname))
      (filter #(fields/ranges-overlap?
                 est-birth-year-range-from-death
                 (match-p/est-birth-year-range %)))
      (filter #(match-p/match-on-gender % death-gender))
      (filter #(match-p/match-on-mm-name % death-mm-name)))))

(defn match-against-marriage
  "Find records in other-rec-coll which could be matches against marriage-rec"
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        marriage-fname (fields/first-forename-from-rec marriage-rec)
        ]
    (->>
      other-rec-coll
      (filter #(= (fields/first-forename-from-rec %) marriage-fname))
      (filter #(let [birth-range (match-p/est-birth-year-range %)
                     est-age-at-marriage (fields/est-age-at-year birth-range marriage-year)]
                 (fields/ranges-overlap? consts/marriage-age-range
                                         est-age-at-marriage))))))

(defn match-against-census
  "Find records in other-rec-coll which could be matches against census-rec"
  [census-rec other-rec-coll]
  nil) ; Not impl.

(extend-protocol match-p/MatchAgainst
  DeathRec
  (match-fn [this] match-against-death)
  BirthRec
  (match-fn [this] match-against-birth)
  MarriageRec
  (match-fn [this] match-against-marriage)
  CensusRec
  (match-fn [this] match-against-census))

(defn not-impl-match
  [match-types]
  ; (println "Matching not impl. for" match-types) no-op
  )

(defn find-single-match
  "Match another type of record (in match-coll) from record-type against source-record
  source-coll is required to check back in the opposite direction that there is also only
  one matching record"
  [source-record source-coll match-coll]
  (let [source-to-new-match-fn (match-p/match-fn source-record)
        single-matching-rec (utils/if-1-only (source-to-new-match-fn source-record match-coll))]
    (when single-matching-rec
      (let [back-match-fn (match-p/match-fn single-matching-rec)]
        (when (utils/if-1-only (back-match-fn single-matching-rec source-coll))
          single-matching-rec)))))

(defn match-for-record
  [match-types record]
  (case match-types
    [:births :deaths] (find-single-match record recs/births recs/deaths)
    [:births :marriages] (find-single-match record recs/births recs/marriages)
    [:births :census] (not-impl-match match-types) ; Not impl
    [:deaths :births] (find-single-match record recs/deaths recs/births)
    [:deaths :marriages] (find-single-match record recs/deaths recs/marriages)
    [:deaths :census] (not-impl-match match-types) ; Not impl
    [:marriages :births] (find-single-match record recs/marriages recs/births)
    [:marriages :deaths] (find-single-match record recs/marriages recs/deaths)
    [:marriages :census] (not-impl-match match-types) ; Not impl
    [:census :census] (not-impl-match match-types) ; Not impl Search in same type of record
    [:census :births] (not-impl-match match-types) ; Not impl
    [:census :deaths] (not-impl-match match-types) ; Not impl
    [:census :marriages] (not-impl-match match-types)
    nil 
    (println "!! UNEXPECTED PAIR !!" match-types)
    ))

