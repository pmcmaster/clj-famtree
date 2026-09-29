(ns famtree.data-gen.moving
  (:require [famtree.data-gen.location :as location]))

;; Moving is not tracked in a record, but people do move which shows up
;; indirectly in the surrounding records

(defn chance-of-moving
  "Chance that someone moves location when they are a given `age`. Does not
  properly model that a family is more likely to all move together"
  [age]
  (cond (< 16 age 65) 0.1
        :else 0.01))

(defn simulate-moving
  "Simulate people moving from their current location to another"
  [year population]
  (let [people-moving (filter #(< (rand) (chance-of-moving (:age %)))
                              population)
        moved-people (map #(assoc % :location (location/random-location))
                          people-moving)]
    (println (count people-moving) "move in" year)
    (->> (remove (set people-moving) population)
         (concat moved-people))))

