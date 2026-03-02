(ns famtree.match.against 
  (:import [famtree.records.core BirthRec DeathRec MarriageRec CensusRec])
  (:require [famtree.match.protocols :as match-p]
            [famtree.records.match-same]
            [famtree.fields :as fields]
            [famtree.consts :as consts]
            [famtree.records.names :as names]))

; Matching funcs for each type of record

(defn match-against-birth
  "Find records in other-rec-coll which could be matches against birth-rec"
  [birth-rec other-rec-coll]
  (let [birth-year (:year birth-rec)
        birth-gender (:gender birth-rec)
        birth-fname (fields/first-forename-from-rec birth-rec)
        birth-mm-name (:mm-name birth-rec)]
    (->> other-rec-coll
         (filter #(match-p/match-on-forename % birth-fname))
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
        death-fname (fields/first-forename-from-rec death-rec)
        death-mm-name (:mm-name death-rec)]
    (->> other-record-coll
         (filter #(<= (:year %) death-year))
         (filter #(match-p/match-on-forename % death-fname))
         (filter #(fields/ranges-overlap?
                    est-birth-year-range-from-death
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % death-gender))
         (filter #(match-p/match-on-mm-name % death-mm-name)))))

(defn match-against-marriage
  "Find records in other-rec-coll which could be matches against marriage-rec"
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        fname1 (fields/first-word-from-field :forename marriage-rec)
        fname2 (fields/first-word-from-field :spouse-forename marriage-rec)
        genders-to-forenames (names/names-by-gender fname1 fname2)]
    (->> other-rec-coll
         (filter #(= (fields/first-forename-from-rec %)
                     (get genders-to-forenames (:gender %))))
         (filter #(let [birth-range (match-p/est-birth-year-range %)
                        est-age-at-marriage (fields/est-age-at-year birth-range marriage-year)]
                    (fields/ranges-overlap? consts/marriage-age-range
                                            est-age-at-marriage))))))

(defn match-against-census
  "Find records in other-rec-coll which could be matches against census-rec"
  [census-rec other-rec-coll]
  (let [est-birth-year-range-from-census (match-p/est-birth-year-range census-rec)
        census-fname (fields/first-forename-from-rec census-rec)
        census-gender (:gender census-rec)]
    (->> other-rec-coll
         (filter #(match-p/match-on-forename % census-fname))
         (filter #(fields/ranges-overlap?
                    est-birth-year-range-from-census
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % census-gender)))))

(extend-protocol match-p/MatchForSamePerson
  DeathRec
  (match-same-fn [this] match-against-death)
  BirthRec
  (match-same-fn [this] match-against-birth)
  MarriageRec
  (match-same-fn [this] match-against-marriage)
  CensusRec
  (match-same-fn [this] match-against-census))

