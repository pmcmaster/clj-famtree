(ns famtree.determ.match.against-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.determ.match.protocols :as match-p]
            [famtree.determ.match.against]
            [famtree.fields.names]
            [famtree.records :refer [->BirthRec
                                     ->DeathRec
                                     ;; ->CensusRec
                                     ->MarriageRec]]))

(def core-surnames-mock
  "Use only this as the 'core surname' for testing"
  #{"SMITH"})

; (defrecord BirthRec [surname forename mm-name gender year rec-ref rd-name])
(def birth-alan-smith
  (->BirthRec "SMITH" "ALAN" "JENKINS" "M" 1832 "b-ref" "b-rdn"))

(def birth-eliz-smith
  (->BirthRec "SMITH", "ELIZABETH" "DAVIS" "F" 1832 "b-ref" "b-rdn"))

;; (defrecord DeathRec
;; [surname forename age-at-death mm-name gender year rec-ref rd-name])
(def death-alan-smith
  (->DeathRec "SMITH" "ALAN" 56 "JENKINS" "M" 1888 "d-ref" "d-rdn"))

(def death-eliz-smith
  (->DeathRec "SMITH" "ELIZABETH" 58 "HASTINGS" "F" 1890 "d-ref" "d-rdn"))

(def death-eliz-jones
  (->DeathRec "JONES" "ELIZABETH" 58 "HASTINGS" "F" 1890 "d-ref" "d-rdn"))

;; (defrecord MarriageRec
;; [surname forename spouse-surname spouse-forename year rec-ref rd-name])
(def marriage-alan-smith-to-eliz-jones
  (->MarriageRec "SMITH" "ALAN" "JONES" "ELIZABETH" 1852 "m-ref" "m-rdn"))

(def marriage-alan-jones-to-eliz-smith
  (->MarriageRec
    "SMITH" "ELIZABETH" "JONES" "ALAN" 1852 "m-ref" "m-rdn"))

;; (defrecord CensusRec
;; [surname forename year gender age-at-census rec-ref rd-name county-city])

;; TODO: No tests for census records yet

(defn match-same-succeeds
  "Match two records against each other - they are expected to match"
  [source-rec other-rec]
  (with-redefs [famtree.fields.names/core-surnames core-surnames-mock]
    (let [match-same-fn (match-p/match-same-fn source-rec)
          match-result (match-same-fn source-rec [other-rec])]
      (is (= 1 (count match-result)))
      (is (= other-rec
             (first match-result))))))

(defn match-same-both-ways
  "Records are able to match against each other in both directions"
  [rec1 rec2]
  (match-same-succeeds rec1 rec2)
  (match-same-succeeds rec2 rec1))

(defn match-same-fails
  "Match two records against each other - they are expected to NOT match"
  [source-rec other-rec]
  (with-redefs [famtree.fields.names/core-surnames core-surnames-mock]
    (let [match-same-fn (match-p/match-same-fn source-rec)
          match-result (match-same-fn source-rec [other-rec])]
      (is (empty? match-result)))))

(defn match-neither-both-ways
  "Records are able to match against each other in both directions"
  [rec1 rec2]
  (match-same-fails rec1 rec2)
  (match-same-fails rec2 rec1))

(deftest match-death-to-birth-test
  (match-same-both-ways death-alan-smith birth-alan-smith))

(deftest match-birth-to-death-test
  (match-same-both-ways birth-alan-smith death-alan-smith))

(deftest match-death-to-marriage-test
  (match-same-both-ways death-alan-smith marriage-alan-smith-to-eliz-jones)
  (match-neither-both-ways death-eliz-smith marriage-alan-smith-to-eliz-jones))

(deftest match-marriage-to-death-test
  (match-same-both-ways marriage-alan-smith-to-eliz-jones death-alan-smith))

(deftest record-after-marriage-test
  (match-neither-both-ways marriage-alan-jones-to-eliz-smith death-eliz-smith))

(deftest match-birth-to-marriage-test
  (match-same-both-ways birth-alan-smith marriage-alan-smith-to-eliz-jones))

(deftest match-marriage-to-birth-test
  (match-same-both-ways marriage-alan-smith-to-eliz-jones birth-alan-smith))

(deftest match-woman-marriage-to-birth-test
  (match-same-both-ways marriage-alan-jones-to-eliz-smith birth-eliz-smith))

