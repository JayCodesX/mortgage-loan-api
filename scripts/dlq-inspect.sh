#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

QUEUE_NAME="${1:-quote-pricing-results-dlq}"
MAX_MESSAGES="${2:-10}"

QUEUE_URL="$(docker compose exec -T localstack awslocal sqs get-queue-url --queue-name "$QUEUE_NAME" --query QueueUrl --output text)"

echo "Inspecting DLQ: $QUEUE_NAME"
docker compose exec -T localstack awslocal sqs receive-message \
  --queue-url "$QUEUE_URL" \
  --max-number-of-messages "$MAX_MESSAGES" \
  --visibility-timeout 1 \
  --wait-time-seconds 1 \
  --attribute-names All \
  --message-attribute-names All \
  --output json
