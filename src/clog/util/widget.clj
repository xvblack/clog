(ns clog.util.widget
  (:use clog.util.stateful-request))

(def widgets (atom {}) )

(defmacro def-widget
  "
  name params
  :body (body-expr)
  "
  [wn params & {:keys [body]} ]
  (list 'swap! 'clog.util.widget/widgets 'assoc wn (list 'fn params (cons 'do (list body)))))

(defn build-widget
  ;;[wn] (build-widget wn [])
  [wn & params] (apply (wn @widgets) params))

(defn react-widget [component-name & [props]]

  [:div
   [:div {:class (str component-name)}]])

(defn wrap-sidebar-widget [& component]
  (into [] (cons :div.sidewidget component)))

;; (react-widget "TagsPicker")

;; (defwidget simple-widget [n]
;;   :body
;;   (prn n))

;; (simple-widget 10)

(macroexpand '(def-widget "aa" [e] :body [:div]))
