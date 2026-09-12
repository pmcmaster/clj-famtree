(ns server.pages.record-collection
  (:require [hiccup.core :as h]
            [server.pages.common :as common]
            [server.record-views :as rec-views]))

;; Pages for basic display of a list of records

(defn coll-list-content
  "List all records for a collection type (such as all marriage records)"
  [coll-name]
  (let [coll-var (intern 'famtree.record-colls.main-records (symbol coll-name))
        coll (var-get coll-var)]
    (h/html
      (for [each-rec coll]
       (rec-views/basic-row-with-link each-rec)))))

(defn content
  "List all records on one page for a collection type"
  [coll-name]
  (h/html
    [:head [:title coll-name " records"]
     (common/oat-header)]
    [:body (coll-list-content coll-name)]))

