(ns famtree.data-gen.core
  (:require [famtree.data-gen.deaths :as deaths]
            [famtree.data-gen.ageing :as ageing]
            [famtree.data-gen.marriages :as marriages]
            [famtree.data-gen.moving :as moving]
            [famtree.data-gen.census :as census]
            [famtree.data-gen.births :as births]
            [famtree.data-gen.people :as people]
            [famtree.data-gen.synthetic-records-store :as store]))

;; Generate some synthetic data which mirrors the structure of downloaded
;; records

;; TODO: Add in some 'gaps' in the records by skipping some randomly

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
                                  ageing/simulate-ageing
                                  (print-population-stats year)
                                  (moving/simulate-moving year)
                                  (print-population-stats year)
                                  (marriages/simulate-marriages year)
                                  (census/simulate-census year)
                                  (births/simulate-births year)
                                  (deaths/simulate-deaths year))]
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
  "Generate some initial people, simulate them for some number of years then
  write out the generated records to a set of .CSV files"
  [& args]
  (let [population (initial-population 400 [])]
    (simulate-time-from 1755 population))
  (store/print-record-summary)
  (deaths/write-csv)
  (births/write-csv)
  (marriages/write-csv)
  (census/write-csv))

