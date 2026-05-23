(ns server.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as resp]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [hiccup.core :as h]
            [famtree.records :as recs]
            [famtree.record-colls.main-records :as rec-colls]
            [famtree.geolocate :as geolocate]
            [server.record-views :as rec-views]
            [clojure.string :as str]
            [server.mapping :as mapping])
  (:use compojure.core
        [hiccup core page]
        [hiccup.middleware :only (wrap-base-url)]) 
  (:gen-class))

; Run with:
; clojure -M -m server.main

(defn basic-row-with-link
  "Basic info for a row with a link to detail for the record"
  [record]
    [:p (rec-views/basic-row record)
    " "
    [:a {:href (str "/record/" (hash record))} "Details"]])

(defn main-page []
  (h/html
    [:head
     [:title "Foo title"]
     [:script {:src "/htmx.min.js"}]]
    [:body
     [:h1 "Famtree"]
     [:p [:a {:href "/reset"} "Reset Data"]]
     [:p [:a {:href "/locations"} "Locations"]]
     (for [rec-coll (sort-by str rec-colls/all-collection-refs)]
       (let [short-coll (last (str/split (str rec-coll) #"/"))]  
        [:p [:a {:href (str "/coll/" short-coll)} rec-coll]]))]))

(defn record-page-content
  "Details for a particular record, looked up based on its hash in a horribly
  inefficient way"
  [rec-hash-str]
  (if-let [rec-hash (Integer/parseInt rec-hash-str)]
    (if-let [found-rec (first
                         (filter
                           #(= rec-hash (hash %))
                           rec-colls/all-records))]
      (rec-views/detail-page found-rec)
      (str "No rec matched hash " rec-hash))))

(defn record-page
  "Page for a single record"
  [rec-hash-str]
  (h/html
    [:head [:title "Record detail"]]
    [:body (record-page-content rec-hash-str)]))

(defn coll-list-content
  "List all records for a collection type (such as all marriage records)"
  [coll-name]
  (let [coll-var (intern 'famtree.record-colls.main-records (symbol coll-name))
        coll (var-get coll-var)]
    (h/html
      (for [each-rec coll]
       (basic-row-with-link each-rec)))))

(defn coll-list-page
  "List all records on one page for a collection type"
  [coll-name]
  (h/html
    [:head [:title coll-name " records"]]
    [:body (coll-list-content coll-name)]))

(defn set-link-page
  "Page to set up links between one record and another record type"
  [rec-hash-str rec-coll]
  (h/html
    [:head [:title "Linking record with " rec-coll]]
    [:body
     (record-page-content rec-hash-str)
     [:p rec-coll]
     (coll-list-content rec-coll)
    ]))

(defn unique-locations
  "Sorted set (alphabetically) of all unique locations in the current records"
  []
  (let [all-locs (map #(str (:rd-name %)
                            " / "
                            (:county-city % "NONE"))
                      rec-colls/all-records)
        locs-set (apply sorted-set all-locs)]
    locs-set))

(defn handler-response []
  {:status 200
   :headers {"Content-Type" "text/plain; charset-UTF-8"}
   :body "Hello blorms\n"})

(defn location-name-from-hash
  "Look up location name in list of locations based on its hash"
  [loc-hash-str]
  (let [loc-hash (Integer/parseInt loc-hash-str)
        found-loc (first
                    (filter
                      #(= loc-hash (hash %))
                      (unique-locations)))]
    found-loc))

(defn first-word-of
  "First word after splitting on spaces"
  [words]
  (first (str/split words #" ")))

(defn location-page
  "Page for editing the info with a named location"
  [loc-hash-str]
  (let [found-loc (location-name-from-hash loc-hash-str)
        first-word (first-word-of found-loc)
        same-start-locs (filter
                          #(= first-word (first-word-of %))
                          (unique-locations))]
    (h/html
      [:head [:title "Location detail for " found-loc]
       (mapping/headers)]
     [:body [:h1 found-loc]
      [:div {:id "map"}]
      [:form
       {:method "post"
        :action "/update-location"}
       (mapping/lat-lng-fields)
       [:ul
        (for [other-loc same-start-locs]
          [:li [:input {:name (str "loc|" (hash other-loc))
                        :type "checkbox"
                        :checked true}
                other-loc]
           (if-let [coord (get @geolocate/location-info other-loc)]
             (str " " coord))
           " "
           [:a {:href
                (str "https://duckduckgo.com/?q=" first-word "&t=osx&ia=web")
                :target "_blank"}
            "Search"]])]
       [:button {:type "submit"} "Update location"]]
      [:p [:a {:href "/locations"} "All Locations"]]
      (mapping/script-default)])))

(defn locations-without-geo
  []
  (filter #(not (contains? @geolocate/location-info %))
          (unique-locations)))

(defn random-location-page
  "A page for a random location which does not have lat-long set"
  []
  (let [random-loc (rand-nth (locations-without-geo))]
    (location-page (str (hash random-loc)))))

(defn locations-page
  "List locations - show all shows all, even those with lat/long already set
  if show-all is not true, then only those with no lat-long set are shown"
  [show-all?]
  (let [all-locs (unique-locations)]
    (h/html
     [:head [:title "Locations"]
      (mapping/headers)]
     [:body 
      [:div {:id "map"}]
      (mapping/lat-lng-form)
      (mapping/script-default)
      [:p (str (count all-locs) " locations (" (count (locations-without-geo))
               " with no location set)")]
      [:ul
       (for [loc all-locs
             :when (or show-all?
                     (not (contains? @geolocate/location-info loc)))]
         (let [loc-hash (hash loc)]
           [:li
            [:a {:href (str "/location/" loc-hash)} loc]]))]])))

(defn loc-param-to-hash
  "Parse location param to hash str"
  [param]
  (last (str/split param #"\|")))

(defn loc-param-to-name
  "Parameter to location name"
  [param]
  (location-name-from-hash (loc-param-to-hash param)))

(defn update-location
  "Update lat/long for one or more locations"
  [req]
  (let [{params :form-params} req
        lat (get params "lat")
        lng (get params "lng")
        locs (filter #(str/starts-with? % "loc") (keys params))
        loc-names (map loc-param-to-name locs)]
    (if (not-any? empty? [lat lng loc-names]) 
      (geolocate/add-lat-lng-to-locations
        {:lat lat :lng lng} loc-names))))

(defroutes main-routes
  (GET "/" [] (main-page))
  (GET "/foo.txt" [] (handler-response))
  (GET "/location/:loc-hash" [loc-hash] (location-page loc-hash))
  (GET "/locations" [] (locations-page false))
  (GET "/random-location" [] (random-location-page))
  (POST "/update-location" req (update-location req) (resp/redirect "/random-location"))
  (GET "/record/:rec-hash" [rec-hash] (record-page rec-hash))
  (GET "/reset" [] (main-page)) ; Currently a no-op. Intended to reset links to blank state
  (GET "/coll/:coll-name" [coll-name] (coll-list-page coll-name))
  (GET "/set-link/:rec-hash/to/:coll-name" [rec-hash coll-name]
       (set-link-page rec-hash coll-name))
  (route/not-found "Page not found"))

(def app
  (-> (handler/site #'main-routes)
      (wrap-base-url)
      (wrap-reload)))

(defn -main [& args]
  (run-jetty #'app {:port 3000}))

