(ns famtree.records.names
  (:require [famtree.records.raw-collections :as raw-colls]
            [famtree.consts :as consts]
            [famtree.fields :as fields]))

(def core-surnames
  "Get a list of all surnames from records except marriage records"
  (->>
    raw-colls/all-records-except-marriage
    (map :surname)
    set
    (map #(first (re-seq #"[A-Z]+" %))) ; Get first part of hyphenated names
    set))

(defn is-core-surname
  "Is surname one of the core names currently used"
  [surname]
  (core-surnames surname))

(def first-names-by-gender
  "Get first names from all available records (except marriage) split out by gender
  Marriage records are not used as they do not have gender data"
  (->>
    raw-colls/all-records-except-marriage
    (group-by #(:gender %))
    (map (fn [[gender recs]] [gender (set (map fields/first-forename-from-rec recs))]))
    (into {})))

(defn female-forename?
  "Is forename a female name? True if it is a female name and not also a male name"
  [forename]
  (and ((get first-names-by-gender consts/female #{}) forename)
       (not ((get first-names-by-gender consts/male #{}) forename))))

(defn male-forename?
  "Is forename a male name? True if it is a male name and also not a female name"
  [forename]
  (and ((get first-names-by-gender consts/male #{}) forename)
       (not ((get first-names-by-gender consts/female #{}) forename))))

(defn gender-for-name
  "Returns a gender for a forename
  May return nil if gender cannot be determined"
  [forename]
  (cond
    (female-forename? forename) consts/female
    (male-forename? forename) consts/male))

(defn infer-gender-from-forename-pair
  "Figure out the gender directly based on forename
  or (in case of nil) on opposite gender of other-forename"
  [forename other-forename]
  (if-let [gender (gender-for-name forename)]
    gender
    (cond
      (male-forename? other-forename) consts/female
      (female-forename? other-forename) consts/male)))

