(ns famtree.data-gen.census
    (:require [famtree.data-gen.synthetic-records-store :as store]))

;; Years when a census was completed in Scotland, and for which
;; (as of 2026) the data is available)
(def census-years #{1841 1851 1861 1871 1881 1891 1901 1911 1921})

(defn simulate-census
  "Create census records if it is a census year. Does not actually
  affect the population at all, so just returns that unchanged"
  [year population]
  (when (contains? census-years year)
    (println year "is a census year")
    (doseq [person population]
      (store/add-record :census year person)))
  population)

