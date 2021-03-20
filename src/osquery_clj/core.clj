(ns osquery-clj.core
  (:require [osquery-clj.os-utils :as os]
            [osquery-clj.cmd-utils :as cmd]
            [osquery-clj.windows-client :as windows]
            [osquery-clj.unix-client :as unix]))

(defn query [query]
  (if (os/is-windows?)
    (windows/query query)
    (unix/query query)))