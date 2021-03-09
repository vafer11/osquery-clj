(ns osquery-clj.unix-client
  (:import (java.io File)
           (org.newsclub.net.unix AFUNIXSocketAddress AFUNIXSocket)
           (org.apache.thrift.transport TIOStreamTransport)
           (org.apache.thrift.protocol TBinaryProtocol)
           (osquery.extensions ExtensionManager$Client)))

(def state (atom {}))

(defn get-client! []
  (let [socket (-> (File. "/var/osquery/osquery.em")
                   (AFUNIXSocketAddress.)
                   (AFUNIXSocket/connectTo))
        transport (TIOStreamTransport. (.getInputStream socket) (.getOutputStream socket))
        protocol (TBinaryProtocol. transport)]
    (reset! state {:transport transport})
    (ExtensionManager$Client. protocol)))

(defn query [query]
  (let [client (get-client!)
        res (.query client query)]
    res))

(defn close [] (.close (:transport @state)))