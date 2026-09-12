(ns famtree.determ.link.child-to-marriage
  (:require [famtree.fields.marriage :as marriage]
            [famtree.printing :as p]
            [famtree.record-colls.main-records :as rec-colls]))

;; Functions for attempting deterministic matching of a marriage record to
;; children of the people who got married. Has a global counter for 'parent-
;; count', so is intended to be used as a one-shot in a batch run, rather
;; than used via (e.g.) a server process)

(defn single-mm-name-from-set
  "Return the set of mother's maiden names from `rec-set`"
  [rec-set]
  (into #{} (map :mm-name) rec-set))

(def parent-count (atom 0))

(defn find-marriage-from-child
  "Find a marriage record which matches that of a child, searching in all
  loaded marriage records. Prints out the results of possible matches"
  ;; TODO: This would be better as a probabilistic/weighted match, instead
  ;; of determinsic. This should be removed once a probabilistic one is
  ;; implemented
  [mm-name-from-child child-recs]
  (let [name-match-marriages
        (filter #(= mm-name-from-child
                    (marriage/female-surname-from-marriage %))
                rec-colls/marriages)
        first-child-rec-year (apply min (map :year child-recs))
        possible-marriages (filter #(<= (:year %) first-child-rec-year)
                                   name-match-marriages)]
    (when (<= 1 (count possible-marriages) 3)
      (swap! parent-count inc)
      (println)
      (println "Possible marriage")
      (doseq [marriage possible-marriages]
        (println marriage))
      (println "Child")
      (p/print-details-for-person-set child-recs))))

(defn find-parents
  "Try to find parents from a set of child records"
  [child-rec-set]
  (let [child-mm-names (single-mm-name-from-set child-rec-set)]
    (doseq [child-mm-name child-mm-names]
      (find-marriage-from-child child-mm-name child-rec-set))))
