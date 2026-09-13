(ns server.main
  (:require [clojure.tools.logging :as logging]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.logger :refer [wrap-with-logger]]
            [ring.util.response :as resp]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [server.pages.main :as main-page]
            [server.pages.record :as record-page]
            [server.pages.record-collection :as rec-coll-page]
            [server.pages.mark-location :as mark-loc-page]
            [server.pages.link-records :as link-records]
            [server.pages.location-list :as loc-list-pages]
            [server.pages.explain-weights :as explain-weights-page]
            [server.location-param :as loc-param]
            [server.location :as location]
            [hiccup.middleware :refer (wrap-base-url)]))

;; Main server routes definition.

; Run with:
; clojure -M:server

(defroutes main-routes
  (GET "/" [] (main-page/content))
  (GET "/location/:loc-hash" [loc-hash] (mark-loc-page/content loc-hash))
  (GET "/locations" [] (loc-list-pages/locations-page false))
  (GET "/locations-by-year/:year" [year]
       (loc-list-pages/location-for-year year))
  (GET "/random-location" [] (mark-loc-page/random-location-page))
  (POST "/update-location" req
        (let [updated-data (loc-param/data-for-update req)]
          (location/update-location updated-data)
          (resp/redirect "/random-location")))
  (GET "/record/:rec-hash" [rec-hash] (record-page/record-page rec-hash))
  (GET "/coll/:coll-name" [coll-name] (rec-coll-page/content coll-name))
  (GET "/set-link/:rec-hash/to/:coll-name" [rec-hash coll-name]
       (link-records/set-link-page rec-hash coll-name))
  (GET "/explain-weights" [] (explain-weights-page/content))
  (route/not-found "Page not found"))

(def app
  (-> (wrap-resource #'main-routes "public")
      (wrap-with-logger)
      (wrap-base-url)
      (wrap-reload)))

(defn -main [& _args]
  (logging/info "Started")
  (run-jetty #'app {:port 3000}))

