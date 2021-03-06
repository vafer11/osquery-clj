(defproject vafer/osquery-clj "0.1.0-alpha"
  :description "A super tiny library to integrate osquery tool to your code."
  :url "https://github.com/vafer11/osquery-clj"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;; https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api
                 [javax.annotation/javax.annotation-api "1.3.2"]
                 ;; https://mvnrepository.com/artifact/org.slf4j/slf4j-api
                 [org.slf4j/slf4j-api "2.0.0-alpha1"]
                 ;; https://mvnrepository.com/artifact/org.slf4j/slf4j-nop
                 [org.slf4j/slf4j-nop "2.0.0-alpha1"]
                 ;; https://mvnrepository.com/artifact/org.apache.thrift/libthrift
                 [org.apache.thrift/libthrift "0.13.0"]
                 ;; https://mvnrepository.com/artifact/net.java.dev.jna/jna
                 [net.java.dev.jna/jna "4.5.0"]
                 ;; https://mvnrepository.com/artifact/net.java.dev.jna/jna-platform
                 [net.java.dev.jna/jna-platform "4.5.0"]]
  :source-paths      ["src"]
  :java-source-paths ["src/java/gen"]
  :java-cmd "C:\\Program Files\\Java\\jdk-12.0.1\\bin\\java"
  :repl-options {:init-ns osquery-clj.core})
