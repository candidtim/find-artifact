(ns findartifact.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [findartifact.views :as views]))


(def maven-central-url "http://search.maven.org/solrsearch/select")

(defn query-maven-central [query from rows]
  (get-in (client/get maven-central-url
            {:query-params {"q" query  "start" from "rows" rows "wt" "json"}
             :as :json})
          [:body :response]))

(defn get-from-maven-central [g a]
  (first (get-in (client/get maven-central-url
                   {:query-params {"q" (format "g:\"%s\" AND a:\"%s\"" g a) "rows" 1 "core" "gav" "wt" "json"}
                    :as :json})
                 [:body :response :docs])))


(defroutes app-routes
  (GET "/" [] (views/index))
  (GET "/search" [q] (views/results q (query-maven-central q 0 3000)))
  (GET "/artifact" [g a] (views/artifact (get-from-maven-central g a)))
  (GET "/about" [] (views/about))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
