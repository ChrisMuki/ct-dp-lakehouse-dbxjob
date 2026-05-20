#!/usr/bin/env bash
# Run the fin_hawk DM transformation tables in dependency order.
# Usage: ./run-hawk-dm-tables.sh [configFile] [--list]
#
# Examples:
#   ./run-hawk-dm-tables.sh
#   ./run-hawk-dm-tables.sh config/config.json
#   ./run-hawk-dm-tables.sh --list

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_FILE="config/config.json"
LIST_ONLY=false

if [[ $# -gt 0 && "$1" != --* ]]; then
  CONFIG_FILE="$1"
  shift
fi

[[ "$CONFIG_FILE" != /* ]] && CONFIG_FILE="$SCRIPT_DIR/$CONFIG_FILE"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --list) LIST_ONLY=true; shift ;;
    *) echo "Unknown arg: $1" >&2; exit 1 ;;
  esac
done

PKG="ct.dna.lakehouse.dm_md.fin_hawk"

# Topologically ordered: leaf tables first, then derived tables.
TABLES=(
  makt_1
  mara
  marc
  t001
  t001k
  t001w
  t023t
  mdm
  mo
  mdp
)

if [[ "$LIST_ONLY" == true ]]; then
  for table in "${TABLES[@]}"; do
    echo "$PKG.$table"
  done
  exit 0
fi

failed=()
succeeded=0

run_table() {
  local table="$1"
  echo ""
  echo "▶ $PKG.$table"
  if sbt "devops/runMain ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint configFile=$CONFIG_FILE $PKG $table"; then
    succeeded=$((succeeded + 1))
  else
    echo "  [FAILED] $PKG.$table" >&2
    failed+=("$PKG.$table")
  fi
}

for table in "${TABLES[@]}"; do
  run_table "$table"
done

echo ""
echo "========================================"
echo "Done. $succeeded succeeded, ${#failed[@]} failed."
if [[ ${#failed[@]} -gt 0 ]]; then
  echo "Failed tables:"
  for t in "${failed[@]}"; do echo "  - $t"; done
  exit 1
fi
