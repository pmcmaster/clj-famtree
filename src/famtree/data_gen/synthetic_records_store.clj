(ns famtree.data-gen.symthetic-records-store)

;; TODO Atom to store records before writing
;; TODO Actual functions for writing

(defn missing-record-chance
  "Chance that a record is missing for a given `year`"
  [year]
  (cond (< year 1800) 0.5
        (< year 1850) 0.4
        (< year 1900) 0.3
        (< year 1950) 0.1
        :else 0.01))

(defn miss-record
  "Should an event occurring in `year` not be recorded?"
  [year]
  (<= (rand) (missing-record-chance year)))

