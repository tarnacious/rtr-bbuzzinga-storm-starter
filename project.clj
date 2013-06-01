(defproject storm-starter "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :java-source-paths ["src/jvm"]
  :resource-paths ["multilang"]
  :main cli
  :aot :all
  :dependencies [
                   [commons-collections/commons-collections "3.2.1"]
                   [com.taoensso/carmine "1.7.0"]
                   [clojurewerkz/elastisch "1.1.0"]
                   [clj-http "0.7.2"]
                 ]

  :profiles {:dev
              {:dependencies [[storm "0.8.2"]
                              [org.clojure/clojure "1.4.0"]]}}
  :min-lein-version "2.0.0"
  )
