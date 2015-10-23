(ns findartifact.central
  (:require [clj-http.client :as client]))


(def maven-central-url "http://search.maven.org/solrsearch/select")

(defn query [query from rows]
  (let [response (get (client/get maven-central-url
                        {:query-params {"q" query  "start" from "rows" rows "wt" "json"} :as :json})
                      :body)]
    (assoc (get response :response) :spelling-suggestions (get-in response [:spellcheck :suggestions 1 :suggestion]))))

(defn query-group [group from rows]
  (get-in (client/get maven-central-url
            {:query-params {"q" (format "g:\"%s\"" group)  "start" from "rows" rows "wt" "json"}
             :as :json})
          [:body :response]))

(defn get-artifact [g a v max-versions]
  (let [response (get-in (client/get maven-central-url
                           {:query-params {"q" (format "g:\"%s\" AND a:\"%s\"" g a) "rows" max-versions "core" "gav" "wt" "json"}
                            :as :json})
                         [:body :response :docs])
        artifact (if (nil? v)
                   (first response)
                   (first (filter #(= (:v %) v) response)))]
    (assoc artifact :versions (map #(:v %) response))))
