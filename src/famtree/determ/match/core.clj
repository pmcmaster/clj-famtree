(ns famtree.determ.match.core
  "Core matching utility functions"
  (:require [famtree.determ.match.protocols :as match-p]
            [famtree.determ.match.against]))

(defn if-1-only
  "Return the element in `coll` if there is only one
  Returns nil if there are zero or > 1 items.
  Does not use count so it's not necessary to realise the whole collection."
  [coll]
  (when-let [elem1 (first coll)]
    (when-not (second coll)
      elem1)))

(defn find-single-match
  "Match another type of record (in collection at `search-coll-ref`) against
  `source-record`. A match occurs when there is one (and only one) match.
  `source-coll` is required to check back in the opposite direction that there
  is also only one matching record looking that way; if Bob matches only
  against record Sue and Sue matches only against Bob then it is a 'good match'
  but if Bob matches against Sue whereas Sue matches against more records than
  just Bob, then it is not considered to be a good match."
  [source-record [source-coll-ref search-coll-ref]]
  (let [source-coll (var-get source-coll-ref)
        search-coll (var-get search-coll-ref)
        source-to-new-match-fn (match-p/match-same-fn source-record)
        matching-recs (source-to-new-match-fn source-record search-coll)
        single-matching-rec (if-1-only matching-recs)]
    (when single-matching-rec
      ;; Found match in one direction, now check for single match in opposite
      ;; direction
      (let [back-match-fn (match-p/match-same-fn single-matching-rec)
            matching-recs-reverse-direction (back-match-fn single-matching-rec
                                                           source-coll)]
        (when (if-1-only matching-recs-reverse-direction)
          single-matching-rec)))))

