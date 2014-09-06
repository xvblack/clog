(ns clog.model.shitter
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [clog.config :as config])
  )

(defn declare-model [name {:keys [:fields :actions]}])






(declare-model :shitter/message
               :fields {:content :string
                       :time :time}
               :constructor {:instant (fn [message] (create :shitter/message {:content message :time (time/now)}))}
               :fetcher)


(create :shitter/message :default
        {:content "aaa"
         :time (time/now)})

(create :shitter/message :instant
        "aaa")

(fetch :shitter/message :multi
       {:time {:lt time/now-200}})

(authing user)