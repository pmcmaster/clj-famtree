(ns famtree.utils_test
  (:require [clojure.test :refer :all]
            [famtree.utils :as u]))

(deftest counts-by-grouping-test
  (is (= (u/counts-by-grouping {:a [1 2 3] :b [5 6]})
         '( [:a 3] [:b 2] ))))

(deftest if-1-only-test
  (is (nil? (u/if-1-only [:a :b])))
  (is (= (u/if-1-only [:a]) :a)))

