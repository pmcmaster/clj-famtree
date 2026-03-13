(ns famtree.records.names_test
  (:require [clojure.test :refer :all]
            [famtree.records.names :as n]))

 ;TODO Assertions here are specific to current data used

(deftest core-surnames-test
  (is (= 10
         (count n/core-surnames))))

(deftest first-names-by-gender-test
  (is (= ["M", "F", "-----"]
         (keys n/first-names-by-gender)))
  (is (= 258
         (count (get n/first-names-by-gender "M"))))
  (is (= 482
         (count (get n/first-names-by-gender "F")))))

(deftest female-forename?-test
  (is (n/female-forename? "ELIZABETH")))

(deftest male-forename?-test
  (is (n/male-forename? "PETER")))

(deftest gender-for-name-test
  (is (= "M"
         (n/gender-for-name "PETER")))
  (is (= "F"
         (n/gender-for-name "CHARLOTTE")))
  (is (nil? (n/gender-for-name "AGNES"))))

