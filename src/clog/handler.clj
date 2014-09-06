(ns clog.handler
  (:use compojure.core
        ring.middleware.cookies
        [clojure.string :only [join]]
        sandbar.stateful-session
        clog.util.stateful-request
        clog.wrap-view
        clog.util.widget
        clog.widgets.basic
        clog.widgets.post)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response]
            [clog.util.stateful-loader :as stateful]
            [clog.model.post :as post]
            [clog.model.user :as user]
            [clojure.java.io :as io]
            [clog.config :as config]
            [clog.util :as util]
            [clojure.data.json :as json]))

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
  (let [result (wrap-view mainpage
                          :sidebars
                          [(build-widget :sidewidget/user)
                           (build-widget :sidewidget/tags-cloud (post/post-tags))
                           (build-widget :sidewidget/rec-posts (post/get-random-posts))
                           #_(build-widget :sidewidget-about-site)])]
    (println "Wrapped")
    result))

(defn page-handler [id]
  (let [id (-> id parse-id)]
    (if-let [page (post/get-page-posts id)]
      (do
        (println "Fetched")
        (wrap-view-with-widgets
         (build-widget :page-v2 page))
        )

      (not-found "akarin akarin"))
    ))

(defn post-handler [id]
  (if-let [post (-> id parse-id post/get-post)]
    (wrap-view-with-widgets
     (build-widget :post post))
    (not-found "akarin kawai")))

(defn post-new-handler []
  (if-let [username (session-get :username)]
    (let [id (post/new-post username)]
      (redirect (str "/posts/" id "/edit")))
    (redirect "/login" "/posts/new")))

(defn post-edit-handler [id]
  (if-let [post (-> id parse-id post/get-post)]
    (if-let [username (get-username)]
      (wrap-view-with-widgets
       (build-widget :post-editor post))
      (redirect "/login")))
  )

(defn post-update-handler [id title content tags as publish]
  (let [id (-> id parse-id)
        publish (= publish "true")
        args (into {} (filter (comp not nil? val) {:id id :title title :author {:as as} :content content :tags tags :status {:draft (not publish)}}))]
    (if-let [write-result (post/update-post args)]
      (do
        (prn write-result)
        (wrap-view-with-widgets [:div write-result]))
      (route/not-found "where is that post?"))))

(map (comp nil? val) {:a nil})

(defn login-handler []
  (if (nil? (session-get :username))
    (wrap-view
     (build-widget :login))
    (wrap-view "already logged in")))

(defn session-new-handler [username password]
  (if-let [user (user/auth-user username password)]
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

(defn user-new-handler [username password rkey email]
  (if (user/validate-rkey? rkey)
    (do
      (user/add-user username password email)
      (wrap-view "registered")
      )
    (wrap-view "register key is not valid"))
  )

(defn tag-handler [tname]
  (let [pp (post/get-post-with-tag tname)]
    (wrap-view-with-widgets (build-widget :page-v2 pp))))

(defn post-drafts-handler []
  (if-not (nil? (session-get :username))
    (wrap-view-with-widgets (build-widget :page-v2 (post/get-drafts)))
    (redirect "/")) )

(defn file-updaload-handler [params]
  (if-not (nil? (session-get :username))
    (let [file (-> params :file :tempfile)
          ext (-> params
                  (:file)
                  (:filename)
                  (clojure.string/split #"\.")
                  (last))
          savename (str (util/rand-id (session-get :username)) "." ext)
          save (io/file config/image-storage-path savename)]
      (io/copy file save)
      (json/write-str {"filename" (str "/images/" savename)}))
    ))

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
  (GET "/tags/:tname" [tname]
       (tag-handler tname))

  (POST "/posts/:id" [id title content tags as publish]
        (post-update-handler id title content tags as publish))

  (POST "/upload" [file :as {params :params}]
        (file-updaload-handler params))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      stateful/wrap-username
      wrap-stateful-request
      wrap-stateful-session
      ))
