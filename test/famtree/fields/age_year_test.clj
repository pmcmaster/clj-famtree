(ns famtree.fields.age-year-test
  (:require [clojure.test :refer :all]
            [famtree.fields.age-year :as age-year]))

(deftest between-years?-test
  (is (not (age-year/between-years? [1995 1997] 1994)))
  (is (age-year/between-years? [1995 1997] 1995))
  (is (age-year/between-years? [1995 1997] 1997))
  (is (not (age-year/between-years? [1995 1997] 1998)))
  (is (nil? (age-year/between-years? nil 1998))))

(deftest ranges-overlap?-test
  (is (age-year/ranges-overlap? [1 2] [2 3]))
  (is (age-year/ranges-overlap? [1 2] '(2 2)))
  (is (age-year/ranges-overlap? '(30 30) [16 65]))
  (is (age-year/ranges-overlap? '(16 65) [33 35]))
  (is (not (age-year/ranges-overlap? [3 13] [14 25])))
  (is (not (age-year/ranges-overlap? [22 13] [12 5])))
  (is (nil? (age-year/ranges-overlap? [1 2] nil))
  (is (nil? (age-year/ranges-overlap? nil [1 2])))))

(deftest est-birth-range-from-age-test
  (is (= (age-year/est-birth-range-from-age 17 1997) [1979 1980]))
  (is (= (age-year/est-birth-range-from-age 0 1997) [1996 1997]))
  (is (nil? (age-year/est-birth-range-from-age 2 nil)))
  (is (nil? (age-year/est-birth-range-from-age nil 2)))
  (is (nil? (age-year/est-birth-range-from-age nil nil))))

(deftest est-age-at-year-test
  (is (= (age-year/est-age-at-year [1990 1991] 2000) [9 10]))
  (is (nil? (age-year/est-age-at-year nil 2000))))

