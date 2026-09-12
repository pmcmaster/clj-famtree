(ns server.pages.record
  (:require [hiccup.core :as h]
            [famtree.record-colls.main-records :as rec-colls]
            [server.record-views :as rec-views]
            [famtree.places :as places]
            [server.pages.common :as common]
            [server.location :as location]
            [server.mapping-js :as mapping-js]
            [famtree.probab.match :as prob-match]))

;; Basic display of info relation to a single record

;; TODO: Implementation

(defn record-from-hash
  "Look-up record based on its hash. Returns the found record, or nil if no
   match"
  [rec-hash-str]
  (when-let [rec-hash (Integer/parseInt rec-hash-str)]
    (first (filter #(= rec-hash (hash %))
                   rec-colls/all-records))))

(defn record-page-content
  "Details for a particular record, looked up based on its hash"
  [rec-hash-str]
  (if-let [found-rec (record-from-hash rec-hash-str)]
    (rec-views/detail-page found-rec)
    (str "No rec matched hash " rec-hash-str)))

(defn linked-records
  "List what records are already linked with this one."
  [_root-rec]
  (h/html
    [:h2 "Linked records"]
    [:p "None"]))

(defn fmt-3dp
  "Format `number` to 3 decimal places"
  [number]
  (format "%.3f" number))

(defn colour-number
  "Color styling for a number. Red if it's <0, green otherwise"
  [number]
  (h/html (if (< number 0)
            [:span {:class "badge" :data-variant "danger"
                    :style "margin-left: 2em;
                           margin-right: 0.5em"}
             (fmt-3dp number)]
            [:span {:class "badge" :data-variant "success"
                    :style "margin-left: 2em;
                           margin-right: 0.5em"}
             (fmt-3dp number)])))

(defn match-score-breakdown
  "Neat formatting of breakdown of a match score"
  [match-scores]
  (for [each-score match-scores]
    (h/html [:li {:class "unstyled"}
             (colour-number (:score each-score))
             (:label each-score)])))

(defn match-scores
  "Display match scores for this record against another record type"
  [record other-rec-coll]
  (let [match-results (prob-match/match-against record other-rec-coll)
        top-6 (take 6 match-results)]
    (h/html
      (for [[weight other-rec] top-6]
        (h/html
          [:p
           [:span {:class "badge outline"
                   :style "margin-right: 1em;
                          font-weight: bold"}
            (fmt-3dp weight)]
           (rec-views/basic-row other-rec)
           [:ul {:class "unstyled"}
            (match-score-breakdown
              (prob-match/match-scores record other-rec))]])))))

(defn record-page
  "Page for a single record"
  [rec-hash-str]
  (when-let [rec (record-from-hash rec-hash-str)]
    (h/html
      [:head [:title "Record detail"]
       (common/oat-header)
       (mapping-js/headers-small)]
      [:body (record-page-content rec-hash-str)
       [:div {:class "card"
              :style "float: left; clear: right"}
        [:h2 "Location"]
        (let [location (places/loc-name-for-rec rec)]
          (h/html [:p "Normalised location name: " location]
                  [:div {:id "map"}]))
        (when-let [geoloc (location/geoloc-for-record rec)]
          (h/html
            [:script {:src "/gen-static/mapping-main.js"}]
            [:script {:src "/gen-static/map-small.js"}]
            (mapping-js/script-add-map-pins [geoloc]))) ]
       [:div {:class "card"
              :style "float: left"}
        (linked-records rec)]
       [:div {:class "card"
              :style "float: left; clear: left"}
        [:h2 "Scores against births"]
        (match-scores rec rec-colls/births)]
       [:div {:class "card"
              :style "float: left"} [:h2 "Scores against deaths"]
        (match-scores rec rec-colls/deaths)]
       [:div {:class "card"
              :style "float: left; clear: left"}
        [:h2 "Scores against marriages"]
        (match-scores rec rec-colls/marriages)]
       [:div {:class "card"
              :style "float: left; clear: right"}
        [:h2 "Scores against marriages-spouse"]
        (match-scores rec rec-colls/marriages-spouse)]
       ])))

