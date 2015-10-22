(ns findartifact.central
  (:require [clj-http.client :as client]))


(def maven-central-url "http://search.maven.org/solrsearch/select")

(defn query [query from rows]
  (get-in (client/get maven-central-url
            {:query-params {"q" query  "start" from "rows" rows "wt" "json"}
             :as :json})
          [:body :response]))

(defn query-group [group from rows]
  (get-in (client/get maven-central-url
            {:query-params {"q" (format "g:\"%s\"" group)  "start" from "rows" rows "wt" "json"}
             :as :json})
          [:body :response]))

(defn get-artifact [g a]
  (first (get-in (client/get maven-central-url
                   {:query-params {"q" (format "g:\"%s\" AND a:\"%s\"" g a) "rows" 1 "core" "gav" "wt" "json"}
                    :as :json})
                 [:body :response :docs])))
