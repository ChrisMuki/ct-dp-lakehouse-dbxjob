#!/usr/bin/env zsh
# Version 2
set -e

################################################################################
# Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | bash
################################################################################

################################################################################
# Databricks CLI
curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | sh
################################################################################




################################################################################
# Coursier

set -uo pipefail

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

echo "==> Downloading Coursier launcher ($ARCH) from $CS_URL ..."

curl -fL  "$CS_URL" | gzip -d > /usr/local/bin/cs
chmod +x /usr/local/bin/cs

su - dnadev -c 'cs setup --yes'
################################################################################

################################################################################
# Java and certificates
set +u
set +o pipefail

# ---- settings ----
JVM_SPEC="temurin:1.17"                          # in sync with postCreateCommand.zsh
CERT_SRC_DIR="$(cd "$(dirname "$0")/certificates" && pwd -P)"   
# -------------------

# 1) Ensure the JDK is installed for the target user
su - dnadev -lc "cs java --jvm '$JVM_SPEC' --setup"

# 2) Resolve that user's JAVA_HOME deterministically
USER_JAVA_HOME="$(su - dnadev -lc "cs java-home --jvm '$JVM_SPEC'")"
STORE="$USER_JAVA_HOME/lib/security/cacerts"
echo "Using JAVA_HOME for dnadev: $USER_JAVA_HOME"
echo "Truststore: $STORE"

# 3) Import/replace all certificates

for crt_file in "$CERT_SRC_DIR"/*.crt; do
  if [ -f "$crt_file" ]; then
    crt_filename=$(basename "$crt_file")
    alias_name="${crt_filename%.crt}"
    echo "Importiere Zertifikat $crt_file mit Alias $alias_name"
    "$USER_JAVA_HOME/bin/keytool" -import -noprompt -trustcacerts -alias "$alias_name" -file "$crt_file" -cacerts -storepass changeit
  fi
done
################################################################################

################################################################################
# git lfs
sudo apt-get install git-lfs
################################################################################

################################################################################
# Pergola Cli 
curl -fsSL https://get.pergo.la/cli/latest/install.sh | bash
################################################################################