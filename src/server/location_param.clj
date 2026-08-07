(ns server.location-param
  (:require [famtree.places :as places]
            [clojure.string :as str]))

;; Functions for converting to/from hashes for locations
;; and locations as parameter names.

;; Identifier which gets tagged on to the start of a location parameter
(def loc-param-tag "loc")

(defn is-loc-param?
  "Predicate tests if `param` is the shape of a location param"
  [param]
  (str/starts-with? param loc-param-tag))

(defn location-name-from-hash
  "Look up location name in list of locations based on its hash"
  [loc-hash-str]
  (let [loc-hash (Integer/parseInt loc-hash-str)
        found-loc (first
                    (filter
                      #(= loc-hash (hash %))
                      (places/unique-locations)))]
    found-loc))

(defn loc-param-to-hash
  "Parse location param to hash str"
  [param]
  (last (str/split param #"\|")))

(defn loc-param-to-name
  "Parameter to location name"
  [param]
  (location-name-from-hash (loc-param-to-hash param)))

(defn param-name-for-loc
  "Parameter name for `location`"
  [location]
  (str loc-param-tag "|" (hash location)))
