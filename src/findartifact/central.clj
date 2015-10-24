(ns findartifact.central
  (:require [clj-http.client :as client]
            [slingshot.slingshot :refer [try+]]
            [findartifact.util :refer [assoc-if]]))


(def ^:const maven-central-url "http://search.maven.org/solrsearch/select")

(defn- query- [query-params]
  (try+
    (:body (client/get maven-central-url {:query-params query-params :as :json}))
    ; errors 400 and 500 most oftenly raised because of bad queries - ignore and return nil
    (catch [:status 400] _ nil)
    (catch [:status 500] _ nil)))

(defn query [query from rows]
  (let [response (query- {"q" query "start" from "rows" rows "wt" "json"})]
    (assoc-if (:response response)
              :spelling-suggestions (get-in response [:spellcheck :suggestions 1 :suggestion]))))

(defn query-group [group from rows]
  (:response (query- {"q" (format "g:\"%s\"" group)  "start" from "rows" rows "wt" "json"})))

(defn get-artifact [g a v max-versions]
  (let [response (get-in
                   (query- {"q" (format "g:\"%s\" AND a:\"%s\"" g a) "rows" max-versions "core" "gav" "wt" "json"})
                   [:response :docs])
        artifact (if (nil? v)
                   (first response)
                   (first (filter #(= (:v %) v) response)))]
    (assoc-if artifact :versions (map #(:v %) response))))
