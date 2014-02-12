(ns clog.handler
  (:use compojure.core
        ring.util.response
        ring.middleware.cookies
        [clojure.string :only [join]]
        ;compojure.handler
        sandbar.stateful-session
        clj-time.format
        clj-time.coerce
        markdown.core
        clog.template)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.page :as page]
            [clog.database :as db]))


(defn wrap-html [res & path]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:title "Clog: A simple blog"]
    [:script {:src "http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/sha256.js"}]
    [:link {:rel "stylesheet" :href "/css/codemirror.css"}]
    [:script {:src "/js/codemirror.js"}]
    [:script {:src "/js/mode/markdown/markdown.js"}]
    [:script {:src "/js/jquery.js"}]
    [:script {:src "/js/clog.js"}]
    [:script {:src "/js/continuelist.js"}]
    ]
   [:body
    [:div {:id "header"}
     [:div {:id "title"}
       [:h1 "Clog"]
       [:h2 "a blog"]]
     [:div {:id "menu"}
       [:p (session-get :username)]]
     ]
    [:div {:id "container"}
     res]]))

(defn response-with-cookies [html cookies]
  (-> (response html) (assoc :cookies cookies)))

;(defn get-page-posts [id]
;  (map #(+ (* (- id 1) 10) %) (range 1 10)))

;; (defn get-post [id]
;;   (merge {:id id} {:title (str "Post " id)
;;    :content (str "This is post with id " id)}) )

;; (defn update-post [id content]
;;   true)

(def time-format (formatter "MMM dd,yyyy hh:mm"))

(defn get-username [] (session-get :username))

(defn ori-post-view [post & username]
  (println post)
  (if (not false)
    [:div
     [:h2 (:title post)]
     [:div
      [:div (str (:author post) " at " (unparse time-format (from-long (:time post)))) ]
      [:span "tags:"][:span (->> post :tags (join " "))]
      (if (not (nil? username)) [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"])
      ]
     [:div (md-to-html-string (:content post))]]
    ))

(defn post-editor-view [post]
  (print (:id post))
  [:div {:data-id (str (:id post)) :class "post-editor"}
   [:script {:src "/js/clog-editor.js"}]
   [:h2 {:contenteditable "true" :class "post-title-editor"} (:title post)]
   [:div
      [:div (str (:author post) " at " (unparse time-format (from-long (:time post)))) ]
      [:span "tags:"][:span (->> post :tags (join " "))]
      ]
   [:textarea {:id "cm-editor"} (:content post)]
   [:button {:onclick "savePost(this)"} "Save"][:span {:id "post-post"}] ])

(defn login-view []
  [:div {:id "login-view"}
   [:form {:action "session/new" :method "post" :onsubmit "return secureSubmit(this)"}
    [:h2 "username"]
    [:input {:type "text" :name "username"}]
    [:h2 "password"]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "login"}]]])

(defn page-handler [id]
  (let [id (. Integer parseInt id)
        pc (db/page-count)]
    (if (and
         (< 0 id)
         (>= pc id))
      (wrap-html
       [:div
       (map
        (fn [po] (post-view po (get-username)))
        (db/get-page-posts id ))
        [:div {:class "pager"}
         (if (< 1 id) [:a {:href (str "/page/" (- id 1))} "Prev"])
         (if (> (- pc 1) id) [:a {:href (str "/page/" (+ id 1))} "Next"])]
        ])
      (not-found "are you finding akarin?") )))

(defroutes app-routes
  (GET "/" [] (page-handler "1") )
  (GET "/ind" []
       (do
         (println (session-get :username))
         (wrap-html (session-get :username))))
  (GET "/login" [] (if (nil? (session-get :username))
                     (wrap-html (login-view))
                     (wrap-html "already logged in")) )
  (POST "/session/new" [username password]
        (let [user (db/auth-user username password)]
          (println user)
          (if user
            (do
              (session-put! :username (:username user))
              (wrap-html [:div "logged in"]))
            (wrap-html [:div
                        [:div "wrong password"]]))) )
  (GET "/page/:id" [id] (page-handler id) )
  (GET "/posts/:id" [id] (wrap-html (post-view (db/get-post (. Integer parseInt id) )(session-get :username)) ))
  (GET "/posts/:id/edit" [id]
       (let [username (session-get :username)]
         (if-not false ;(nil? username)
           (wrap-html (post-editor-view (db/get-post (. Integer parseInt id) )))
           (redirect "/login"))) )

  (POST "/posts/:id" [id title content]
        (let [id (. Integer parseInt id)
              writeresult (db/update-post {:id id :title title :content content })]
          (wrap-html [:div writeresult])))

  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (wrap-stateful-session (handler/site app-routes))
;;   (-> app-routes
;;       wrap-stateful-session
;;       wrap-params
;;       wrap-multipart-params
;;       wrap-nested-params
;;       wrap-keyword-params)
  )
