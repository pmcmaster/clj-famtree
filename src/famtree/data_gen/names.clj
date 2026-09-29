(ns famtree.data-gen.names)

; Functions for generating random names

; The 'core surname' is the one which will be shared among all collected
; records
(def core-surname "SMITH")

; ...though there may be some unusual spelling variations
(def alternate-core-surname-spellings
  #{"SMYTH" "SMYTHE" "SMIT"})

(def male-names
  ["Jim" "James" "Jacob" "Joseph" "Bob" "Jenkins" "Shawn" "Paulo"])

(def female-names
  ["Susan" "Sheila" "Seonaid" "Selina" "Briony" "Sharon" "Sarah" "Suzanne"])

(defn random-name
  "Random name for gender. Returns pair of first name and surname"
  [gender]
  (if (= gender "M")
    (rand-nth male-names)
    (rand-nth female-names)))
