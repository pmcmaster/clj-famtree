(ns famtree.record-specs
  "Specs for records and fields they contain"
  (:require [clojure.spec.alpha :as s]))

(s/def ::surname string?)
(s/def ::forename string?)
(s/def ::mm-name string?)
(s/def ::gender string?) ;; TODO: Specify allowable values
(s/def ::year int?)
(s/def ::rec-ref string?)
(s/def ::rd-name string?)

(s/def ::birth-rec
  (s/keys :req-un [::surname
                   ::forename
                   ::mm-name
                   ::gender
                   ::year
                   ::rec-ref
                   ::rd-name]))

(s/def ::age-at-death int?)
(s/def ::death-rec
  (s/keys :req-un [::surname
                   ::forename
                   ::age-at-death
                   ::mm-name
                   ::gender
                   ::year
                   ::rec-ref
                   ::rd-name]))

(s/def ::spouse-surname string?)
(s/def ::spouse-forename string?)
(s/def ::marriage-rec
  (s/keys :req-un [::surname
                   ::forename
                   ::spouse-surname
                   ::spouse-forename
                   ::year
                   ::rec-ref
                   ::rd-name]))

(s/def ::age-at-census int?)
(s/def ::county-city string?)
(s/def ::census-rec
  (s/keys :req-un [::surname
                   ::forename
                   ::year
                   ::gender
                   ::age-at-census
                   ::rec-ref
                   ::rd-name
                   ::county-city]))

(s/def ::any-record
  (s/or :birth ::birth-rec
        :death ::death-rec
        :marriage ::marriage-rec
        :census ::census-rec))

