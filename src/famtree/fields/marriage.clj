(ns famtree.fields.marriage
  "Functions for deriving fields based on marriage records"
  (:require [famtree.fields.basic :as fields]
            [famtree.fields.names :as names]
            [famtree.fields.gender :as gender]))

(def age-range
  "Range of ages when someone might get married.
  Upper limit is somewhat arbitrary."
  [16 65])

(defn surnames-before-after
  "What is someone's surname after they are married?
  Depends on their gender and who they are marrying
  Returns tuple of `[before-name after-name]`"
  [gender surname-before-marriage partner-surname]
  [surname-before-marriage (if (= gender gender/female)
                             partner-surname
                             surname-before-marriage)])

(defn names
  "Return the names from marriage record, and the names for spouse
  Returns [fname sname sp-fname sp-sname]"
  [marriage-rec]
  [(fields/first-word-from-field :forename marriage-rec)
   (:surname marriage-rec)
   (fields/first-word-from-field :spouse-forename marriage-rec)
   (:spouse-surname marriage-rec)])

(defn matches-surname-at-date
  "Does the data for this marriage (surnames and year) match up with the
  `event-year` and `event-surname`"
  [marriage-year [surname-before-mar surname-after-mar]
   event-year event-surname]
  (cond (< event-year marriage-year) (names/surnames-match
                                       event-surname surname-before-mar)
        (= event-year marriage-year) (names/surname-matches-surnames
                                       event-surname
                                       [surname-before-mar surname-after-mar])
        (> event-year marriage-year) (names/surnames-match
                                       event-surname surname-after-mar)))

(defn possible-core-person
  "Is the primary person in this record never going to be a 'core name' person.
  Example would be a 'JONES', male, when the core surname is 'SMITH'.
  They are going to be JONES both before and after marriage."
  ([marriage-rec]
   (apply possible-core-person (names marriage-rec)))
  ([primary-forename
    primary-surname
    other-forename
    _other-surname]
   (let [primary-gender (gender/infer-gender-from-forename-pair
                          primary-forename
                          other-forename)]
     (or (= primary-gender gender/female)
         (names/is-core-surname primary-surname)))))
