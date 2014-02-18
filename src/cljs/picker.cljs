(ns clog
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            #_[jayq.macros :as jq :include-macros true]))

;; (defn hello []
;;   "hello")

;; (declare tags-picker)

;; (defn widget [{:keys [data] :as app} owner]
;;   (reify
;;     om/IRenderState
;;     (render-state [this]
;;             (tags-picker data))))

;; (defn init-state [saber tags]
;;   (atom {:tags tags
;;    :saber saber}))

;; (defn tags-picker [data]
;;   (html [:div [:ul (map (fn [x] [:li x]) (:tags @data))]])
;;   )

;; (def ^:export app-state (init-state "" [:a :b :c]))

;; (om/root widget app-state {:target (.getElementById js/document "picker")})

(def picker-dom (.getElementById js/document "picker"))

(.log js/console picker-dom)



(defn widget [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil (:text data)))))

(om/root widget {:text "Hello world!"}
  {:target picker-dom})
