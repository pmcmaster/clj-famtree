(ns famtree.server.pages.mark-location
  (:require 
    [hiccup.core :as h]
    [clojure.string :as str]
    [famtree.places :as places]
    [famtree.geolocate :as geolocate]
    [famtree.server.pages.common :as common]
    [famtree.server.location :as location]
    [famtree.server.location-param :as loc-param]
    [famtree.server.mapping-js :as mapping-js]))

;; Page content for setting or updating geolocation information for a location

(defn first-word-of
  "First word after splitting on spaces"
  [words]
  (first (str/split words #" ")))

(defn content
  "Page for editing the info with a named location"
  ;; TODO: Split this out a bit, possibly by extracting the breadcrumb logic
  ;; to somewhere it can be reused?
  [loc-hash-str]
  (let [found-loc (loc-param/location-name-from-hash loc-hash-str)
        first-word (first-word-of found-loc)
        same-start-locs (filter
                          #(= first-word (first-word-of %))
                          (places/unique-locations))]
    (h/html
      [:head [:title "Location detail for " found-loc]
       (common/oat-header)
       (mapping-js/headers-default)]
     [:body 
      [:nav {:aria-label "Breadcrumb"}
       [:ol {:class "unstyled hstack"
             :style "font-size: var(--text-7)"}
        [:li [:a {:href "/" :class "unstyled"} "Home"]]
        [:li {:aria-hidden "true"} "/"]
        [:li [:a {:href "/locations" :class "unstyled"} "Locations"]]
        [:li {:aria-hidden "true"} "/"]
        [:li {:class "unstyled"} [:strong found-loc]]]]

      [:div {:id "map"}]
      [:div {:class "card"} [:form
        {:method "post"
         :action "/update-location"}

        [:label {:data-field true}
         "Latitude "
         [:input {:id "lat" :name "lat"
                  :type "text" :disabled true
                  :style ""}]]
        [:label {:data-field true}
         "Longitude "
         [:input {:id "lng" :name "lng"
                  :type "text" :disabled true
                  :style ""}]]
        [:p "Associate lat/long with locations:"]
        [:ul {:class "unstyled"}
         (for [other-loc same-start-locs]
           [:li {:class "unstyled"} [:input
                                     {:name (loc-param/param-name-for-loc
                                              other-loc)
                                      :type "checkbox"
                                      :checked false}
                                     " " other-loc]
            (when-let [coord (get @geolocate/location-info other-loc)]
              (str " " coord))
            " "
            ;; Link to web search for the place
            [:a {:href
                 (str "https://duckduckgo.com/?q=" first-word "&t=osx&ia=web")
                 :target "_blank"}
             "Search"]])]
        [:button {:type "submit"} "Update location"]]]
      [:script {:src "/gen-static/mapping-main.js"}]
      [:script {:src "/gen-static/map-default.js"}]
      (mapping-js/script-marker-on-click)])))

(defn random-location-page
  "A page for a random location which does not have lat-long set"
  []
  (let [random-loc (rand-nth (location/locations-without-geo))]
    (content (str (hash random-loc)))))

