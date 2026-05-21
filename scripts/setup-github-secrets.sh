#!/usr/bin/env bash
# Usage: place a .env file in the repo root and run this script from the repo root
# It will call `gh secret set KEY --body 'value'` for each non-empty, non-comment line.

set -euo pipefail

ENV_FILE=".env"
if [ ! -f "$ENV_FILE" ]; then
  echo "No .env file found in $(pwd). Create one with the variables to upload as secrets." >&2
  exit 1
fi

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI not found. Install and run 'gh auth login' first." >&2
  exit 1
fi

echo "Reading $ENV_FILE and uploading secrets to GitHub (current repo)..."

while IFS= read -r line || [ -n "$line" ]; do
  # skip empty lines and comments
  trimmed="$(echo "$line" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
  if [ -z "$trimmed" ] || [[ "$trimmed" == \#* ]]; then
    continue
  fi
  if ! echo "$trimmed" | grep -q '='; then
    echo "Skipping invalid line: $trimmed" >&2
    continue
  fi
  key="$(echo "$trimmed" | cut -d '=' -f1)"
  value="$(echo "$trimmed" | cut -d '=' -f2- )"
  key_trimmed="$(echo "$key" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
  value_trimmed="$(echo "$value" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
  if [ -z "$key_trimmed" ]; then
    echo "Skipping line with empty key: $trimmed" >&2
    continue
  fi
  echo "Setting secret: $key_trimmed"
  gh secret set "$key_trimmed" --body "$value_trimmed"
done < "$ENV_FILE"

echo "Done. Confirm secrets in GitHub Settings → Secrets and variables → Actions."
