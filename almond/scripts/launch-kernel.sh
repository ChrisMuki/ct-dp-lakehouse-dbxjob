#!/usr/bin/env bash
# Forwards to the Almond kernel installed by `coursier launch almond -- --install ...`,
# but injects the sbt-managed project classpath via --extra-class-path so notebooks
# can `import` everything in the `almond` sbt subproject (and its dependsOn projects).
#
# Jupyter substitutes {connection_file} into the argv list before exec.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CP_FILE="$REPO_ROOT/almond/target/classpath.txt"
LAUNCHER_JAR="$HOME/.local/share/jupyter/kernels/scala-sbt/launcher.jar"

if [[ ! -s "$CP_FILE" ]]; then
  echo "[almond-launcher] $CP_FILE missing. Running 'sbt almond/writeClasspath' ..." >&2
  (cd "$REPO_ROOT" && sbt -batch -no-colors "almond/writeClasspath" 1>&2)
fi

PROJECT_CP="$(cat "$CP_FILE")"

exec java \
  -cp "$LAUNCHER_JAR" \
  coursier.bootstrap.launcher.Launcher \
  --id scala-sbt \
  --display-name "Scala (sbt almond)" \
  --extra-class-path "$PROJECT_CP" \
  --connection-file "$@"
