(ns osquery-clj.cmd-utils)

(def cmd-command "osqueryd --ephemeral --disable_logging --disable_database \\ --extensions_socket \\\\.\\pipe\\osquery.em")

(defn spawn-instance [] (. (Runtime/getRuntime) exec cmd-command))
(defn run-command [command] (. (Runtime/getRuntime) exec command))