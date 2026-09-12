(ns server.pages.location-list
  (:require [hiccup.core :as h]
            [famtree.places :as places]
            [famtree.geolocate :as geolocate]
            [server.pages.common :as common]
            [server.location :as location]
            [server.mapping-js :as mapping-js]
            [server.record-views :as rec-views]))

;; Pages which show multiple locations

(defn locations-page
  "List locations - `show-all` shows all, even those with lat/long already set
  if show-all is not true, then only those with no lat-long set are shown"
  [show-all?]
  (let [all-locs (places/unique-locations)]
    (h/html
     [:head [:title "Locations"]
      (common/oat-header)]
     [:body 
      [:nav {:aria-label "Breadcrumb"}
       [:ol {:class "unstyled hstack"
             :style "font-size: var(--text-7)"}
        [:li
         [:a {:href "/" :class "unstyled"} "Home"]]
        [:li {:aria-hidden "true"} "/"]
        [:li {:class "unstyled"}
         [:strong "Locations"]]]]
      [:div {:class "card"}
       [:p (str (count all-locs)
                " locations ("
                (count (location/locations-without-geo))
                " with no location set)")]
       [:ul
        (for [loc all-locs
              :when (or show-all?
                        (not (geolocate/has-geoloc? loc)))]
          (let [loc-hash (hash loc)]
            [:li
             [:a {:href (str "/location/" loc-hash)} loc]]))]]])))

(defn location-for-year
  "Show all location with activity for a given year"
  [year-str]
  (let [year (Integer/parseInt year-str)
        {all-recs-for-year :recs
         all-locs-for-year :locs} (location/locations-for-year year)]
    (h/html
      [:head [:title "Locations for " (str year)]
       (common/oat-header)
       (mapping-js/headers-default)]
      [:body 
       [:nav {:aria-label "Breadcrumb"}
        [:ol {:class "unstyled hstack"
              :style "font-size: var(--text-7)"}
         [:li [:a {:href "/" :class "unstyled"} "Home"]]
         [:li {:aria-hidden "true"} "/"]
         [:li {:class "unstyled"}
          "Locations for " [:strong (str year)]]
          ]]
       [:p {:style "font-size: var(--text-7))"}
        [:a {:href (str "/locations-by-year/" (dec year))}
         "Prev (" (str (dec year) ")")]
        " | "
        [:a {:href (str "/locations-by-year/" (inc year))}
         "Next (" (str (inc year)) ")"]]
       [:div {:id "map" :class "card"}]
       [:script {:src "/gen-static/mapping-main.js"}]
       [:script {:src "/gen-static/map-default.js"}]
       (mapping-js/script-add-map-pins all-locs-for-year)
       [:p "Does not show census records; only birth/deaths/marriages"]
       [:div {:class "card"}
        [:h2 "Records for " (str year)]
        (for [each-rec all-recs-for-year]
          (rec-views/basic-row-with-link each-rec))]])))

