(ns clog.template.util
  (:require [hiccup-bridge.core :as hc]
            [clj-time.core :as time]
            [hiccup.page :as page])
  (:use clj-time.format
        clj-time.coerce
        markdown.core
        clog.config))


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

(defn format-time [time-long]
  (let [time-format (formatter "MMM dd,yyyy hh:mm")]
    (unparse time-format (from-long time-long))))

(defn wrap-ul [& items]
  [:ul
   (map (fn [x] [:li x]) items)])
