(ns famtree.geolocate
  (:require [clojure.java.io :as io]))

;; Functions which relate to a geographical lat/long location for a place name
;; These are saved to disk and stored during program execution in an atom

(def location-filename
  "data/locations.txt")

(defn location-data-from-file
  "Read location data from the data file"
  []
  (if (.exists (io/file location-filename)) 
    (read-string (slurp location-filename))
    {}))

(defonce location-info (atom (location-data-from-file)))

(defn write-location-data-to-file
  "Write location data to file"
  [data]
  (spit location-filename (pr-str data)))

(defn add-lat-lng-to-locations
  "Associate lat/lng with some locations
  The same `lat-lng` is set for all of the places in `locations`"
  [lat-lng locations]
  (swap! location-info
         (fn [existing]
           (reduce
             (fn [coll loc] (assoc coll loc lat-lng))
             existing
             locations)))
  (write-location-data-to-file @location-info))

(defn has-geoloc?
  "Predicate returns true when place named `location` has associated
  geolocation info"
  [location]
  (contains? @location-info location))
