(ns server.pages.link-records
  (:require [hiccup.core :as h]
            [server.pages.record :as record-page]
            [server.pages.record-collection :as rec-coll-page]))

;; Pages for linking together records

;; TODO: Implementation of functionality!!

(defn set-link-page
  "Page to set up links between one record and another record type"
  [rec-hash-str rec-coll]
  (h/html
    [:head [:title "Linking record with " rec-coll]]
    [:body
     (record-page/record-page-content rec-hash-str)
     [:p rec-coll]
     (rec-coll-page/coll-list-content rec-coll)]))

