(ns famtree.data-gen.core
  (:require [famtree.data-gen.deaths :refer [simulate-deaths]]
            [famtree.data-gen.ageing :refer [simulate-ageing]]
            [famtree.data-gen.marriage :refer [simulate-marriages]]
            [famtree.data-gen.moving :refer [simulate-moving]]
            [famtree.data-gen.census :refer [simulate-census]]
            [famtree.data-gen.births :refer [simulate-births]]
            [famtree.data-gen.people :as people]
            [famtree.data-gen.synthetic-records-store :as store]))

;; Generate some synthetic data which mirrors the structure of downloaded
;; records

;; TODO: Will eventually write out data to the /data directory as .CSV files
;; So far only simulates the people and stores the data during execution

;; Run via: clojure -X famtree.data-gen.core/generate

(defn print-population-stats
  "Print out population stats. Needs to return the population so that this
  can be used with ->>"
  [year population]
  (println year "- population is" (count population))
  population)

(defn simulate-time-from
  "Generate some events for a year. Arbitrary cut-off at 1960"
  [year population]
  (when (< year 1960)
    (let [updated-population (->> population
                                  (print-population-stats year)
                                  simulate-ageing
                                  (print-population-stats year)
                                  (simulate-moving year)
                                  (print-population-stats year)
                                  (simulate-marriages year)
                                  (simulate-census year)
                                  (simulate-births year)
                                  (simulate-deaths year))]
      (recur (inc year) updated-population))))

(defn initial-population
  "Generate a random starting population of `size`
  These people exist before any records of them do"
  [size population]
  (if (zero? size)
    population
    (recur (dec size) (conj population
                            (people/random-core-person)))))

(defn generate
  "Generate a set of .CSV files for a pretend population of people"
  [& args]
  (let [population (initial-population 400 [])]
    (simulate-time-from 1755 population))
  (store/print-record-summary))

