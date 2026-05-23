(ns famtree.geolocate
  (:require [clojure.java.io :as io]))

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
  "Associate lat/lng with some locations"
  [lat-lng locations]
  (swap! location-info
         (fn [existing]
           (reduce
             (fn [coll loc] (assoc coll loc lat-lng))
             existing
             locations)))
  (write-location-data-to-file @location-info))


