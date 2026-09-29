(ns famtree.data-gen.ageing)

; Simulates everyone ageing. Actual records often don't record age with much
; accuracy, so a wobble around age might be added before records are generated
; from a simulated person

;; TODO: Add some wobble into the age data, likely not here but when is is
;; written for some records

(defn simulate-ageing
  "Increment everyone's age by one year"
  [population]
  (println "Everyone ages one year")
  (map #(update-in % [:age] inc)
       population))

