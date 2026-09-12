(ns famtree.mapping-cljs.map-default
  (:require [famtree.mapping-cljs.mapping-main :as mapping]))

(defn setup-map
  "Sets up map at a suitable location and zoom level for a 'default'-sized map"
  []
  (mapping/setup-map [56.71 -4.1] 7))

