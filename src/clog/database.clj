(ns clog.database
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

(mg/connect-via-uri! (str "mongodb://" (:username config/db-config) ":" (:password config/db-config) "@troup.mongohq.com:10001/Clog"))

(defn- time-now [] (timec/to-long (time/now)))

(defn- post-count []
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
  (coll/find-one-as-map "posts" {:id id}))

(defn get-posts [param]
  (coll/find-maps "posts" param))

(defn get-drafts []
  (get-posts {:status {:draft true}}))

(defn- update [coll fp up]
  (let [to-$set (fn to-$set [attr-map]
                  (reduce (fn [new-attr-map [k v]] (if (map? v)
                                        (map #(assoc new-attr-map (keyword (str (name k) "." (name (first %)))) (second %)) (to-$set v))
                                        (assoc new-attr-map k v)))))])
  )
(defn to-$set [attr-map]
  (reduce (fn [new-attr-map [k v]] (if (map? v)
                                     (let [v (to-$set v)]
                                       (reduce (fn [new-attr-map [kk vv]] (assoc new-attr-map (keyword (str (name k) "." (name kk))) vv))
                                            new-attr-map v))
                                     (assoc new-attr-map k v))) {} attr-map))

;; (defn update-post [post]
;;   (let [ori (coll/find-one-as-map "posts" {:id (:id post)})
;;         new (deep-merge ori post)]
;;     (println new)
;;     (coll/update "posts" {:id (:id post)} new)))

(defn update-post [post]
  (let [set-attrs (to-$set post)]
    (coll/update "posts" {:id (:id post)} {$set set-attrs})))

(defn publish-post [id]
  (update-post {:id id :status {:draft false}}))

(defn draft-post [id]
  (update-post {:id id :status {:draft true}}))

(defn page-count [& pp]
  (+ (quot (post-count) (if (nil? pp) 10 pp) ) 1) )

(defn validate-post-id [id]
  (let [pc (post-count)]
    (if (and (> id 0) (<= id pc))
      id
      nil)))

(defn validate-page-id [id]
  (let [pc (post-count)]
    (if (and (> id 0) (<= id (page-count)))
      id
      nil)))

(defn get-page-posts [id & pp]
  (let [pp (if (not (nil? pp)) pp 10)]
    (with-collection "posts"
      (find {:status {:draft false}})
      (sort (array-map :time -1))
      (paginate :page id :per_page pp))
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

(defn get-user [username]
  (coll/find-one-as-map "users" {:username username}))

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

(defn rand-key []
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
    false) )

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

(with-collection "posts"
  (find {})
  (sort {:time -1} )
  (paginate :page 1 :per_page 10))
