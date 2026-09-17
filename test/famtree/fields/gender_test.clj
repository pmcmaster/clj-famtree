(ns famtree.fields.gender-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.fields.gender :refer [first-names-by-gender
                                           female-forename?
                                           male-forename?
                                           gender-for-name
                                           infer-gender-from-forename-pair]]))

;; TODO: Test assertions are tightly tied to the data set I am using.
;; They should not be.

(deftest first-names-by-gender-test
  (is (= #{"M", "F", "-----"}
         (set (keys first-names-by-gender))))
  (is (= 258
         (count (get first-names-by-gender "M"))))
  (is (= 482
         (count (get first-names-by-gender "F")))))

(deftest female-forename?-test
  (is (female-forename? "ELIZABETH")))

(deftest male-forename?-test
  (is (male-forename? "PETER")))

(deftest gender-for-name-test
  (is (= "M"
         (gender-for-name "PETER")))
  (is (= "F"
         (gender-for-name "CHARLOTTE")))
  (is (nil? (gender-for-name "AGNES"))))

(deftest infer-gender-from-forename-pair-test
  (is (= "M"
         (infer-gender-from-forename-pair "PETER" "CHARLOTTE")))
  (is (= "F"
         (infer-gender-from-forename-pair "CHARLOTTE" "PETER")))
  (is (= "M"
         (infer-gender-from-forename-pair "AGNES" "CHARLOTTE")))
  (is (= "F"
         (infer-gender-from-forename-pair "AGNES" "PETER")))
  (is (nil?
        (infer-gender-from-forename-pair "BLAH1" "BLAH2"))))
