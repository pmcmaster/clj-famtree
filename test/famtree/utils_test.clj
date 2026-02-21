(ns famtree.utils_test
  (:require [clojure.test :refer :all]
            [famtree.utils :as u]))

(deftest test-counts-by-grouping 
  (is (= (u/counts-by-grouping {:a [1 2 3] :b [5 6]})
         '( [:a 3] [:b 2] ))))

(deftest test-if-1-only
  (is (nil? (u/if-1-only [:a :b])))
  (is (= (u/if-1-only [:a]) :a)))

