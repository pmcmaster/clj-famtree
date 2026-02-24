(ns famtree.match.core
  (:require [famtree.match.protocols :as match-p]
            [famtree.match.against]
            [famtree.utils :as utils]))

(defn find-single-match
  "Match another type of record (in match-coll) against source-record
  source-coll is required to check back in the opposite direction that there is also only
  one matching record looking that way"
  [source-record source-coll match-coll]
  (let [source-to-new-match-fn (match-p/match-fn source-record)
        matching-recs (source-to-new-match-fn source-record match-coll)
        single-matching-rec (utils/if-1-only matching-recs)]
    (when single-matching-rec
      (let [back-match-fn (match-p/match-fn single-matching-rec)
            matching-recs-reverse-direction (back-match-fn single-matching-rec source-coll)]
        (when (utils/if-1-only matching-recs-reverse-direction)
          single-matching-rec)))))

