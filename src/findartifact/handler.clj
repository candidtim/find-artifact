(ns findartifact.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [findartifact.central :as central]
            [findartifact.views :as views]))


(defroutes app-routes
  (GET "/" [] (views/index))
  (GET "/search" [q] (views/results q (central/query q 0 3000)))
  (GET "/artifact" [g a] (views/artifact (central/get-artifact g a)))
  (GET "/about" [] (views/about))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
