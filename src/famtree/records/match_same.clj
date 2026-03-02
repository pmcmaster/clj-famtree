(ns famtree.records.match-same
  (:import [famtree.records.core BirthRec DeathRec MarriageRec CensusRec])
  (:require [famtree.fields :as fields]
            [famtree.consts :as consts]
            [famtree.match.protocols :as match-p]))

(extend-protocol match-p/MatchSamePerson
  BirthRec
  (est-birth-year-range [this] [(:year this) (:year this)])
  (match-on-forename [this other-forename] (= (fields/first-forename-from-rec this)
                                              other-forename))
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  DeathRec
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-death this) (:year this)))
  (match-on-forename [this other-forename] (= (fields/first-forename-from-rec this)
                                              other-forename))
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name))

  MarriageRec
  (est-birth-year-range [this]
    (let [marriage-year (:year this)]
      (->> consts/marriage-age-range
           (map #(- marriage-year %))
           sort)))
  (match-on-forename [this other-forename] ((set (map #(fields/first-word-from-field % this)
                                                   [:forename :spouse-forename]))
                                            other-forename))
  (match-on-gender [this other-gender] true) ;; TODO: Implement gender check for marriage recs
  (match-on-mm-name [this other-mm-name] true) ; No mmn data in a marriage rec; always match

  CensusRec
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-census this) (:year this)))
  (match-on-forename [this other-forename] (= (fields/first-forename-from-rec this)
                                              other-forename))
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] true)) ; No mmn data in a census rec; always match

