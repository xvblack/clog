(ns clog.template.view
  (:require [hiccup.page :as page]
            [clojure.data.json :as json]
            [clog.database :as db])
  (:use markdown.core
        clog.config
        clog.util
        clog.template.util
        [clojure.string :only [join]]))

(defn login-view []
  [:div {:id "login-view"}
   [:form {:action "session/new" :method "post" :onsubmit "return secureSubmit(this)"}
    [:h2 "Username:"]
    [:input {:type "text" :name "username"}]
    [:h2 "Password:"]
    [:input {:type "password" :name "password"}]
    [:br ]
    [:input {:type "submit" :value "login"}]]])

(defn user-info-view [username]
  [:div {:id "user-info"}
   [:div
    [:label "name"]
    [:p username]]
   ])

(defn new-post-view []
  [:div
   (link-to-new-post)])

(defn post-view [post & [username]]
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
     (if-not (nil? username)
       [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"]))]])

(defn page-view [id & [username]]
  (let [pc (db/page-count)]
     [:div
      (if (not (nil? username))
        (new-post-view))
      [:div
       (map
        (fn [po] (post-view po username))
        (db/get-page-posts id ))
       [:div {:class "pager"}
        (if (< 1 id) [:a {:href (str "/page/" (- id 1))} "Prev"])
        (if (> (- pc 1) id) [:a {:href (str "/page/" (+ id 1))} "Next"])]
       ]]
     ))

(defn title-editor [post]
  [:h2 {:contenteditable "true" :class "posttitle post-title-editor font-hei"} (:title post)]
  )

(defn author-as-editor [post]

  (let [author (:author post)
        username (:username author)
        as (:as author)]
    [:span
     username
     " as "
     [:span {:contenteditable "true" :class "post-as-editor"} as]])
  )

(defn post-editor-view [post]
  [:div {:data-id (str (:id post)) :data-tags (json/write-str (:tags post)) :class "post post-editor"}
   (link-to-view-post (:id post))
   (title-editor post)
   [:p.postmeta
      [:div {:class "metaEditor"}
       (author-as-editor post)
       " at "
       (format-time (:time post))
       [:span "tags:"]
       #_[:span (->> post :tags (join " "))]
       [:div {:id "picker"}]
       ]
      ]
   [:textarea {:id "cm-editor"} (:content post)]
   [:button {:class "bluebtn" }{:onclick "savePost(this)"} "Save"][:span {:id "post-post"}] ]
  #_[:div {:data-id (str (:id post)) :class "post post-react-editor"}])
