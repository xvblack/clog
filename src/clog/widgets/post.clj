(ns clog.widgets.post
  (:use clog.util.widget
        clog.util.stateful-request
        clog.util
        markdown.core)
  (:require [clog.database :as db]
            [clojure.data.json :as json]))

(def-widget :post-edit-link [post]
  :body
  [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"])

(def-widget :post [post]
  :body
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
    [:div.postcontent.font-hei
     (md-to-html-string (:content post))]]
   [:div.post-actions
    (wrap-ul
     (if-not (nil? (request-get :username))
       (build-widget :post-edit-link post)
       ))]])

(def-widget :action/new-post []
  :body
  [:a {:href "/posts/new"} "New post"])

(def-widget :posts [posts]
  :body
  [:div
   (map
    (fn [po]
      (build-widget :post po))
    posts)])

(def-widget :page [id pc posts]
  :body
  [:div
   #_(if (not (nil? (request-get :username)))
     (build-widget :action/new-post))
   (build-widget :posts posts)
   [:div {:class "pager"}
    (if (< 1 id) [:a {:href (str "/page/" (- id 1))} "Prev"])
    (if (> (- pc 1) id) [:a {:href (str "/page/" (+ id 1))} "Next"])]
   ]
  )

(def-widget :title-editor [post]
  :body
  [:h2 {:contenteditable "true" :class "posttitle post-title-editor font-hei"} (:title post)])

(def-widget :author-as-editor [post]
  :body
  (let [author (:author post)
        username (:username author)
        as (:as author)]
    [:span
     [:span username]
     " as "
     [:span {:contenteditable "true" :class "post-as-editor"} as]]))

(def-widget :publish-check [post]
  :body
  (if (not (-> post :status :draft)) [:input {:type "checkbox" :class "publish-check" :checked "on"} "publish"] [:input {:type "checkbox" :class "publish-check"} "publish"]))

(def-widget :post-editor [post]
  :body
  [:div {:data-id (str (:id post)) :data-tags (json/write-str (:tags post)) :class "post post-editor"}
   [:a {:href (str "/posts/" (:id post))}]
   (build-widget :title-editor post)
   [:p.postmeta
    [:div {:class "metaEditor"}
     (build-widget :author-as-editor post)
     [:span {:class "editor-time"} " at " (format-time (:time post)) ]
     [:br ]
     [:span "tags:"]
     #_[:span (->> post :tags (join " "))]
     [:div {:class "picker"}] ;; should be replace by react-widget
     ]
    ]
   [:textarea {:class "cm-editor"} (:content post)]
   (build-widget :publish-check post)
   [:br ]
   [:button {:onclick "savePost(this)" :class "bluebtn"} "Save"][:span {:class "post-post"}]]
  #_[:div {:data-id (str (:id post)) :class "post post-react-editor"}])

(def-widget :page-v2 [page]
  :body
  [:div
   (build-widget :posts (:posts page) )
   [:div {:class "pager"}
    (let [id (:id page)]
      (list
       (if (:prev page) [:a {:href (str "/page/" (- id 1))} "Prev"])
       (if (:next page) [:a {:href (str "/page/" (+ id 1))} "Next"])))]
   ]
  )
