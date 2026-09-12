(ns famtree.fields.marriage-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.fields.marriage :as marriage]))

(deftest surnames-before-after-test
  (is (= (marriage/surnames-before-after "F" "SMITH" "JONES")
         ["SMITH" "JONES"]))
  (is (= (marriage/surnames-before-after "M" "SMITH" "JONES")
         ["SMITH" "SMITH"]))
  (is (= (marriage/surnames-before-after nil "SMITH" "JONES")
         ["SMITH" "SMITH"]))
  (is (= (marriage/surnames-before-after "----" "SMITH" "JONES")
         ["SMITH" "SMITH"])))

(def mock-record
  {:forename "DAVID ALEC"
   :surname "SMITH-DAVIS"
   :spouse-forename "SUZIE JANE"
   :spouse-surname "SUZIESON"})

(deftest names-test
  (is (= (marriage/names mock-record)
         ["DAVID" "SMITH-DAVIS" "SUZIE" "SUZIESON"])))

