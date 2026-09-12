(ns famtree.determ.core
  "Main entry point for project when running deterministically.
  Runs the main matching process and triggers output of results"
  (:require [famtree.record-colls.main-records :as rec-colls]
            [famtree.printing :as p]
            [famtree.determ.match.collate-same :as collate-same]
            [famtree.determ.match.same-person :as match-same]
            [famtree.determ.link.census-same-ref :as census-same-ref]
            [famtree.determ.link.child-to-marriage :as child-to-mar]
            [famtree.problems.contradictions :as contra]))

;; Interesting experiment to build up some Clojure familiarity
;; but ultimately a bit of a dead-end for actually achieving the goal
;; of linking together records accurately. The 'determinstic' code will likely
;; all be removed after the 'probabilistic' implementation is more complete,
;; and it once it's clear what can/cannot be reused from determ to probab.

(defn match-and-collate
  "Match records together and collate them into groups per-person. A 'matched'
  record in this context is one where two records refer to the same person,
  such as a birth record and a death record, or two census records, etc." 
  []
  (->> (rec-colls/all-source-recs-with-types)
       (match-same/match-records)
       (collate-same/pairs-to-sets)))

(defn households
  "Entry point: Show determinstic linking of possible households. Households
  which are linked are intended to be the same household across different
  census years."
  [& args]
  (p/print-record-summary rec-colls/all-collection-refs)
  (census-same-ref/show-households)
  (census-same-ref/show-county-city))

(defn link-same-and-parents
  "Entry point: Determinstically link together records which relate to the
  same person. Show summary of contradiatory situations. Basic attempt at
  linking children to parents"
  [& args]
  (p/print-record-summary rec-colls/all-collection-refs)
   (let [results (match-and-collate)]
     (p/print-collated-results results)
     (doseq [each-set results]
       (contra/check-set each-set))
     (println "Problems:" @contra/problem-count)
     (doseq [each-set results]
       (child-to-mar/find-parents each-set))
   (println "Possible people with parents:" @child-to-mar/parent-count)))


  
