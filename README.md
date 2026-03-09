# Mortgage Loan API

Spring Boot API Gateway project with borrower/loan APIs, amortization calculators, MySQL persistence, and a public React landing page.

## Tech Stack
- Java 21
- Spring Boot 3.3.5
- Spring Cloud Gateway
- Spring Data JPA
- MySQL 8
- React + Vite
- Nginx
- Maven
- Docker + Docker Compose

## Architecture (Docker + Nginx)
The app now runs as four containers:

- `edge` (Nginx reverse proxy) exposed on `http://localhost:8088`
- `web` (Nginx serving built React app)
- `api` (Spring Boot backend)
- `mysql` (MySQL 8.4)

Traffic flow:
- Browser -> `edge` (`localhost:8088`)
- `edge` routes `/` to `web`
- `edge` routes `/api/*` to `api` (with `/api` stripped)
- `api` talks to `mysql` over the compose network

## Run Everything with Docker
```bash
cd /Users/JayCodesX/Projects/mortgage-loan-api
docker compose up -d --build
```

Open:
- App: `http://localhost:8088`
- App (TLS): `https://localhost:8443`
- API health through edge: `http://localhost:8088/actuator/health`
- API health through edge (TLS): `https://localhost:8443/actuator/health` (use `-k` with curl for local self-signed cert)

If local TLS cert files are missing, generate them:
```bash
./scripts/generate-local-certs.sh
```

Stop:
```bash
docker compose down
```

## Production TLS Profile (Let's Encrypt)
This repo now includes a production edge profile:
- `edge-prod` (public nginx on `80/443`)
- `certbot` (certificate issuance/renewal)

### 1) Set your domain
Edit these values in:
- `nginx/default.prod.conf`

Replace:
- `your-domain.example.com`
with your real domain in:
- both `server_name` lines
- both certificate file paths under `/etc/letsencrypt/live/...`

### 2) Issue first certificate
Run the production stack (without TLS cert yet):
```bash
docker compose --profile prod up -d mysql api web edge-prod
```

Request an initial cert:
```bash
docker compose --profile prod run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  -d your-domain.example.com \
  --email you@example.com \
  --agree-tos --no-eff-email
```

Reload nginx:
```bash
docker compose --profile prod restart edge-prod
```

One-command alternative:
```bash
./scripts/tls-init.sh your-domain.example.com you@example.com
```

### 3) Renew certificates (rotation)
Run renewal:
```bash
docker compose --profile prod run --rm certbot renew
```

Reload nginx to pick up renewed certs:
```bash
docker compose --profile prod restart edge-prod
```

One-command alternative:
```bash
./scripts/tls-renew.sh
```

### 4) Schedule automatic renewal
Example cron (host machine):
```bash
0 3 * * * cd /Users/JayCodesX/Projects/mortgage-loan-api && ./scripts/tls-renew.sh
```

## Local Dev (No Docker for app)
Start only MySQL:
```bash
docker compose up -d mysql
```

Run backend:
```bash
cd /Users/JayCodesX/Projects/mortgage-loan-api
mvn -Dmaven.repo.local=.m2 spring-boot:run
```

Run frontend:
```bash
cd /Users/JayCodesX/Projects/mortgage-loan-api/web
npm install
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```

## Build and Test Backend
```bash
mvn -Dmaven.repo.local=.m2 clean package
```

## Build Frontend
```bash
cd /Users/JayCodesX/Projects/mortgage-loan-api/web
npm run build
```

## API Endpoints
- `POST /borrowers`
- `POST /loans`
- `GET /loans/{id}`
- `GET /loans/{id}/amortization`
- `GET /loans/amortization/calculate?principal={p}&annualInterestRate={rate}&termYears={years}`
- `GET /loans/mortgage-payment/calculate?loanAmount={loanAmount}&downPayment={downPayment}&annualInterestRate={rate}&termYears={years}`

## Nginx Concepts to Learn (Practical)
1. Reverse proxy
- Nginx receives HTTP requests and forwards them to internal services using `proxy_pass`.

2. Path routing
- `location /` serves frontend.
- `location /api/` forwards to backend.

3. Path rewrite
- We strip `/api` before forwarding so backend keeps existing routes (`/loans`, `/borrowers`).

4. SPA fallback
- In `web/nginx.conf`, `try_files $uri /index.html;` allows React routes to load directly.

5. Headers
- `X-Forwarded-*` headers preserve original client/protocol context through proxies.

6. TLS and redirect
- Edge Nginx now redirects `http://localhost:8088` to `https://localhost:8443`.
- A local self-signed certificate is stored in `nginx/certs` for development.

9. Dev vs prod edge config
- `nginx/default.conf` is for local dev TLS (self-signed cert, `8088/8443`).
- `nginx/default.prod.conf` is for real domain TLS via Let's Encrypt on `80/443`.

10. ACME challenge
- In prod, `location /.well-known/acme-challenge/` serves token files for Let's Encrypt HTTP validation.

7. HSTS
- `Strict-Transport-Security` tells browsers to prefer HTTPS for subsequent requests.

8. CSP
- `Content-Security-Policy` restricts allowed script/style/font/image/connect sources and reduces XSS risk.

## Docker Concepts to Learn (Practical)
1. Image vs container
- Image = packaged blueprint.
- Container = running instance of that image.

2. Multi-stage builds
- API Dockerfile uses Maven stage to compile, then copies JAR into a small JRE image.
- Web Dockerfile builds React with Node, then serves static files with Nginx.

3. Compose networking
- Services talk using service names (`api`, `web`, `mysql`) on an internal Docker network.

4. Port publishing vs expose
- `ports` publishes container port to your machine.
- `expose` is only for internal container-to-container communication.

5. Volumes
- `mysql_data` persists database data across container restarts.

## Key Config Files
- `/Users/JayCodesX/Projects/mortgage-loan-api/docker-compose.yml`
- `/Users/JayCodesX/Projects/mortgage-loan-api/Dockerfile`
- `/Users/JayCodesX/Projects/mortgage-loan-api/nginx/default.conf`
- `/Users/JayCodesX/Projects/mortgage-loan-api/nginx/default.prod.conf`
- `/Users/JayCodesX/Projects/mortgage-loan-api/nginx/Dockerfile`
- `/Users/JayCodesX/Projects/mortgage-loan-api/web/Dockerfile`
- `/Users/JayCodesX/Projects/mortgage-loan-api/web/nginx.conf`
- `/Users/JayCodesX/Projects/mortgage-loan-api/scripts/tls-init.sh`
- `/Users/JayCodesX/Projects/mortgage-loan-api/scripts/tls-renew.sh`
