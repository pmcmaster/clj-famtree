(ns famtree.load
  "Load data from files in 'data' directory into a map of appropriate shape
  Does not actually result in Records at this point"
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.spec.alpha :as s]))

(defn rows-for-record-type 
  "Read the raw CSV from a file named for record-type"
  [record-type]
  (let [path (str "data/" record-type ".csv")]
    (with-open [reader (io/reader path)]
      (doall (csv/read-csv reader :separator \tab)))))

(def non-std-fields
  {"Mother's Maiden Name" :mm-name
   "County / City" :county-city
   "Ref" :rec-ref})

(defn field-name-to-keyword 
  "Convert `field-name` from header row from CSV file into keywords
  Has some specific replacements of long or punctuated names, or ones which
  conflict with language terms, which are substituted using non-std-fields"
  [field-name]
  (or (non-std-fields field-name)
      (-> field-name
          (str/replace " " "-")
          str/lower-case
          keyword)))

(defn parse-int-or-nil
  "Parse value to an Integer if possible, otherwise return nil"
  [value]
  ;; NB: Does not currently throw an exception but may be nil
  (if (re-matches #"[0-9]+" value)
    (Integer/parseInt value)))

(defn is-numeric-field?
  "Is `field-key` for a field which contains a numeric field"
  [field-key]
  (some #(str/starts-with? (str field-key) %) [":year" ":age-"]))

(defn parse-field-value-fn
  "Returns function suitable for parsing fields in the field identified by
  `field-key`."
  [field-key]
  (if (is-numeric-field? field-key)
    parse-int-or-nil
    identity))

(defn row-to-map
  "Convert a list of data (`data-for-a-row`) to a map, using the `field-keys`
  provided and converting some values according to the field names"
  [data-for-a-row field-keys]
  (let [parse-fns (map parse-field-value-fn field-keys)]
    (into {}
         (map (fn [[k v f]] [k (f v)]))
         (map list field-keys data-for-a-row parse-fns))))

(defn data-as-map
  "Process the raw records for a record type into a map.
  raw-data expected to have a header row which is used to generate keys used in
  the output map"
  [raw-data]
  (let [header-row (first raw-data)
        data-rows (rest raw-data)
        field-keys (map field-name-to-keyword header-row)]
    (map #(row-to-map % field-keys) data-rows)))

(defn data-for-type
  "The properly formatted map of data for a given record-type"
  [record-type]
  (data-as-map (rows-for-record-type record-type)))

(s/fdef data-for-type
        :args (s/cat :s string?)
        :ret (s/coll-of :famtree.record-specs/any-record))

