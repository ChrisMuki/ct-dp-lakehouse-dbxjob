#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# load_env_file [FILE]
# Liest eine Docker-kompatible .env-Datei (KEY=VALUE) ein,
# ignoriert Kommentare/Leerzeilen und exportiert die Variablen exakt.
load_env_file() {
  local file="${1:-}" 
  # Standard: .proxy.env direkt neben diesem Skript
  if [ -z "$file" ]; then
    file="$SCRIPT_DIR/.proxy.env"
  fi
  [ -f "$file" ] || { echo "env file not found: $file" >&2; return 1; }
  while IFS= read -r line || [ -n "$line" ]; do
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    [[ -z "${line// /}" ]] && continue
    # Nur erstes '=' auftrennen
    if [[ "$line" == *"="* ]]; then
      local key="${line%%=*}"
      local value="${line#*=}"
      # Whitespace um key entfernen
      key="${key##[[:space:]]*}"; key="${key%%[[:space:]]*}"
      # Validen key erzwingen
      if [[ "$key" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]]; then
        # Wert exakt exportieren (inkl. Sonderzeichen wie |,*,,)
        eval "export \"$key\"=\"$value\""
      fi
    fi
  done < "$file"
}

load_env_file "$SCRIPT_DIR/.proxy.env"

