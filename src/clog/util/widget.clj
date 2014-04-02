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

(def ^:dynamic widget-path ())

(defn last-widget-id [wn]
  (second
   (first
    (filter
     (fn [item] (= wn (first item)))
     widget-path))))

(defn this-id [wn]
  (str wn "-" (last-widget-id wn)))

(defn this-dom [wn]
  (str "document.getElementById(" (this-id wn) ")"))

(defn build-widget
  ;;[wn] (build-widget wn [])
  [wn & params]
  (if (nil? (request-get :_widgets)) (request-put :_widgets (atom {})))
  (let [a (request-get :_widgets)]
    ;; (prn a)
    (swap! a merge {wn (if-not (nil? (wn @a))
                         (inc (wn @a))
                         0)})
    (binding [widget-path (conj widget-path [wn (wn @a)])]
      (apply (wn @widgets) params))))

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

;; (macroexpand '(def-widget "aa" [e] :body [:div]))
