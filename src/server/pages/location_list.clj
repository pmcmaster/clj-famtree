(ns server.pages.location-list
  (:require [hiccup.core :as h]
            [famtree.places :as places]
            [famtree.geolocate :as geolocate]
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
     [:head [:title "Locations"]]
     [:body 
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
            [:a {:href (str "/location/" loc-hash)} loc]]))]])))

(defn location-for-year
  "Show all location with activity for a given year, and a type of record"
  [year-str record-types]
  (let [year (Integer/parseInt year-str)
        {all-recs-for-year :recs
         all-locs-for-year :locs} (location/locations-for-year year)]
    (h/html
      [:head [:title "Locations for " (str year)]
       (mapping-js/headers)]
      [:body [:h1 "Locations for " (str year)]
       [:p
        [:a {:href (str "/year/" (dec year))} "Prev"]
        " | "
        [:a {:href (str "/year/" (inc year))} "Next"]]
       [:div {:id "map"}]
       (mapping-js/script-default)
       [:script {:type "text/javascript"}
        (for [loc all-locs-for-year
              :when (and (:lat loc) (:lng loc))]
          (str "L.marker([" (:lat loc) "," (:lng loc) "]).addTo(map);\n"))]
       (for [each-rec all-recs-for-year]
        (rec-views/basic-row-with-link each-rec))])))

