#!/usr/bin/env zsh
set -e

su - sparkdev << 'EOF'
{
  echo 'echo "Check installed components and versions - lengthy on first run!"'

  echo 'az --version | head -n 1'
  echo 'echo "databricks $(databricks --version | head -n 1)"'
  echo 'sbt --version | head -n 1'

  echo 'echo "💡 Sign in to Azure Databricks with:"'
  echo 'echo "   databricks auth login --host https://<your-workspace>.azuredatabricks.net"'
  echo 'echo "💡 For Azure, run: az login"'
} >> ~/.zshrc
EOF
