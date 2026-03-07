# Bookstore Microservice App

> ⚠️ **Work in Progress** — This project is actively under development. New microservices and features are being added incrementally.

A Spring Boot-based microservice application for managing a bookstore, built with a multi-module Maven structure.

---

## Modules

| Module                 | Port | Description                                         | Status |
|------------------------|------|-----------------------------------------------------|--------|
| `catalog-service`      | 8081 | Manages book catalog and inventory                  | Done   |
| `order-service`        | 8082 | Handles order creation, processing, and events      | Done   |
| `notification-service` | 8083 | Sends email notifications for order lifecycle events | Done   |

---

## Tech Stack

### Core
- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA** with PostgreSQL
- **Flyway** – Database migrations
- **Lombok** – Boilerplate reduction
- **Spring Boot Actuator** – Health, info & metrics endpoints
- **SpringDoc OpenAPI (Swagger UI)** – API documentation

### Messaging
- **RabbitMQ** – Async event messaging between services
- **Spring AMQP** – RabbitMQ integration

### Resilience
- **Resilience4j** – Retry and Circuit Breaker for inter-service HTTP calls
- **ShedLock** – Distributed scheduled job locking (prevents duplicate execution across multiple instances)

### Patterns
- **Outbox Pattern** – Reliable event publishing via a transactional outbox table
- **Idempotent Consumer** – Notification service deduplicates events using a processed event store

### Notifications
- **Spring Mail** – Email sending via JavaMailSender
- **MailHog** – Local SMTP server for capturing and inspecting emails during development

### Testing
- **Testcontainers** – Integration testing with real PostgreSQL and RabbitMQ containers
- **WireMock** (via Testcontainers) – Mocking external HTTP services in integration tests
- **REST Assured** – API-level integration testing
- **Instancio** – Test data generation
- **JUnit 5** – Test framework

### Observability
- **Micrometer + Prometheus** – Metrics collection

### Build & Tooling
- **Spotless** – Code formatting (Palantir Java Format)
- **Docker Compose** – Local infrastructure
- **GitHub Actions** – CI/CD pipeline per service
- **git-commit-id-maven-plugin** – Exposes git info via Actuator `/info` endpoint

---

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.9+ (or use the included `mvnw` wrapper)

---

## Getting Started

### 1. Start Infrastructure

Starts PostgreSQL (catalog, order & notification databases), RabbitMQ, and MailHog:

```bash
docker compose -f deployment/docker-compose/infra.yml up -d
```

### 2. Build the Project

```bash
./mvnw clean package -DskipTests
```

### 3. Run a Service

```bash
# Catalog Service
cd catalog-service && ./mvnw spring-boot:run

# Order Service
cd order-service && ./mvnw spring-boot:run

# Notification Service
cd notification-service && ./mvnw spring-boot:run
```

| Service                | URL                          |
|------------------------|------------------------------|
| `catalog-service`      | `http://localhost:8081`      |
| `order-service`        | `http://localhost:8082`      |
| `notification-service` | `http://localhost:8083`      |
| RabbitMQ UI            | `http://localhost:15672`     |
| MailHog UI             | `http://localhost:8025`      |

---

## API Documentation (Swagger UI)

| Service                | Swagger UI URL                                  |
|------------------------|-------------------------------------------------|
| `catalog-service`      | `http://localhost:8081/swagger-ui/index.html`   |
| `order-service`        | `http://localhost:8082/swagger-ui/index.html`   |
| `notification-service` | `http://localhost:8083/swagger-ui/index.html`   |

---

## Configuration

Services use environment variables with sensible defaults for local development.

### catalog-service

| Variable      | Default                                        | Description         |
|---------------|------------------------------------------------|---------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:15432/catalog_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres`                                     | DB username         |
| `DB_PASSWORD` | `password`                                     | DB password         |

### order-service

| Variable            | Default                                      | Description         |
|---------------------|----------------------------------------------|---------------------|
| `DB_URL`            | `jdbc:postgresql://localhost:25432/order_db` | PostgreSQL JDBC URL |
| `DB_USERNAME`       | `postgres`                                   | DB username         |
| `DB_PASSWORD`       | `password`                                   | DB password         |
| `RABBITMQ_HOST`     | `localhost`                                  | RabbitMQ host       |
| `RABBITMQ_PORT`     | `5672`                                       | RabbitMQ AMQP port  |
| `RABBITMQ_USERNAME` | `guest`                                      | RabbitMQ username   |
| `RABBITMQ_PASSWORD` | `guest`                                      | RabbitMQ password   |

### notification-service

| Variable            | Default                                              | Description                      |
|---------------------|------------------------------------------------------|----------------------------------|
| `DB_URL`            | `jdbc:postgresql://localhost:35432/notification_db`  | PostgreSQL JDBC URL              |
| `DB_USERNAME`       | `postgres`                                           | DB username                      |
| `DB_PASSWORD`       | `password`                                           | DB password                      |
| `RABBITMQ_HOST`     | `localhost`                                          | RabbitMQ host                    |
| `RABBITMQ_PORT`     | `5672`                                               | RabbitMQ AMQP port               |
| `RABBITMQ_USERNAME` | `guest`                                              | RabbitMQ username                |
| `RABBITMQ_PASSWORD` | `guest`                                              | RabbitMQ password                |
| `MAIL_HOST`         | `127.0.0.1`                                          | SMTP host (MailHog locally)      |
| `MAIL_PORT`         | `1025`                                               | SMTP port                        |
| `MAIL_USERNAME`     | `PLACEHOLDER`                                        | SMTP username                    |
| `MAIL_PASSWORD`     | `PLACEHOLDER`                                        | SMTP password                    |

---

## Actuator Endpoints

| Service                | Health                                  | Info                                  |
|------------------------|-----------------------------------------|---------------------------------------|
| `catalog-service`      | `GET localhost:8081/actuator/health`    | `GET localhost:8081/actuator/info`    |
| `order-service`        | `GET localhost:8082/actuator/health`    | `GET localhost:8082/actuator/info`    |
| `notification-service` | `GET localhost:8083/actuator/health`    | `GET localhost:8083/actuator/info`    |

---

## Running Tests

```bash
# Run tests for all modules
./mvnw test

# Run tests for a specific module
cd catalog-service && ./mvnw test
cd order-service && ./mvnw test
cd notification-service && ./mvnw test
```

> Tests use Testcontainers and require Docker to be running.

---

## Code Formatting

This project enforces consistent formatting using [Spotless](https://github.com/diffplug/spotless) with [Palantir Java Format](https://github.com/palantir/palantir-java-format).

Auto-format all modules before committing:

```bash
./mvnw spotless:apply
```

---

## CI/CD

Each service has its own GitHub Actions workflow that triggers on pushes to `main` affecting that service's directory:

| Workflow               | Trigger Path              | Actions                                    |
|------------------------|---------------------------|--------------------------------------------|
| `catalog-service`      | `catalog-service/**`      | Build → Test → Build & Push Docker Image   |
| `order-service`        | `order-service/**`        | Build → Test → Build & Push Docker Image   |
| `notification-service` | `notification-service/**` | Build → Test → Build & Push Docker Image   |

Docker images are published to Docker Hub under `neeraj310100/bookstore-<service-name>`.

Required GitHub Secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`.

---

## Project Structure

```
bookstore-microservice-app/
├── .github/
│   └── workflows/
│       ├── catalog-service.yml       # CI/CD for catalog-service
│       ├── order-service.yml         # CI/CD for order-service
│       └── notification-service.yml  # CI/CD for notification-service
├── catalog-service/                  # Book catalog microservice
│   ├── src/
│   │   ├── main/java/                # Domain, service, controller, exception handler
│   │   └── test/java/                # Integration tests (Testcontainers + REST Assured)
│   └── pom.xml
├── order-service/                    # Order management microservice
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── clients/              # Catalog service HTTP client (Resilience4j)
│   │   │   ├── config/               # RabbitMQ & ShedLock scheduler config
│   │   │   ├── domain/               # Entities, services, repositories, outbox events
│   │   │   ├── jobs/                 # Scheduled jobs (event publishing, order processing)
│   │   │   └── web/                  # REST controllers & global exception handler
│   │   └── test/java/                # Integration tests (Testcontainers + WireMock + REST Assured)
│   └── pom.xml
├── notification-service/             # Email notification microservice
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── config/               # RabbitMQ config (listener container factory)
│   │   │   ├── domain/               # NotificationService, event entity & repository
│   │   │   └── events/               # RabbitMQ listener handlers per event type
│   │   └── test/java/                # Integration tests (Testcontainers)
│   └── pom.xml
├── deployment/
│   └── docker-compose/
│       ├── infra.yml                 # Local infrastructure (PostgreSQL x3, RabbitMQ, MailHog)
│       └── apps.yml                  # Application services (all 3 microservices)
├── pom.xml                           # Parent POM (multi-module)
└── README.md
```

---

## Key Design Decisions

### Outbox Pattern
Order events (created, delivered, cancelled, error) are first written to an `order_events` table within the same transaction as the order. A separate scheduled job (`OrderEventsPublishingJob`) then picks them up and publishes them to RabbitMQ. This guarantees no events are lost even if RabbitMQ is temporarily unavailable.

### Idempotent Consumer
The notification-service stores the `eventId` of every processed event in its own `order_events` table. Before processing any incoming message, it checks for a duplicate `eventId` and skips it if already handled. This prevents duplicate emails in case RabbitMQ redelivers a message.

### ShedLock for Distributed Scheduling
Both scheduled jobs (`OrderEventsPublishingJob` and `OrderProcessingJob`) are protected with ShedLock. This ensures that in a multi-instance deployment, only one instance executes the job at a time, preventing duplicate event publishing or duplicate order processing.

### Resilience4j for Inter-Service Communication
The `order-service` communicates with `catalog-service` via HTTP to
