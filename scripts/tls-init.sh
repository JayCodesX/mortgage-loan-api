#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <domain> <email>"
  echo "Example: $0 api.jaycodesx.com jay@jaycodesx.dev"
  exit 1
fi

DOMAIN="$1"
EMAIL="$2"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Starting prod profile containers required for cert issuance..."
COMPOSE_PROFILES=prod docker compose up -d mysql api web edge-prod

echo "Requesting initial Let's Encrypt certificate for ${DOMAIN}..."
COMPOSE_PROFILES=prod docker compose run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  -d "${DOMAIN}" \
  --email "${EMAIL}" \
  --agree-tos --no-eff-email

echo "Reloading edge-prod to use the issued certificate..."
COMPOSE_PROFILES=prod docker compose restart edge-prod

cat <<EOT
TLS initialization complete.
Next:
1) Update nginx/default.prod.conf server_name and cert paths to match ${DOMAIN}
2) Verify https://${DOMAIN}
EOT
