(ns famtree.fields.names-test
  (:require [clojure.test :refer :all]
            [famtree.fields.names :as n]))

;; TODO: Assertions here are specific to current data used
;; Tests should not depend on that data

(deftest first-part-of-hyphenated-name-test
  (is (= (n/first-part-of-hyphenated-name "SMITH-JENKINS")
         "SMITH"))
  (is (= (n/first-part-of-hyphenated-name "SMITH")
         "SMITH"))
  (is (= (n/first-part-of-hyphenated-name "SMITH JONES"))
      "SMITH")
  (is (nil? (n/first-part-of-hyphenated-name nil)))
  (is (nil? (n/first-part-of-hyphenated-name "-----"))))

(deftest core-surnames-test
  (is (= 10
         (count n/core-surnames))))

(deftest first-forename-from-rec-test
  (is (= (n/first-forename-from-rec {:forename "Bob John"}) "Bob")))

(deftest is-core-surname-test
  (is (n/is-core-surname "MCMASTER")))

(deftest surname-matches-surnames-test
  (is (n/surname-matches-surnames "MCMASTER" ["MACMASTER" "DAVIS"]))
  (is (not (n/surname-matches-surnames "MCMASTER" ["DAVIS"])))
  (is (n/surname-matches-surnames "DAVIS" ["MACMASTER" "DAVIS"]))
  (is (not (n/surname-matches-surnames "MCMASTER" []))))

(deftest surnames-match-test
  (is (n/surnames-match "MCMASTER" "MACMASTER"))
  (is (not (n/surnames-match "SMITH" "JONES")))
  (is (n/surnames-match "SMITH" "SMITH")))
