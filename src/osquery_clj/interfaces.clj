(ns osquery-clj.interfaces)

(gen-interface
  :name osquery-clj.interfaces.IWindowsNamedPipeLibrary
  :extends [com.sun.jna.platform.win32.Kernel32]
  :methods
  [[GetOverlappedResult [com.sun.jna.platform.win32.WinNT$HANDLE
                         com.sun.jna.Pointer
                         com.sun.jna.ptr.IntByReference
                         boolean] boolean]

   [ReadFile [com.sun.jna.platform.win32.WinNT$HANDLE
              com.sun.jna.Memory
              int
              com.sun.jna.ptr.IntByReference
              com.sun.jna.Pointer] boolean]

   [CreateFile [String
                int
                int
                com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES
                int
                int
                com.sun.jna.platform.win32.WinNT$HANDLE] com.sun.jna.platform.win32.WinNT$HANDLE]

   [CreateEvent [com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES
                 boolean
                 boolean
                 boolean] com.sun.jna.platform.win32.WinNT$HANDLE]

   [CloseHandle [com.sun.jna.platform.win32.WinNT$HANDLE] boolean]

   [WriteFile [com.sun.jna.platform.win32.WinNT$HANDLE
               bytes
               int
               com.sun.jna.ptr.IntByReference
               com.sun.jna.Pointer] boolean]

   [GetLastError [] int]])