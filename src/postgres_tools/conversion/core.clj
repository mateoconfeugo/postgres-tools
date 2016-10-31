(ns postgres-tools.conversion.core
  (:require [clojure.spec :as s]
            [jdbc.proto :as proto])
  (:import (clojure.lang Keyword IPersistentMap)
           (org.postgresql.util PGobject)))


;; Create a new type SpecValue which connects value with it's spec
(defrecord SpecValue [spec value])
(defn specval [spec value] (SpecValue. spec value))

;; User data specs
(s/def :house/config map?)
(s/def :house/material #{:brick :wood})
(s/def :house/doorsize #{1 2 3})

;; Conversion functions

(defn as-pg-object [type-name]
  (fn [v] (doto (PGobject.) (.setType type-name) (.setValue v))))

(defn map->pg-jsonb [m])
(defn kw->pg-enum [enum-type] (fn [x]))
(defn int->pg-enum [enum-type] (fn [x]))

(defn pg-value->map [pgobj _])
(defn pg-value->kw [pgobj _])
(defn pg-value->int [pgobj _])

;; Conversions config
;; - one direction at the time

(defn pg-type? [type-name]
  (fn [x] (= (.getType ^PGobject x) type-name)))

(def to-db-config
  ;; [value-class-slot optional-spec-slot conversion-func-slot]
  [[SpecValue :house/material (kw->pg-enum "MATERIAL")]
   [SpecValue :house/doorsize (int->pg-enum "DOORSIZE")]
   [IPersistentMap map->pg-jsonb]])

(def from-db-config
  ;; [value-class-slot checker-func-slot conversion-func-slot]
  [[PGobject (pg-type? "JSONB") pg-value->map]
   [PGobject (pg-type? "MATERIAL") pg-value->kw]
   [PGobject (pg-type? "DOORSIZE") pg-value->int]])

(def testdata
  {:config   {:a 1 :b 2}
   :material (specval :house/material :brick)
   :doorsize (specval :house/doorsize 2)})

;; THIS SHOULD WORK ON QUERY PARAMETERS TOO!!

;; And finally create what is necessary to create conversions
;; This way it's in control what actual conversions there are

;; (create-to-db-conversions to-db-config)
;; (create-from-db conversions from-db-config)


;; SO all of the above will generate something like this

(extend-protocol proto/ISQLType
  SpecValue
  (as-sql-type [v]
    (case (:spec v)
      :house/material ('fn1 (:value v))
      :house/doorsize ('fn2 (:value v))
      v))
  IPersistentMap
  (as-sql-type [v] ('fn4 v)))

(extend-protocol proto/ISQLResultSetReadColumn
  PGobject
  (from-sql-type [v _ metadata _]
    (cond
      ('checkf1 v) ('fn5 v metadata)
      ('checkf2 v) ('fn6 v metadata)
      ('checkf3 v) ('fn7 v metadata)
      :default v)))
