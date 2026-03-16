#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
ENV_FILE="$ROOT_DIR/.env.integration"

if [[ "${1:-}" == "--start" ]]; then
  docker compose --env-file "$ENV_FILE" --profile integration -f "$COMPOSE_FILE" up -d --build
fi

node "$ROOT_DIR/scripts/local-smoke.mjs"
