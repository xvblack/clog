(ns clog.template
  (:require [hiccup-bridge.core :as hc]
            [clojure.walk :as walk]
            [clj-time.core :as time])
  (:use clj-time.format
        clj-time.coerce
        markdown.core))

(defn simple-recur-path [tr ks]
    (cond
     (keyword? tr) (if (tr ks) {tr []} nil)
     (map? tr) nil
     (coll? tr) (reduce
                 concat
                 []
                 (map-indexed
                  (fn [ind item]
                    (let [res (simple-recur-path item ks)]
                      (map (fn [[k v]][k (cons ind v)]) res))) tr) )))

(defn recur-path [tr ks]
  (let [ks (set ks)
        places (into {} (simple-recur-path tr ks))]
    places
    ))

(defn build-template [template]
  (fn [update-value]
    (let [marks (map (fn [mark] #_(keyword (str "mark#" (clojure.string/join "" (drop 1 (str mark))) ))mark) (keys update-value))
          places (recur-path template marks)
          positions (into {} (map (fn [x] [(first x) (into [] (drop-last 1 (second x)))]) places))]
      (reduce (fn [template [k v]]
                (update-in template
                           (into [] v)
                           (fn [x] (k update-value)) ))
              template positions))) )

(def container-template (build-template (hc/html-file->hiccup "src/clog/templates/container.html")))

(hc/html-file->hiccup "src/clog/templates/post-example.html")

(defn format-time [time-long]
  (let [time-format (formatter "MMM dd,yyyy hh:mm")]
  (unparse time-format (from-long time-long))))

(defn post-view [post & username]
  [:div.post
   [:div {:id (str "post" (:id post)) :class "postwrap"}
    [:h2.posttitle.font-hei (:title post)]
    [:p.postmeta (str "Posted on "
                      (format-time (:time post))
                      " | By "
                      (:as (:author post))
                      " | Tags "
                      (clojure.string/join " " (:tags post))
                      " ")]
    (if (not (nil? username)) [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"])
    [:div.postcontent.font-hei (md-to-html-string (:content post))]]])

;;[:ul (map (fn [t] [:li t]) (:tags post))]

(post-view {:id 109 :title "aaa" :time (to-long (time/now)) :author {:username "arthur" :as "saber"} :tags ["minecraft"]})
