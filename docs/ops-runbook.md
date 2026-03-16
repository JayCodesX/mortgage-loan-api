# Operations Runbook

## Seeded Demo Accounts
- Admin: `admin@jaycodesx.dev` / `StrongPass123!`
- User: `jay@jaycodesx.dev` / `StrongPass123!`

The admin app and `GET /api/metrics/admin/summary` now require an `ADMIN` role token.

## Start Minimal Stack For Queue Troubleshooting
```bash
cd <your-project-folder>/mortgage-loan-api
docker compose up -d --build mysql redis localstack api auth-service borrower-service pricing-service lead-service notification-service web admin-web edge
```

## Inspect Dead-Letter Queues
```bash
./scripts/dlq-inspect.sh quote-pricing-results-dlq
./scripts/dlq-inspect.sh quote-lead-results-dlq
./scripts/dlq-inspect.sh quote-notification-events-dlq
```

Optional second argument controls max messages:
```bash
./scripts/dlq-inspect.sh quote-pricing-results-dlq 5
```

## Replay Dead-Letter Queue Messages
Replay from a DLQ back to its primary queue:
```bash
./scripts/dlq-replay.sh quote-pricing-results-dlq quote-pricing-results
./scripts/dlq-replay.sh quote-lead-results-dlq quote-lead-results
./scripts/dlq-replay.sh quote-notification-events-dlq quote-notification-events
```

Optional third argument controls max messages:
```bash
./scripts/dlq-replay.sh quote-pricing-results-dlq quote-pricing-results 3
```

## Queue Map
- `quote-pricing-requests` -> consumed by `pricing-service`
- `quote-pricing-results` -> consumed by `api`
- `quote-lead-requests` -> consumed by `lead-service`
- `quote-lead-results` -> consumed by `api`
- `quote-notification-events` -> consumed by `notification-service`

DLQs:
- `quote-pricing-results-dlq`
- `quote-lead-results-dlq`
- `quote-notification-events-dlq`

## Typical Failure Checks
1. Confirm the stack is healthy:
```bash
curl -sk https://localhost:8443/actuator/health
```

2. Check quote metrics and replay counters:
```bash
curl -sk https://localhost:8443/api/metrics/quotes
```

3. Inspect the relevant DLQ.

4. Replay one or two messages first, not the whole queue.

5. Recheck:
- quote status
- quote metrics
- admin summary

## Shutdown
```bash
docker compose down
```
