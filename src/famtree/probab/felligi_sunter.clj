(ns famtree.probab.felligi-sunter)

;; Uses Felligi-Sunter method to match records.
;; Details well-explained in talk and linked resources at:
;; https://github.com/oakmac/record-linking-talk/tree/master
;; And more about the method in particular, and the maths at:
;; https://www.robinlinacre.com/probabilistic_linkage/

(defn match-fn-name-to-short
  "Strip out parts of a match fn to just the short part"
  [match-fn]
  (let [match-fn-str (str match-fn)
        fn-only (re-find #"\$(.*)_QMARK_@" match-fn-str)]
    (second fn-only)))

(defn compare-using-fn
  "Returns [compare-fn match-probs] if compare-fn successfully matches rec1
  against rec2.
  Returns nil if the comparison fails"
  [rec1 rec2 [compare-fn match-probs]]
  (when (compare-fn rec1 rec2)
    [compare-fn match-probs]))

(defn first-match-from-fns-and-weights
  "Run through compare functions in `comparison-fns-and-weights` for `rec1` vs
  `rec2`. For the first one which successfully matches, return the short name
  of the fn, and the weights that go with it"
  [comparison-fns-and-weights rec1 rec2]
  (let [best-comparison (some #(compare-using-fn rec1 rec2 %)
                              comparison-fns-and-weights)
        [comp-fn weights] best-comparison]
    {:label (match-fn-name-to-short comp-fn)
     :weights weights}))

(defn match-score-from-weights
  "Calculate the the match score based on having a match, given `match-prob`
  and `unmatch-prob`"
  [{:keys [match-prob unmatch-prob]}]
  (Math/log (/ match-prob unmatch-prob)))

;; NB: unmatch-score-from-weights is currently unused.
;; There is always 'a match' with the functions which are currently used.
;; This is the case as there should be a catch-all function at the end where
;; the 'condition' is 'do these two fields not match?', with appropriate
;; weights

; (defn unmatch-score-from-weights
;   "What is the weight based on NOT having a match, given `match-prob` and
;   `unmatch-prob`"
;   [match-prob unmatch-prob]
;   (Math/log (/ (- 1 match-prob) (- 1 unmatch-prob))))

(defn match-score-for-field
  "Return a map of the label for the match function which succeeded, and the
  match score that resulted from that. This is used independently of
  calculating the overall match score for two records, and is for display of a
  breakdown of the matching score for specific records"
  [rec1 rec2 fns-and-weights]
  (let [{:keys [label weights]} (first-match-from-fns-and-weights
                                  fns-and-weights rec1 rec2)
        score (match-score-from-weights weights)]
    {:label label :score score}))

