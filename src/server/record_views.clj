(ns server.record-views
  (:require [famtree.records]
            [hiccup.core :as h]
            [clojure.string :as str])
  (:import [famtree.records
            BirthRec DeathRec MarriageRec CensusRec]))

(defprotocol HTMLDisplay
  "Functions for printing records in HTML"
  (basic-row [this] "Basic record details in a row of text")
  (detail-page [this] "Full detail for a record"))

(def basic-sep
  "Record separator for basic display"
  " | ")

(def common-fields
  "Fields which all records have"
  [:year :forename :surname :rd-name :rec-ref])

(defn field-detail-page
  "Page with detail on a record and its fields"
  [rec rec-type field-list]
  (h/html
    [:h1 rec-type " record for " (:surname rec) ", " (:forename rec)]
    (for [field common-fields]
      [:p (str field) ": " (str (field rec))])
    (for [field field-list]
      [:p (str field) ": " (str (field rec))])))

(extend-protocol HTMLDisplay
  DeathRec
  (basic-row [this] (str/join basic-sep ["D" (:year this) (:forename this) (:surname this)]))
  (detail-page [this] (field-detail-page this "Death" [:gender :mm-name :age-at-death]))
  BirthRec
  (basic-row [this] (str/join basic-sep ["B" (:year this) (:forename this) (:surname this)]))
  (detail-page [this] (field-detail-page this "Birth" [:gender :mm-name]))
  CensusRec
  (basic-row [this] (str/join basic-sep ["C" (:year this) (:forename this) (:surname this)]))
  (detail-page [this] (field-detail-page this "Census" [:gender :county-city :age-at-census]))
  MarriageRec
  (basic-row [this] (str/join basic-sep ["M" (:year this) (:forename this) (:surname this)]))
  (detail-page [this] (field-detail-page this "Marriage" [:spouse-forename :spouse-surname])))

