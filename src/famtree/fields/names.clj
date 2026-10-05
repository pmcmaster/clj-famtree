(ns famtree.fields.names
  "Functions for dealing with names in records and names across all records"
  (:require [famtree.record-colls.raw-records :as raw-colls]
            [famtree.fields.basic :as fields]
            [famtree.data-cache :as cache]))

(defn first-part-of-hyphenated-name
  "Get the first part of a hyphenated or two-part name
  Returns whole name in case of no hyphenation"
  [a-name]
  (and a-name (first (re-seq #"[A-Z]+" a-name))))

(defn first-forename-from-rec
  "Get the first forename from a record {:forename 'Bob David'}
  would return 'Bob'}"
  [rec]
  (fields/first-word-from-field :forename rec))

(defn core-surnames-from-records
  "What are the core surnames, derived from the existing records"
  []
  (into #{}
        (map #(-> % :surname first-part-of-hyphenated-name))
        (raw-colls/all-records-except-marriage)))

(defn core-surnames
  "Get a list of all surnames from records except marriage records.
  This is used to establish what names are 'core' names in the records.
  Marriage records are expected to include other names from one party in
  addition to one of the parties having a 'core' name."
  []
  (cache/cached-or-load
    :core-surnames
    core-surnames-from-records))

(defn is-core-surname
  "Is `surname` one of the core names currently used"
  [surname]
  ((core-surnames) surname))

(defn surname-matches-surnames
  "Does `surname` match any of `surnames-coll`.
  Also a match if `surname` is a core name and `surnames-coll` contains at
  least one core name"
  [surname surnames-coll]
  (or (and (is-core-surname surname)
           (some is-core-surname surnames-coll))
      ((set surnames-coll) surname)))

(defn surnames-match
  "Do surnames match? If either is a core-surname, then match if both are core"
  [surname1 surname2]
  (surname-matches-surnames surname1 (hash-set surname2)))


