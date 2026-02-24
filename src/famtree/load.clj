(ns famtree.load
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

;; Data loading

(defn rows-for-record-type 
  "Read the raw CSV from a file named for record-type"
  [record-type]
  (let [path (str "data/" record-type ".csv")]
    (with-open [reader (io/reader path)]
      (doall (csv/read-csv reader :separator \tab)))))

(defn field-name-to-keyword 
  "Convert header row from CSV file into keywords
  Has some specific replacements of long or punctuated names, or ones which conflict with language terms"
  [fieldname]
  (let [non-std-fields {"Mother's Maiden Name" :mm-name "County / City" :county-city "Ref" :rec-ref}
        non-std-name (non-std-fields fieldname)]
    (if (nil? non-std-name)
      (-> fieldname
          (str/replace " " "-")
          str/lower-case
          keyword)
      non-std-name)))

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
      (parse-int-or-nil field-val) ; NB: Does not currently throw an exception - may be nil
      field-val)))

(defn row-to-map
  "Convert a list of lists of text fields to a map, using the field-keys provided
  and converting some values according to the field names"
  [text-row-coll field-keys]
  (reduce-kv
    (fn [result-map k v] (assoc result-map k (parse-field-value k v)))
    {}
    (zipmap field-keys text-row-coll)))

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

