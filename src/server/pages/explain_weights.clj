(ns server.pages.explain-weights
  (:require [hiccup.core :as h]
            [server.pages.common :as common]
            [famtree.probab.comp-forename :as comp-forename]
            [famtree.probab.comp-gender :as comp-gender]
            [famtree.probab.comp-location :as comp-location]
            [famtree.probab.felligi-sunter :refer [match-fn-name-to-short]]))

;; TODO Add more of a dynamic explanation of what the probabilities may
;; represent (cooincidence, high/low cardinality etc.))

(defn weight-explaination-for
  [fns-and-weights section-title]
  (h/html [:div {:class "card"}
    [:h2 section-title]
    (for [[comp-fn {:keys [match-prob unmatch-prob]}]
          fns-and-weights]
      (h/html [:p [:strong (match-fn-name-to-short comp-fn)]]
              [:ul
               [:li (str (* match-prob 100)) "% chance that two records which "
                "relate to the same person will match on this specific rule"]
               [:li (str (* unmatch-prob 100)) "% chance that if this rule "
                "matches that the records are being compared are not related
                and are just a coincidence"]]))]))

(defn content
  "Page content that explains the currently configured weights used for 
  probabilistic matching"
  []
  (h/html
    [:head [:title "Weights Explanation"]
     (common/oat-header)]
    [:body
     [:nav {:aria-label "Breadcrumb"}
      [:ol {:class "unstyled hstack"
            :style "font-size: var(--text-7)"}
       [:li
        [:a {:href "/" :class "unstyled"} "Home"]]
       [:li {:aria-hidden "true"} "/"]
       [:li {:class "unstyled"}
        [:strong "Explain Weights"]]]]
     [:p "Explanation of the currently-configured weights which are used for "
      "matching pairs of records"]
     (weight-explaination-for comp-forename/fns-and-weights "Forename")
     (weight-explaination-for comp-gender/fns-and-weights "Gender")
     (weight-explaination-for comp-location/fns-and-weights "Location")
     [:p "Note: More detailed explanation of the maths behind the match (m)"
      "and unmatch (u) weights is at "
      [:a {:href "https://www.robinlinacre.com/m_and_u_values/"}
       "Robin Linacre's article on the topic"]]]))
