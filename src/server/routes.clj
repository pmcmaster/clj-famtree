(ns server.routes
  (:use compojure.core
        server.views
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.resoonse :as response]))

;; TODO: Remove this module? Unused? Move stuff from server.main?
()
