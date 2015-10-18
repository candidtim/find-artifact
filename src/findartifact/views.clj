(ns findartifact.views
  (:require [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate base "templates/base.html" [title content]
  [:#title] (enlive/content title)
  [:div#main] (enlive/substitute content))

(enlive/defsnippet search-form "templates/searchform.html" [:form#search] [autofocus query]
  [:input#query] (if autofocus (enlive/set-attr :autofocus "true") identity)
  [:input#query] (if query (enlive/set-attr :value query) identity))

(def build-tools [
  [:maven "Maven" "<dependency>\n  <groupId>%s</groupId>\n  <artifactId>%s</artifactId>\n  <version>%s</version>\n</dependency>"]
  [:gradle "Gradle" "'%s:%s:%s'"]
  [:sbt "SBT" "libraryDependencies += \"%s\" %% \"%s\" %% \"%s\""]
  [:ivy "Ivy" "<dependency org=\"%s\" name=\"%s\" rev=\"%s\"/>"]
  [:buildr "Buildr" "'%s:%s:jar:%s'"]
  [:grape "Grape" "@Grab(group='%s', module='%s', version='%s')"]
  [:lein "Leiningen" "[%s/%s \"%s\"]"]])

(def default-build-tool :gradle)


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
    [:a.name] (enlive/set-attr :href (format "artifact?g=%s&a=%s" (:g artifact) (:a artifact)))
    [:span.latest-version] (enlive/content (str (:latestVersion artifact)))
    [:span.version-count] (enlive/content (str (dec (:versionCount artifact))))
    [:span.location] (enlive/content (:repositoryId artifact))))

(defn results [query query-result]
  (base (format "%s - Find Artifact" query) (results-content (search-form false query) query-result)))


; Artifact details

(defn get-classifiers [all-artifacts]
  (map #(.substring % 1 (.indexOf % "."))
  (filter #(.startsWith % "-")
  all-artifacts)))

(enlive/defsnippet artifact-content "templates/artifact.html" [:div#main] [form {group :g artifact :a version :v all-artifacts :ec}]
  [:div#search] (enlive/content form)
  ; tabs titles
  [:ul.nav-tabs [:li.tab-item]] (enlive/clone-for [[tool tool-name _] build-tools]
    [:a.tab-title] (enlive/content tool-name)
    [:a.tab-title] (enlive/set-attr :href (format "#tab-%s" tool-name))
    [:li.tab-item] (if (= tool default-build-tool) (enlive/add-class "active") identity))
  ; tabs contents
  [:div.tab-content [:div.tab-pane]] (enlive/clone-for [[tool tool-name tool-fmt] build-tools]
    [:pre.dependency-id] (enlive/content (format tool-fmt group artifact version))
    [:pre.dependency-id] (enlive/set-attr :id (format "dependency-%s" tool-name))
    [:div.add-info] (if (empty? (get-classifiers all-artifacts)) nil identity)
    [:span.classifiers] (enlive/content (clojure.string/join ", " (get-classifiers all-artifacts)))
    [:div.btn-copy] (enlive/set-attr :data-clipboard-target (format "dependency-%s" tool-name))
    [:div.btn-set-default] (enlive/set-attr :title (format "Make %s default" tool-name))
    [:div.btn-set-default] (enlive/set-attr :onclick (format "setDefaultBuildTool('%s')" tool-name))
    [:span.btn-title] (enlive/content tool-name)
    [:div.tab-pane] (if (= tool default-build-tool) (enlive/add-class "active") identity)
    [:div.tab-pane] (enlive/set-attr :id (format "tab-%s" tool-name))))

(defn artifact [{group :g artifact-name :a :as artifact}]
  (base "Artifact - Find Artifact" (artifact-content (search-form false (format "%s:%s" group artifact-name)) artifact)))
