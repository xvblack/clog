(ns clog.widgets.basic
  (:require [clojure.data.json :as json])
  (:use clog.util.widget))

(def-widget :about []
  :body
  (wrap-sidebar-widget
      [:h3 "About"]
      [:p " I feel alright!"]))

;; (build-widget :about [])

(def-widget :js-loader [& scripts]
  :body
  (into [] (map (fn [n] [:script {:src n}]) scripts)))

;; (build-widget :js-loader "/jquery.js" "a")

(def-widget :js-share-data [jsname data]
  :body
  [:script {:type "text/javascript"}
   (str "var " jsname "="
        (json/write-str data))])

;; (build-widget :js-share-data "share_data" {:a :b})

(def-widget :css-loader [& csss]
  :body
  (into [] (map (fn [css] [:link {:rel "stylesheet" :href css}]) csss)))

;; (build-widget :css-loader "/page.css")