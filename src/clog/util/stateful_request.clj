(ns clog.util.stateful-request)


(declare ^:dynamic request-state)

(defn wrap-stateful-request [handler]
  (fn [request]
    (binding [request-state (atom {})]
      (handler request))))

(defn request-put [k v]
  (swap! request-state (fn [a b] (merge a {k b})) v))

(defn request-get [k]
  (-> @request-state k))
