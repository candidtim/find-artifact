(ns findartifact.views
  (:require [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate base "templates/base.html" [title content]
  [:#title] (enlive/content title)
  [:div#main] (enlive/substitute content))

(enlive/defsnippet search-form "templates/searchform.html" [:form#search] [autofocus query]
  [:input#query] (if autofocus (enlive/set-attr :autofocus "true") identity)
  [:input#query] (if query (enlive/set-attr :value query) identity))


; Index
(enlive/defsnippet index-content "templates/index.html" [:div#main] [form]
  [:div#search] (enlive/content form))

(defn index []
  (base "Find Artifact - Consise UI for wide Maven artifact search" (index-content (search-form true nil))))


; About
(enlive/defsnippet about-content "templates/about.html" [:div#main] [])

(defn about []
  (base "About - Find Artifact" (about-content)))


; Results list
(enlive/defsnippet results-content "templates/results.html" [:div#main] [form {results :docs :as query-result}]
  [:div#search] (enlive/content form)
  [:span#results-count] (enlive/content (str (:numFound query-result)))
  [:div.list [:div.artifact]] (enlive/clone-for [artifact results]
    [:a.name] (enlive/content (:id artifact))
    [:a.name] (enlive/set-attr :href (format "artifact?c=%s" (:id artifact)))
    [:span.latest-version] (enlive/content (str (:latestVersion artifact)))
    [:span.version-count] (enlive/content (str (dec (:versionCount artifact))))
    [:span.location] (enlive/content (:repositoryId artifact))))

(defn results [query query-result]
  (base (format "%s - Find Artifact" query) (results-content (search-form false query) query-result)))
