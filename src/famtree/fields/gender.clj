(ns famtree.fields.gender
    "Functions that relate to deriving gender from records that don't directly
    have a gender field"
    (:require [famtree.record-colls.raw-records :as raw-colls]
              [famtree.fields.names :as names]))

;; TODO: Map these to keywords at load-time?
(def female "F")
(def male "M")

(defn first-forenames-from
  "Returns a set of only the first forenames from `rec-coll`"
  [rec-coll]
  (into #{} (map names/first-forename-from-rec) rec-coll))

(def first-names-by-gender
  "Get first names from all available records (except marriage) split out by
  gender. Marriage records are not used as they do not have gender data"
  (->> raw-colls/all-records-except-marriage
       (group-by #(:gender %))
       (into {} (map (fn [[gender recs-for-gender]]
                       [gender (first-forenames-from recs-for-gender)])))))

(def male-forenames
  "Forenames which are only found in male records"
  (get first-names-by-gender male #{}))

(def female-forenames
  "Forenames which are only found in female records"
  (get first-names-by-gender female #{}))

(defn female-forename?
  "Is `forename` a female name?
  True if it is a female name and not also a male name"
  [forename]
  (and (female-forenames forename)
       (not (male-forenames forename))))

(defn male-forename?
  "Is `forename` a male name?
  True if it is a male name and also not a female name"
  [forename]
  (and (male-forenames forename)
       (not (female-forenames forename))))

(defn gender-for-name
  "Returns a gender for a `forename`
  May return nil if gender cannot be determined"
  [forename]
  (cond
    (female-forename? forename) female
    (male-forename? forename) male))

(defn infer-gender-from-forename-pair
  "Figure out the gender directly based on `forename`
  or (in case of nil) on 'opposite' gender of `other-forename`"
  [forename other-forename]
  (if-let [gender (gender-for-name forename)]
    gender
    (cond
      (male-forename? other-forename) female
      (female-forename? other-forename) male)))

