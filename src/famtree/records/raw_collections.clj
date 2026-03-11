(ns famtree.records.raw-collections
  (:require [famtree.records.core :use [map->BirthRec
                                        map->DeathRec
                                        map->MarriageRec
                                        map->CensusRec]]
            [famtree.load :as load]))

(def births (map map->BirthRec (load/data-for-type "births")))
(def deaths (map map->DeathRec (load/data-for-type "deaths")))
(def all-census (map map->CensusRec (load/data-for-type "census")))
(def marriages (map map->MarriageRec (load/data-for-type "marriages")))
(def marriages-spouse (map map->MarriageSpouseRec (load/data-for-type "marriages")))

(def all-collection-refs [#'births #'deaths #'marriages #'marriages-spouse #'all-census])

(def all-records-except-marriage
  "One big collection with everything except marriage records"
  (->>
    all-collection-refs
    (filter #(not= #'marriages %))
    (filter #(not= #'marriages-spouse %))
    (map var-get)
    (reduce concat)))

