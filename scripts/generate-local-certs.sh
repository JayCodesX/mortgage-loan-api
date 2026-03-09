#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CERT_DIR="${ROOT_DIR}/nginx/certs"
mkdir -p "${CERT_DIR}"

openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout "${CERT_DIR}/localhost.key" \
  -out "${CERT_DIR}/localhost.crt" \
  -subj "/CN=localhost"

echo "Generated local TLS certs:"
echo "- ${CERT_DIR}/localhost.key"
echo "- ${CERT_DIR}/localhost.crt"
