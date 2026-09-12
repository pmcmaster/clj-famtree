(ns server.mapping-js
  (:require [hiccup.core :as h]))

;; Produces <script> content for interacting with a map (as in map of the
;; world) UI. Includes associated css for different map sizes.

;; TODO: Avoid hardcoded JS strings; call out to include external JS template
;; files instead? Works for now but not very clean-feeling.

;; Some of this has been converted to CLJS (in famtree/mapping_cljs).

(defn map-css
  "CSS for the map element. Allows setting of size (square map)"
  [px-size]
  (str "#map "
       "{ height: " px-size "px;"
       "width: " px-size "px; }"))

(defn headers-with-size
  "Headers for including a small map UI
  Configurable size via `px-size`"
  [px-size]
  (h/html
    [:link {:rel "stylesheet"
            :href "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
            :integrity "sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
            :crossorigin ""}]
    [:script {:src "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
              :integrity "sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo="
              :crossorigin ""}]
    [:style {:type "text/css"} (map-css px-size)]))

(defn headers-default
  "Headers for including a small map UI"
  []
  (headers-with-size 800))

(defn headers-small
  "Headers to style map in a small size"
  []
  (headers-with-size 300))

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

(defn script-add-map-pins
  "Adds pins to a map with the specified `locations`. Locations are expected
  to have a lat and lng value to determine the position for pin placement"
  [locations]
  (h/html
    [:script {:type "text/javascript"}
     (for [loc locations
           :when (and (:lat loc) (:lng loc))]
       (str "L.marker([" (:lat loc) "," (:lng loc) "]).addTo(map);\n"))]))

