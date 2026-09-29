(ns famtree.data-gen.people
  (:require [famtree.data-gen.names :as names]
            [famtree.data-gen.location :as location]))

;; Common functions for generating people and names
;; Limited number of names and locations, so will get some identity conflicts
;; if we don't also have some kind of unique ID for each person who is 'alive'
(def person-id (atom 0))


(defn new-person
  "Generate a new person - all new person generation should call this
  so that person-id gets set appropriately"
  [surname fname gender age location]
  (swap! person-id inc)
  {:fname fname
   :sname surname
   :gender gender
   :age age
   :location location
   :id @person-id})

(defn new-woman
  "Generate a new woman with the specified `surname` `location` and `age`"
  [surname location age]
  (new-person surname
              (rand-nth names/female-names)
              "F"
              age
              location))

(defn random-person
  "Generate a person in a random location with a random age, with the specified
  `surname`"
  [surname]
  (let [gender (rand-nth ["M" "F"])]
    (new-person surname
                (names/random-name gender) ; fname
                gender
                (rand-nth (range 0 90)) ; age
                (location/random-location))))

(defn random-core-person
  "Generate a person in a random location with a random age, with the core
  surname"
  []
  (random-person names/core-surname))


