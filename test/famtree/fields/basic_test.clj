(ns famtree.fields.basic-test
  (:require [clojure.test :refer :all]
            [famtree.fields.basic :as f]))

(deftest no-data?-test
  (is (nil? (f/no-data? 1)))
  (is (nil? (f/no-data? "abc")))
  (is (= (f/no-data? "---") "---")))

(deftest first-word-from-field-test
  (is (= (f/first-word-from-field :foo {:foo "Flip-flop"}) "Flip-flop")) 
  (is (= (f/first-word-from-field :foo {:foo "Flip flop"}) "Flip")))

(deftest =-and-has-data?-test
  (is (not (f/=-and-has-data? "--" 1)))
  (is (not (f/=-and-has-data? 1 "--")))
  (is (f/=-and-has-data? "hi" "hi"))
  (is (f/=-and-has-data? 1 1)))

(deftest =-or-no-data?-test
  (is (= (f/=-or-no-data? "--" "--") "--"))
  (is (f/=-or-no-data? 1 1))
  (is (f/=-or-no-data? "foo" "foo"))
  (is (= (f/=-or-no-data? "foo" "--") "--"))
  (is (= (f/=-or-no-data? "--" "foo") "--"))
  (is (not (f/=-or-no-data? "foo" "bar"))))
