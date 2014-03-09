(ns clog.model.user
  (:refer-clojure :exclude [sort find compare])
  (:require [monger.collection :as coll]
            [digest]
            [clog.config :as config])
  (:use monger.query
        clog.util)
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]
           [org.mindrot.jbcrypt BCrypt]))

(defn- check-username-exist? [username]
  (= (coll/count "users" {:username username}) 1) )

(defn add-user [username password]
  (let [salt (BCrypt/gensalt)
        password_hashed (BCrypt/hashpw password salt)]
    (if (not (check-username-exist? username))
      (coll/insert "users" {:_id (ObjectId.) :username username :password_hashed password_hashed :salt salt})
      false)))

(defn auth-user [username password]
  (let [user (coll/find-one-as-map "users" {:username username})
        salt (:salt user)]
    (if (check-username-exist? username)
      (if (= (BCrypt/hashpw password salt) (:password_hashed user))
        user
        false)
      false)))

(defn get-user [username]
  (coll/find-one-as-map "users" {:username username}))

(defn remove-user [username]
  (coll/remove "users" {:username username}))

(defn- rand-key []
  (clojure.string/join (map (fn [_] (rand-nth "0123456789abcdefghijklmnopqrstuvwxyz")) (range 20))))

(defn add-rkey []
  (let [rkey (rand-key)]
    (coll/insert "rkeys" {:_id (ObjectId.) :rkey rkey})
    rkey))

(defn validate-rkey? [rkey]
  (if-let [rk (coll/find-one-as-map "rkeys" {:rkey rkey})]
    (do
      (coll/remove "rkeys" {:rkey rkey})
      true)
    false))
