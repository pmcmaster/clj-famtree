(ns famtree.match.core
  (:require [famtree.match.protocols :as match-p]
            [famtree.match.against]
            [famtree.utils :as utils]))

(defn find-single-match
  "Match another type of record (in collection at search-coll-ref) against source-record
  source-coll is required to check back in the opposite direction that there is also only
  one matching record looking that way"
  [source-record [source-coll-ref search-coll-ref]]
  (let [source-coll (var-get source-coll-ref)
        search-coll (var-get search-coll-ref)
        source-to-new-match-fn (match-p/match-same-fn source-record)
        matching-recs (source-to-new-match-fn source-record search-coll)
        single-matching-rec (utils/if-1-only matching-recs)]
    (when single-matching-rec
      (let [back-match-fn (match-p/match-same-fn single-matching-rec)
            matching-recs-reverse-direction (back-match-fn single-matching-rec source-coll)]
        (when (utils/if-1-only matching-recs-reverse-direction)
          single-matching-rec)))))

