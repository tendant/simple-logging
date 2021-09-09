(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]
            [deps-deploy.deps-deploy :as dd]
            [clojure.string :as string]))

(def lib 'org.clojars.wang/simple-logging)
;; if you want a version of MAJOR.MINOR.COMMITS:
(def version (format "0.0.%s" (b/git-count-revs nil)))
(def basis (b/create-basis {:project "deps.edn"}))

(defn jar
  "Run the CI pipeline of tests (and build the JAR)."
  [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/clean)
      (bb/jar))
  opts)

(def ^:private default-target "target")
(def ^:private default-basis (b/create-basis {:project "deps.edn"}))
(defn- default-class-dir [target] (str target "/classes"))
(defn- default-jar-file [target lib version]
  (format "%s/%s-%s.jar" target (name lib) version))

(defn install
  "Install to local maven repository ~/.m2"
  [{:keys [target class-dir jar-file] :as opts}]
  (let [target (or target default-target)
        class-dir (or class-dir (default-class-dir target))
        jar-file  (or jar-file (default-jar-file target lib version))]
    (-> opts
        (jar)
        (assoc :lib lib
               :version version
               :basis basis
               :target target
               :class-dir class-dir
               :jar-file jar-file)
        (b/install))
    opts))

(defn clojars
  "Deploy the JAR to Clojars.
  Requires: lib, version"
  [{:keys [lib version target class-dir jar-file] :as opts}]
  (assert (and lib version) "lib and version are required for deploy")
  (let [target    (or target default-target)
        class-dir (or class-dir (default-class-dir target))
        jar-file  (or jar-file (default-jar-file target lib version))]
    (dd/deploy (merge {:installer :remote :artifact jar-file
                       :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
                      opts)))
  opts)

(defn deploy
  [opts]
  (-> opts
      (assoc :lib lib :version version)
      (git-tag-version {:version version})
      (jar)
      (clojars)))

(defn git-tag-version
  "Shells out to git and tag current commit using version:
    git tag <version>
  Options:
    :dir - dir to invoke this command from, by default current directory
    :path - path to count commits for relative to dir"
  [{:keys [dir path version] :or {dir "."} :as opts}]
  (println {:command-args (cond-> ["git" "tag" version]
                       path (conj "--" path))
            :dir (.getPath (b/resolve-path dir))
            :out :capture})
  (-> {:command-args (cond-> ["git" "tag" version]
                       path (conj "--" path))
       :dir (.getPath (b/resolve-path dir))
       :out :capture}
      b/process
      :out)
  opts)
