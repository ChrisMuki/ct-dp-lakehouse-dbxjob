#!/usr/bin/env zsh
set -euo pipefail

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

su - sparkdev -c 'cs setup --yes'