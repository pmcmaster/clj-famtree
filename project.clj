(defproject famtree "0.1.0-SNAPSHOT"
  :description "Matches birth/death/marriage/census records on shared fields"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/data.csv "1.1.1"]]
  :main ^:skip-aot famtree.core
  :target-path "target/%s"
  :profiles {:profiling 
             {:dependencies
              [[com.clojure-goes-fast/clj-async-profiler "2.0.0-beta1"]]
              :jvm-opts ["-Djdk.attach.allowAttachSelf"]}


             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})

;; lein with-profile +profiling
; (comment 
;   (require '[clj-async-profiler.core :as prof])
;   (prof/profile (-main))             
;   (prof/serve-ui 8080)
;   )
