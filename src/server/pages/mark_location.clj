(ns server.pages.mark-location
  (:require 
    [hiccup.core :as h]
    [clojure.string :as str]
    [famtree.places :as places]
    [famtree.geolocate :as geolocate]
    [server.pages.common :as common]
    [server.location :as location]
    [server.location-param :as loc-param]
    [server.mapping-js :as mapping-js]))

;; Page content for setting or updating geolocation information for a location

(defn first-word-of
  "First word after splitting on spaces"
  [words]
  (first (str/split words #" ")))

(defn content
  "Page for editing the info with a named location"
  [loc-hash-str]
  (let [found-loc (loc-param/location-name-from-hash loc-hash-str)
        first-word (first-word-of found-loc)
        same-start-locs (filter
                          #(= first-word (first-word-of %))
                          (places/unique-locations))]
    (h/html
      [:head [:title "Location detail for " found-loc]
       (common/oat-header)
       (mapping-js/headers)]
     [:body [:h1 found-loc]
      [:div {:id "map"}]
      [:form
       {:method "post"
        :action "/update-location"}
       (mapping-js/lat-lng-fields)
       [:ul
        (for [other-loc same-start-locs]
          [:li [:input {:name (loc-param/param-name-for-loc other-loc)
                        :type "checkbox"
                        :checked false}
                other-loc]
           (when-let [coord (get @geolocate/location-info other-loc)]
             (str " " coord))
           " "
           [:a {:href
                (str "https://duckduckgo.com/?q=" first-word "&t=osx&ia=web")
                :target "_blank"}
            "Search"]])]
       [:button {:type "submit"} "Update location"]]
      [:p [:a {:href "/locations"} "All Locations"]]
      (mapping-js/script-default)
      (mapping-js/script-marker-on-click)])))

(defn random-location-page
  "A page for a random location which does not have lat-long set"
  []
  (let [random-loc (rand-nth (location/locations-without-geo))]
    (content (str (hash random-loc)))))

