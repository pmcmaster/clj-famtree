(ns famtree.match.against-test
  (:import [famtree.records.core BirthRec DeathRec MarriageRec CensusRec])
  (:require [clojure.test :refer :all]
            [famtree.match.protocols :as match-p]
            ))

; (defrecord BirthRec [surname forename mm-name gender year rec-ref rd-name])
(def birth1
  (BirthRec. "SMITH" "ALAN" "JENKINS" "M" 1832 "b-ref" "b-rdn"))

; (defrecord DeathRec [surname forename age-at-death mm-name gender year rec-ref rd-name])
(def death1
  (DeathRec. "SMITH" "ALAN" 56 "JENKINS" "M" 1888 "d-ref" "d-rdn"))

(def death2
  (DeathRec. "SMITH" "ELIZABETH" 58 "HASTINGS" "F" 1890 "d-ref" "d-rdn"))

; (defrecord MarriageRec [surname forename spouse-surname spouse-forename year rec-ref rd-name])
(def marriage1
  (MarriageRec. "SMITH" "ALAN" "JONES" "ELIZABETH" 1852 "m-ref" "m-rdn"))

; (defrecord CensusRec [surname forename year gender age-at-census rec-ref rd-name county-city])

(defn match-same-succeeds
  "Match two records against each other - they are expected to match"
  [source-rec other-rec]
  (let [match-same-fn (match-p/match-same-fn source-rec)
        match-result (match-same-fn source-rec [other-rec])]
    (is (= 1 (count match-result)))
    (is (= other-rec
           (first match-result)))))

(defn match-same-both-ways
  "Records are able to match against each other in both directions"
  [rec1 rec2]
  (match-same-succeeds rec1 rec2)
  (match-same-succeeds rec2 rec1))

(deftest test-match-death-to-birth
  (match-same-both-ways death1 birth1))

(deftest test-match-birth-to-death
  (match-same-both-ways birth1 death1))

(deftest test-match-death-to-marriage
  (match-same-both-ways death1 marriage1)
  (match-same-both-ways death2 marriage1))

(deftest test-match-marriage-to-death
  (match-same-both-ways marriage1 death1)
  (match-same-both-ways marriage1 death2))

(deftest test-match-birth-to-marriage
  (match-same-both-ways birth1 marriage1))

(deftest test-match-marriage-to-birth
  (match-same-both-ways marriage1 birth1))

