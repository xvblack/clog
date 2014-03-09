(ns clog.util
  (:require [clojure.data.json :as json]
            [clj-time.core :as time]
            [clj-time.coerce :as timec]
            [clj-time.format :as timef]
            [digest]))

(defn link-to [text & args]
  [:a {:href (apply str args)} text])

(defn link-to-view-post [id]
  (link-to (str "back to post" id) "/posts/" id))

(defn link-to-edit-post [id]
  (link-to "/posts/" id "/edit"))

(defn link-to-new-post []
  (link-to "new post" "/posts/new"))

(link-to-view-post 1)

(defn deep-merge-with
  " COPY from clojure contrib
  Like merge-with, but merges maps recursively, applying the given fn
  only when there's a non-map at a particular level.

  (deepmerge + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
               {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
    (fn m [& maps]
      (if (every? map? maps)
        (apply merge-with m maps)
        (apply f maps)))
    maps))

(defn deep-merge [& maps]
  (apply deep-merge-with (fn [_ x] x) maps))

(defn format-time [time-long]
  (let [time-format (timef/formatter "MMM dd,yyyy hh:mm")]
    (timef/unparse time-format (timec/from-long time-long))))

(defn wrap-ul [& items]
  [:ul
   (map (fn [x] [:li x]) items)])

(defn js-call [func & args]
  (str func "(" args ")"))

(defn time-now [] (timec/to-long (time/now)))

(defn rand-id [username]
  (digest/md5 (str username (time-now))))
