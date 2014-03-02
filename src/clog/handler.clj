(ns clog.handler
  (:use compojure.core
        ring.middleware.cookies
        [clojure.string :only [join]]
        sandbar.stateful-session
        clog.template.view
        clog.config
        clog.util.stateful-request
        clog.wrap-view
        clog.util.widget
        clog.widgets.basic
        clog.widgets.post)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clog.database :as db]
            [ring.util.response :as response]
            [clog.util.stateful-loader :as stateful]))

(defn get-username []
  (session-get :username))

(defn parse-id [id]
  (if (instance? Number id)
    id
    (. Integer parseInt id)))

(defn redirect [url & [back]]
  (prn back)
  (if-not (nil? back) (flash-put! :redirect-back back))
  (response/redirect url))

(defn not-found [content & [url]]
  (response/not-found content))

(defn redirect-back [& [default]]
  (if-let [url (flash-get :redirect-back)]
    (response/redirect url)
    (response/redirect default)))

(defn wrap-view-with-widgets [mainpage]
  (wrap-view mainpage
             :sidebars
             [(build-widget :sidewidget/user)
              #_(build-widget :sidewidget-about-site)]))

(defn page-handler [id]
  (if-let [id (-> id parse-id db/validate-page-id)]
    (let [pc (db/page-count)
          posts (db/get-page-posts id)]
      (wrap-view-with-widgets
       (build-widget :page id pc posts)))
    (not-found "are you finding akarin?") ))

(defn post-handler [id]
  (if-let [id (-> id parse-id db/validate-post-id)]
    (wrap-view-with-widgets
     (build-widget :post
      (db/get-post id)))
    (not-found "akarin? who is akarin?")))

(defn post-new-handler []
  (if-let [username (session-get :username)]
    (let [id (db/new-post username)]
      (redirect (str "/posts/" id "/edit")))
    (redirect "/login" "/posts/new")))

(defn post-edit-handler [id]
  (if-let [id (-> id parse-id db/validate-post-id)]
    (if-let [username (get-username)]
      (wrap-view-with-widgets
       (build-widget :post-editor (db/get-post id)))
      (redirect "/login")))
  )

(defn post-update-handler [id title content tags as publish]
  (if-let [username (get-username)]
    (if-let [id (db/validate-post-id (. Integer parseInt id))]
      (let [publish (= publish "true")
            args (into {} (filter (comp not nil? val) {:id id :title title :author {:as as} :content content :tags tags :status {:draft (not publish)}}))
            writeresult (db/update-post args)]
        (println args)
        (wrap-view-with-widgets [:div writeresult]))
      (route/not-found ""))))

(map (comp nil? val) {:a nil})

(defn login-handler []
  (if (nil? (session-get :username))
    (wrap-view
     (build-widget :login))
    (wrap-view "already logged in")))

(defn session-new-handler [username password]
  (if-let [user (db/auth-user username password)]
    (do
      (session-put! :username (:username user))
      (redirect-back "/"))
    (wrap-view [:div
                [:div "wrong password"]])) )

(defn register-handler []
  (if (nil? (session-get :username))
    (wrap-view
     (build-widget :register))
    (wrap-view "already logged in")))

(defn user-new-handler [username password rkey]
  (if (db/validate-rkey? rkey)
    (do
      (db/add-user username password)
      (wrap-view "registered")
      )
    (wrap-view "register key is not valid"))
  )

(defn post-drafts-handler []
  (if-not (nil? (session-get :username))
    (wrap-view-with-widgets (build-widget :posts (db/get-drafts)))
    (redirect "/")) )

(defroutes app-routes
  (GET "/" [] (page-handler 1) )
  (GET "/ind" []
       (do
         (request-put :a "asuna")
         (println (session-get :username))
         (wrap-view (request-get :a))))
  (GET "/login" []
       (login-handler) )
  (POST "/session/new" [username password]
        (session-new-handler username password))
  (GET "/register" []
       (register-handler))
  (POST "/users/new" [username password rkey]
        (user-new-handler username password rkey))
  (GET "/posts/new" []
       (post-new-handler))
  (GET "/posts/drafts" []
       (post-drafts-handler))
  (GET "/page/:id" [id]
       (page-handler id) )
  (GET "/posts/:id" [id]
       (post-handler id))
  (GET "/posts/:id/edit" [id]
       (post-edit-handler id))

  (POST "/posts/:id" [id title content tags as publish]
        (post-update-handler id title content tags as publish))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      stateful/wrap-username
      wrap-stateful-request
      wrap-stateful-session))
