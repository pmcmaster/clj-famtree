(ns famtree.load
  "Load data from files in 'data' directory into a map of appropriate shape"
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn rows-for-record-type 
  "Read the raw CSV from a file named for record-type"
  [record-type]
  (let [path (str "data/" record-type ".csv")]
    (with-open [reader (io/reader path)]
      (doall (csv/read-csv reader :separator \tab)))))

(defn field-name-to-keyword 
  "Convert `field-name` from header row from CSV file into keywords
  Has some specific replacements of long or punctuated names, or ones which
  conflict with language terms"
  [field-name]
  (let [non-std-fields {"Mother's Maiden Name" :mm-name
                        "County / City" :county-city
                        "Ref" :rec-ref}
        non-std-name (non-std-fields field-name)]
    (or non-std-name
        (-> field-name
            (str/replace " " "-")
            str/lower-case
            keyword))))

(defn parse-int-or-nil
  "Parse value to an Integer if possible, otherwise return nil"
  [value]
  (if (re-matches #"[0-9]+" value)
    (Integer/parseInt value)))

(defn parse-field-value
  "Parse field-val into appropriate value, behaviour based on field-key"
  [field-key field-val]
  (let [field-name (str field-key)
        numeric-field (some #(str/starts-with? field-name %) [":year" ":age-"])]
    (if numeric-field
      ;; NB: Does not currently throw an exception but may be nil
      (parse-int-or-nil field-val) 
      field-val)))

(defn row-to-map
  "Convert a list of data (`data-for-a-row`) to a map, using the `field-keys`
  provided and converting some values according to the field names"
  [data-for-a-row field-keys]
  (reduce-kv
    (fn [result-map k v] (assoc result-map k (parse-field-value k v)))
    {}
    (zipmap field-keys data-for-a-row)))

(defn data-as-map
  "Process the raw records for a record type into a map.
  raw-data expected to have a header row which is used to generate keys used in
  the output map"
  [raw-data]
  (let [header-row (first raw-data)
        header-fields (map field-name-to-keyword header-row)
        data-rows (rest raw-data)]
    (map #(row-to-map % header-fields) data-rows)))

(defn data-for-type
  "The properly formated map of data for a given record-type"
  [record-type]
  (data-as-map (rows-for-record-type record-type)))

