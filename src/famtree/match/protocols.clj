(ns famtree.match.protocols
  "Protocols for functions used when matching records together.")

(defprotocol MatchSamePerson
  "Functions used for matching records against other collections of records"
  (est-birth-year-range [this]
                        "Estimated earliest and latest birth year from info
                        in a record")
  (match-on-forename [this other-forename]
                     "Does this record match based other-forename")
  (match-on-surname [this other-forename year]
                    "At `year` does `this` record's :surname match
                    `other-surname`")
  (match-on-gender [this other-gender] "other-gender would be 'M' or 'F'")
  (match-on-mm-name [this other-mm-name] "match on mother's maiden name"))

(defprotocol MatchForSamePerson
  (match-same-fn [this]
                 "Should return a function which matches this type of record
                 against other types, attempting to find two records that
                 relate to the SAME person"))

