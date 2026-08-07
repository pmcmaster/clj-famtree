(ns server.pages.record
  (:require [hiccup.core :as h]
            [famtree.record-colls.main-records :as rec-colls]
            [server.record-views :as rec-views]))

;; Basic display of info relation to a single record

(defn record-page-content
  "Details for a particular record, looked up based on its hash in a horribly
  inefficient way"
  [rec-hash-str]
  (if-let [rec-hash (Integer/parseInt rec-hash-str)]
    (if-let [found-rec (first
                         (filter
                           #(= rec-hash (hash %))
                           rec-colls/all-records))]
      (rec-views/detail-page found-rec)
      (str "No rec matched hash " rec-hash))))

(defn record-page
  "Page for a single record"
  [rec-hash-str]
  (h/html
    [:head [:title "Record detail"]]
    [:body (record-page-content rec-hash-str)]))

