(defproject can "0.2.1-SNAPSHOT"
  :description "Permissions library for Clojure[Script]"
  :url "https://github.com/shaolang/can"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :aliases {"test-cljs"  ["trampoline" "run" "-m" "kaocha.runner" "unit-cljs"]}

  :profiles {:dev {:dependencies [[lambdaisland/kaocha      "0.0-389"]
                                  [lambdaisland/kaocha-cljs "0.0-16"]
                                  [org.clojure/clojure      "1.10.0"]]}}

  :repl-options {:init-ns can.can}

  :scm {:name "git"
        :url "https://github.com/shaolang/can"})
