(ns famtree.records.match-same
  "Implementation of functions to match a record against another for the
  same person (e.g., birth and death records for one person)."
  (:require [famtree.fields :as fields]
            [famtree.consts :as consts]
            [famtree.records.names :as names]
            [famtree.match.protocols :as match-p])
  (:import [famtree.records.core
            BirthRec DeathRec MarriageRec MarriageSpouseRec CensusRec])
  )

(extend-protocol match-p/MatchSamePerson
  BirthRec
  (est-birth-year-range [this] [(:year this) (:year this)])
  (match-on-forename [this other-forename]
    (= (fields/first-forename-from-rec this)
       other-forename))
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  DeathRec
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-death this) (:year this)))
  (match-on-forename [this other-forename]
    (= (fields/first-forename-from-rec this)
       other-forename))
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  MarriageRec
  (est-birth-year-range [this]
    (let [marriage-year (:year this)]
      (->> consts/marriage-age-range
           (map #(- marriage-year %))
           sort)))
  (match-on-forename [this other-forename]
    (= (fields/first-forename-from-rec this)
       other-forename))
  (match-on-surname [this other-surname other-year]
    (let [marriage-year (:year this)
          marriage-forename (fields/first-word-from-field
                              :forename this)
          marriage-surname (fields/first-word-from-field
                             :surname this)
          other-forename (fields/first-word-from-field
                           :spouse-forename this)
          other-mar-surname (fields/first-word-from-field
                          :spouse-surname this)
          marriage-gender (names/infer-gender-from-forename-pair
                            marriage-forename other-forename)
          surname-after-marriage (if (= marriage-gender consts/female)
                                   other-mar-surname
                                   marriage-surname)]
      (cond
        ;; Event before marriage
        (< other-year marriage-year)
        (= other-surname marriage-surname)
        ;; Event after marriage
        (> other-year marriage-year)
        (= other-surname surname-after-marriage)
        ;; Event same year as marriage
        (= other-year marriage-year)
        ((hash-set marriage-surname surname-after-marriage)
         other-surname))))
  (match-on-gender [this other-gender]
    (= (names/infer-gender-from-forename-pair
         (fields/first-word-from-field :forename this)
         (fields/first-word-from-field :spouse-forename this))
       other-gender))
  ;; No mmn data in a marriage rec; always match
  (match-on-mm-name [this other-mm-name] true)

  MarriageSpouseRec
  (est-birth-year-range [this]
    (let [marriage-year (:year this)]
      (->> consts/marriage-age-range
           (map #(- marriage-year %))
           sort)))
  (match-on-forename [this other-forename]
    (= (fields/first-word-from-field :spouse-forename this)
       other-forename))
  (match-on-surname [this other-surname other-year]
    (let [marriage-year (:year this)
          marriage-forename (fields/first-word-from-field
                              :spouse-forename this)
          marriage-surname (fields/first-word-from-field
                             :spouse-surname this)
          other-forename (fields/first-word-from-field
                           :forename this)
          other-mar-surname (fields/first-word-from-field
                          :surname this)
          marriage-gender (names/infer-gender-from-forename-pair
                            marriage-forename other-forename)
          surname-after-marriage (if (= marriage-gender consts/female)
                                   other-mar-surname
                                   marriage-surname)]
      (cond
        ;; Event before marriage
        (< other-year marriage-year)
        (= other-surname marriage-surname)
        ;; Event after marriage
        (> other-year marriage-year)
        (= other-surname surname-after-marriage)
        ;; Event same year as marriage
        (= other-year marriage-year)
        ((hash-set marriage-surname surname-after-marriage)
         other-surname))))
  (match-on-gender [this other-gender]
    (= (names/infer-gender-from-forename-pair
         (fields/first-word-from-field :spouse-forename this)
         (fields/first-word-from-field :forename this))
       other-gender))
  ;; No mmn data in a marriage rec; always match
  (match-on-mm-name [this other-mm-name] true)

  CensusRec
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-census this) (:year this)))
  (match-on-forename [this other-forename]
    (= (fields/first-forename-from-rec this)
       other-forename))
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  ;; No mmn data in a marriage rec; always match
  (match-on-mm-name [this other-mm-name] true))

