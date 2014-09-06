(ns clog.model.singleton
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [clog.config :as config])
  (:use monger.operators
        clog.util
        clog.model.util)
  (:import [org.bson.types ObjectId]))


(def ^:dynamic id 1)

;; (coll/insert "singletons" {:id id})

(defn sget [k]
  (-> (coll/find-one-as-map "singletons" {:id id})
      k))

(defn sset [k v]
  (coll/update "singletons" {:id id} {$set {k v}}))

(defn sappend [k elem]
  (coll/update "singletons" {:id id} {$push {k elem}}))

(defn sunion [k elems]
  (coll/update "singletons" {:id id} {$addToSet {k elems}}))


(sset :a :b)
(coll/find-one-as-map "singletons" {:id id})

(sget :a)
