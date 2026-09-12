(ns famtree.places
  (:require [famtree.record-colls.main-records :as rec-colls]))

;; Functions for working with standardised place names for records
;; Does not include any positional (lat/long) handling here

;; TODO: Distinction between this module and the 'geolocate' module is not very
;; clear/useful

(defn loc-name-for-rec
  "Standardised location name for a record"
  [rec]
  (str (:rd-name rec)
       " / "
       (:county-city rec "NONE")))

(defn unique-locations
  "Sorted set (alphabetically) of all unique locations in the current records"
  []
  (let [all-locs (map loc-name-for-rec
                      rec-colls/all-records)
        locs-set (apply sorted-set all-locs)]
    locs-set))

