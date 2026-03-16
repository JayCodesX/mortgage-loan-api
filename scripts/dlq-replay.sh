#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SOURCE_QUEUE="${1:-quote-pricing-results-dlq}"
TARGET_QUEUE="${2:-quote-pricing-results}"
MAX_MESSAGES="${3:-10}"

if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required to replay DLQ messages." >&2
  exit 1
fi

SOURCE_URL="$(docker compose exec -T localstack awslocal sqs get-queue-url --queue-name "$SOURCE_QUEUE" --query QueueUrl --output text)"
TARGET_URL="$(docker compose exec -T localstack awslocal sqs get-queue-url --queue-name "$TARGET_QUEUE" --query QueueUrl --output text)"

PAYLOAD="$(docker compose exec -T localstack awslocal sqs receive-message \
  --queue-url "$SOURCE_URL" \
  --max-number-of-messages "$MAX_MESSAGES" \
  --visibility-timeout 30 \
  --wait-time-seconds 1 \
  --attribute-names All \
  --message-attribute-names All \
  --output json)"

MESSAGE_COUNT="$(printf '%s' "$PAYLOAD" | jq '.Messages | length // 0')"
if [ "$MESSAGE_COUNT" -eq 0 ]; then
  echo "No messages available in $SOURCE_QUEUE"
  exit 0
fi

echo "Replaying $MESSAGE_COUNT message(s) from $SOURCE_QUEUE -> $TARGET_QUEUE"

printf '%s' "$PAYLOAD" | jq -c '.Messages[]' | while IFS= read -r message; do
  body="$(printf '%s' "$message" | jq -r '.Body')"
  receipt_handle="$(printf '%s' "$message" | jq -r '.ReceiptHandle')"

  printf '%s' "$body" | docker compose exec -T localstack sh -lc \
    "cat > /tmp/dlq-replay-body.json && awslocal sqs send-message --queue-url '$TARGET_URL' --message-body file:///tmp/dlq-replay-body.json >/dev/null"

  docker compose exec -T localstack awslocal sqs delete-message \
    --queue-url "$SOURCE_URL" \
    --receipt-handle "$receipt_handle" >/dev/null
done

echo "Replay complete."
