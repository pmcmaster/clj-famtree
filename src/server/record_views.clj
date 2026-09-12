(ns server.record-views
  (:require [famtree.records]
            [hiccup.core :as h]
            [clojure.string :as str])
  (:import [famtree.records
            BirthRec DeathRec MarriageRec CensusRec]))

;; Basic display of records in HTML in some semi-helpful (though not pretty) way

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
    [:nav {:aria-label "Breadcrumb"}
     [:ol {:class "unstyled hstack"
           :style "font-size: var(--text-7)"}
      [:li
       [:a {:href "/" :class "unstyled"} "Home"]]
      [:li {:aria-hidden "true"} "/"]
      [:li
       [:a {:href "/locations" :class "unstyled"}
        rec-type " record for " (:surname rec) ", " (:forename rec)]]]]
    [:div {:class "card"
           :style "float: left"}
     (for [field common-fields]
       [:p (str field) ": " (str (field rec))])
     (for [field field-list]
       [:p (str field) ": " (str (field rec))])]))

;; Scope to remove duplication here. Leaving as-is for now while some of the
;; output is adjusted often for records
(extend-protocol HTMLDisplay
  DeathRec
  (basic-row [this] (str/join basic-sep ["D" (:year this)
                                         (:forename this) (:surname this)
                                         (:gender this)
                                         (:mm-name this)
                                         (:age-at-death this)
                                         (:rd-name this)]))
  (detail-page [this] (field-detail-page this "Death" [:gender
                                                       :mm-name :age-at-death]))
  BirthRec
  (basic-row [this] (str/join basic-sep ["B" (:year this)
                                         (:forename this) (:surname this)
                                         (:gender this)
                                         (:mm-name this)
                                         (:rd-name this)]))
  (detail-page [this] (field-detail-page this "Birth" [:gender :mm-name]))
  CensusRec
  (basic-row [this] (str/join basic-sep ["C" (:year this)
                                         (:forename this) (:surname this)]))
  (detail-page [this] (field-detail-page this "Census" [:gender :county-city
                                                        :age-at-census]))
  MarriageRec
  (basic-row [this] (str/join basic-sep ["M" (:year this)
                                         (:forename this) (:surname this)
                                         (:spouse-forename this)
                                         (:spouse-surname this)
                                         (:rd-name this)]))
  (detail-page [this] (field-detail-page this "Marriage" [:spouse-forename
                                                          :spouse-surname])))

(defn basic-row-with-link
  "Basic info for a row with a link to detail for the record"
  [record]
    [:p (basic-row record)
    " "
    [:a {:href (str "/record/" (hash record))} "Details"]])

