(ns simple.logging
  (:require [taoensso.timbre :as log]
            [taoensso.encore :as enc]
            [cheshire.core :as json]))

(defn json-output-fn
  ([data]
   (json-output-fn nil data))
  ([opts data]
   (let [{:keys [no-stacktrace? stacktrace-fonts]} opts
         {:keys [level ?err ?ns-str ?file ?line vargs_ hostname_ timestamp_ msg_ level] :as data} data
         loc (str (or ?ns-str ?file "?") ":" (or ?line "?"))
         err (when-not no-stacktrace?
               (when-let [err ?err]
                 (str enc/system-newline (log/stacktrace err opts))))
         msg (str @msg_ err)]
     (json/generate-string {:timestamp @timestamp_
                            :level     level
                            :loc loc
                            :hostname  @hostname_
                            :message   msg}))))

(def default-json-output-fn (partial json-output-fn {:stacktrace-fonts {}}))

(defn json-appender
  []
  (merge (taoensso.timbre.appenders.core/println-appender)
         {:enabled? true
          :output-fn default-json-output-fn}))

(def default-println-output-fn (partial log/default-output-fn {:stacktrace-fonts {}}))

(defn println-appender
  []
  (merge (taoensso.timbre.appenders.core/println-appender)
         {:enabled? true
          :output-fn default-println-output-fn}))
