(ns server.pages.main
  (:require [hiccup.core :as h]
            [famtree.record-colls.main-records :as rec-colls]
            [clojure.string :as str]
            [server.pages.common :as common]))

(defn content
  "Main landing page with links to other pages"
  []
  (h/html
    [:head
     [:title "FamTree Main Page"]
     (common/oat-header)]
    [:body
     [:h1 "FamTree Main Page"]
     [:div {:class "card"}
      [:p [:a {:href "/locations"} "Locations without geo-data"]]
      [:p [:a {:href "/year/1855"} "By Year"] " (defaults to 1855)"]]
     [:article {:class "card"}
      [:h2 "Record sets"]
      (for [rec-coll (sort-by str rec-colls/all-collection-refs)]
        (let [short-coll (last (str/split (str rec-coll) #"/"))]  
          [:p [:a {:href (str "/coll/" short-coll)} rec-coll]]))]]))
   

