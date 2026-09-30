(ns famtree.data-gen.births
  (:require [famtree.data-gen.people :as people]
            [famtree.data-gen.names :as names]
            [famtree.data-gen.synthetic-records-store :as store]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

; Simulate births of people. They are born in the same location as their
; parent, and have age zero to begin with
; There is only a relation to one parent, which may be a mother or father, not
; to both

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
  [year person]
  (when (< (rand) (chance-of-child (:age person)))
    (println person "has a child")
    (let [new-baby (random-baby (:sname person) (:location person))]
      (store/add-record :birth year [person new-baby])
     new-baby)))

(defn simulate-births
  "Builds up a collection of new children and combines them with
  `population`"
  [year population]
  (let [new-children-or-nils (map
                               #(child-for year %)
                               population)
        new-children (filter some? new-children-or-nils)]
    (concat population new-children)))

(def header-row
  ["Surname" "Forename" "Mother's Maiden Name" "Gender"
   "Year" "Ref" "RD Name"])

(defn record-to-csv-order
  "Convert the info stored for an event into the right shape for writing to 
  CSV file for this record type."
  [[year [_parent-record child-record]]]
  ;; TODO Parent record is currently ignored. Hook up adding mother's maiden
  ;; name (if that is stored for a mother) in some cases? "Real" data does not
  ;; always have info for this field anyway."
  [(:sname child-record) (:fname child-record) "-----" (:gender child-record)
   year (:id child-record) (:location child-record)])

(def filename "data/births.csv")

(defn write-csv
  "Write header row and data for death records"
  []
  (when (.exists (io/file filename))
    (println "Exiting as " filename "file exists")
    (System/exit 0))
  (io/make-parents filename)
  (let [birth-records (get @store/record-store :birth)]
    (with-open [writer (io/writer filename)]
     (csv/write-csv writer [header-row] :separator \tab)
     (let [records-for-csv (map record-to-csv-order birth-records)]
       (csv/write-csv writer records-for-csv :separator \tab)))))

