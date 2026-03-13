(ns famtree.records.raw-collections
  "Unfiltered records, from the load functions, for each record type"
  (:require [famtree.records.core :use [map->BirthRec
                                        map->DeathRec
                                        map->CensusRec
                                        map->MarriageRec
                                        map->MarriageSpouseRec]]
            [famtree.load :as load]))

(def births (map map->BirthRec (load/data-for-type "births")))
(def deaths (map map->DeathRec (load/data-for-type "deaths")))
(def all-census (map map->CensusRec (load/data-for-type "census")))

(def marriages (map map->MarriageRec (load/data-for-type "marriages")))
(def marriages-spouse (map map->MarriageSpouseRec
                           (load/data-for-type "marriages")))

(def all-collection-refs
  "All references to each of the available collections"
  (hash-set
    #'births #'deaths #'marriages #'marriages-spouse #'all-census))

(def all-records-except-marriage
  "One big collection with everything except marriage records.
  Used elsewhere to derive the 'core' surnames being looked at."
  (->>
    all-collection-refs
    (filter #(not= #'marriages %))
    (filter #(not= #'marriages-spouse %))
    (map var-get)
    (reduce concat)))

