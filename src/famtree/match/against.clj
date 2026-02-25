(ns famtree.match.against 
  (:import [famtree.records.core BirthRec DeathRec MarriageRec CensusRec])
  (:require [famtree.match.protocols :as match-p]
            [famtree.fields :as fields]
            [famtree.consts :as consts]))

; Matching funcs for each type of record

(defn match-against-birth
  "Find records in other-rec-coll which could be matches against birth-rec"
  [birth-rec other-rec-coll]
  (let [birth-year (:year birth-rec)
        birth-gender (:gender birth-rec)
        birth-fname (match-p/first-forename birth-rec)
        birth-mm-name (:mm-name birth-rec)]
    (->> other-rec-coll
         (filter #(= (match-p/first-forename %) birth-fname))
         (filter #(fields/between-years?
                    (match-p/est-birth-year-range %)
                    birth-year))
         (filter #(match-p/match-on-gender % birth-gender))
         (filter #(match-p/match-on-mm-name % birth-mm-name)))))

(defn match-against-death
  "Find records in other-rec-coll which could be matches against death-rec"
  [death-rec other-record-coll]
  (let [death-year (:year death-rec)
        est-birth-year-range-from-death (match-p/est-birth-year-range death-rec)
        death-gender (:gender death-rec)
        death-fname (match-p/first-forename death-rec)
        death-mm-name (:mm-name death-rec)]
    (->> other-record-coll
         (filter #(<= (:year %) death-year))
         (filter #(= (match-p/first-forename %) death-fname))
         (filter #(fields/ranges-overlap?
                    est-birth-year-range-from-death
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % death-gender))
         (filter #(match-p/match-on-mm-name % death-mm-name)))))

(defn match-against-marriage
  "Find records in other-rec-coll which could be matches against marriage-rec"
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        marriage-fname (match-p/first-forename marriage-rec)]
    (->> other-rec-coll
         (filter #(= (match-p/first-forename %) marriage-fname))
         (filter #(let [birth-range (match-p/est-birth-year-range %)
                        est-age-at-marriage (fields/est-age-at-year birth-range marriage-year)]
                    (fields/ranges-overlap? consts/marriage-age-range
                                            est-age-at-marriage))))))

(defn match-against-census
  "Find records in other-rec-coll which could be matches against census-rec"
  [census-rec other-rec-coll]
  (let [est-birth-year-range-from-census (match-p/est-birth-year-range census-rec)
        census-fname (match-p/first-forename census-rec)
        census-gender (:gender census-rec)]
    (->> other-rec-coll
         (filter #(= (match-p/first-forename %) census-fname))
         (filter #(fields/ranges-overlap?
                    est-birth-year-range-from-census
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % census-gender)))))

(extend-protocol match-p/MatchAgainst
  DeathRec
  (match-fn [this] match-against-death)
  BirthRec
  (match-fn [this] match-against-birth)
  MarriageRec
  (match-fn [this] match-against-marriage)
  CensusRec
  (match-fn [this] match-against-census))

