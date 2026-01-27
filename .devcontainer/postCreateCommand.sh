#!/usr/bin/env bash
set -euo pipefail

####################
##### Clean up #####
####################
echo "Cleaning up old build artifacts..."
rm -rf .bloop
rm -rf .metals
rm -rf .vscode
rm -rf project/project/
rm -rf project/target/
rm -rf project/metals.sbt
rm -rf target/


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