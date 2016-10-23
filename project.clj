(defproject postgres-tools "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [funcool/clojure.jdbc "0.9.0"]
                 [org.postgresql/postgresql "9.4.1210"]]
  :profiles {:dev {:resource-paths ["test/resources"]
                   :dependencies [[hikari-cp "1.6.1"]
                                  [mount "0.1.10"]
                                  [metosin/lokit "0.1.0"]
                                  [juxt/iota "0.2.3"]]}})