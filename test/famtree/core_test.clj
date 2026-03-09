(ns famtree.core-test
  (:require [clojure.test :refer :all]
            [famtree.records.collections :as rec-colls]
            [famtree.core :refer :all]))


(def mock-results
  {[:key1-1 :key1-2] {
                      :rec1 :rec2
                      :rec3 :rec4}
   [:key-1-2 :key2-2] {
                       :rec4 :rec5
                       :rec6 :rec1
                       :rec7 :rec8}
   [#'rec-colls/marriages :key3-1] {:m1 :m2}
   [:key4-1 #'rec-colls/marriages] {:n1 :n2}})

(deftest test-process-non-marriage-results
    (is (= (process-non-marriage-results mock-results)
           #{#{:rec4 :rec5 :rec3} #{:rec1 :rec2 :rec6} #{:rec7 :rec8}})))

(deftest test-relates-to-either
  (is (= (relates-to-either #{:a} :a :b)
         #{:a}))
  (is (= (relates-to-either #{:a :b :c} :z :b)
         #{:a :b :c})
  (is (nil? (relates-to-either #{:z} :a :b)))))

(deftest test-update-and-link
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

