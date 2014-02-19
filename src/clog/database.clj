(ns clog.database
  (:refer-clojure :exclude [sort find compare])
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [clj-time.core :as time]
            [clj-time.coerce :as timec]
            [digest]
            [clog.config :as config])
  (:use [monger.query]
        clog.util)
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]
           [org.mindrot.jbcrypt BCrypt]))

(mg/connect-via-uri! (str "mongodb://" (:username config/db-config) ":" (:password config/db-config) "@troup.mongohq.com:10001/Clog"))

(defn time-now [] (timec/to-long (time/now)))

(defn post-count []
  (coll/count "posts") )

(declare insert-post)

(defn new-post [username]
  (insert-post username "akarin" "" [] ""))

(defn insert-post [author as title tags content]
  (let [_id (ObjectId.)
        id (+ 1 (post-count))]
    (coll/insert "posts" {:_id _id :id id  :title title :status {:draft true} :author {:username author :as as} :tags tags :content content :time (time-now)}  )
    id))

(defn get-post [id]
  (first (coll/find-maps "posts" {:id id})))

(defn update-post [post]
  (let [ori (coll/find-one-as-map "posts" {:id (:id post)})
        new (deep-merge ori post)]
    (println new)
    (coll/update "posts" {:id (:id post)} new)))

(defn page-count [& pp]
  (+ (quot (post-count) (if (nil? pp) 10 pp) ) 1) )

(defn validate-post-id [id]
  (let [pc (post-count)]
    (if (and (> id 0) (<= id pc))
      id
      nil)))

(page-count)

(defn get-page-posts [id & pp]
  (let [pp (if (not (nil? pp)) pp 10)]
    (with-collection "posts"
          (find {})
          (sort (array-map :time -1))
          (limit 10)
          (skip (* 10 (- id 1))))
  ))

(defn check-username-exist? [username]
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

(defn remove-user [username]
  (coll/remove "users" {:username username}))

(defn rand-tag []
  (let [tt ["moew" "wow" "minamisawa" "minecraft" "solidot" "tnt" "creeper"]]
    (into [] (filter (fn [s] (if (> (rand-int 10) 5) s)) tt)) ))

(defn rand-ret [arr]
  (fn [] (get arr (rand-int (count arr)))))

(defn register-as [username as]
  )

(def rand-as (rand-ret ["saber" "king arthur" "excalibur"]))

;; (coll/remove "posts" {})

;; (get-post 10)

;; (for [i (range 100)](insert-post "saber" (rand-as) (str "post " i) (rand-tag)  (str "post " i)))

;; (->> (get-post 100) :tags (clojure.string/join " "))


;; (coll/count "users" {:username "aaa"})

;; (coll/find-maps "users" {:username "arthur"})

;; (def a (BCrypt/gensalt))

;; (BCrypt/hashpw "aa" a)

;; (remove-user "arthur")

;; (add-user "arthur" (digest/sha-256 "saber") )

;; (update-post {:id 108 :title "post 99" :content "post 99 is here aaa"})
