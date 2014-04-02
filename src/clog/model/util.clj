(ns clog.model.util
  (:refer-clojure :exclude [sort find compare])
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [clj-time.core :as time]
            [clj-time.coerce :as timec]
            [digest]
            [clog.config :as config])
  (:use monger.query
        monger.operators
        clog.util)
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]
           [org.mindrot.jbcrypt BCrypt]))

(def initialized (atom false))

(if-not @initialized
  (do
    (mg/connect-via-uri! (str "mongodb://" (:username config/db-config) ":" (:password config/db-config) "@" (:server config/db-config) "/" (:database config/db-config)))
    (swap! initialized (fn [_] true))))

(defn rand-tag []
  (let [tt ["moew" "wow" "minamisawa" "minecraft" "solidot" "tnt" "creeper"]]
    (into [] (filter (fn [s] (if (> (rand-int 10) 5) s)) tt)) ))

(defn rand-ret [arr]
  (fn [] (get arr (rand-int (count arr)))))

(defn register-as [username as]
  )

(def rand-as (rand-ret ["saber" "king arthur" "excalibur"]))

(defn to-$set [attr-map]
  (reduce (fn [new-attr-map [k v]] (if (map? v)
                                     (let [v (to-$set v)]
                                       (reduce (fn [new-attr-map [kk vv]] (assoc new-attr-map (keyword (str (name k) "." (name kk))) vv))
                                            new-attr-map v))
                                     (assoc new-attr-map k v))) {} attr-map))

