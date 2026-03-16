ROOT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
COMPOSE := docker compose --env-file $(ROOT_DIR).env.integration --profile integration -f $(ROOT_DIR)docker-compose.yml

.PHONY: up smoke e2e down

up:
	$(COMPOSE) up -d --build

smoke:
	$(ROOT_DIR)scripts/local-smoke.sh

e2e:
	cd $(ROOT_DIR)e2e && npm run test

down:
	$(COMPOSE) down
