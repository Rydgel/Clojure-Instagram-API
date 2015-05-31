(defproject instagram-api/instagram-api "0.2.0"
  :description "Clojure Instagram interface"
  :url "https://github.com/Rydgel/Clojure-Instagram-API"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-clojars "0.7.0"]
            [lein-swank "1.4.4"]
            [lein-eclipse "1.0.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]
                 [http.async.client "0.4.5"]]
  :profiles {:dev {:source-paths ["dev"]}})
