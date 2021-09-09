(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]
            [deps-deploy.deps-deploy :as dd]
            ))

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

(defn deploy
  "Deploy the JAR to Clojars.
  Requires: lib, version"
  [{:keys [target class-dir lib version jar-file] :as opts}]
  (assert (and lib version) "lib and version are required for deploy")
  (let [target    (or target default-target)
        class-dir (or class-dir (default-class-dir target))
        jar-file  (or jar-file (default-jar-file target lib version))]
    (-> opts
        (jar)
        (dd/deploy (merge {:installer :remote :artifact jar-file
                           :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
                          opts))))
  opts)