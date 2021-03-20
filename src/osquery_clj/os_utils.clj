(ns osquery-clj.os-utils
  (:import java.io.File))

(defn get-os-name [] (System/getProperty "os.name"))
(defn is-windows? [] (. (get-os-name) startsWith "Windows"))
(defn is-unix? [] (. (get-os-name) startsWith "Unix"))
