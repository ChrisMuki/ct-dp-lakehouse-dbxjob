#!/usr/bin/env bash

URL="https://aka.ms/InstallAzureCLIDeb"

NON_PROXY_HOSTS_ENV="127.0.0.1,localhost,.conti.de,ct-ind.com,contiwan.com"
NON_PROXY_HOSTS_JAVA="127.0.0.1|localhost|*.conti.de|*.ct-ind.com|*.contiwan.com"


PROXY_CANDIDATES=(
    "cias:cias.geoazure.conti.de:8080"
    "bpa-eqx:sia-proxy-bpa-eqx.conti.de:8080"

)

OUT_FILE="${1:-/tmp/proxy.env}"

echo "# Proxy detection result" > "$OUT_FILE"

write_proxy_env() {
    local name="$1"
    local host="$2"
    local port="$3"
    local url="http://$host:$port"

    echo "Container needs $url"
    echo "CONTAINER_NEEDED_PROXY=$name" >> "$OUT_FILE"
    echo "http_proxy=$url" >> "$OUT_FILE"
    echo "https_proxy=$url" >> "$OUT_FILE"
    echo "HTTP_PROXY=$url" >> "$OUT_FILE"
    echo "HTTPS_PROXY=$url" >> "$OUT_FILE"
    echo "NO_PROXY=$NON_PROXY_HOSTS_ENV" >> "$OUT_FILE"
    echo "JAVA_proxyHost=$host" >> "$OUT_FILE"
    echo "JAVA_proxyPort=$port" >> "$OUT_FILE"
    echo "JAVA_nonProxyHosts=$NON_PROXY_HOSTS_JAVA" >> "$OUT_FILE"
    echo "JAVA_TOOL_OPTIONS=-Dhttp.proxyHost=${host} -Dhttp.proxyPort=${port} -Dhttp.nonProxyHosts=${NON_PROXY_HOSTS_JAVA} -Dhttps.proxyHost=${host} -Dhttps.proxyPort=${port} -Dhttps.nonProxyHosts=${NON_PROXY_HOSTS_JAVA} -Djava.net.useSystemProxies=true" >> "$OUT_FILE"
    echo "COURSIER_OPTS=-J-Dhttp.proxyHost=${host} -J-Dhttp.proxyPort=${port} -J-Dhttp.nonProxyHosts=${NON_PROXY_HOSTS_JAVA} -J-Dhttps.proxyHost=${host} -J-Dhttps.proxyPort=${port} -J-Dhttps.nonProxyHosts=${NON_PROXY_HOSTS_JAVA} -J-Djava.net.useSystemProxies=true" >> "$OUT_FILE"
}


if curl -s --head --request GET "$URL" >/dev/null; then
    echo "Container does not need a proxy"
    exit 0
fi


for candidate in "${PROXY_CANDIDATES[@]}"; do
    IFS=":" read -r name host port <<< "$candidate"
    proxy_url="http://$host:$port"

    if curl -s --head --request GET -x "$proxy_url" "$URL" >/dev/null; then
        write_proxy_env "$name" "$host" "$port"
        exit 0
    fi
done

echo "testInternet Failed (no working proxy found)"
exit 1

