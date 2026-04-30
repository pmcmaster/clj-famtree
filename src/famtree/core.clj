(ns famtree.core
  "Main entry point for project
  Runs the main matching process and triggers output of results"
  (:require [famtree.determ.core :as determ]))

(defn households
  "Entry point: Show determinstic linking of possible households"
  ; clojure -X famtree.core/households
  [& args]
  (determ/households))

(defn link-same-and-parents
  "Entry point: Determinstically link together records which relate to the same person
  Show summary of contradiatory situations. Basic attempt at linking children to parents"
  ; clojure -X famtree.core/link-same-and-parents
  [& args]
  (determ/link-same-and-parents))


