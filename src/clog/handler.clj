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
        clog.template.view
        clog.config)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.page :as page]
            [clog.database :as db]))

(defn response-with-cookies [html cookies]
  (-> (response html) (assoc :cookies cookies)))

;(defn get-page-posts [id]
;  (map #(+ (* (- id 1) 10) %) (range 1 10)))

;; (defn get-post [id]
;;   (merge {:id id} {:title (str "Post " id)
;;    :content (str "This is post with id " id)}) )

;; (defn update-post [id content]
;;   true)

;; (def time-format (formatter "MMM dd,yyyy hh:mm"))

(defn get-username [] (session-get :username))

;; (defn ori-post-view [post & username]
;;   (println post)
;;   (if (not false)
;;     [:div
;;      [:h2 (:title post)]
;;      [:div
;;       [:div (str (:author post) " at " (unparse time-format (from-long (:time post)))) ]
;;       [:span "tags:"][:span (->> post :tags (join " "))]
;;       (if (not (nil? username)) [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"])
;;       ]
;;      [:div (md-to-html-string (:content post))]]
;;     ))

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
      (wrap-view
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
         (wrap-view (session-get :username))))
  (GET "/login" [] (if (nil? (session-get :username))
                     (wrap-view (login-view))
                     (wrap-view "already logged in")) )
  (POST "/session/new" [username password]
        (let [user (db/auth-user username password)]
          (println user)
          (if user
            (do
              (session-put! :username (:username user))
              (wrap-view [:div "logged in"]))
            (wrap-view [:div
                        [:div "wrong password"]]))) )
  (GET "/page/:id" [id] (page-handler id) )
  (GET "/posts/:id" [id] (wrap-view (post-view (db/get-post (. Integer parseInt id) )(session-get :username)) ))
  (GET "/posts/:id/edit" [id]
       (let [username (session-get :username)]
         (if-not false ;(nil? username)
           (wrap-view (post-editor-view (db/get-post (. Integer parseInt id) )))
           (redirect "/login"))) )

  (POST "/posts/:id" [id title content tags as]
        (let [id (. Integer parseInt id)
              args (into {} (filter val {:id id :title title :content content :tags tags :as as}))
              writeresult (db/update-post args)]
          (println args)
          (wrap-view [:div writeresult])))

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
