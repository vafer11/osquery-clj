(ns osquery-clj.core
  (:require [osquery-clj.os-utils :as os]
            [osquery-clj.cmd-utils :as cmd]
            [osquery-clj.windows-client :as windows]))

(defn execute-windows-query []
  (cmd/spawn-instance)
  (-> (windows/get-client)
      (.query "select name from processes;")
      (.-response)
      (println)))

(defn execute-linux-query []
  (println "Need to be done..."))

(defn main []
  (if (os/is-windows?)
    (execute-windows-query)
    (execute-linux-query)))
