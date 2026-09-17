(ns famtree.server.pages.link-records
  (:require [hiccup.core :as h]
            [famtree.server.pages.common :as common]
            [famtree.server.pages.record :as record-page]
            [famtree.server.pages.record-collection :as rec-coll-page]))

;; Pages for linking together records

;; TODONEXT
;; TODO: Implementation of functionality!!

(defn set-link-page
  "Page to set up links between one record and another record type"
  [rec-hash-str rec-coll]
  (h/html
    [:head [:title "Linking record with " rec-coll]
     (common/oat-header)]
    [:body
     (record-page/record-page-content rec-hash-str)
     [:p rec-coll]
     (rec-coll-page/coll-list-content rec-coll)]))

