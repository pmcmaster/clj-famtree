(ns famtree.core-test
  (:require [clojure.test :refer :all]
            [famtree.core :refer :all]))

(deftest update-and-link-test
  (let [start-set #{#{:a :b}
                    #{:d :e}}]
    (is (= (update-and-link start-set [:b :c])
           #{#{:a :b :c}
             #{:d :e}}))
    (is (= (update-and-link start-set [:a :c])
           #{#{:a :b :c}
             #{:d :e}}))
    (is (= (update-and-link start-set [:x :y])
           #{#{:a :b}
             #{:d :e}
             #{:x :y}}))))

