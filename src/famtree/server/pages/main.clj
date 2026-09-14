(ns famtree.server.pages.main
  (:require [hiccup.core :as h]
            [famtree.record-colls.main-records :as rec-colls]
            [clojure.string :as str]
            [famtree.server.pages.common :as common]))

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
      [:p
       [:a {:href "/locations"} "Locations without geo-data"]
       " &mdash; Locations (basically place names) which do not have a lat/long assigned
       to them"]
      [:p [:a {:href "/locations-by-year/1855"} "By Year"]
       " &mdash; Shows records for a given year on a map. 1855 is the currently the
       date for earliest available records"]
      [:p [:a {:href "/explain-weights"} "Explain weights"]
       " &mdash; Break-down of the currently configured weights for matching records"]]
     [:article {:class "card"}
      [:h2 "Record sets"]
      [:p "Lists of all records for each type. (Census records are split into
          a group per-census-year, as those are all effectively independent
          data sets for each census)"]
      (for [rec-coll (sort-by str rec-colls/all-collection-refs)]
        (let [short-coll (last (str/split (str rec-coll) #"/"))]  
          [:p [:a {:href (str "/coll/" short-coll)} rec-coll]]))]]))
   

