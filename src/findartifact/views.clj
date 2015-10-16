(ns findartifact.views
  (:require [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate base "templates/base.html" [title content]
  [:#title] (enlive/content title)
  [:div#main] (enlive/substitute content))


; Index
(enlive/defsnippet index-content "templates/index.html" [:div#main] [])

(defn index []
  (base "Find Artifact - Consise UI for wide Maven artifact search" (index-content)))


; About
(enlive/defsnippet about-content "templates/about.html" [:div#main] [])

(defn about []
  (base "About - Find Artifact" (about-content)))


; Results list
(enlive/defsnippet results-content "templates/results.html" [:div#main] [query {results :docs :as query-result}]
  [:input#query] (enlive/set-attr :value query)
  [:span#results-count] (enlive/content (str (:numFound query-result)))
  [:div.list [:div.artifact]] (enlive/clone-for [artifact results]
    [:a.name] (enlive/content (:id artifact))
    [:a.name] (enlive/set-attr :href (format "artifact?c=%s" (:id artifact)))
    [:span.latest-version] (enlive/content (str (:latestVersion artifact)))
    [:span.version-count] (enlive/content (str (dec (:versionCount artifact))))
    [:span.location] (enlive/content (:repositoryId artifact))))

(defn results [query query-result]
  (base (format "%s - Find Artifact" query) (results-content query query-result)))
