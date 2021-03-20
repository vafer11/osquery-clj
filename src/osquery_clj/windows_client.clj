(ns osquery-clj.windows-client
  (:require [osquery-clj.windows-impl :as w]
            [osquery-clj.cmd-utils :as cmd])
  (:import (org.apache.thrift.transport TIOStreamTransport)
           (org.apache.thrift.protocol TBinaryProtocol)
           (osquery.extensions ExtensionManager$Client)))

(def state (atom {}))

(defn init! []
  (cmd/spawn-instance)
  (let [API (w/get-API)
        pipe-handle (w/get-pipe-handle API)
        reader-waitable (w/get-waitable API)
        writer-waitable (w/get-waitable API)
        input-stream (w/get-input-stream API pipe-handle reader-waitable)
        output-stream (w/get-output-stream API pipe-handle writer-waitable)
        transport (TIOStreamTransport. input-stream output-stream)
        protocol (TBinaryProtocol. transport)
        client (ExtensionManager$Client. protocol)]
    (.open transport)
    (reset! state {:transport transport
                   :API API
                   :pipe-handle pipe-handle
                   :rwaitable reader-waitable
                   :wwritable writer-waitable
                   :in input-stream
                   :out output-stream
                   :client client})))

(defn close []
  (let [{transport :transport API :API ph :pipe-handle rw :rwaitable ww :wwritable in :in out :out} @state]
    (when-not (nil? transport) (.close transport))
    (when-not (nil? in) (.close in))
    (when-not (nil? out) (.close out))
    (.CloseHandle API ph)
    (.CloseHandle API rw)
    (.CloseHandle API ww)))

; If an exception is thrown, will retry until 5 times.

(defn query [query]
  (loop [n 10]
    (let [result (try (init!)
                      (-> (.query (:client @state) query) (.-response) (vec))
                      (catch Exception e nil))]
      (if result
        (do (close) result)
        (if (< n 0) [] (recur (dec n)))))))