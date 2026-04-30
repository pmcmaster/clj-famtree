(ns famtree.determ.match.same
  "Implementation of functions to match a record against another for the
  same person (e.g., birth and death records for one person)."
  (:require [famtree.records]
            [famtree.fields.names :as names]
            [famtree.fields.age-year :as age-year]
            [famtree.fields.basic :as fields]
            [famtree.fields.marriage :as marriage]
            [famtree.fields.gender :as gender]
            [famtree.determ.match.protocols :as match-p])
  (:import [famtree.records
            BirthRec DeathRec MarriageRec CensusRec]))

(extend-protocol match-p/MatchSamePerson
  BirthRec
  (est-birth-year-range [this] [(:year this) (:year this)])
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  DeathRec
  (est-birth-year-range [this]
    (age-year/est-birth-range-from-age (:age-at-death this) (:year this)))
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  MarriageRec
  (est-birth-year-range [this]
    (let [marriage-year (:year this)]
      (->> marriage/age-range
           (map #(- marriage-year %))
           sort)))
  (match-on-surname [this other-surname other-year]
    (let [marriage-year (:year this)
          [forename surname
           spouse-forename spouse-surname] (marriage/names this)
          marriage-gender (gender/infer-gender-from-forename-pair
                            forename spouse-forename)
          surnames-before-after (marriage/surnames-before-after
                                  marriage-gender surname spouse-surname)]
      (marriage/matches-surname-at-date marriage-year
                                        surnames-before-after
                                        other-year other-surname)))
  (match-on-gender [this other-gender]
    (= (gender/infer-gender-from-forename-pair
         (fields/first-word-from-field :forename this)
         (fields/first-word-from-field :spouse-forename this))
       other-gender))
  ;; No mmn data in a marriage rec; always match
  (match-on-mm-name [this other-mm-name] true)

  CensusRec
  (est-birth-year-range [this]
    (age-year/est-birth-range-from-age (:age-at-census this) (:year this)))
  (match-on-surname [this other-surname _year]
    (:surname this) other-surname)
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  ;; No mmn data in a marriage rec; always match
  (match-on-mm-name [this other-mm-name] true))

