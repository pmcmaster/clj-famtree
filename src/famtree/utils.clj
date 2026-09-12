(ns famtree.utils
  (:require [famtree.record-colls.main-records :as rec-colls]))

;; 'Utility' functions. Not sure what these have in common other than
;; they don't have a clearer place to live.

;; TODO: Move these elsewhere and remove this module?

(defn avg-name-occurrence
  "Probability of a first name appearing multiple times
  in a set of records `rec-coll`"
  [rec-coll]
  (let [rec-count (count rec-coll)
        repeated-name-count (->> rec-coll
                                  (map :forename)
                                  frequencies
                                  vals
                                  (filter #(> % 1))
                                  (apply +))]
    (println repeated-name-count rec-count)
    (/ repeated-name-count rec-count)))

(defn census-name-stats
  "Print out stats for first names in census records
  
  So far just shows one value representing probability of a randomly chosen
  forename being one which occurs multiple times in the data"
  [& args]
 (let [colls (map var-get rec-colls/census-by-year-syms)
       name-occurs (map avg-name-occurrence colls)
       avg (/ (apply + name-occurs) (count colls))]

   (println (double avg))))
