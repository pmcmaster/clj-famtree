(ns famtree.probab.example
  [:require [famtree.record-colls.main-records :as rec-colls]
   [famtree.printing :as p]
   [famtree.probab.match :as prob-match]])

;; Example of matching using probablistic weighting, trying to find all cross-
;; matches against two years of census records

(defn match-census
  "Match two sets of census records against each other probabilistically
  Prints out the best (and worst) matches"
  []
  (p/print-record-summary rec-colls/all-collection-refs)
  (let [source-coll rec-colls/census-1911
        target-coll rec-colls/census-1921]
    (doseq [source-rec source-coll]
      (println)
      (println "Source:" source-rec)
      (let [targets-with-weights (prob-match/match-against source-rec target-coll)]
        (doseq [[weight rec] (take 6 targets-with-weights)]
          (println weight rec))
        (println "...")
        (doseq [[weight rec] (take-last 2 targets-with-weights)]
          (println weight rec))))))

