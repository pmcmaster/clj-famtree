(ns famtree.determ.match.against 
  "Functions to match a type of record against another record"
  (:require [famtree.determ.match.protocols :as match-p]
            [famtree.determ.match.same]
            [famtree.fields.names :as names]
            [famtree.fields.marriage :as marriage]
            [famtree.fields.age-year :as age-year]
            [famtree.fields.gender :as gender])
  (:import [famtree.records BirthRec DeathRec
            MarriageRec CensusRec]))

;; TODO: Split these out into lists of predicates.
;; This should allow some kind of 'explain match' (or lack of match)
;; feature.

(defn match-against-birth
  "Find records in other-rec-coll which could be matches against birth-rec"
  [birth-rec other-rec-coll]
  (let [birth-year (:year birth-rec)
        birth-gender (:gender birth-rec)
        birth-forename (names/first-forename-from-rec birth-rec)
        birth-mm-name (:mm-name birth-rec)]
    (->> other-rec-coll
         (filter #(= birth-forename (:forename %)))
         (filter #(age-year/between-years?
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
        death-forename (names/first-forename-from-rec death-rec)
        death-surname (:surname death-rec)
        death-mm-name (:mm-name death-rec)]
    (->> other-record-coll
         (filter #(<= (:year %) death-year))
         (filter #(= death-forename (:forename %)))
         (filter #(match-p/match-on-surname % death-surname death-year))
         (filter #(age-year/ranges-overlap?
                    est-birth-year-range-from-death
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % death-gender))
         (filter #(match-p/match-on-mm-name % death-mm-name)))))

(defn match-against-marriage
  "Find records in `other-rec-coll` which could be matches against marriage
  with `marriage-year` and names (4)"
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        [forename surname
         partner-forename partner-surname] (marriage/names marriage-rec)
        marriage-gender (gender/infer-gender-from-forename-pair
                          forename partner-forename)
        surnames-before-after (marriage/surnames-before-after
                                marriage-gender
                                surname partner-surname)]
    (->> other-rec-coll
         (filter #(= forename (:forename %)))
         (filter #(marriage/matches-surname-at-date
                   marriage-year surnames-before-after
                   (:year %) (:surname %)))
         (filter #(match-p/match-on-gender % marriage-gender))
         ;; TODO: Split following function up
         (filter #(let [birth-range (match-p/est-birth-year-range %)
                        est-age-at-marriage (age-year/est-age-at-year
                                              birth-range marriage-year)]
                    (age-year/ranges-overlap? marriage/age-range
                                            est-age-at-marriage))))))

(defn match-against-census
  "Find records in other-rec-coll which could be matches against census-rec"
  [census-rec other-rec-coll]
  (let [est-birth-year-range-from-census (match-p/est-birth-year-range
                                           census-rec)
        census-forename (names/first-forename-from-rec census-rec)
        census-gender (:gender census-rec)]
    (->> other-rec-coll
         (filter #(= census-forename (:forename %)))
         (filter #(age-year/ranges-overlap?
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

