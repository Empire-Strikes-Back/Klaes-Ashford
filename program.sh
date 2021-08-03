#!/bin/bash

repl(){
  clj \
    -X:repl deps-repl.core/process \
    :main-ns rocinante.main \
    :port 7788 \
    :host '"0.0.0.0"' \
    :repl? true \
    :nrepl? false
}

main(){
  clojure \
    -J-Dclojure.core.async.pool-size=1 \
    -J-Dclojure.compiler.direct-linking=false \
    -M -m rocinante.main
}

uberjar(){
  clj \
    -X:uberjar genie.core/process \
    :uberjar-name out/rocinante.standalone.jar \
    :main-ns rocinante.main
  mkdir -p out/jpackage-input
  mv out/rocinante.standalone.jar out/jpackage-input/
}


"$@"