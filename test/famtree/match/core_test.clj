(ns famtree.match.core-test
  (:require [clojure.test :refer :all]
            [famtree.match.core :refer :all]))

(deftest if-1-only-test
  (is (= :a (if-1-only [:a])))
  (is (nil? (if-1-only [])))
  (is (nil? (if-1-only [:a :b]))))

;; TODO: Tests for find-single-match
