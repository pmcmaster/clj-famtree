(ns famtree.records
  (:require [famtree.load :as load]
            [famtree.fields :as fields]
            [famtree.consts :as consts]
            [famtree.match.protocols :as match-p]))

(defrecord BirthRec [surname forename mm-name gender year rec-ref rd-name]
  match-p/MatchableRecord
  (est-birth-year-range [this]
    [(:year this) (:year this)])
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name)))

(defrecord DeathRec [surname forename age-at-death mm-name gender year rec-ref rd-name]
  match-p/MatchableRecord
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-death this) (:year this)))
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] (= (:mm-name this) other-mm-name)))

(defrecord MarriageRec [surname forename spouse-surname spouse-forename year rec-ref rd-name]
  match-p/MatchableRecord
  (est-birth-year-range [this] ; TODO Simplify this check
    (let [marriage-year (:year this)]
      [(- marriage-year (second consts/marriage-age-range))
       (- marriage-year (first consts/marriage-age-range))]))
  (match-on-gender [this other-gender] true) ;; TODO: Implement gender check for marriage recs
  (match-on-mm-name [this other-mm-name] true)) ; No data on mmn for a marriage rec; always match

(defrecord CensusRec [surname forename year gender age-at-census rec-ref rd-name county-city]
  match-p/MatchableRecord
  (est-birth-year-range [this]
    (fields/est-birth-range-from-age (:age-at-census this) (:year this)))
  (match-on-gender [this other-gender] (= (:gender this) other-gender))
  (match-on-mm-name [this other-mm-name] true)) ; No data on mmn for a census rec; always match

;; Main data collections

(def births (map map->BirthRec (load/data-for-type "births")))
(def deaths (map map->DeathRec (load/data-for-type "deaths")))
(def marriages (map map->MarriageRec (load/data-for-type "marriages")))
(def census (map map->CensusRec (load/data-for-type "census")))

(def all-collections [births deaths marriages census])

