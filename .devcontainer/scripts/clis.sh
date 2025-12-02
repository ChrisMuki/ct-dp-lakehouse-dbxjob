#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$SCRIPT_DIR/parse_proxy_env.sh"


echo "===> Azure CLI Installing..."
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
echo "===>  Azure CLI Done"


echo "===>  Pergola CLI => Installing..."
curl -fsSL https://get.pergo.la/cli/latest/install.sh | bash
echo "===> Pergola CLI Done"


echo "===> Databricks CLI Installing..."
if [ -z "${CONTAINER_NEEDED_PROXY:-}" ]; then
    curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | \
        sudo \
        bash
else
    curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | \
        sudo \
        http_proxy=$http_proxy \
        https_proxy=$https_proxy \
        HTTP_PROXY=$HTTP_PROXY \
        HTTPS_PROXY=$HTTPS_PROXY \
        bash
fi
echo "===> Databricks CLI Done"
