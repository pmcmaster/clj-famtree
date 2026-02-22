(ns famtree.core
  (:require [famtree.records :as recs]
            [famtree.printing :as prt]
            [famtree.utils :as utils]
            [famtree.match :as match])
  (:gen-class))
  
;; Main execution

(defn -main
  "Read in records and process them."
  [& args]
    (println "Loaded:")
    (prt/print-record-summary)
    
    (doseq [type-pair (repeatedly utils/random-record-type-pair)]
      (let [[source-rec-type _] type-pair
            source-rec (rand-nth (source-rec-type recs/by-keyword))
            successfully-matching-record (match/match-for-record type-pair source-rec)]
        (if successfully-matching-record
          (do 
            (println "==== Matched 1-1 ====")
            (println source-rec)
            (println successfully-matching-record)
            (println))))))
