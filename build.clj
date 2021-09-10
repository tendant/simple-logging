(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]
            [simple.build :as sb]))

(def lib 'org.clojars.wang/simple-logging)
;; if you want a version of MAJOR.MINOR.COMMITS:
(def version (format "1.0.%s" (b/git-count-revs nil)))

(def scm {:url "https://github.com/tendant/simple-logging"})

(defn jar
  [opts]
  (-> opts
      (assoc :lib lib :version version :scm scm)
      (bb/clean)
      (bb/jar)))

(defn install
  [opts]
  (-> opts
      (assoc :lib lib :version version :scm scm)
      (sb/install)))

(defn release
  [opts]
  (-> opts
      (assoc :lib lib :version version :scm scm)
      (sb/release)))
