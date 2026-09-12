(ns famtree.probab.comp-basic)

;; Basic low-level comparison functions which operate on field content

(defn exact-matches-field?
  "Does a given field match for two records?"
  [field rec1 rec2]
  (= (field rec1) (field rec2)))

