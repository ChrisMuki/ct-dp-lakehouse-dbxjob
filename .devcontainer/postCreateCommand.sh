#!/usr/bin/env bash
set -euo pipefail

JVMOPTS=".jvmopts"
TEMPLATE=".jvmopts.template"

# Immer neu starten: Template kopieren oder leere Datei anlegen
if [ -f "$TEMPLATE" ]; then
    cp "$TEMPLATE" "$JVMOPTS"
else
    : > "$JVMOPTS"   # leere Datei erzeugen
fi


escape_jvm_value() {
    local raw="$1"
    raw="${raw//\\/\\\\}"   # escape existing backslashes
    raw="${raw//|/\\|}"        # escape pipes so JVM reads them literally
    printf '%s' "$raw"
}

append_jvm_opt() {
    local key="$1"
    local value
    value="$(escape_jvm_value "$2")"
    printf -- '-D%s=%s\n' "$key" "$value" >> "$JVMOPTS"
}

if [ -z "${CONTAINER_NEEDED_PROXY:-}" ]; then
    :
else
    append_jvm_opt http.proxyHost "$JAVA_proxyHost"
    append_jvm_opt http.proxyPort "$JAVA_proxyPort"
    append_jvm_opt http.nonProxyHosts "$JAVA_nonProxyHosts"
    append_jvm_opt https.proxyHost "$JAVA_proxyHost"
    append_jvm_opt https.proxyPort "$JAVA_proxyPort"
    append_jvm_opt https.nonProxyHosts "$JAVA_nonProxyHosts"
fi

echo "$JVMOPTS created/patched"

echo "Cleaning up old build artifacts..."
rm -rf .metals
rm -rf .vscode
rm -rf project/project/
rm -rf project/target/
rm -rf project/metals.sbt
rm -rf target/


(sbt compile || true)

echo 'Devcontainer setup completed'
