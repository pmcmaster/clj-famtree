(ns famtree.core
  (:require [famtree.records :as recs]
            [famtree.printing :as prt]
            [famtree.utils :as utils]
            [famtree.match.core :as match])
  (:gen-class))

;; Main execution

(defn -main
  "Read in records and process them."
  [& args]
  (println "Loaded:")
  (prt/print-record-summary)

  (doseq [[source-rec-coll target-rec-coll] (repeatedly utils/rand-pair-of-record-lists)]
    (let [source-rec (rand-nth source-rec-coll)
          matching-rec (match/find-single-match source-rec source-rec-coll target-rec-coll)]
      (if matching-rec
        (do 
          (println "==== Matched 1-1 ====")
          (println source-rec)
          (println matching-rec)
          (println))))))

