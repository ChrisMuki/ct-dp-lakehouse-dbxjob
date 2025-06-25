#!/usr/bin/env zsh
set -e

# if ! command -v pip3 &>/dev/null; then
#   apt-get update && apt-get install -y python3 python3-pip
# fi

# su - sparkdev -c 'pip3 install --user --upgrade databricks-cli'
# su - sparkdev -c 'grep -qxF "export PATH=\"\$HOME/.local/bin:\$PATH\"" ~/.bashrc || echo "export PATH=\"\$HOME/.local/bin:\$PATH\"" >> ~/.bashrc'


# Why not using official Databricks docu installation method?
curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | sh