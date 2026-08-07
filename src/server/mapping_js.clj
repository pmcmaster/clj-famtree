(ns server.mapping-js
  (:require [hiccup.core :as h]))

;; Produces <script> content for interacting with a map (as in map of the
;; world) UI

(defn headers
  "Headers for including a small map UI"
  []
  (h/html
    [:link {:rel "stylesheet"
            :href "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
            :integrity "sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
            :crossorigin ""}]
    [:script {:src "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
              :integrity "sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo="
              :crossorigin ""}]
    [:style {:type "text/css"}
     "#map { height: 850px; width: 850px; }"]))

(defn lat-lng-fields
  "Input fields for lat/long"
  []
  (h/html
    [:input {:id "lat" :name "lat"}]
    [:input {:id "lng" :name "lng"}]))

(defn lat-lng-form
  "Form elements for lat/long display and submission. Set on map click"
  []
  (h/html
    [:form
     (lat-lng-fields)]))

(defn script-marker-on-click
  "Script snippet to add a pin on-click in on the map element"
  []
  (h/html
    [:script {:type "text/javascript"}
     "
     var marker;
     map.on('click', function(e) {
     if(marker)
     map.removeLayer(marker);
     console.log(e.latlng);
     document.getElementById('lat').value = e.latlng.lat;
     document.getElementById('lng').value = e.latlng.lng;
     marker = L.marker(e.latlng).addTo(map);
     });"]))

(defn script-position
  "Script for defaults and interaction with map"
  [lat, lng, zoom]
  (h/html
    [:script {:type "text/javascript" }
     "var map = L.map('map').setView([" lat "," lng "]," zoom ");
     L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',
     { maxZoom: 19,
     attribution: '&copy; <a href=\"http://www.openstreetmap.org/copyright\">OpenStreetMap</a>'}
     ).addTo(map);"]))

(defn script-default
  "Script for defaults and interaction with map"
  []
  (script-position 56.72, -4.1, 7))

