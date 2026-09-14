(ns famtree.server.location-param
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

(defn data-for-update
  "Parse out data for updating the lat/long associated with some locations
  Data arrives as one single lat/long, and a list of locations to
  associate with that position.
  Return is:
  [{:lat 2.3 :lng :34.5} [Loc1 Loc2 Loc3...]"
  [req]
  (let [{params :form-params} req
        {:strs [lat lng]} params
        loc-params (filter is-loc-param? (keys params))
        loc-names (map loc-param-to-name loc-params)]
    [{:lat lat :lng lng} loc-names]))
