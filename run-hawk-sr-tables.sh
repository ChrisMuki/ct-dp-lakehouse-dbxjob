#!/usr/bin/env bash
# Run only the SR tables that feed the fin_hawk transformation locally.
# Usage: ./run-hawk-sr-tables.sh [configFile] [--schema SCHEMA] [--list]
#
# Examples:
#   ./run-hawk-sr-tables.sh
#   ./run-hawk-sr-tables.sh config/config.json
#   ./run-hawk-sr-tables.sh config/config.json --schema ct_gbl_p12
#   ./run-hawk-sr-tables.sh --list

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_FILE="config/config.json"
FILTER_SCHEMA=""
LIST_ONLY=false

if [[ $# -gt 0 && "$1" != --* ]]; then
  CONFIG_FILE="$1"
  shift
fi

[[ "$CONFIG_FILE" != /* ]] && CONFIG_FILE="$SCRIPT_DIR/$CONFIG_FILE"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --schema)
      FILTER_SCHEMA="$2"
      shift 2
      ;;
    --list)
      LIST_ONLY=true
      shift
      ;;
    *)
      echo "Unknown arg: $1" >&2
      exit 1
      ;;
  esac
done

FIN_HAWK_DIR="$SCRIPT_DIR/lakehouse/src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk"

import_search() {
  if command -v rg >/dev/null 2>&1; then
    rg '^import ct\.dna\.lakehouse\.sr\.[^.]+\.\{[^}]+ => [^}]+' "$FIN_HAWK_DIR" \
      --glob '*.scala' \
      --glob '!package.scala' \
      --no-filename
  else
    grep -hE '^import ct\.dna\.lakehouse\.sr\.[^.]+\.\{[^}]+ => [^}]+' "$FIN_HAWK_DIR"/*.scala
  fi
}

mapfile -t HAWK_TABLES < <(
  import_search \
  | sed -E 's/^import ct\.dna\.lakehouse\.sr\.([^.]+)\.\{([[:alnum:]_]+) => [^}]+\}$/ct.dna.lakehouse.sr.\1.\2/' \
  | sort -u
)

if [[ ${#HAWK_TABLES[@]} -eq 0 ]]; then
  echo "No HAWK SR tables found under $FIN_HAWK_DIR" >&2
  exit 1
fi

failed=()
succeeded=0

run_table() {
  local pkg="$1"
  local table="$2"
  echo ""
  echo "▶ $pkg.$table"
  if sbt "devops/runMain ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint configFile=$CONFIG_FILE $pkg $table"; then
    succeeded=$((succeeded + 1))
  else
    echo "  [FAILED] $pkg.$table" >&2
    failed+=("$pkg.$table")
  fi
}

for qualified_table in "${HAWK_TABLES[@]}"; do
  pkg="${qualified_table%.*}"
  schema="${pkg##*.}"
  table="${qualified_table##*.}"

  [[ -n "$FILTER_SCHEMA" && "$schema" != "$FILTER_SCHEMA" ]] && continue

  if [[ "$LIST_ONLY" == true ]]; then
    echo "$pkg.$table"
  else
    run_table "$pkg" "$table"
  fi
done

if [[ "$LIST_ONLY" == true ]]; then
  exit 0
fi

echo ""
echo "========================================"
echo "Done. $succeeded succeeded, ${#failed[@]} failed."
if [[ ${#failed[@]} -gt 0 ]]; then
  echo "Failed tables:"
  for t in "${failed[@]}"; do echo "  - $t"; done
  exit 1
fi