(ns famtree.fields_test
  (:require [clojure.test :refer :all]
            [famtree.fields :as f]))

(deftest no-data?-test
  (is (nil? (f/no-data? 1)))
  (is (nil? (f/no-data? "abc")))
  (is (= (f/no-data? "---") "---")))

(deftest first-forename-from-rec-test
  (is (= (f/first-forename-from-rec {:forename "Bob John"}) "Bob")))

(deftest =-and-has-data?-test
  (is (= (f/=-and-has-data? "--" 1) false))
  (is (= (f/=-and-has-data? 1 "--") false))
  (is (= (f/=-and-has-data? "hi" "hi") true))
  (is (= (f/=-and-has-data? 1 1) true)))

(deftest =-or-no-data?-test
  (is (= (f/=-or-no-data? "--" "--") "--"))
  (is (= (f/=-or-no-data? 1 1) true))
  (is (= (f/=-or-no-data? "foo" "foo") true))
  (is (= (f/=-or-no-data? "foo" "--") "--"))
  (is (= (f/=-or-no-data? "--" "foo") "--"))
  (is (= (f/=-or-no-data? "foo" "bar") false)))

(deftest between-years?-test
  (is (= (f/between-years? [1995 1997] 1994)) false)
  (is (= (f/between-years? [1995 1997] 1995)) true)
  (is (= (f/between-years? [1995 1997] 1997)) true)
  (is (= (f/between-years? [1995 1997] 1998)) false)
  (is (nil? (f/between-years? nil 1998))))

(deftest ranges-overlap?-test
  (is (= (f/ranges-overlap? [1 2] [2 3]) true))
  (is (= (f/ranges-overlap? [1 2] '(2 2)) true))
  (is (= (f/ranges-overlap? '(30 30) [16 65]) true))
  (is (= (f/ranges-overlap? '(16 65) [33 35]) true))
  (is (nil? (f/ranges-overlap? [1 2] nil))
  (is (nil? (f/ranges-overlap? nil [1 2])))))

(deftest est-birth-range-from-age-test
  (is (= (f/est-birth-range-from-age 17 1997) [1979 1980]))
  (is (= (f/est-birth-range-from-age 0 1997) [1996 1997]))
  (is (nil? (f/est-birth-range-from-age 2 nil)))
  (is (nil? (f/est-birth-range-from-age nil 2)))
  (is (nil? (f/est-birth-range-from-age nil nil))))

(deftest est-age-at-year-test
  (is (= (f/est-age-at-year [1990 1991] 2000) [9 10]))
  (is (nil? (f/est-age-at-year nil 2000))))

