(defproject simple-logging "0.0.0"
  :description "FIXME: write description"
  :url "https://github.com/org.clojars.wang/simple-logging"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.taoensso/timbre "5.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.19"]
                 [org.slf4j/slf4j-api "1.7.21"]
                 [org.slf4j/log4j-over-slf4j "1.7.30"]
                 [cheshire/cheshire "5.10.0"]]
  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[simple-logging \"[0-9.]*\"\\\\]/[simple-logging \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])
