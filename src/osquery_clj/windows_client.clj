(ns osquery-clj.windows-client
  (:require [osquery-clj.windows-impl :as w])
  (:import (org.apache.thrift.transport TIOStreamTransport)
           (org.apache.thrift.protocol TBinaryProtocol)
           (osquery.extensions ExtensionManager$Client)))

(defn get-client []
  (let [API (w/get-API)
        pipe-handle (w/get-pipe-handle API)
        reader-waitable (w/get-waitable API)
        writer-waitable (w/get-waitable API)
        input-stream (w/get-input-stream API pipe-handle reader-waitable)
        output-stream (w/get-output-stream API pipe-handle writer-waitable)]

    (let [transport (TIOStreamTransport. input-stream output-stream)
          protocol (TBinaryProtocol. transport)]
      (.open transport)
      (let [client (ExtensionManager$Client. protocol)] client))))

; Transport .close need to be implemented
; if(this.transport != null) this.transport.close()
;
; Close handle ...
; 	public void close() throws IOException {
;	    API.CloseHandle(pipeHandle);
;	    API.CloseHandle(readerWaitable);
;	    API.CloseHandle(writerWaitable);
;	}

