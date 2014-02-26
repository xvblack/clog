(ns clog.widgets.post
  (:use clog.util.widget
        clog.util.stateful-request
        clog.util
        markdown.core))

(def-widget :post-edit-link [post]
  :body
  [:a {:href (str "/posts/" (:id post) "/edit")} "Edit"])

(def-widget :post [post]
  :body
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
     (if-not (nil? (request-get :username))
       (build-widget :post-edit-link post)
       ))]])
