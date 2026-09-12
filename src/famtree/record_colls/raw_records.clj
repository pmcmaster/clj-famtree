(ns famtree.record-colls.raw-records
  "Unfiltered records, from the load functions, for each record type"
  (:require [famtree.load :as load]
            [famtree.records :refer [map->BirthRec
                                    map->DeathRec
                                    map->CensusRec
                                    map->MarriageRec]]))

(def births (map map->BirthRec (load/data-for-type "births")))
(def deaths (map map->DeathRec (load/data-for-type "deaths")))
(def all-census (map map->CensusRec (load/data-for-type "census")))
(def marriages (map map->MarriageRec (load/data-for-type "marriages")))

(defn swap-spouse-partner
  "Switch the spouse- names to the other fields and vice-versa"
  [data-map]
  (-> data-map
      (assoc :surname (:spouse-surname data-map))
      (assoc :forename (:spouse-forename data-map))
      (assoc :spouse-surname (:surname data-map))
      (assoc :spouse-forename (:forename data-map))))

(def marriages-spouse (->> (load/data-for-type "marriages")
                           (map swap-spouse-partner)
                           (map map->MarriageRec)))

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

