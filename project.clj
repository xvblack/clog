(defproject clog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [jayq "2.5.0"]
                 [hiccup "1.0.5"]
                 [compojure "1.1.6"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.2"]]
  :ring {:handler clog.handler/app}
  :source-paths ["src/clj"]
  :cljsbuild {
    :builds {
      :main {
            :source-path "src/cljs"
            :compiler {
                      :output-to "resources/public/js/cljs.js"
                      :optimizations :simple
                      :pretty-print true}}}}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
