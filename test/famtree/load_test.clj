(ns famtree.load-test
  (:require [clojure.test :refer :all]
            [famtree.load :as l]))

(deftest test-field-name-to-keyword
  (testing "Tests the field-to-keyword conversion"
    (is (= (l/field-name-to-keyword "sdfdD") :sdfdd))
    (is (= (l/field-name-to-keyword "Mother's Maiden Name") :mm-name))
    (is (= (l/field-name-to-keyword "County / City") :county-city))
    (is (= (l/field-name-to-keyword "This Has Spaces") :this-has-spaces))))

(deftest test-parse-int-or-nil
  (is (= (l/parse-int-or-nil "234") 234))
  (is (nil? (l/parse-int-or-nil "s2324"))))

(deftest test-parse-field-value
  (is (= (l/parse-field-value :blah "123") "123"))
  (is (= (l/parse-field-value :age-something "27") 27))
  (is (= (l/parse-field-value :year "1984") 1984))
  (is (nil? (l/parse-field-value :year "something-not-num1984")))) 

(deftest test-row-to-map
  (let [field-keys [:one :two :year]
        row-data ["First text" "Second text" "1969"]
        expected-output {:one "First text" :two "Second text" :year 1969}]
    (is (= (l/row-to-map row-data field-keys) expected-output))))

(deftest test-data-as-map
  (let [row1 ["Name" "Place" "Age-Of-Dog"]
        row2 ["Bob" "Moon" "4"]
        row3 ["Chip" "Space" "---"]
        data [row1 row2 row3]
        expected [{:name "Bob" :place "Moon" :age-of-dog 4} {:name "Chip" :place "Space" :age-of-dog nil}]]
    (is (= (l/data-as-map data) expected))))
    