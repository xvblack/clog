(ns clog.handler
  (:use compojure.core
        ring.middleware.cookies
        [clojure.string :only [join]]
        sandbar.stateful-session
        clog.template.view
        clog.config)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clog.database :as db]
            [ring.util.response :as response]))

(defn get-username [] (session-get :username))

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

(defn page-handler [id]
  (if-let [id (-> id parse-id db/validate-page-id)]
    (page-view id (get-username))
    (not-found "are you finding akarin?") ))

(defn post-handler [id]
  (if-let [id (-> id parse-id db/validate-post-id)]
    (wrap-view
     (post-view
      (db/get-post id)
      (session-get :username))
     (get-username))
    (not-found "akarin? who is akarin?")))

(defn post-new-handler []
  (if-let [username (session-get :username)]
    (let [id (db/new-post username)]
      (redirect (str "/posts/" id "/edit")))
    (redirect "/login" "/posts/new")))

(defn post-edit-handler [id]
  (if-let [id (-> id parse-id db/validate-post-id)]
    (if-let [username (get-username)]
      (wrap-view (post-editor-view (db/get-post id )))
      (redirect "/login")))
  )

(defn post-update-handler [id title content tags as]
  (if-let [username (get-username)]
    (if-let [id (db/validate-post-id (. Integer parseInt id))]
      (let [args (into {} (filter val {:id id :title title :author {:as as} :content content :tags tags}))
            writeresult (db/update-post args)]
        (println args)
        (wrap-view [:div writeresult]))
      (route/not-found ""))))

(defn login-handler []
  (if (nil? (session-get :username))
    (wrap-view (login-view))
    (wrap-view "already logged in")))

(defn session-new-handler [username password]
  (if-let [user (db/auth-user username password)]
    (do
      (session-put! :username (:username user))
      (redirect-back "/"))
    (wrap-view [:div
                [:div "wrong password"]])) )

(defroutes app-routes
  (GET "/" [] (page-handler 1) )
  ;;   (GET "/ind" []
  ;;        (do
  ;;          (println (session-get :username))
  ;;          (wrap-view (session-get :username))))
  (GET "/login" []
       (login-handler) )
  (POST "/session/new" [username password]
        (session-new-handler username password))
  (GET "/posts/new" []
       (post-new-handler))
  (GET "/page/:id" [id]
       (page-handler id) )
  (GET "/posts/:id" [id]
       (post-handler id))
  (GET "/posts/:id/edit" [id]
       (post-edit-handler id))

  (POST "/posts/:id" [id title content tags as]
        (post-update-handler id title content tags as))

  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (wrap-stateful-session (handler/site app-routes)))
