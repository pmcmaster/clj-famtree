(ns famtree.records.names
  (:require [famtree.records.collections :as rec-colls]
            [famtree.consts :as consts]
            [famtree.fields :as fields]))

(defn all-surnames-except-marriage-recs
  "Get a list of all surnames from all records except the marriage records"
  []
  (->>
    (rec-colls/all-records-except-marriage)
    (map :surname)
    set
    (map #(first (re-seq #"[A-Z]+" %))) ; Get first part of hyphenated names
    set))

(defn other-names-from-marriage-recs
  "Enumerate all the names from marriage recs. Find any where one of the parties in the marriage
  has a surname which is not in the list of passed in surnames"
  [known-surnames]
  (doseq [each-rec (var-get #'rec-colls/marriages)]
    (when-not (or (known-surnames (:surname each-rec))
                  (known-surnames (:spouse-surname each-rec)))
      (println "Unusual record:")
      (println each-rec))))

(defn check-marriage-surnames
  "Check for any marriage records that do not have at least one surname in the other records"
  []
  (other-names-from-marriage-recs all-surnames-except-marriage-recs))

(def first-names-by-gender
  "Get first names from all available records (except marriage) split out by gender
  Marriage records are not used as they do not have gender data"
  (->>
    (rec-colls/all-records-except-marriage)
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

(defn names-by-gender
  "Takes two names as input and tries to figure out which is male and which is female
  Returns a map of gender to each name.
  Returns empty map in case genders cannot be determined for both names."
  [name1 name2]
  (let [genders-to-names (-> {}
                             (assoc (gender-for-name name1) name1)
                             (assoc (gender-for-name name2) name2))]
    (if (= (count genders-to-names) 2)
      genders-to-names
      {})))

