(ns famtree.mapping-cljs.mapping-main
  (:require-macros [hiccups.core :as h])
  #_:clj-kondo/ignore ; clj-kondo doesn't like this 'unused require' but
  ;; it appears to be required (as in hiccups docs)
  (:require [hiccups.runtime :as hiccupsrt]
            ))

;; js/L. in this context is the Leaflet library for mapping
;; https://leafletjs.com/

;; No realy pressing reason to have this as cljs instead of plain JavaScript,
;; though it is quite a bit more readable

(defonce map-elem
  ;; Initialises the map element (inside a div with id 'map') on the page)
  (.map js/L. "map"))

(def attribution
  (h/html
    "&copy; "
    [:a {:href "http://www.openstreetmap.org/copyright"}
     "OpenStreetMap"]))

(def tile-layer
  "Create the normal map tile later to use, max zoom and attribution.
  Returns the tile layer, which subsequently needs added to the map itself"
  (.tileLayer js/L.
              "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
              (clj->js {:maxZoom 13
                        :attribution attribution})))

(defn setup
  "Setup the map with basic tile layer. Associates the initialised map with the
  'map' JS var so that it can be referenced by scripts which are not yet
  converted to CLJS"
  []
  (println "Loaded mapping_main script")
  (.addTo tile-layer map-elem)
  (set! js/map map-elem))

(defn setup-map
  "Setup the map (map-elem) to position lat-lng and zoom level specified
  NB: lat-lng needs to be a js array before it is passed to Leaflet, so
  is converted here"
  [lat-lng zoom]
  (.setView map-elem
            (apply array lat-lng)
            zoom))

(defn reload!
  "Currently unused - hook called on reload from shadow-cljs"
  []
  "Called reload! hook")
