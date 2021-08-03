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

j-package(){
  OS=${1:?"Need OS type (windows/linux/mac)"}

  echo "Starting compilation..."

  if [ "$OS" == "windows" ]; then
    J_ARG="--win-menu --win-dir-chooser --win-shortcut"
          
  elif [ "$OS" == "linux" ]; then
      J_ARG="--linux-shortcut"
  else
      J_ARG=""
  fi

  jpackage \
    --input out/jpackage-input \
    --dest out \
    --main-jar rocinante.standalone.jar \
    --name "rocinante" \
    --main-class clojure.main \
    --arguments -m \
    --arguments rocinante.main \
    --app-version "1" \
    $J_ARG
}

"$@"