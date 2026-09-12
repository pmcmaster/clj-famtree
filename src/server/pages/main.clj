(ns server.pages.main
  (:require [hiccup.core :as h]
            [famtree.record-colls.main-records :as rec-colls]
            [clojure.string :as str]))

(defn content
  "Main landing page with links to other pages"
  []
  (h/html
    [:head
     [:title "FamTree Main Page"]
     [:script {:src "/htmx.min.js"}]]
    [:body
     [:h1 "FamTree Main Page"]
     [:p [:a {:href "/locations"} "Locations without geo-data"]]
     [:p [:a {:href "/year/1855"} "By Year"] " (defaults to 1855)"]
     (for [rec-coll (sort-by str rec-colls/all-collection-refs)]
       (let [short-coll (last (str/split (str rec-coll) #"/"))]  
        [:p [:a {:href (str "/coll/" short-coll)} rec-coll]]))]))

