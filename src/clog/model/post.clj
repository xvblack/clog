(ns clog.model.post
  (:refer-clojure :exclude [sort find compare])
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [clog.config :as config])
  (:use monger.query
        monger.operators
        clog.util
        clog.model.util)
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]
           [org.mindrot.jbcrypt BCrypt]))

(defn- post-count []
  (coll/count "posts"))

(defn post-tags []
  (coll/distinct "posts" :tags))

(defmacro defscope [name parameter & body]
  `(defn ~name [query# ~@parameter] (merge query# (-> {} ~@body))))

(defmacro build-query [& body]
  `(-> (empty-query (.getCollection mg/*mongodb-database* "posts")) ~@body))

(defn append-meta [q m]
  (let [old-meta (meta q)
        new-meta (merge old-meta {:data m})]
    (with-meta q new-meta)))

(defn- exec-with-meta [q]
  (let [meta (meta q)]
    (with-meta (exec q) meta)))

(defn save-count [q]
  (prn q)
  (let [count (coll/count (:collection q) (:query q))]
    q))

(defscope page [id]
  #_(save-count)
  (paginate :page id :per_page 10))



(meta (exec-with-meta
 (build-query
  (tap)
  (find {:status {:draft false}})
  (append-meta {:a :b})
  (page 1))))

(def t (build-query
 (find {:status {:draft false}})
 (append-meta {:a :b})
 ))

(page t 1)


(defscope published []
  (find {:status {:draft false}}))

(defscope draft []
  (find {:status {:draft true}}))



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

(defn- get-posts [param]
  (coll/find-maps "posts" param))

(defn- update [coll fp up]
  (let [to-$set (fn to-$set [attr-map]
                  (reduce (fn [new-attr-map [k v]] (if (map? v)
                                        (map #(assoc new-attr-map (keyword (str (name k) "." (name (first %)))) (second %)) (to-$set v))
                                        (assoc new-attr-map k v)))))])
  )

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

(defn- page-count [coll pp query]
  (/ (coll/count coll query) pp))

(defn get-page-posts [id & [pp]]
  (let [pp (if (not (nil? pp)) pp 10)
        pc (page-count "posts" pp {:status {:draft false}})
        posts (with-collection "posts"
                (published)
                (sort (array-map :time -1))
                (paginate :page id :per_page pp))]
    (if-not (= (count posts) 0)
      {:id id
       :prev (> id 1)
       :next (< id pc)
       :posts posts}
      nil)))

(defmacro return-page [id & body]

  )


(defn get-drafts []
  (let [posts (with-collection "posts"
                (draft)
                (sort (array-map :time -1)))]
    {:id nil
     :prev false
     :next false
     :posts posts}))

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
  (paginate :page 1 :per_page 10)
  (tap))
