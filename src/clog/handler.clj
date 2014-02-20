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
    [:h2 "Username:"]
    [:input {:type "text" :name "username"}]
    [:h2 "Password:"]
    [:input {:type "password" :name "password"}]
    [:br ]
    [:input#submitbutton {:type "submit" :value "login"}]]])

(defn page-handler [id]
  (let [id (. Integer parseInt id)
        pc (db/page-count)]
    (if (and
         (< 0 id)
         (>= pc id))
      (page-view id (get-username))
      (not-found "are you finding akarin?") )))

(defn post-handler [id]

  (let [id (. Integer parseInt id)]
    (if (and (> id 0) (<= id (db/post-count) ))
      (wrap-view
        (post-view
         (db/get-post id)
         (session-get :username)) )
      (not-found "akarin? who is akarin?"))))

(defn post-new-handler []
  (let [username (session-get :username)]
    (if-not (nil? username)
      (let [id (db/new-post username)]
        (redirect (str "/posts/" id "/edit")))
      (redirect "/login"))))

(defn post-edit-handler [id]
  (if-let [id (db/validate-post-id (. Integer parseInt id))]
    (let [username (session-get :username)]
      (if-not (nil? username)
        (wrap-view (post-editor-view (db/get-post id )))
        (redirect "/login"))))
  )

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
  (GET "/posts/new" []
       (post-new-handler))
  (GET "/page/:id" [id]
       (page-handler id) )
  (GET "/posts/:id" [id]
       (post-handler id))
  (GET "/posts/:id/edit" [id]
       (post-edit-handler id))

  (POST "/posts/:id" [id title content tags as]
        (if-let [id (db/validate-post-id (. Integer parseInt id))]
          (let [args (into {} (filter val {:id id :title title :author {:as as} :content content :tags tags}))
                writeresult (db/update-post args)]
            (println args)
            (wrap-view [:div writeresult]))
          (route/not-found "")) )

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
