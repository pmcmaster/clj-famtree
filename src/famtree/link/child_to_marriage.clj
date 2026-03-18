(ns famtree.link.child-to-marriage
  (:require [famtree.fields.marriage :as marriage]
            [famtree.printing :as p]
            [famtree.record-colls.main-records :as rec-colls]))

(defn single-mm-name-from-set
  "Return if there is a single mother's maiden name"
  [rec-set]
  (into #{} (map :mm-name) rec-set))

(def parent-count (atom 0))

(defn find-marriage-from-child
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
  "Try to find parents from a child record"
  [child-rec-set]
  (let [child-mm-names (single-mm-name-from-set child-rec-set)]
    (doseq [child-mm-name child-mm-names]
      (find-marriage-from-child child-mm-name child-rec-set))))
