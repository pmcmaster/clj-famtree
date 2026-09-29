(ns famtree.data-gen.location)

; Locations don't have nice names, and just come out like: Place-12

(defn random-location
  "Return a random placename up to the hardcoded range"
  []
  (let [location-number (rand-nth (range 30))]
    (str "Place-" location-number)))

