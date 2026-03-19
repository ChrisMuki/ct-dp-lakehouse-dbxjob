#!/usr/bin/env sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ "${1-}" = "" ]; then
  echo "Usage: $0 <stage>" >&2
  echo "Example: $0 dev | $0 prod" >&2
  exit 1
fi

STAGE="$1"
BUILD_ID=$(date +"%Y%m%d-%H%M")

ASSET_PATH="/tmp/cicd"
ROOT_PATH="$SCRIPT_DIR/../"
JAR_PATH="$SCRIPT_DIR/../lakehouse/target/scala-2.13/lakehouse.jar"

CONFIG_FILE="deployment/configFiles/$STAGE.json"
set -eu

echo "[info] running sbt clean test lakehouse/assembly" >&2
if ! sbt "clean;test;lakehouse/assembly" ; then
    echo "[error] Build failed" >&2
    exit 1
fi

echo "[INFO] Deploy to $STAGE" >&2

if ! sbt \
    "cicd/runMain ct.dna.lakehouse.cicd.Deploy \
        \"stage=$STAGE\" \
        \"buildId=$BUILD_ID\" \
        \"rootPath=$ROOT_PATH\" \
        \"assetPath=$ASSET_PATH\" \
        \"jarPath=$JAR_PATH\" \
        \"configFile=$CONFIG_FILE\"" ; then
    echo "[ERROR] Deployment failed" >&2
    exit 1
fi

echo "[SUCCESS] Done: stage=$STAGE buildId=$BUILD_ID" >&2
