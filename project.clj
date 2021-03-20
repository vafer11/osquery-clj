(defproject vafer/osquery-clj "0.2.1-alpha"
  :description "A super tiny library to integrate osquery tool to your code."
  :url "https://github.com/vafer11/osquery-clj"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [javax.annotation/javax.annotation-api "1.3.2"]
                 [org.slf4j/slf4j-api "2.0.0-alpha1"]
                 [org.slf4j/slf4j-nop "2.0.0-alpha1"]
                 [org.apache.thrift/libthrift "0.13.0"]
                 [net.java.dev.jna/jna "4.5.0"]
                 [net.java.dev.jna/jna-platform "4.5.0"]
                 [com.kohlschutter.junixsocket/junixsocket-core "2.3.2"]]

  :source-paths      ["src"]
  :java-source-paths ["src/java/gen"]
  :repl-options {:init-ns osquery-clj.core}
  :aot [osquery-clj.interfaces])
