(ns clog.util.stateful-loader
  (:use clog.util.stateful-request
        sandbar.stateful-session))

(defn wrap-username [handler]
  (fn [request]
    (request-put :username (session-get :username))
    (handler request)))
