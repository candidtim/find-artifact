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

(defn frequent-groups [result-list]
  (let [grouped (group-by #(:g %) result-list)
        group-count (zipmap (keys grouped) (map #(count %) (vals grouped)))
        worthy-groups (filter #(> (group-count %) 5) (keys grouped))]
    (sort (comparator (fn [a b] (> (group-count a) (group-count b)))) worthy-groups)))

(enlive/defsnippet results-content "templates/results.html" [:div#main] [form {results :docs :as query-result}]
  [:div#search] (enlive/content form)
  [:span#results-count] (enlive/content (str (:numFound query-result)))
  [:div.list [:div.artifact]] (enlive/clone-for [artifact results]
    [:a.name] (enlive/content (:id artifact))
    [:a.name] (enlive/set-attr :href (format "artifact?g=%s&a=%s" (:g artifact) (:a artifact)))
    [:span.latest-version] (enlive/content (str (:latestVersion artifact)))
    [:span.version-count] (enlive/content (str (dec (:versionCount artifact))))
    [:span.location] (enlive/content (:repositoryId artifact)))
  [:div.spell-suggestions] (if (nil? (:spelling-suggestions query-result)) (enlive/set-attr :class "hidden") identity)
  [:div.spell-suggestions [:span.spell-suggestion]] (enlive/clone-for [suggestion (:spelling-suggestions query-result)]
    [:a.suggestion] (enlive/content suggestion))
  [:div.group-suggestions] (if (< (count (frequent-groups results)) 2) (enlive/set-attr :class "hidden") identity)
  [:div.group-suggestions [:span.group-suggestion]] (enlive/clone-for [group (frequent-groups results)]
    [:a.group-name] (enlive/content group)
    [:a.group-name] (enlive/set-attr :href (format "search?q=g:%s" group))))

(defn results [query query-result]
  (base (format "%s - Find Artifact" query) (results-content (search-form false query) query-result)))


; Artifact details

(defn get-classifiers [all-artifacts]
  (sort
    (comparator (fn [a b] (< (.length a) (.length b))))
    all-artifacts))

(defn download-url [group artifact version classifier]
  ; http://search.maven.org/remotecontent?filepath=com/jolira/guice/3.0.0/guice-3.0.0.pom
  (format "http://search.maven.org/remotecontent?filepath=%s/%s/%s/%s-%s%s"
    (clojure.string/replace group \. \/) artifact version artifact version classifier))

(enlive/defsnippet artifact-content "templates/artifact.html" [:div#main] [form {group :g artifact :a version :v all-artifacts :ec all-versions :versions}]
  [:div#search] (enlive/content form)
  ; tabs titles
  [:ul.nav-tabs [:li.tab-item]] (enlive/clone-for [[tool tool-name _] build-tools]
    [:a.tab-title] (enlive/content tool-name)
    [:li.tab-item] (enlive/set-attr :id (format "tab-title-%s" tool-name))
    [:a.tab-title] (enlive/set-attr :href (format "#tab-%s" tool-name)))
  ; tabs contents
  [:div.tab-content [:div.tab-pane]] (enlive/clone-for [[tool tool-name tool-fmt] build-tools]
    [:pre.dependency-id] (enlive/content (format tool-fmt group artifact version))
    [:pre.dependency-id] (enlive/set-attr :id (format "dependency-%s" tool-name))
    [:span.classifiers [:a.download]] (enlive/clone-for [classifier (get-classifiers all-artifacts)]
      [:a.download] (enlive/set-attr :href (download-url group artifact version classifier))
      [:a.download] (enlive/content (.substring classifier 1)))
    [:span.version-count] (enlive/content (str (count all-versions)))
    [:div.all-versions [:span.version]] (enlive/clone-for [version all-versions]
      [:a.version] (enlive/content version)
      [:a.version] (enlive/set-attr :href (format "artifact?g=%s&a=%s&v=%s" group, artifact version)))
    [:a.btn-copy] (enlive/set-attr :id (format "btn-copy-%s" tool-name))
    [:a.btn-copy] (enlive/set-attr :onclick (format "window['findartifact'].copy('btn-copy-%s', 'dependency-%s')" tool-name tool-name))
    [:a.btn-set-default] (enlive/set-attr :id (format "btn-set-default-%s" tool-name))
    [:a.btn-set-default] (enlive/set-attr :onclick (format "window['findartifact'].setDefaultBuildTool('btn-set-default-%s', '%s')" tool-name tool-name))
    [:span.btn-title] (enlive/content tool-name)
    [:div.tab-pane] (enlive/set-attr :id (format "tab-%s" tool-name))))

(defn artifact [{group :g artifact-name :a :as artifact}]
  (base "Artifact - Find Artifact" (artifact-content (search-form false (format "%s:%s" group artifact-name)) artifact)))
