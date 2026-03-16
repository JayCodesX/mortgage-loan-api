#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
ENV_FILE="$ROOT_DIR/.env.integration"
E2E_DIR="$ROOT_DIR/e2e"

if [[ "${1:-}" == "--start" ]]; then
  docker compose --env-file "$ENV_FILE" --profile integration -f "$COMPOSE_FILE" up -d --build
fi

if [[ ! -d "$E2E_DIR/node_modules" ]]; then
  echo "e2e dependencies are missing; run: cd $E2E_DIR && npm install && npx playwright install chromium" >&2
  exit 1
fi

cd "$E2E_DIR"
npx playwright test tests/public-demo.spec.js tests/admin-console.spec.js
