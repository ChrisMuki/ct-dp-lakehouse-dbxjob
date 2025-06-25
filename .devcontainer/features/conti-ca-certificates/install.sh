#!/usr/bin/env bash
set -euo pipefail

apt-get update && apt-get install -y ca-certificates
mkdir -p /usr/local/share/ca-certificates/

CERT_SRC_DIR="$(dirname "$0")/certificates"

echo "in feature install 'conti-ca-certificates' $CERT_SRC_DIR"

if [ ! -d "$CERT_SRC_DIR" ]; then
  echo "Certificate directory $CERT_SRC_DIR not found!" >&2
  exit 1
fi

# Alle .crt-Dateien kopieren
cp "$CERT_SRC_DIR"/*.crt /usr/local/share/ca-certificates/

# Truststore aktualisieren (Debian/Ubuntu)
update-ca-certificates
