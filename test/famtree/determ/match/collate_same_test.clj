(ns famtree.determ.match.collate-same-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.determ.match.collate-same :refer [update-and-link]]))

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

