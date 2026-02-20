(ns famtree.match
  (:require [famtree.fields :as fields]
            [famtree.records :as recs]
            [famtree.utils :as utils]))

(defn- match-death-to-birth-get-1
  [death-record]
  (let [est-birth-year (fields/est-birth-from-death-rec death-record)
        death-gender (:gender death-record)
        death-fname (fields/first-forename-from-rec death-record)
        death-mmn (:mm-name death-record)
        matching-birth-recs (->>
                            recs/births
                            (filter #(= est-birth-year (:year %)))
                            (filter #(= death-gender (:gender %)))
                            (filter #(= death-fname (fields/first-forename-from-rec %)))
                            (filter #(fields/=-and-has-data? death-mmn (:mm-name %))))]
        (utils/if-1-only matching-birth-recs)))

(defn- match-birth-to-death-get-1
  [birth-record]
  (let [birth-year (:year birth-record)
        birth-gender (:gender birth-record)
        birth-fname (fields/first-forename-from-rec birth-record)
        birth-mmn (:mm-name birth-record)
        matching-death-recs (->>
                            recs/deaths
                            (filter #(= birth-year (fields/est-birth-from-death-rec %)))
                            (filter #(= birth-gender (:gender %)))
                            (filter #(= birth-fname (fields/first-forename-from-rec %)))
                            (filter #(fields/=-and-has-data? birth-mmn (:mm-name %))))]
      (utils/if-1-only matching-death-recs)))

;; Matching by record type

(defn- not-impl-match
  [match-types]
  ;(println "Matching not impl. for" match-types) no-op
  )

(defn- match-birth-to-death
  [birth-record]
  (let [single-matching-death-rec (match-birth-to-death-get-1 birth-record)]
    (if single-matching-death-rec
      (if (match-death-to-birth-get-1 single-matching-death-rec)
        single-matching-death-rec))))
        
(defn- match-death-to-birth
  [death-record]
  (let [single-matching-birth-rec (match-death-to-birth-get-1 death-record)]
    (if single-matching-birth-rec
      (if (match-birth-to-death-get-1 single-matching-birth-rec)
        single-matching-birth-rec))))

(defn match-for-record
  [match-types record]
  (case match-types
    [:births :deaths] (match-birth-to-death record)
    [:births :marriages] (not-impl-match match-types)
    [:births :census] (not-impl-match match-types)
    [:deaths :births] (match-death-to-birth record)
    [:deaths :marriages] (not-impl-match match-types)
    [:deaths :census] (not-impl-match match-types)
    [:marriages :births] (not-impl-match match-types)
    [:marriages :deaths] (not-impl-match match-types)
    [:marriages :census] (not-impl-match match-types)
    [:census :census] (not-impl-match match-types) ; Search in same type of record
    [:census :births] (not-impl-match match-types)
    [:census :deaths] (not-impl-match match-types)
    [:census :marriages] (not-impl-match match-types)
    (println "!! UNEXPECTED PAIR !!" match-types)))
    