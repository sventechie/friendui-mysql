(defproject sventechie/friendui-mysql "0.0.3-SNAPSHOT"
  :description "A helper that implements friendui storage protocol for MySQL"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [de.sveri/clojure-commons "0.1.4"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.34"]
                 [yesql "0.5.0-rc2"] ; db access
                 [enlive "1.1.5"] ; html templating
                 [de.sveri/friendui "0.4.6"]]
  :profiles {:repl {:dependencies []}}
  :deploy-repositories [["clojars-self" {:url "https://clojars.org/repo"
                                         :sign-releases false}]])
