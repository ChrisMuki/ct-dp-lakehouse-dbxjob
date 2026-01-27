#!/usr/bin/env bash

URL="https://aka.ms/InstallAzureCLIDeb"


PROXY_CANDIDATES=(
    "cias:cias.geoazure.conti.de:8080"
    "bpa-eqx:sia-proxy-bpa-eqx.conti.de:8080"
)

# Pro Proxy unterschiedliche NO_PROXY-Werte
# Passe diese Maps nach Bedarf an (Keys = Proxy-Name aus PROXY_CANDIDATES)
declare -A NON_PROXY_HOSTS_ENV_MAP=(
    ["cias"]="127.0.0.1,localhost,.conti.de,ct-ind.com,contiwan.com,contitech.de"
    ["bpa-eqx"]="127.0.0.1,localhost"
)

declare -A NON_PROXY_HOSTS_JAVA_MAP=(
    ["cias"]="127.0.0.1|localhost|*.conti.de|*.ct-ind.com|*.contiwan.com|*.contitech.de"
    ["bpa-eqx"]="127.0.0.1|localhost"
)

OUT_FILE="${1:-/tmp/proxy.env}"

echo "# Proxy detection result" > "$OUT_FILE"

write_proxy_env() {
    local name="$1"
    local host="$2"
    local port="$3"
    local url="http://$host:$port"

    # Proxy-spezifische NO_PROXY-Werte ermitteln (fallback auf Default)
    local non_proxy_env="${NON_PROXY_HOSTS_ENV_MAP[$name]:-$DEFAULT_NON_PROXY_HOSTS_ENV}"
    local non_proxy_java="${NON_PROXY_HOSTS_JAVA_MAP[$name]:-$DEFAULT_NON_PROXY_HOSTS_JAVA}"

    echo "Container needs $url"
    echo "CONTAINER_NEEDED_PROXY=$name" >> "$OUT_FILE"
    echo "http_proxy=$url" >> "$OUT_FILE"
    echo "https_proxy=$url" >> "$OUT_FILE"
    echo "HTTP_PROXY=$url" >> "$OUT_FILE"
    echo "HTTPS_PROXY=$url" >> "$OUT_FILE"
    echo "NO_PROXY=$non_proxy_env" >> "$OUT_FILE"
    echo "JAVA_proxyHost=$host" >> "$OUT_FILE"
    echo "JAVA_proxyPort=$port" >> "$OUT_FILE"
    echo "JAVA_nonProxyHosts=$non_proxy_java" >> "$OUT_FILE"
    echo "JAVA_TOOL_OPTIONS=-Dhttp.proxyHost=${host} -Dhttp.proxyPort=${port} -Dhttp.nonProxyHosts=${non_proxy_java} -Dhttps.proxyHost=${host} -Dhttps.proxyPort=${port} -Dhttps.nonProxyHosts=${non_proxy_java} -Djava.net.useSystemProxies=true" >> "$OUT_FILE"
    echo "COURSIER_OPTS=-J-Dhttp.proxyHost=${host} -J-Dhttp.proxyPort=${port} -J-Dhttp.nonProxyHosts=${non_proxy_java} -J-Dhttps.proxyHost=${host} -J-Dhttps.proxyPort=${port} -J-Dhttps.nonProxyHosts=${non_proxy_java} -J-Djava.net.useSystemProxies=true" >> "$OUT_FILE"
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

