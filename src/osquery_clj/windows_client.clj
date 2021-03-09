(ns osquery-clj.windows-client
  (:require [osquery-clj.windows-impl :as w]
            [osquery-clj.cmd-utils :as cmd])
  (:import (org.apache.thrift.transport TIOStreamTransport)
           (org.apache.thrift.protocol TBinaryProtocol)
           (osquery.extensions ExtensionManager$Client)))

(def state (atom {}))

(defn get-client! []
  (let [API (w/get-API)
        pipe-handle (w/get-pipe-handle API)
        reader-waitable (w/get-waitable API)
        writer-waitable (w/get-waitable API)
        input-stream (w/get-input-stream API pipe-handle reader-waitable)
        output-stream (w/get-output-stream API pipe-handle writer-waitable)]

    (let [transport (TIOStreamTransport. input-stream output-stream)
          protocol (TBinaryProtocol. transport)]
      (.open transport)
      (let [client (ExtensionManager$Client. protocol)]
        (reset! state {:transport transport
                       :API API
                       :pipe-handle pipe-handle
                       :rwaitable reader-waitable
                       :wwritable writer-waitable})
        client))))

(defn query [query]
  (cmd/spawn-instance)
  (-> (get-client!)
      (.query query)
      (.-response)))

(defn close []
  (let [{transport :transport API :API ph :pipe-handle rw :rwaitable ww :wwritable} @state]
    (when-not (nil? transport) (.close transport))
    (.CloseHandle API ph)
    (.CloseHandle API rw)
    (.CloseHandle API ww)))

