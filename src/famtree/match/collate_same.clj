(ns famtree.match.collate-same)

;; Collage pairs of records into sets where the pairs share a common record.
;; e.g., [[:a :b] [:b :c]] becomes [#{:a :b :c}]

(defn find-subset-containing-either
  "Find the first set in seq `xs` containing either `val1` or `val2`"
  [val1 val2 xs]
  (let [target-set (hash-set val1 val2)]
    (first (filter #(some target-set %) xs))))

(defn update-results-set
  "Remove `old-set` and add `new-set` to enclosing set `containing-set`"
  [containing-set old-set new-set]
  (-> containing-set
      (disj old-set)
      (conj new-set)))

(defn update-and-link
  "Add source-rec and dest-rec to the set of sets of existing records
  They should both be added to the set which already contain one of the
  records"
  [set-of-record-sets [source-rec dest-rec]]
  (if-let [existing-set (find-subset-containing-either
                          source-rec dest-rec
                          set-of-record-sets)]
    (->> (conj existing-set source-rec dest-rec)
     (update-results-set set-of-record-sets existing-set)) 
    (conj set-of-record-sets (hash-set source-rec dest-rec))))

(defn pairs-to-sets
  "Build up a set of sets, where each contained set is entirely records relating
  to the same person
  `matches-by-type` is a map with keys being a source and destination reference
  for the record collection values are a pair of records
  (source-rec and dest-rec)"
  [matches-by-type]
  (->> matches-by-type
       vals
       (reduce concat [])
       (reduce update-and-link #{})))
