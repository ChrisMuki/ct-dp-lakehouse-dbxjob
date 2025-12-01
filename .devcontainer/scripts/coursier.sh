#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$SCRIPT_DIR/parse_proxy_env.sh"


# Detect system architecture
ARCH="$(uname -m)"

case "$ARCH" in

  x86_64)
    CS_URL="https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz"
    ;;

  aarch64|arm64)
    CS_URL="https://github.com/coursier/launchers/raw/master/cs-aarch64-pc-linux.gz"
    ;;

  *)
    echo "Unsupported architecture: $ARCH"
    exit 1
    ;;

esac


echo "===> Coursier => Downloading Coursier launcher ($ARCH) from $CS_URL ..."
curl -fL  "$CS_URL" | gzip -d > /home/vscode/cs
chmod +x /home/vscode/cs


if [ -z "${CONTAINER_NEEDED_PROXY:-}" ]; then
  /home/vscode/cs  \
    setup --jvm temurin:1.17 --yes
else
  /home/vscode/cs -J-Dhttps.proxyHost=$JAVA_proxyHost -J-Dhttps.proxyPort=$JAVA_proxyPort \
    setup --jvm temurin:1.17 --yes
fi


echo "===> Coursier Done"

