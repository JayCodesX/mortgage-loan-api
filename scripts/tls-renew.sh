#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Running Let's Encrypt renewal..."
COMPOSE_PROFILES=prod docker compose run --rm certbot renew

echo "Restarting edge-prod to load renewed certificates..."
COMPOSE_PROFILES=prod docker compose restart edge-prod

echo "TLS renewal flow complete."
