(ns famtree.records.core)

(defrecord BirthRec [surname forename mm-name gender year rec-ref rd-name])
(defrecord DeathRec [surname forename age-at-death mm-name gender year rec-ref rd-name])
(defrecord MarriageRec [surname forename spouse-surname spouse-forename year rec-ref rd-name])
(defrecord CensusRec [surname forename year gender age-at-census rec-ref rd-name county-city])

