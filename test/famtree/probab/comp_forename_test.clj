(ns famtree.probab.comp-forename-test
  (:require [clojure.test :refer [deftest is]]
            [famtree.probab.comp-forename :as comp-forename]))
(defn =3dp
  "Are args equal to 3dp"
  [arg1 arg2]
  (< (abs (- arg1 arg2))
     0.0009))

(deftest =3dp-test
  (is (=3dp 1.0 1.0))
  (is (=3dp 1.000 1.0009))
  (is (not (=3dp 1.000 1.001))))

(deftest jaro-match-forename
  (is (= (comp-forename/forename-jaro-match-score
           {:forename "BOB"}
           {:forename "BOB"})
         1.0))
  (is (=3dp (comp-forename/forename-jaro-match-score
           {:forename "Rob"}
           {:forename "Robert"})
         0.8833))
  (is (=3dp (comp-forename/forename-jaro-match-score
           {:forename "Rbt"}
           {:forename "Robert"})
         0.7))
  (is (=3dp (comp-forename/forename-jaro-match-score
           {:forename "Rachel"}
           {:forename "Robert"})
         0.6))
  (is (=3dp (comp-forename/forename-jaro-match-score
           {:forename "Tho"}
           {:forename "Thomas"})
         0.8833))
  (is (< (comp-forename/forename-jaro-match-score
           {:forename "SUSAN"}
           {:forename "MICHAEL"})
         0.45)))

