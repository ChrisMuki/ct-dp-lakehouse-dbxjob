#!/usr/bin/env bash
# Usage: ./testInternet.sh [output_file]

URL="https://aka.ms/InstallAzureCLIDeb"

PROXY_CIAS_HOST="cias.geoazure.conti.de"
PROXY_CIAS_PORT="8080"
PROXY_CIAS="http://$PROXY_CIAS_HOST:$PROXY_CIAS_PORT"

OUT_FILE="${1:-/tmp/proxy.env}"

echo "# Proxy detection result" > "$OUT_FILE"

# Test ohne Proxy
if curl -s --head --request GET "$URL" >/dev/null; then
    echo "Container does not need a proxy"
else
    # Test mit CIAS Proxy
    if curl -s --head --request GET -x "$PROXY_CIAS" "$URL" >/dev/null; then
        echo "Container needs $PROXY_CIAS"
        echo "CONTAINER_NEEDED_PROXY=cias" >> "$OUT_FILE"
        echo "http_proxy=$PROXY_CIAS" >> "$OUT_FILE"
        echo "https_proxy=$PROXY_CIAS" >> "$OUT_FILE"
        echo "HTTP_PROXY=$PROXY_CIAS" >> "$OUT_FILE"
        echo "HTTPS_PROXY=$PROXY_CIAS" >> "$OUT_FILE"
        echo "NO_PROXY=127.0.0.1,localhost,.conti.de" >> "$OUT_FILE"
        echo "JAVA_proxyHost=$PROXY_CIAS_HOST" >> "$OUT_FILE"
        echo "JAVA_proxyPort=$PROXY_CIAS_PORT" >> "$OUT_FILE"
        echo "JAVA_nonProxyHosts=127.0.0.1|localhost|*.conti.de" >> "$OUT_FILE"
    else
        echo "testInternet Failed"
        exit 1
    fi
fi

