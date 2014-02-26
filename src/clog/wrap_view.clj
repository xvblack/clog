(ns clog.wrap-view
  (:require [hiccup.page :as page]
            [clojure.data.json :as json]
            [clog.database :as db])
  (:use markdown.core
        clog.config
        clog.util
        clog.util.stateful-request
        clog.util.widget
        clog.widgets.basic
        [clojure.string :only [join]]))

(defn share-map []
  (if-not (request-get :_js-share)
    (request-put :_js-share (atom {})))
  (request-get :_js-share))

(defn wrap-view [content & {:keys [sidebars]}]
  (prn sidebars)
  (let [sidebars (if-not (vector? (first sidebars)) [sidebars] sidebars)]
    (page/html5
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
   [:head
    [:title "Clog: A simple blog"]
    (build-widget :js-loader
                  "http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js")
;;     [:script {:src "http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js"}]
    (build-widget :css-loader
                  "/css/codemirror.css"
                  "/css/style.css"
                  "/css/fonts.css"
                  )
;;     [:link {:rel "stylesheet" :href "/css/codemirror.css"}]
;;     [:link {:rel "stylesheet" :href "/css/style.css"}]
;;     [:link {:rel "stylesheet" :href "/css/fonts.css"}]
    (build-widget :js-loader
                  "/js/codemirror.js"
                  "/js/mode/markdown/markdown.js"
                  "/js/continuelist.js"
                  "/js/jquery.js"
                  "/js/page.js"
                  "/js/clog.js"
                  "/js/clog-editor.js"
                  "/js/react.js"
                  "/js/JSXTransformer.js")
;;     [:script {:src "/js/codemirror.js"}]
;;     [:script {:src "/js/mode/markdown/markdown.js"}]
;;     [:script {:src "/js/jquery.js"}]
;;     [:script {:src "/js/page.js"}]
;;     [:script {:src "/js/clog.js"}]
;;     [:script {:src "/js/continuelist.js"}]
;;     [:script {:src "/js/clog-editor.js"}]
;;     [:script {:src "/js/react.js"}]
;;     [:script {:src "/js/JSXTransformer.js"}]
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
     (map (fn [sw]
            [:div.sidewidget sw])
          sidebars)
     [:div.sidewidget
      [:h3 "Featured Posts"]
      [:p " I feel alright! "]]
     [:div.sidewidget
      [:h3 "Featured Posts"]
      [:p " I feel alright! "]]]
    #_[:script {:src "/js/cljs.js"}]
])))
