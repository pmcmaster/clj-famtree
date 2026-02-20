(ns famtree.load
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

;; Data loading

(defn- rows-for-record-type [record-type]
  "Read the raw CSV from a file named for record-type"
  (let [path (str "data/" record-type ".csv")]
    (with-open [reader (io/reader path)]
      (doall (csv/read-csv reader :separator \tab)))))

(defn- field-name-to-keyword [fieldname]
  "Convert header row from CSV file into keywords, with some replacements of long names"
  (let [non-std-fields {"Mother's Maiden Name" :mm-name "County / City" :county-city}
        non-std-name (non-std-fields fieldname)]
    (if (nil? non-std-name)
      (-> fieldname
        (str/replace " " "-")
        str/lower-case
        keyword)
      non-std-name)))

(defn- row-to-map
  "Convert a list of lists of text fields to a map, using the field-keys provided"
  [text-row-coll field-keys]
  (let [key-value-pairs (map vector field-keys text-row-coll)]
    ;; TODO Convert years and ages to integers
    ;; TODO and gender to a keyword?
    (reduce (fn [output-map [field-key field-val]] (assoc output-map field-key field-val))
      {}
      key-value-pairs)))

(defn- data-as-map
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