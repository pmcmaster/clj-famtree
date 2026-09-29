(ns famtree.data-gen)

;; Generate some synthetic data which mirrors the structure of downloaded
;; records

;; Limited number of names and locations, so will get some identity conflicts
;; if we don't also have some kind of unique ID for each person who is 'alive'
(def person-id (atom 0))

(def core-surname "SMITH")

(def alternate-core-surname-spellings
  #{"SMYTH" "SMYTHE" "SMIT"})

(def male-names
  ["Jim" "James" "Jacob" "Joseph" "Bob" "Jenkins" "Shawn" "Paulo"])

(def female-names
  ["Susan" "Sheila" "Seonaid" "Selina" "Briony" "Sharon" "Sarah" "Suzanne"])

(defn random-location
  []
  (let [location-number (rand-nth (range 30))]
    (str "Place-" location-number)))

(defn random-name
  "Random name for gender. Returns pair of first name and surname"
  [gender]
  (if (= gender "M")
    (rand-nth male-names)
    (rand-nth female-names)))

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
              (rand-nth female-names)
              "F"
              age
              location))

(defn random-person
  "Generate a person in a random location with a random age, with the specified
  `surname`"
  [surname]
  (let [gender (rand-nth ["M" "F"])]
    (new-person surname
                (random-name gender) ; fname
                gender
                (rand-nth (range 0 90)) ; age
                (random-location))))

(defn random-baby
  "Generate a person in a random location with a random age, with the specified
  `surname`"
  [surname location]
  (let [gender (rand-nth ["M" "F"])]
    (new-person surname
                (random-name gender) ; fname
                gender
                0 ; age
                location)))

;; RECORD RANDOMISATION

(defn missing-record-chance
  "Chance that a record is missing for a given `year`"
  [year]
  (cond (< year 1800) 0.5
        (< year 1850) 0.4
        (< year 1900) 0.3
        (< year 1950) 0.1
        :else 0.01))

(defn miss-record
  "Should an event occurring in `year` not be recorded?"
  [year]
  (<= (rand) (missing-record-chance year)))

;; AGEING

(defn simulate-ageing
  "Increment everyone's age by one year"
  [population]
  (println "Everyone ages one year")
  (map #(assoc % :age (inc (:age %)))
       population))

;; DEATH

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
           ;; TODO: Generate death record HERE
           false)
       true)
    population))

;; BIRTH

(defn chance-of-child
  "Annual chance a person will have a child at `age`."
  [age]
  (cond (< 18 age 55) 0.14
        :else 0))

(defn child-for
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

;; CENSUS

;; Years when a census was completed in Scotland, and for which
;; (as of 2026) the data is available)
(def census-years #{1841 1851 1861 1871 1881 1891 1901 1911 1921})

(defn simulate-census
  "Create census records if it is a census year. Does not actually
  affect the population at all, so just returns that unchanged"
  [year population]
  (when (contains? census-years year)
    (println year "is a census year"))
  population)

;; MARRIAGE

(defn chance-of-marriage
  "Annual chance that someone gets married. It's arbitarily 1/4 of the chance
  of having a child"
  [age]
  (cond (< 18 age 70) 0.035
        :else 0))

(defn new-person-for-man-marrying
  [year man-getting-married]
  ;; TODO: Generate marriage record HERE
  (let [spouse (new-woman (:sname man-getting-married)
                          (:location man-getting-married)
                          (:age man-getting-married))]
    (println spouse "created by marriage")
    spouse))

(defn woman-gets-married
  [year woman-getting-married]
  ;; TODO: Generate marriate record HERE
  (println woman-getting-married "leaves tracked population due to marriage"))

(defn simulate-marriages
  "Simulate people getting married (primarily to generate marriage records).
  Women getting married lose their 'core surname' so drop out of the tracked
  population. Men getting married marry a person from outside the tracked
  population, which adds a new person to the population. This new person is the
  same age as the person they marry.
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

;; MOVING (not tracked in a record, but people do move which shows up
;; indirectly in the surrounding records))

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
        moved-people (map #(assoc % :location (random-location))
                          people-moving)]
    (println (count people-moving) "move in" year)
    (->> (remove (set people-moving) population)
         (concat moved-people))))

;; TOP-LEVEL FUNCTIONS

(defn print-population-stats
  "Print out population stats. Needs to return the population so that this
  can be used with ->>"
  [year population]
  (println year "- population is" (count population))
  population)

(defn simulate-time-from
  "Generate some events for a year. Aribtrary cut-off at 1960"
  [year population]
  (when (< year 1960)
    (let [updated-population (->> population
                                  (print-population-stats year)
                                  simulate-ageing
                                  (print-population-stats year)
                                  (simulate-moving year)
                                  (print-population-stats year)
                                  (simulate-marriages year)
                                  (simulate-census year)
                                  (simulate-births year)
                                  (simulate-deaths year))]
      (recur (inc year) updated-population))))

(defn initial-population
  "Generate a random starting population of `size`
  These people exist before any records of them do"
  [size population]
  (if (zero? size)
    population
    (recur (dec size) (conj population (random-person core-surname)))))

(defn generate
  "Generate a set of .CSV files for a pretend population of people"
  [& args]
  (let [population (initial-population 400 [])]
    (simulate-time-from 1755 population)))

