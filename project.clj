(defproject clog "0.1.0-SNAPSHOT"
  :description "clog: a blog system built in clojure"
  :url "https://github.com/xvblack/clog"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [jayq "2.5.0"]
                 [hiccup "1.0.5"]
                 [compojure "1.1.6"]
                 [com.novemberain/monger "1.7.0"]
                 [clj-time "0.6.0"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [digest "1.4.3"]
                 [sandbar "0.4.0-SNAPSHOT"]
                 [markdown-clj "0.9.41"]
                 [hiccup-bridge "1.0.0-SNAPSHOT"]
                 [om "0.3.6"]
                 [sablono "0.2.6"]
                 [org.clojure/data.json "0.2.4"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [org.pegdown/pegdown "1.4.2"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.2"]]
  :ring {:handler clog.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}})
