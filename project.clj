(defproject tm "0.1.0-SNAPSHOT"
  :description "Time Series Merge Problem"
  :url ""
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure       "1.8.0"]
                 [org.clojure/tools.cli     "0.3.5"]
                 [org.xerial/sqlite-jdbc    "3.21.0.1"]
                 [clj-time                  "0.14.2"]
                 [org.clojure/java.jdbc     "0.7.5"
                  :exclusions [mysql/mysql-connector-java
                               org.apache.derby/derby
                               hsqldb
                               net.sourceforge.jtds/jtds]]]
  :main ^:skip-aot tm.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :jvm-opts ["-Xms512m" "-Xmx512m"]}
             :dev     {:source-paths   #{"dev"}
                       :resource-paths #{"resources"}
                       :plugins        [[lein-cljfmt "0.5.4"]]
                       :cljfmt         {:indentation?                    false
                                        :file-pattern                    #"\.clj[sc]?$"
                                        :remove-consecutive-blank-lines? false}}})
