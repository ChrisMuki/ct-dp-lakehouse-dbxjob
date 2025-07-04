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

su - dnadev -c 'cs java --jvm temurin:1.17 --setup'
echo "JAVA_HOME:" 
echo $JAVA_HOME

cs java --jvm temurin:1.17 --update --env
echo "Eval --env"
eval "$(cs java --jvm temurin:1.17 --update --env)"

echo "JAVA_HOME:" 
echo $JAVA_HOME


CERT_SRC_DIR="$(dirname "$0")/certificates"
echo "in feature install 'java-17-setup' $CERT_SRC_DIR"
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert1 -file "$CERT_SRC_DIR"/conti-ca-cert1.crt -cacerts -storepass changeit
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert2 -file "$CERT_SRC_DIR"/conti-ca-cert2.crt -cacerts -storepass changeit
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert3 -file "$CERT_SRC_DIR"/conti-ca-cert3.crt -cacerts -storepass changeit
################################################################################

################################################################################
# git lfs
sudo apt-get install git-lfs
################################################################################