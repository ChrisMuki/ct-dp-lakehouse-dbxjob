#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$SCRIPT_DIR/parse_proxy_env.sh"


echo "===> Java confiog for vscode..."

/home/vscode/cs java --jvm temurin:1.17 --setup


set +u  # export CS_FORMER_JAVA_HOME="$JAVA_HOME" sonst zum fehler führt
eval "$(/home/vscode/cs java --jvm temurin:1.17 --env)"
set -u

echo "===> Java confiog for vscode Done at $JAVA_HOME"

CERT_SRC_DIR="/usr/local/share/ca-certificates/"   

STORE="$JAVA_HOME/lib/security/cacerts"
echo "===> Java Keystore updating at: $STORE"

# 3) Import/replace all certificates

for crt_file in "$CERT_SRC_DIR"/*.crt; do
  if [ -f "$crt_file" ]; then
    crt_filename=$(basename "$crt_file")
    alias_name="${crt_filename%.crt}"
    echo "Importiere Zertifikat $crt_file mit Alias $alias_name"
    "$JAVA_HOME/bin/keytool" -import -noprompt -trustcacerts -alias "$alias_name" -file "$crt_file" -cacerts -storepass changeit
  fi
done

echo "===> Java Keystore Done"