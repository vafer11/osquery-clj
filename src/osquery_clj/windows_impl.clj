(ns osquery-clj.windows-impl
  (:import (com.sun.jna.win32 W32APIOptions)
           (com.sun.jna Native Memory)
           (java.io InputStream OutputStream IOException)
           (java.util Arrays)
           (com.sun.jna.platform.win32 WinNT WinBase$OVERLAPPED WinError WinNT$HANDLE)
           (com.sun.jna.ptr IntByReference)))

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
      (throw (IOException. "Failed to open named pipe...")))
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
          (when-not imm
            (when-not (= (.GetLastError API) WinError/ERROR_IO_PENDING)
              (throw (IOException. "ReadFile failed..."))))
          (when-not (.GetOverlappedResult API pipe-handle (.getPointer olap) read true)
            (throw (IOException. "GetOverlappedResult() failed for read operation...")))
          (when-not (= (.getValue read) len)
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
          (when-not imm
            (when-not (= (.GetLastError API) WinError/ERROR_IO_PENDING)
              (throw (IOException. "WriteFile() failed..."))))
          (when-not (.GetOverlappedResult API pipe-handle (.getPointer olap) written true)
            (throw (IOException. "GetOverlappedResult() failed for write operation...")))
          (when-not (= (.getValue written) len)
            (throw (IOException. "WriteFile() wrote less bytes than requested..."))))))))