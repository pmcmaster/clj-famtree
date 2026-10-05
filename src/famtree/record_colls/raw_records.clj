(ns famtree.record-colls.raw-records
  "Unfiltered records, from the load functions, for each record type"
  (:require [famtree.load :as load]
            [famtree.data-cache :as cache]
            [famtree.records :refer [map->BirthRec
                                    map->DeathRec
                                    map->CensusRec
                                    map->MarriageRec]]))

(defn births
  "Records for births, either from cached values, or by loading from disk"
  []
  (cache/cached-or-load :births
                        #(map map->BirthRec (load/data-for-type "births"))))

(defn deaths
  "Records fir deaths, either from cached values, or by loading from disk"
  []
  (cache/cached-or-load :deaths
                  #(map map->DeathRec (load/data-for-type "deaths"))))

(defn all-census
  "All census records, either from cached calues, or by loading from disk"
  []
  (cache/cached-or-load :census
                  #(map map->CensusRec (load/data-for-type "census"))))

(defn marriages
  "Marriage records, either from cached values, or by loading from disk"
  []
  (cache/cached-or-load :marriages
                  #(map map->MarriageRec (load/data-for-type "marriages"))))

(defn swap-spouse-partner
  "Switch the spouse- names to the other fields and vice-versa"
  [data-map]
  (-> data-map
      (assoc :surname (:spouse-surname data-map))
      (assoc :forename (:spouse-forename data-map))
      (assoc :spouse-surname (:surname data-map))
      (assoc :spouse-forename (:forename data-map))))

(defn marriages-spouse
  "Marriage records but with names swapped between main/spouse parties"
  []
  (cache/cached-or-load :marriages-spouse
                  #(->> (load/data-for-type "marriages")
                        (map swap-spouse-partner)
                        (map map->MarriageRec))))

(defn all-records-except-marriage
  "One big collection with everything except marriage records.
  Used elsewhere to derive the 'core' surnames being looked at."
  []
  (concat (births)
          (deaths)
          (all-census)))

