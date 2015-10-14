(ns findartifact.views
  (:require [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate index "templates/index.html" [])

(enlive/deftemplate results "templates/results.html" [{results :docs :as query-result}]
  [:span#results-count] (enlive/content (str (:numFound query-result)))
  [:div.list [:div.artifact]] (enlive/clone-for [artifact results]
    [:a.name] (enlive/content (:id artifact))
    [:a.name] (enlive/set-attr :href (format "artifact?c=%s" (:id artifact)))
    [:span.latest-version] (enlive/content (str (:latestVersion artifact)))
    [:span.version-count] (enlive/content (str (dec (:versionCount artifact))))
    [:span.location] (enlive/content (:repositoryId artifact))))

(enlive/deftemplate about "templates/about.html" [])
