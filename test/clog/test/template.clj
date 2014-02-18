(ns clog.test.template
  (:require [hiccup-bridge.core :as hc]
            [clojure.walk :as walk]
            [clj-time.core :as time]
            [hiccup.page :as page])
  (:use clj-time.format
        clj-time.coerce
        markdown.core))


(post-view {:id 109 :title "aaa" :time (to-long (time/now)) :author {:username "arthur" :as "saber"} :tags ["minecraft"]})
