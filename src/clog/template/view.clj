(ns clog.template.view
  (:require [hiccup.page :as page]
            [clojure.data.json :as json]
            [clog.database :as db])
  (:use markdown.core
        clog.config
        clog.util
        clog.template.util
        [clojure.string :only [join]]))

(defn wrap-view [content & path]
  (page/html5
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
   [:head
    [:title "Clog: A simple blog"]
    [:script {:src "http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js"}]
    [:link {:rel "stylesheet" :href "/css/codemirror.css"}]
    [:link {:rel "stylesheet" :href "/css/style.css"}]
    [:link {:rel "stylesheet" :href "/css/fonts.css"}]
    [:script {:src "/js/codemirror.js"}]
    [:script {:src "/js/mode/markdown/markdown.js"}]
    [:script {:src "/js/jquery.js"}]
    [:script {:src "/js/page.js"}]
    [:script {:src "/js/clog.js"}]
    [:script {:src "/js/continuelist.js"}]
    [:script {:src "/js/clog-editor.js"}]
    [:script {:src "/js/react.js"}]
    [:script {:src "/js/JSXTransformer.js"}]
    [:script {:type "text/javascript"}]
    [:script {:type "text/jsx" :src "/js/picker.js"}]
    ]
   [:body
    [:div#wrapper
     [:div#header
      [:a {:href "/"} [:div#logo]]
      [:button#onlyButton.action.bluebtn
       [:span.label "≡"]]]
     [:div#contents content]
     [:div#footer
      [:p "I love sigsig. It is a dream!"]]]
    [:div#sidebar
     [:div.sidewidget
      [:h3 "About"]
      [:p " I feel alright! "]]
     [:div.sidewidget
      [:h3 "Featured Posts"]
      [:p " I feel alright! "]]
     [:div.sidewidget
      [:h3 "Featured Posts"]
      [:p " I feel alright! "]]]
    #_[:script {:src "/js/cljs.js"}]
]))

(defn new-post-view []
  [:div
   (link-to-new-post)])

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
    [:div.postcontent.font-hei
     (md-to-html-string (:content post))]]
   [:div.post-actions
    (wrap-ul
     (if (not (nil? username))
       [:p.postaction [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"]]))]])

(defn page-view [id & username]
  (let [pc (db/page-count)]
    (wrap-view
       [:div
        (new-post-view)
        [:div
       (map
        (fn [po] (post-view po username))
        (db/get-page-posts id ))
        [:div {:class "pager"}
         (if (< 1 id) [:a {:href (str "/page/" (- id 1))} "Prev"])
         (if (> (- pc 1) id) [:a {:href (str "/page/" (+ id 1))} "Next"])]
        ]])))

(defn title-editor [post]
  [:h2 {:contenteditable "true" :class "posttitle post-title-editor font-hei"} (:title post)]
  )

(defn author-as-editor [post]

  (let [author (:author post)
        username (:username author)
        as (:as author)]
    [:span
     [:span username]
     " as "
     [:span {:contenteditable "true" :class "post-as-editor"} as]])
  )

(defn post-editor-view [post]
  (print (:id post))
  [:div {:data-id (str (:id post)) :data-tags (json/write-str (:tags post)) :class "post post-editor"}
   (link-to-view-post (:id post))
   (title-editor post)
    [:div.editormeta
      (author-as-editor post)
      " at "
      (format-time (:time post))
      [:span "tags:"]
      #_[:span (->> post :tags (join " "))]
      [:br ]
      [:div {:id "picker"}]
     ]
   [:textarea {:id "cm-editor"} (:content post)]
   [:button.greenbtn {:onclick "savePost(this)"} [:span.label "Save"]] [:span {:id "post-post"}] ]
  #_[:div {:data-id (str (:id post)) :class "post post-react-editor"}])
