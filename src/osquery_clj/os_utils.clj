(ns osquery-clj.os-utils)

(defn get-os-name [] (System/getProperty "os.name"))
(defn is-windows? [] (. (get-os-name) startsWith "Windows"))
(defn is-unix? [] (. (get-os-name) startsWith "Unix"))
