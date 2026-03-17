(ns famtree.core
  "Main entry point for project
  Runs the main matching process and triggers output of results"
  (:require [famtree.record-colls.main-records :as rec-colls]
            [famtree.printing :as p]
            [famtree.match.collate-same :as collate-same]
            [famtree.match.same-person :as match-same]
            [famtree.problems.contradictions :as contra])
  (:gen-class))

(defn match-and-collate
 "Match records together and collate them into groups per-person" 
 []
 (->> (rec-colls/all-source-recs-with-types)
      (match-same/match-records)
      (collate-same/pairs-to-sets)))

(defn -main
  [& args]
  (p/print-record-summary rec-colls/all-collection-refs)
  (let [results (match-and-collate)]
    (p/print-collated-results results)
    (doseq [each-set results]
      (contra/check-set each-set))
    (println "Problems:" @contra/problem-count)))

