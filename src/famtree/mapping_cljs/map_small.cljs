(ns famtree.mapping-cljs.map-small
  (:require [famtree.mapping-cljs.mapping-main :as mapping]))

(defn setup-map
  "Sets up map at a suitable location and zoom level for a 'small' map"
  []
  (mapping/setup-map [57.5 -4.1] 5))

