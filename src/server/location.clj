(ns server.location
  (:require
    [famtree.record-colls.main-records :as rec-colls]
    [famtree.places :as places]
    [famtree.geolocate :as geolocate]))

;; Common functions for listing locations, or updating info relating to a
;; location.

(defn update-location
  "Update lat/long for one or more locations"
  [[{:keys [lat lng]} loc-names]]
  (if (not-any? empty? [lat lng loc-names]) 
    (geolocate/add-lat-lng-to-locations
      {:lat lat :lng lng} loc-names)))

(defn locations-without-geo
  "List of locations which do not have geolocation data"
  []
  (filter #(not (contains? @geolocate/location-info %))
          (places/unique-locations)))

(defn locations-for-year
  "All locations and records for a given year.
  Returns a map with keys :locs and :recs
  which are respectively a list of locations
  and a list of records"
  [year]
  (let [all-recs-for-year (filter
                            #(= (:year %) year)
                            rec-colls/all-records-except-census)
        all-locs (into {} places/loc-name-for-rec all-recs-for-year)
        all-geolocs (map #(get @geolocate/location-info %) all-locs)]
    {:locs all-geolocs
     :recs all-recs-for-year}))

(defn geoloc-for-record
  "Returns geolocation for a record"
  [record]
  (get @geolocate/location-info (places/loc-name-for-rec record)))
