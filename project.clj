(defproject innogrid "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.clojure/tools.logging "1.3.0"]
                 [ch.qos.logback/logback-classic "1.5.6"]
                 [funcool/struct "1.4.0"]
                 [clojure.java-time "1.4.2"]
                 [cprop "0.1.20"]
                 [expound "0.9.0"]
                 [mount "0.1.19"]
                 [nrepl "1.2.0"]
                 [cheshire "5.13.0"]
                 [luminus-transit "0.1.6"]
                 [luminus/ring-undertow-adapter "1.3.1" :excutions [undertow-core]]
                 [io.undertow/undertow-core "2.3.17.Final"]
                 [metosin/muuntaja "0.6.10"]
                 [metosin/schema-tools "0.13.1"]
                 [metosin/reitit "0.7.2"]
                 [metosin/ring-http-response "0.9.4"]
                 [ring/ring-core "1.12.2"]
                 [ring/ring-defaults "0.5.0"]
                 [com.zaxxer/HikariCP "5.1.0"]
                 [org.mariadb.jdbc/mariadb-java-client "3.3.3"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [honeysql/honeysql "1.0.461"]
                 [com.walmartlabs/lacinia "1.2.2"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot innogrid.cicd

  :plugins [] 

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "uber.jar"
             :source-paths ["env/common/clj" "env/prd/clj" ]
             :resource-paths ["env/prd/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[org.clojure/tools.namespace "1.5.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.12.2"]
                                 [ring/ring-mock "0.4.0"]
                                 [selmer "1.12.61"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]
                                 [jonase/eastwood "1.4.3"]
                                 [cider/cider-nrepl "0.50.2"]]
                  
                  :source-paths ["env/common/clj" "env/dev/clj" ]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"] }
   :profiles/dev {}
   :profiles/test {}})
