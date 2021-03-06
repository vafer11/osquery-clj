(ns osquery-clj.core
  (:require [osquery-clj.os-utils :as os]
            [osquery-clj.cmd-utils :as cmd])
  (:import (com.sun.jna.win32 W32APIOptions)
           (com.sun.jna Native Memory)
           (java.io InputStream OutputStream IOException)
           (java.util Arrays)
           (com.sun.jna.platform.win32 WinNT WinBase$OVERLAPPED WinError WinNT$HANDLE)
           (com.sun.jna.ptr IntByReference)
           (org.apache.thrift.transport TIOStreamTransport)
           (org.apache.thrift.protocol TBinaryProtocol)
           (osquery.extensions ExtensionManager$Client ExtensionResponse)))


(def obj1 (reify osquery-clj.interfaces.iexample1
            (getName [this] "Valentin")
            (getLastName [this] "Fernandez")))

(defn get-API []
  (let [API ^osquery-clj.interfaces.IWindowsNamedPipeLibrary (Native/loadLibrary
                                                               "kernel32"
                                                               osquery-clj.interfaces.IWindowsNamedPipeLibrary
                                                               W32APIOptions/UNICODE_OPTIONS)]
    API))

(defn get-pipe-handle [API]
  (let [pipe-handle ^WinNT$HANDLE (.CreateFile API
                                               "\\\\.\\pipe\\osquery.em"
                                               (bit-or WinNT/GENERIC_READ WinNT/GENERIC_WRITE)
                                               0
                                               nil
                                               WinNT/OPEN_EXISTING
                                               WinNT/FILE_FLAG_OVERLAPPED
                                               nil)]
    (when (.equals WinNT/INVALID_HANDLE_VALUE pipe-handle)
      (throw (IOException. (str "Failed to open named pipe. Error code: " (.GetLastError API)))))
    pipe-handle))

(defn get-waitable [API] (.CreateEvent API nil true false nil))

(defn get-input-stream [API pipe-handle reader-waitable]
  (proxy [InputStream] []
    (read [b off len]
      (let [read-buffer (Memory. len) olap (WinBase$OVERLAPPED.)]
        (set! (.hEvent olap) reader-waitable)
        (.write olap)
        (let [imm (.ReadFile API pipe-handle read-buffer len nil (.getPointer olap))
              read (IntByReference.)]
          (when (not imm)
            (when (not= (.GetLastError API ) WinError/ERROR_IO_PENDING)
              (throw (IOException. "ReadFile failed..."))))
          (when (not (.GetOverlappedResult API pipe-handle (.getPointer olap) read true))
            (throw (IOException. "GetOverlappedResult() failed for read operation...")))
          (when (not= (.getValue read) len)
            (throw (IOException. "ReadFile() read less bytes than requested...")))
          (let [byte-array (.getByteArray read-buffer 0 len)]
            (System/arraycopy byte-array 0 b off len)
            len))))))

(defn get-output-stream [API pipe-handle writer-waitable]
  (proxy [OutputStream] []
    (write [b off len]
      (let [data (Arrays/copyOfRange b off (+ off len))
            olap (WinBase$OVERLAPPED.)]
        (set! (.hEvent olap) writer-waitable)
        (.write olap)
        (let [imm (.WriteFile API ^WinNT$HANDLE pipe-handle data len nil (.getPointer olap))
              written (IntByReference.)]
          (when (not imm)
            (when (not= (.GetLastError API) WinError/ERROR_IO_PENDING)
              (throw (IOException. "WriteFile() failed..."))
              ))
          (when (not (.GetOverlappedResult API pipe-handle (.getPointer olap) written true))
            (throw (IOException. "GetOverlappedResult() failed for write operation...")))
          (when (not= (.getValue written) len)
            (throw (IOException. "WriteFile() wrote less bytes than requested..."))))))))

(defn get-windows-named-pipe-impl [state]
  (reify osquery-clj.interfaces.IWindowsNamedPipe
    (getInputStream [this] (:in state))
    (getOutputStream [this] (:out state))
    (close [this] (doto (:API state)
                    (.CloseHandle (:pipe-handle state))
                    (.CloseHandle (:rwaitable state))
                    (.CloseHandle (:wwaitable state))))))

(defn main []
  (cmd/spawn-instance)
  ;;(cmd/run-command "notepad")
  (.getName obj1)
  (.toString (get-API))

  (let [API (get-API)
        pipe-handle (get-pipe-handle API)
        reader-waitable (get-waitable API)
        writer-waitable (get-waitable API)
        input-stream (get-input-stream API pipe-handle reader-waitable)
        output-stream (get-output-stream API pipe-handle writer-waitable)
        state {:API API
               :pipe-handle pipe-handle
               :rwaitable reader-waitable
               :wwaitable writer-waitable
               :in input-stream
               :out output-stream}
        impl (get-windows-named-pipe-impl state)]
    (let [transport (TIOStreamTransport. (.getInputStream impl) (.getOutputStream impl))
          protocol (TBinaryProtocol. transport)]
      (.open transport)
      (let [client (ExtensionManager$Client. protocol)]
        (let [res ^ExtensionResponse (.query client "select name from processes;")]
          (println (.-response res)))))))
