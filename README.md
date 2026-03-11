# Bookstore Microservice App
A production-style Spring Boot microservice application for managing an online bookstore. Demonstrates service decomposition, event-driven architecture, OAuth2 security, resilience patterns, and full-stack observability.
---
## Architecture
```
┌──────────────────────────────────────────────────────────────────────┐
│                          Browser / Client                            │
└──────────────────────────────────┬───────────────────────────────────┘
                                   │ HTTP :8080
                                   ▼
┌──────────────────────────────────────────────────────────────────────┐
│              bookstore-webapp  (:8080)                               │
│    Thymeleaf + Alpine.js  │  OAuth2 Client (Authorization Code)      │
└──────────────┬────────────────────────────────────┬─────────────────┘
               │                                    │  Bearer token forwarded
               └──────────────┬─────────────────────┘
                              │ HTTP :8989
                              ▼
┌──────────────────────────────────────────────────────────────────────┐
│              api-gateway  (:8989)                                    │
│   Spring Cloud Gateway  │  Aggregated Swagger UI                     │
│   /catalog/**  ─────────┐      /orders/**  ─────────────────┐        │
└─────────────────────────┼────────────────────────────────────┼───────┘
                          │ :8081                              │ :8082 + JWT
                          ▼                                    ▼
           ┌──────────────────────┐            ┌───────────────────────────────┐
           │   catalog-service    │◄─RestClient─│       order-service           │
           │                      │  Resilience4j│                               │
           │  Book catalog CRUD   │  CB + Retry  │  OAuth2 Resource Server       │
           │  PostgreSQL :15432   │             │  Order create / query         │
           └──────────────────────┘             │  OrderValidator (price check) │
                                                │  PostgreSQL :25432            │
                                                │                               │
                                                │  ┌─────────────────────────┐  │
                                                │  │  Outbox Pattern         │  │
                                                │  │  order_events table     │  │
                                                │  │  + ShedLock job (5s)    │  │
                                                │  └──────────┬──────────────┘  │
                                                └─────────────│─────────────────┘
                                                              │ AMQP
                                                              ▼
                                                ┌──────────────────────────────┐
                                                │   RabbitMQ  (:5672)          │
                                                │   Exchange: orders-exchange   │
                                                │   Queues: new-orders         │
                                                │           delivered-orders   │
                                                │           cancelled-orders   │
                                                │           error-orders       │
                                                └──────────────┬───────────────┘
                                                               │ AMQP
                                                               ▼
                                                ┌──────────────────────────────┐
                                                │   notification-service       │
                                                │                              │
                                                │   Idempotent RabbitMQ        │
                                                │   consumer → MailHog         │
                                                │   PostgreSQL :35432          │
                                                └──────────────────────────────┘
┌──────────────────────────────────────────────────────────────────────┐
│  Identity & Observability                                            │
│  Keycloak :9191   — OAuth2 / OIDC (issues & validates JWTs)         │
│  Prometheus :9090 — scrapes /actuator/prometheus from all services   │
│  Grafana :3000    — metrics (Prometheus) + logs (Loki) + traces      │
│  Tempo :3200      — distributed tracing via OTLP / Zipkin           │
│  MailHog :8025    — captures outbound emails locally                │
└──────────────────────────────────────────────────────────────────────┘
```
---
## Tech Stack
| Layer | Technologies |
|---|---|
| **Language / Framework** | Java 21, Spring Boot 3.x |
| **API Gateway** | Spring Cloud Gateway (WebFlux), SpringDoc OpenAPI (aggregated Swagger UI) |
| **Data** | Spring Data JPA, PostgreSQL, Flyway |
| **Messaging** | RabbitMQ, Spring AMQP |
| **Security** | Keycloak 26, Spring Security OAuth2 (Client + Resource Server) |
| **Resilience** | Resilience4j (Retry + CircuitBreaker), ShedLock |
| **Frontend** | Thymeleaf, Alpine.js, Bootstrap 5, jQuery, Spring HTTP Interface |
| **Observability** | Micrometer, Prometheus, Grafana, Loki, Promtail, Grafana Tempo |
| **Testing** | Testcontainers (PostgreSQL, RabbitMQ, Keycloak), WireMock, REST Assured, MockMvc, Instancio |
| **Build / CI** | Maven (multi-module), Spotless (Palantir), Docker Compose, GitHub Actions |
---
## Key Patterns
- **Outbox Pattern** — order events are persisted in the same DB transaction as the order, then published to RabbitMQ by a scheduled job every 5 s (guarded by ShedLock)
- **Idempotent Consumer** — `notification-service` deduplicates RabbitMQ messages by storing and checking `eventId` before processing
- **Resilience4j Circuit Breaker + Retry** — wraps `order-service → catalog-service` HTTP calls with a fallback to `Optional.empty()`
- **OAuth2 two-layer security** — `bookstore-webapp` (OAuth2 Client) forwards access tokens as `Bearer` headers; `order-service` (Resource Server) validates JWTs against Keycloak
---
## Modules
| Module | Port | Role |
|---|---|---|
| `bookstore-webapp` | 8080 | Thymeleaf SSR frontend, OAuth2 Client |
| `api-gateway` | 8989 | Spring Cloud Gateway, aggregated Swagger UI |
| `catalog-service` | 8081 | Book catalog REST API |
| `order-service` | 8082 | Order management, OAuth2 Resource Server |
| `notification-service` | 8083 | Email notifications via RabbitMQ |
---
## Getting Started
**Prerequisites:** Java 21+, Docker, Maven 3.9+
```bash
# 1. Start infrastructure (PostgreSQL ×3, RabbitMQ, Keycloak, MailHog)
docker compose -f deployment/docker-compose/infra.yml up -d
# 2. Run any service
cd order-service && ./mvnw spring-boot:run
# 3. (Optional) Start observability stack
docker compose -f deployment/docker-compose/monitoring.yml up -d
```
> Keycloak auto-imports the `bookstore` realm on startup. Admin: `admin / admin1234` at `http://localhost:9191`.
---
## Key URLs
| | URL |
|---|---|
| Web App | `http://localhost:8080` |
| API Gateway / Swagger | `http://localhost:8989/swagger-ui/index.html` |
| RabbitMQ UI | `http://localhost:15672` |
| MailHog | `http://localhost:8025` |
| Grafana | `http://localhost:3000` |
| Keycloak | `http://localhost:9191` |
---
## Running Tests
```bash
./mvnw spotless:apply   # format (enforced at compile)
./mvnw clean verify     # all tests (requires Docker for Testcontainers)
```
---
## CI/CD
Each service has an independent GitHub Actions workflow triggered on changes to its own directory — builds, tests, and pushes a Docker image to Docker Hub (`neeraj310100/bookstore-<service>`).
**Required secrets:** `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`
