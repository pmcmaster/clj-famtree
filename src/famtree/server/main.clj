(ns famtree.server.main
  (:require [clojure.tools.logging :as logging]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.logger :refer [wrap-with-logger]]
            [ring.util.response :as resp]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [famtree.server.pages.main :as main-page]
            [famtree.server.pages.record :as record-page]
            [famtree.server.pages.record-collection :as rec-coll-page]
            [famtree.server.pages.mark-location :as mark-loc-page]
            [famtree.server.pages.link-records :as link-records]
            [famtree.server.pages.location-list :as loc-list-pages]
            [famtree.server.pages.explain-weights :as explain-weights-page]
            [famtree.server.location-param :as loc-param]
            [famtree.server.location :as location]
            [hiccup.middleware :refer (wrap-base-url)]))

;; Main server routes definition.

; Run with the 'server' alias:
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
  (->
    ;; Serves static resources
    (wrap-resource #'main-routes "public")
    (wrap-with-logger)
    (wrap-base-url)
    ;; Dynamic reloading for development
    (wrap-reload)))

(defn -main
  "Main server process, starts jetty server process"
  [& _args]
  (logging/info "Started")
  (run-jetty #'app {:port 3000}))

