(ns famtree.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv])
  (:gen-class))

;; Data loading

(defn rows-for-record-type [record-type]
  "Read the raw CSV for file named for record-type"
  (let [path (str "data/" record-type ".csv")]
    (with-open [reader (io/reader path)]
      (doall (csv/read-csv reader :separator \tab)))))

(defn field-name-to-keyword [fieldname]
  "Convert header row from CSV file into keywords, with some replacements of long names"
  (let [non-std-fields {"Mother's Maiden Name" :mm-name "County / City" :county-city}
        non-std-name (non-std-fields fieldname)]
    (if (nil? non-std-name)
      (-> fieldname
        (str/replace " " "-")
        str/lower-case
        keyword)
      non-std-name)))

(defn row-to-map
  "Convert a list of lists of text fields to a map, using the field-keys provided"
  [text-row field-keys]
  (let [key-value-pairs (map vector field-keys text-row)]
    (reduce (fn [output-map [field-key field-val]] (assoc output-map field-key field-val))
      {}
      key-value-pairs)))

(defn data-as-map
  "Process the raw records for a record type.
  raw-data expected to have a header row which is used to generate keys used in the output map"
  [raw-data]
  (let [header-row (first raw-data)
        header-fields (map field-name-to-keyword header-row)
        data-rows (rest raw-data)]
      (map #(row-to-map % header-fields) data-rows)))

(defn data-for-type
  "The properly formated map of data for a given record-type"
  [record-type]
  (data-as-map (rows-for-record-type record-type)))

;; Main data collections
      
(def births (data-for-type "births"))
(def deaths (data-for-type "deaths"))
(def marriages (data-for-type "marriages"))
(def census (data-for-type "census"))

(def all-record-types #{:births :deaths :marriages :census})

;; Helper functions

(defn counts-by-grouping
  "Count of records for each group"
  [grouped-records]
  (map
    (fn [record]
      (let [region-name (first record)
          record-count (-> record second count)]
        (vector region-name record-count)))
    grouped-records))
    
(def record-colls-by-keyword
    {:births births
     :deaths deaths
     :marriages marriages
     :census census})

;; Print helpers

(defn show-stats
  "Print out details of info referenced by data-symbol"
  [data-symbol]
  (let [data (var-get data-symbol)]
    (println data-symbol (count data) "records")))
    
(defn print-grouped-data
  [grouped-data]
  (doseq [[data-key coll] grouped-data]
    (println data-key)
      (doseq [data-row coll]
        (println "  " data-row))))

(defn print-records
  [records]
  (doseq [record records]
    (println record)))

(defn print-census-records
  [records]
  (let [grouped-by-year (group-by :year records)]
    (doseq [[year records-for-a-year] grouped-by-year]
      (println "==" year "==")
      (print-records records-for-a-year))))

;; Processing of data

(defn census-counts-by-region
  "Count of census records for each region"
  []
  (counts-by-grouping (group-by :county-city census)))

;; Computed record queries predicates

(defn est-birth-from-death-rec
  [death-record]
  (try
    (let [death-year (Integer/parseInt (:year death-record))
          age-at-death (Integer/parseInt (:age-at-death death-record))]
      ; TODO: Work in the difference in ages of a year depending on time-of-year
      (- death-year age-at-death))
  (catch Exception ex ; TODO: Specific exception type and clean up the records!!
    nil)))

(defn first-forename-from-rec
  [rec]
  (first (str/split (:forename rec) #" ")))

;; Matching by record type

(defn not-impl-match
  [match-types]
  ;(println "Matching not impl. for" match-types) no-op
  )

(defn match-death-to-birth-get-1
  [death-record]
  (let [est-birth-year (est-birth-from-death-rec death-record)
        death-gender (:gender death-record)
        death-fname (first-forename-from-rec death-record)
        matching-birth-recs (->>
                            births
                            (filter #(= est-birth-year (Integer/parseInt (:year %))))
                            (filter #(= death-gender (:gender %)))
                            (filter #(= death-fname (first-forename-from-rec %))))]
      (if (= 1 (count matching-birth-recs))
        (first matching-birth-recs))))

(defn match-birth-to-death-get-1
  [birth-record]
  (let [birth-year (Integer/parseInt (:year birth-record))
        birth-gender (:gender birth-record)
        birth-fname (first-forename-from-rec birth-record)
        matching-death-recs (->>
                            deaths
                            (filter #(= birth-year (est-birth-from-death-rec %)))
                            (filter #(= birth-gender (:gender %)))
                            (filter #(= birth-fname (first-forename-from-rec %))))]
      (if (= 1 (count matching-death-recs))
        (first matching-death-recs))))

(defn match-birth-to-death
  [birth-record]
  (let [single-matching-death-rec (match-birth-to-death-get-1 birth-record)]
    (if single-matching-death-rec
      (if (match-death-to-birth-get-1 single-matching-death-rec)
        single-matching-death-rec))))

(defn match
  [match-types record]
  (case match-types
    ; TODO: Use keywords instead of actual variable refs
    [:births :deaths] (match-birth-to-death record)
    [:births :marriages] (not-impl-match match-types)
    [:births :census] (not-impl-match match-types)
    [:deaths :births] (not-impl-match match-types)
    [:deaths :marriages] (not-impl-match match-types)
    [:deaths :census] (not-impl-match match-types)
    [:marriages :births] (not-impl-match match-types)
    [:marriages :deaths] (not-impl-match match-types)
    [:marriages :census] (not-impl-match match-types)
    [:census :census] (not-impl-match match-types) ; Search in same type of record
    [:census :births] (not-impl-match match-types)
    [:census :deaths] (not-impl-match match-types)
    [:census :marriages] (not-impl-match match-types)
    (println "!! UNEXPECTED PAIR !!" match-types)))

;; Matching records

(defn random-record-pairing
  "Return a pair of record types to try to match"
  []
  (let [type-list (vec all-record-types)
        type1 (rand-nth type-list)]
    (if (= type1 #'census) ; Census is only type one person can show up in multiple times
      [type1 (rand-nth type-list)]
      [type1 (rand-nth (vec (disj all-record-types type1)))])))
      
(defn match-record-type
  [[record1-type record2-type] record]
  (match [record1-type record2-type] record))
  
;; Main execution

(defn -main
  "Read in records and process them."
  [& args]
    (show-stats #'births)
    (show-stats #'deaths)
    (show-stats #'marriages)
    (show-stats #'census)
    
    (doseq [rec (sort-by second (census-counts-by-region))]
      (println rec))
    (println)
    
    (doseq [type-pair (repeatedly random-record-pairing)]
      (let [[record1-type _] type-pair
            record1 (rand-nth (record1-type record-colls-by-keyword))
            successfully-matching-record (match-record-type type-pair record1)]
        (if successfully-matching-record
          (do 
            (println "Matched")
            (println record1)
            (println successfully-matching-record)
            (println))))))
