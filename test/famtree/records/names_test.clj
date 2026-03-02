(ns famtree.records.names_test
  (:require [clojure.test :refer :all]
            [famtree.records.names :as n]))

 ;TODO Assertions here are specific to current data used

(deftest test-all-surnames
  (is (= 10
         (count (n/all-surnames-except-marriage-recs)))))

(deftest test-first-names-by-gender
  (is (= ["M", "F", "-----"]
         (keys n/first-names-by-gender)))
  (is (= 258
         (count (get n/first-names-by-gender "M"))))
  (is (= 482
         (count (get n/first-names-by-gender "F")))))

(deftest test-female-forename?
  (is (n/female-forename? "ELIZABETH")))

(deftest test-male-forename?
  (is (n/male-forename? "PETER")))

(deftest test-gender-for-name
  (is (= "M"
         (n/gender-for-name "PETER")))
  (is (= "F"
         (n/gender-for-name "CHARLOTTE")))
  (is (nil? (n/gender-for-name "AGNES"))))

(deftest test-names-by-gender
  (is (= {}
         (n/names-by-gender "ROBERT" "LILIAS")))
  (is (= {"M" "DAVID" "F" "SUSAN"}
         (n/names-by-gender "DAVID" "SUSAN"))))

(deftest test-all-surnames-except-marriage-recs
  (is (= 10
         (count (n/all-surnames-except-marriage-recs)))))

