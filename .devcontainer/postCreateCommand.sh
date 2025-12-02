#!/usr/bin/env bash
set -euo pipefail

####################
##### Clean up #####
####################
echo "Cleaning up old build artifacts..."
rm -rf .metals
rm -rf .vscode
rm -rf project/project/
rm -rf project/target/
rm -rf project/metals.sbt
rm -rf target/

####################
##### JVM OPTS #####
####################
JVMOPTS=".jvmopts"
TEMPLATE=".jvmopts.template"

# Immer neu starten: Template kopieren oder leere Datei anlegen
if [ -f "$TEMPLATE" ]; then
    cp "$TEMPLATE" "$JVMOPTS"
else
  : > "$JVMOPTS"   # create empty file
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
    append_jvm_opt java.net.useSystemProxies "true"

    # JVM-Optionen auch Coursier explizit mitgeben (Metals nutzt eigenen Coursier)
    if [ -f "$JVMOPTS" ]; then
      # aus Zeilen eine einzelne Zeile mit Leerzeichen machen
      COURSIER_JVM_OPTS="$(tr '\n' ' ' < "$JVMOPTS")"
      export COURSIER_JVM_OPTS
    fi
fi

  echo "$JVMOPTS created/patched"


####################
#### sbt config ####
####################
SBT_CRED_TARGET_DIR="$HOME/.sbt"
SBT_CRED_TARGET="$SBT_CRED_TARGET_DIR/.credentials"
SBT_CRED_SOURCE="$HOME/host.home/.sbt/.credentials"

if [[ -f "$SBT_CRED_SOURCE" ]]; then
  echo "Copying sbt credentials into container: $SBT_CRED_SOURCE -> $SBT_CRED_TARGET"
  mkdir -p "$SBT_CRED_TARGET_DIR"
  cp "$SBT_CRED_SOURCE" "$SBT_CRED_TARGET"
else
  echo "No sbt credentials found at $SBT_CRED_SOURCE, skipping copy."
fi


####################
##### sbt init #####
####################
(sbt --batch reload || true)


####################
#### git Config ####
####################
GITCONFIG_PATH="$HOME/.gitconfig"
HOST_GITCONFIG_PATH="$HOME/host.home/.gitconfig"

# nur etwas tun, wenn die Host-.gitconfig existiert
if [[ ! -f "$HOST_GITCONFIG_PATH" ]]; then
  echo "Host gitconfig '$HOST_GITCONFIG_PATH' not found, skipping linking."
else
  # Vorhandene .gitconfig (Datei oder Symlink) entfernen
  if [[ -e "$GITCONFIG_PATH" || -L "$GITCONFIG_PATH" ]]; then
    echo "Existing .gitconfig found, removing it..."
    rm -f "$GITCONFIG_PATH"
  fi
  echo "Copying host .gitconfig into container: $HOST_GITCONFIG_PATH -> $GITCONFIG_PATH"
  cp "$HOST_GITCONFIG_PATH" "$GITCONFIG_PATH"
fi


echo 'Devcontainer setup completed'