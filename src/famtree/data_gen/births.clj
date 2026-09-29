(ns famtree.data-gen.births
  (:require [famtree.data-gen.people :as people]
            [famtree.data-gen.names :as names]))

; Simulate births of people. They are born in the same location as their
; parents, and have age zero to begin with

(defn random-baby
  "Generate a person in a given `location` with a random age, with the
  specified `surname`"
  [surname location]
  (let [gender (rand-nth ["M" "F"])]
    (people/new-person
      surname
      (names/random-name gender) ; fname
      gender
      0 ; age
      location)))

(defn chance-of-child
  "Annual chance a person will have a child at `age`. This is quite sensitive
  in relation to the death rate, and not changing them together can result
  in population explosion or extinction"
  [age]
  (cond (< 18 age 55) 0.14
        :else 0))

(defn child-for
  "Generate a new child related to `person`"
  [person]
  (when (< (rand) (chance-of-child (:age person)))
    (println person "has a child")
    ;; TODO Generate birth record HERE
    (random-baby (:sname person) (:location person))))

(defn simulate-births
  "Builds up a collection of new children and combines them with
  `population`"
  [year population]
  (let [new-children-or-nils (map
                               #(child-for %)
                               population)
        new-children (filter some? new-children-or-nils)]
    (concat population new-children)))

