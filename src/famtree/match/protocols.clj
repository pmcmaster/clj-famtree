(ns famtree.match.protocols)

(defprotocol MatchSamePerson
  "Functions used for matching records against other collections of records"
  (est-birth-year-range [this] "Estimated earliest and latest birth year from info in a record")
  (match-on-forename [this other-forename] "Does this record match based other-forename")
  (match-on-gender [this other-gender] "other-gender would be 'M' or 'F'")
  (match-on-mm-name [this other-mm-name] "match on mother's maiden name"))

(defprotocol MatchForSamePerson
  (match-same-fn [this] "Should return a function which matches this type of record against other types, attempting to find two records that relate to the same person"))

