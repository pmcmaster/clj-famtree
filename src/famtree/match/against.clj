(ns famtree.match.against 
  "Functions to match a type of record against another record"
  (:require [famtree.match.protocols :as match-p]
            [famtree.records.match-same]
            [famtree.fields :as fields]
            [famtree.consts :as consts]
            [famtree.records.names :as names])
  (:import [famtree.records.core BirthRec DeathRec
            MarriageRec MarriageSpouseRec CensusRec]))

;; TODO: Split these out into lists of predicates.
;; This should allow some kind of 'explain match' (or lack of match)
;; feature.

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
        death-surname (:surname death-rec)
        death-mm-name (:mm-name death-rec)]
    (->> other-record-coll
         (filter #(<= (:year %) death-year))
         (filter #(match-p/match-on-forename % death-fname))
         (filter #(match-p/match-on-surname % death-surname death-year))
         (filter #(fields/ranges-overlap?
                    est-birth-year-range-from-death
                    (match-p/est-birth-year-range %)))
         (filter #(match-p/match-on-gender % death-gender))
         (filter #(match-p/match-on-mm-name % death-mm-name)))))

(defn match-against-marriage
  "Find records in other-rec-coll which could be matches against marriage-rec"
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        marriage-forename (fields/first-word-from-field :forename marriage-rec)
        marriage-surname (fields/first-word-from-field :surname marriage-rec)
        other-forename (fields/first-word-from-field
                         :spouse-forename marriage-rec)
        other-surname (fields/first-word-from-field
                        :spouse-surname marriage-rec)
        marriage-gender (names/infer-gender-from-forename-pair
                          marriage-forename other-forename)
        surname-after-marriage (if (= marriage-gender consts/female)
                                 other-surname
                                 marriage-surname)]
    (->> other-rec-coll
         (filter #(cond
                    ;; Event before marriage
                   (< (:year %) marriage-year)
                   (= (:surname %) marriage-surname)
                   ;; Event after marriage
                   (> (:year %) marriage-year)
                   (= (:surname %) surname-after-marriage)
                   ;; Event same year as marriage
                   (= (:year %) marriage-year)
                   ((hash-set marriage-surname surname-after-marriage)
                    (:surname %))))
         (filter #(match-p/match-on-gender % marriage-gender))
         ;; TODO: Split following function up
         (filter #(let [birth-range (match-p/est-birth-year-range %)
                        est-age-at-marriage (fields/est-age-at-year
                                              birth-range marriage-year)]
                    (fields/ranges-overlap? consts/marriage-age-range
                                            est-age-at-marriage))))))

(defn match-against-marriage-spouse
  "Find records in other-rec-coll which could be matches against marriage-rec
  Differs from marriage matching in that it uses spouse names, not plain names"
  ;; TODO: Refactor to extract out common code (lots of it) and pass in
  ;; different names
  [marriage-rec other-rec-coll]
  (let [marriage-year (:year marriage-rec)
        marriage-forename (fields/first-word-from-field
                            :spouse-forename marriage-rec)
        marriage-surname (fields/first-word-from-field
                           :spouse-surname marriage-rec)
        other-forename (fields/first-word-from-field :forename marriage-rec)
        other-surname (fields/first-word-from-field
                        :surname marriage-rec)
        marriage-gender (names/infer-gender-from-forename-pair
                          marriage-forename other-forename)
        surname-after-marriage (if (= marriage-gender consts/female)
                                 other-surname
                                 marriage-surname)]
    (->> other-rec-coll
         (filter #(cond
                    ;; Event before marriage
                   (< (:year %) marriage-year)
                   (= (:surname %) marriage-surname)
                   ;; Event after marriage
                   (> (:year %) marriage-year)
                   (= (:surname %) surname-after-marriage)
                   ;; Event same year as marriage
                   (= (:year %) marriage-year)
                   ((hash-set marriage-surname surname-after-marriage)
                    (:surname %))))
         (filter #(match-p/match-on-forename % marriage-forename))
         (filter #(match-p/match-on-gender % marriage-gender))
         (filter #(let [birth-range (match-p/est-birth-year-range %)
                        est-age-at-marriage (fields/est-age-at-year
                                              birth-range marriage-year)]
                    (fields/ranges-overlap? consts/marriage-age-range
                                            est-age-at-marriage))))))

(defn match-against-census
  "Find records in other-rec-coll which could be matches against census-rec"
  [census-rec other-rec-coll]
  (let [est-birth-year-range-from-census (match-p/est-birth-year-range
                                           census-rec)
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
  MarriageSpouseRec
  (match-same-fn [this] match-against-marriage-spouse)
  CensusRec
  (match-same-fn [this] match-against-census))

