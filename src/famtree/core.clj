(ns famtree.core
  "Main entry point for project
  Runs the main matching process and triggers output of results"
  (:require [famtree.determ.core :as determ]
            [famtree.probab.example :as probab-ex]))

;; 'determ' functions do a basic brute-force attempt at deterministic matching
;; of records. This does not give good results, and most of that code will
;; likely be removed eventually.

;; The 'probab' functions do a probabalistic, weighted attempt at finding
;; matches between records. This approach should give better results, and be
;; more flexible for future development.

(defn households
  "Entry point: Show determinstic linking of possible households"
  ; clojure -X famtree.core/households
  [& _args]
  (determ/households))

(defn link-same-and-parents
  "Entry point: Determinstically link together records which relate to the same
  person Show summary of contradiatory situations. Basic attempt at linking
  children to parents in the 'dumbest' way possible"
  ; clojure -X famtree.core/link-same-and-parents
  [& _args]
  (determ/link-same-and-parents))

(defn prob-match-census
  "Entry point: Probabilistically match some sample census records"
  ; clojure -X famtree.core/prob-match-census
  [& _args]
  (probab-ex/match-census))
