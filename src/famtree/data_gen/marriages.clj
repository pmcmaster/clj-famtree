(ns famtree.data-gen.marriages
  (:require [famtree.data-gen.people :as people]
            [famtree.data-gen.names :as names]
            [famtree.data-gen.synthetic-records-store :as store]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

; Simulate marriage. The way the records I normally use are organised they only
; cover the 'core surname'. This means that when a woman with that core surname
; gets married she will typically get a new surname, and disappear from the
; set of records I have.

; Conversely, the spouse of a man with a core surname will just appear in the
; records when they gain a core surname through marriage, with no record of
; them previously (at least in the records I have).

(defn chance-of-marriage
  "Annual chance that someone gets married. It's arbitarily 1/4 of the chance
  of having a child"
  [age]
  (cond (< 18 age 70) 0.035
        :else 0))

(defn new-woman
  "Generate a new woman with the specified `surname` `location` and `age`"
  [surname location age]
  (people/new-person
    surname
    (rand-nth names/female-names)
    "F"
    age
    location))

(defn new-person-for-man-marrying
  "Generate a new person who `man-getting-married` is marrying. Unrealistically
  they will always have the same age as the man"
  [year man-getting-married]
  (let [spouse (new-woman (:sname man-getting-married)
                          ;; TODO: Generate a random maiden name here
                          (:location man-getting-married)
                          (:age man-getting-married))]
    (store/add-record :marriage-in year [man-getting-married spouse])
    (println spouse "created by marriage")
    spouse))

(defn woman-gets-married
  "When a woman 'leaves' the tracked population by losing her core surname
  through marriage"
  [year woman-getting-married]
  ;; TODO: Need to generate the man being married with a random name, for the
  ;; marriage record
  (store/add-record :marriage-out year [woman-getting-married])
  (println woman-getting-married "leaves tracked population due to marriage"))

(defn simulate-marriages
  "Simulate people getting married (primarily to generate marriage records).
  Does not currently make people who are already married less likely to get
  married again"
  [year population]
  (let [marriage-people (filter #(< (rand) (chance-of-marriage (:age %)))
                                population)
        married-by-gender (group-by :gender marriage-people)
        men-getting-married (get married-by-gender "M")
        new-women (map #(new-person-for-man-marrying year %)
                       men-getting-married)
        women-getting-married (get married-by-gender "F")]
    (doseq [woman women-getting-married]
      (woman-gets-married year woman))
    (->> population
        (remove (set women-getting-married))
        (concat new-women))))

(def header-row
  ["Surname" "Forename" "Spouse Surname" "Spouse Forename" "Year" "Ref"
   "RD Name"])

(defn record-to-csv-order-marriage-in
  "Maps a pair of person records to the correct order for writing to CSV"
  [[year [male-record female-record]]]
  [(:sname male-record) (:fname male-record)
   (:sname female-record) (:fname female-record)
   year (:id male-record) (:location male-record)])

(defn record-to-csv-order-marriage-out
  "Maps a single person record to the correct order for writing to CSV"
  [[year [single-record]]]
  [(:sname single-record) (:fname single-record)
   "Someone" "Else"
   year (:id single-record) (:location single-record)])

(def filename "data/marriages.csv")

(defn write-csv
  "Write header row and data for death records"
  []
  (when (.exists (io/file filename))
    (println "Exiting as" filename "file exists")
    (System/exit 0))
  (io/make-parents filename)
  (let [marriage-in-records (get @store/record-store :marriage-in)
        marriage-out-records (get @store/record-store :marriage-out)]
    (with-open [writer (io/writer filename)]
     (csv/write-csv writer [header-row] :separator \tab)
     (let [in-records-for-csv (map record-to-csv-order-marriage-in
                                   marriage-in-records)
           out-records-for-csv (map record-to-csv-order-marriage-out
                                    marriage-out-records)]
       (csv/write-csv writer in-records-for-csv :separator \tab)
       (csv/write-csv writer out-records-for-csv :separator \tab)))))

