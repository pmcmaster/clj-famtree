(ns server.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :as resp]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [server.pages.main :as main-page]
            [server.pages.record :as record-page]
            [server.pages.record-collection :as rec-coll-page]
            [server.pages.mark-location :as mark-loc-page]
            [server.pages.link-records :as link-records]
            [server.pages.location-list :as loc-list-pages]
            [server.location-param :as loc-param]
            [server.location :as location])
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)]))

;; Main server routes definition.

; Run with:
; clojure -M -m server.main

(defroutes main-routes
  (GET "/" [] (main-page/content))
  (GET "/location/:loc-hash" [loc-hash] (mark-loc-page/content loc-hash))
  (GET "/locations" [] (loc-list-pages/locations-page false))
  (GET "/year/:year" [year] (loc-list-pages/location-for-year year :all))
  (GET "/random-location" [] (mark-loc-page/random-location-page))
  (POST "/update-location" req
        (let [updated-data (loc-param/data-for-update req)]
          (location/update-location updated-data)
          (resp/redirect "/random-location")))
  (GET "/record/:rec-hash" [rec-hash] (record-page/record-page rec-hash))
  (GET "/coll/:coll-name" [coll-name] (rec-coll-page/content coll-name))
  (GET "/set-link/:rec-hash/to/:coll-name" [rec-hash coll-name]
       (link-records/set-link-page rec-hash coll-name))
  (route/not-found "Page not found"))

(def app
  (-> (handler/site #'main-routes)
      (wrap-base-url)
      (wrap-reload)))

(defn -main [& args]
  (run-jetty #'app {:port 3000}))

