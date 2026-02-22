(ns famtree.records
  (:require [famtree.load :as load]))

;; Main data collections
      
(def births (load/data-for-type "births"))
(def deaths (load/data-for-type "deaths"))
(def marriages (load/data-for-type "marriages"))
(def census (load/data-for-type "census"))

(def all-types #{:births :deaths :marriages :census})

(def by-keyword
    {:births births
     :deaths deaths
     :marriages marriages
     :census census})

(defrecord CensusRec [surname forename year gender age-at-census rec-ref re-name county-city])

