(ns clog.util
  (:require [clojure.data.json :as json])
  (:use clostache.parser))

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

(defmacro defwidget [wn params & {:keys [body]} ]
  (list 'defn wn params (cons 'do body)))

(defn react-widget [component-name & [props]]
  [:div
   [:div {:class (str component-name)}]
   [:script {:type "javascript"}
    (render
     ""
     )
    ]])

(react-widget "TagsPicker")

(defwidget simple-widget [n]
  :body
  (prn n))

(simple-widget 10)
