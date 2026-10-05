(ns famtree.fields.names-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.fields.names :as n]
            [famtree.data-test-helper :as data-helper]))

;; Some of the tests here (those that use data-helper) read records from the
;; data_sample directory, and depend on the data in those files

(deftest first-part-of-hyphenated-name-test
  (is (= (n/first-part-of-hyphenated-name "SMITH-JENKINS")
         "SMITH"))
  (is (= (n/first-part-of-hyphenated-name "SMITH")
         "SMITH"))
  (is (= (n/first-part-of-hyphenated-name "SMITH JONES")
         "SMITH"))
  (is (nil? (n/first-part-of-hyphenated-name nil)))
  (is (nil? (n/first-part-of-hyphenated-name "-----"))))

(deftest core-surnames-test
  (data-helper/do-with-test-data
    (is (= 2
           (count (n/core-surnames))))))

(deftest first-forename-from-rec-test
  (is (= (n/first-forename-from-rec {:forename "Bob John"}) "Bob")))

(deftest is-core-surname-test
  (data-helper/do-with-test-data
    (is (n/is-core-surname "SMITH"))))

(deftest surname-matches-surnames-test
  (data-helper/do-with-test-data
    (is (n/surname-matches-surnames "SMITH" ["SMYTH" "DAVIS"]))
    (is (not (n/surname-matches-surnames "SMITH" ["DAVIS"])))
    (is (n/surname-matches-surnames "DAVIS" ["MACMASTER" "DAVIS"]))
    (is (not (n/surname-matches-surnames "SMITH" [])))))

(deftest surnames-match-test
  (data-helper/do-with-test-data
    (is (n/surnames-match "SMITH" "SMYTH"))
    (is (not (n/surnames-match "SMITH" "JONES")))
    (is (n/surnames-match "SMITH" "SMITH"))))
