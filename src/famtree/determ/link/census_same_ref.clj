(ns famtree.determ.link.census-same-ref
  (:require [famtree.printing :as p]
            [famtree.fields.names :as names]
            [famtree.record-colls.main-records :as rec-colls]
            [clojure.set :as set]
            [clojure.string :as str])
  (:import [famtree.records CensusRec]))

;; Deterministic matching of census records with other census records
;; primarily based on the concept of a 'household' (people who are in the same
;; house at the time of a census)

(defn print-by-generations
  "Print out a household split into generations, based on the rather arbitrary
  distinction of a 16 year gap between two people"
  ;; TODO: Could possibly be done more clearly with partition-by somehow?
  ([people-coll]
   (let [people-sorted (sort-by :age-at-census > people-coll)
         generation 1]
     (when-let [first-person (first people-sorted)]
       (println "  Generation" generation)
       (println first-person)
       (print-by-generations generation
                             (:age-at-census first-person)
                             (rest people-sorted)))))
  ([generation prev-person-age people-sorted]
   (when-let [person (first people-sorted)]
     (let [this-age (:age-at-census person)]
       (if (>= (- prev-person-age this-age) 16)
         (do (println "  Generation" (inc generation))
             (println person)
             (recur (inc generation) this-age (rest people-sorted)))
         ;; Else - same generation
         (do (println person)
             (recur generation this-age (rest people-sorted))))))))

(def all-census-recs
  (apply concat (map var-get rec-colls/census-by-year-syms)))

(def census-by-location
  "All census recs grouped by location"
  (into (sorted-map)
        (group-by (fn [r] [(:county-city r) (:rd-name r)])
                  all-census-recs)))

(defn grouped-by-year
  "Group seq of items by year"
  [records]
  (into (sorted-map) (group-by :year records)))

(defn rd-names-only
  "Get only the rd-name fields from a set of records"
  [records]
  (into (sorted-set)
        (map (fn [r] (str/upper-case (:rd-name r))))
        records))

(defn show-households
  "Print out records grouped by households, from census records"
  []
  (doseq [[loc recs-for-loc] census-by-location]
    (println)
    (println)
    (println "======" loc "=====")
    (let [grouped-by-year (grouped-by-year recs-for-loc)]
      (doseq [[year recs-for-year] grouped-by-year]
        (println)
        (println)
        (println "=====" year "=====")
        (doseq [[_ recs-for-rec-ref] (group-by :rec-ref recs-for-year)]
          (println)
          (print-by-generations recs-for-rec-ref))))))
(defn group-by-household
  "Group `records` by :rec-ref which should roughly equate to households"
  [records]
  (group-by :rec-ref records))

(defn show-county-city
  "Print out disappearing or new county/city entries from one census year
  to the next (typically 10 years later)"
  ;; TODO: Function is long. Should be broken up
  []
  (let [by-year (grouped-by-year all-census-recs)]
    (let [[prev-year-census this-year-census] (first (partition 2 1 by-year))
          [prev-year prev-year-recs] prev-year-census
          [this-year this-year-recs] this-year-census
          year-diff (- this-year prev-year)
          this-year-recs-right-age (filter
                                     #(> (:age-at-census %) year-diff)
                                     this-year-recs)
          prev-year-by-household (group-by-household prev-year-recs)
          this-year-by-household (group-by-household this-year-recs-right-age)]
      (println)
      (println prev-year "to" this-year)
      (println year-diff "years")
      (doseq [[rec-ref records] this-year-by-household]
        (println)
        (println "Household of " (count records))
        (let [fnames (into #{} (map names/first-forename-from-rec) records)
              matches (filter (fn [[_other-ref other-recs]]
                                (= (into #{}
                                         (map names/first-forename-from-rec)
                                         other-recs)
                                   fnames))
                              prev-year-by-household)]
          (println "Match count:" (count matches))
          (when (= (count matches) 1)
            (println matches)
            (println)
            (println records)))))))


