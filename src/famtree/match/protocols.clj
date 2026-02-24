(ns famtree.match.protocols)

(defprotocol MatchableRecord
  "Functions used for matching records against other collections of records"
  (est-birth-year-range [this] "Estimated earliest and latest birth year")
  (match-on-gender [this other-gender] "other-gender would be 'M' or 'F'")
  (match-on-mm-name [this other-mm-name] "match on mother's maiden name"))

(defprotocol MatchAgainst
  (match-fn [this] "Should return a function which matches this type of record against other types"))

