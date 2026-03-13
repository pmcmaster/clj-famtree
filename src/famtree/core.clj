(ns famtree.core
  "Main entry point for project
  Runs the main matching process and triggers output of results"
  (:require [famtree.record-colls.main-records :as rec-colls]
            [famtree.printing :as p]
            [famtree.match.collate-same :as collate-same]
            [famtree.match.same-person :as match-same])
  (:gen-class))

(defn -main
  [& args]
  (p/print-record-summary rec-colls/all-collection-refs)
  (->> (rec-colls/all-source-recs-with-types)
       (match-same/match-records)
       (collate-same/pairs-to-sets)
       (p/print-collated-results)))

