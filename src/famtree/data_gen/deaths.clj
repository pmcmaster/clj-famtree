(ns famtree.data-gen.deaths
  (:require [famtree.data-gen.synthetic-records-store :as store]))

; Simulate people dying. Values used are quite sensitive in relation to those
; use for birth rates. If one is changed, likely need to change the other to
; avoid population explosion or collapse

(defn chance-of-death
  "What is the annual chance that someone dies at a given age?"
  [age]
  (cond (< age 2) 0.2
        (< age 55) 0.02
        (< age 75) 0.3
        (< age 90) 0.8
        :else 1))

(defn simulate-deaths
  "Run through the population and take out people who die"
  [year population]
  (filter
    #(if (< (rand) (chance-of-death (:age %)))
       (do (println % "dies")
           (store/add-record :death year %)
           false)
       true)
    population))

