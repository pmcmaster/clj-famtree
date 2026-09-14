(ns famtree.server.pages.common
  (:require [hiccup.core :as h]))

(defn oat-header
  "Header elements for JS and CSS styling using the 'oat' UI libraries
  details at: https://oat.ink"
  []
  (h/html
    ;; https://oat.ink
    [:script {:src "/oat.min.js" :defer true}]
    [:link {:rel "stylesheet" :href "/oat.min.css"}]
    [:link {:rel "stylesheet" :href "/site-default.css"}]))
