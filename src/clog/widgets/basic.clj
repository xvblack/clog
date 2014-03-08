(ns clog.widgets.basic
  (:require [clojure.data.json :as json]
            [clog.database])
  (:use clog.util.widget
        clog.util.stateful-request))

(def-widget :about []
  :body
  (wrap-sidebar-widget
      [:h3 "About"]
      [:p " I feel alright!"]))

;; (build-widget :about [])

(def-widget :js-loader [& scripts]
  :body
  (into () (map (fn [n] [:script {:src n}]) (reverse scripts))))

;; (build-widget :js-loader "/jquery.js" "a")

(def-widget :js-share-data [jsname data]
  :body
  [:script {:type "text/javascript"}
   (str "var " jsname "="
        (json/write-str data))])

;; (build-widget :js-share-data "share_data" {:a :b})

(def-widget :css-loader [& csss]
  :body
  (into () (map (fn [css] [:link {:rel "stylesheet" :href css}]) (reverse csss))))

;; (build-widget :css-loader "/page.css")

;; (def-widget :about-author [profile-name]
;;   ())

(def-widget :sidewidget/user []
  :body
  (if-let [username (request-get :username)]
    (build-widget :user-info username)
    (build-widget :guest-info))
  )

(def-widget :login []
  :body
  [:div {:id "login-view"}
   [:form {:action "session/new" :method "post" :onsubmit "return secureSubmit(this)"}
    [:h2 "username"]
    [:input {:type "text" :name "username"}]
    [:h2 "password"]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "login"}]]
   [:a {:href "/register"} "register"]])

(def-widget :register []
  :body
  [:div {:id "register-view"}
   [:form {:action "users/new" :method "post" :onsubmit "return secureSubmit(this)"}
    [:h2 "username"]
    [:input {:type "text" :name "username"}]
    [:h2 "password"]
    [:input {:type "password" :name "password"}]
    [:h2 "register key"]
    [:input {:type "text" :name "rkey"}]
    [:input {:type "submit" :value "register"}]]])

(def-widget :user-avatar [username]
  :body
  [:img {:id "userInfoAvatar" :src (str "/img/avatar/" username ".png") :width "64px" :height "64px"}])

(def-widget :user-info [username]
  :body
  [:div {:id "user-info"}
   (build-widget :user-avatar username)
   [:div {:id "userActions"}
    [:p {:class "username"} username]
    [:p [:a {:href "/posts/drafts"} "Drafts"]]
    [:p (build-widget :action/new-post)]]
   ])

(def-widget :guest-info []
  :body
  [:div {:id "user-info"}
   (build-widget :user-avatar "guest")
   [:div {:id "userActions"}
    [:p {:class "username"} "Akarin"]
    [:p [:a {:href "/login"} "Login"]]]
   ])
