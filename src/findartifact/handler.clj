(ns findartifact.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [redirect]]
            [ring.util.codec :refer [form-encode]]
            [clojure.core.match :refer [match]]
            [clojure.core.match.regex :refer :all]
            [findartifact.central :as central]
            [findartifact.views :as views]))

(def ^:const max-results 3000)

(defroutes app-routes
  (GET "/" [] (views/index))
  (GET "/search" [q] (match q
    #"g:.*\s.*" (let [[_ g r] (re-find #"g:(.*)\s(.*)" q)]
                  (views/results q (central/query (str g " " r) 0 max-results)))
    #"g:.*"     (let [[_ g] (re-find #"g:(.*)" q)]
                  (views/results q (central/query-group g 0 max-results)))
    #".*:.*"    (let [[_ g a] (re-find #"(.*):(.*)" q)]
                  (redirect (str "/artifact?" (form-encode {:g g :a a}))))
    :else (views/results q (central/query q 0 max-results))))
  (GET "/artifact" [g a v] (views/artifact (central/get-artifact g a v max-results)))
  (GET "/about" [] (views/about))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
