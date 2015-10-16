(ns findartifact.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [findartifact.views :as views]))


(defn query-maven-central [query from rows]
  (get-in (client/get "http://search.maven.org/solrsearch/select"
            {:query-params {"q" query  "start" from "rows" rows "wt" "json"} :as :json})
          [:body :response]))

(defroutes app-routes
  (GET "/" [] (views/index))
  (GET "/search" [q] (views/results q (query-maven-central q 0 20)))
  (GET "/about" [] (views/about))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
