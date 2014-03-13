(ns clog.widgets.shitter
  (:use clog.util.widget))


(def-widget :shitter [channel]
  :body
  (react-widget :shitter channel))
