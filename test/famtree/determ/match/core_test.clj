(ns famtree.determ.match.core-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.determ.match.core :refer [if-1-only]]))

(deftest if-1-only-test
  (is (= :a (if-1-only [:a])))
  (is (nil? (if-1-only [])))
  (is (nil? (if-1-only [:a :b]))))

;; TODO: Tests for find-single-match
