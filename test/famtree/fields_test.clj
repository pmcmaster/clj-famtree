(ns famtree.fields_test
  (:require [clojure.test :refer :all]
            [famtree.fields :as f]))

(deftest test-est-age-from-death-rec
  (is (= (f/est-birth-from-death-rec {:year 1990 :age-at-death 0} ) 1990))
  (is (= (f/est-birth-from-death-rec {:year 1990 :age-at-death 10} ) 1980))
  (is (nil? (f/est-birth-from-death-rec {:year nil :age-at-death nil} )))
  (is (nil? (f/est-birth-from-death-rec {:year 1990 :age-at-death nil} )))
  (is (nil? (f/est-birth-from-death-rec {:year nil :age-at-death 10} ))))

(deftest test-no-data?
  (is (nil? (f/no-data? 1)))
  (is (nil? (f/no-data? "abc")))
  (is (= (f/no-data? "---") "---")))

(deftest test-first-forename-from-rec
  (is (= (f/first-forename-from-rec {:forename "Bob John"}) "Bob")))

(deftest test-=-and-has-data?
  (is (= (f/=-and-has-data? "--" 1) false))
  (is (= (f/=-and-has-data? 1 "--") false))
  (is (= (f/=-and-has-data? "hi" "hi") true))
  (is (= (f/=-and-has-data? 1 1) true)))

(deftest test-=-or-no-data?
  (is (= (f/=-or-no-data? "--" "--") "--"))
  (is (= (f/=-or-no-data? 1 1) true))
  (is (= (f/=-or-no-data? "foo" "foo") true))
  (is (= (f/=-or-no-data? "foo" "--") "--"))
  (is (= (f/=-or-no-data? "--" "foo") "--"))
  (is (= (f/=-or-no-data? "foo" "bar") false)))

