# simple-logging
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.wang/simple-logging.svg)](https://clojars.org/org.clojars.wang/simple-logging)

A Clojure library designed to simplify logging configuration.

```clj
[org.clojars.wang/simple-logging "1.0.21"]
```

```clj
org.clojars.wang/simple-logging {:mvn/version "1.0.21"}
```

## Usage

```clj
[taoensso.timbre :as log]
[simple.logging :as slog]

(log/merge-config! {:appenders {:json (slog/json-appender)
                                :println (slog/println-appender)}})
```

To bridge slf4j:

```clj
com.fzakaria/slf4j-timbre #:mvn{:version "0.3.19"}
org.slf4j/slf4j-api #:mvn{:version "1.7.21"}
```

To bridge log4j:
```clj
org.slf4j/log4j-over-slf4j #:mvn{:version "1.7.30"}
```

To bridge jboss logging:

```
-Dorg.jboss.logging.provider=slf4j
```

To bridge jboss logging in ```clj -M:run```:
```clj
:run {:main-opts ["-m" "app.core"]
      :extra-paths ["test"]
      :jvm-opts ["-Dorg.jboss.logging.provider=slf4j"]}
```

## Development

1. Install to local ~/.m2 maven repository

    clj -T:build install
    
2. Deploy to clojars

    clj -T:build deploy

## License

Copyright © 2021 Lei

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
