(ns famtree.fields.marriage
  "Functions for deriving fields based on marriage records"
  (:require [famtree.fields.basic :as fields]
            [famtree.fields.names :as names]
            [famtree.fields.gender :as gender]))

(def age-range
  "Range of ages when someone might get married.
  Upper limit is pretty arbitrary."
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

(defn female-surname-from-marriage
  "What is the surname of the female participant in a marriage"
  ;; TODO: Needs to handle case of not being able to determine gender for
  ;; either party in the marriage?
  [marriage-rec]
  (let [person-gender (gender/infer-gender-from-forename-pair
                        (:forename marriage-rec)
                        (:spouse-forename marriage-rec))]
    (cond
      (= person-gender gender/female) (:surname marriage-rec)
      (= person-gender gender/male) (:spouse-surname marriage-rec))))

(defn matches-surname-at-date
  "Does the data for this marriage (surnames and year) match up with the
  `event-year` and `event-surname`"
  ;; TODO: If `event-year` is = to marriage-year then event may have occurred
  ;; before OR after the marriage, so need to add logic to consider either
  ;; surname
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
  They are going to be JONES both before and after marriage, so will never be
  a SMITH. Concept of 'core name' here relates to the fact that all of this
  operates on a collection of records gathered for one surname only, though it
  obviously includes people who marry into or out of that family. See more
  info in famtree.fields.names/core-surnames"
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

