(defproject findartifact "1.0.0"
  :description "Maven Repository search tool that cannot be simpler"
  :url "https://github.com/candidtim/find-artifact"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [enlive "1.1.6"]
                 [clj-http "2.0.0"]
                 [slingshot "0.12.2"]
                 [cheshire "5.5.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler findartifact.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
