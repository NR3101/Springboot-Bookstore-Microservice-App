# Bookstore Microservice App

> ⚠️ **Work in Progress** — This project is actively under development. New microservices and features are being added incrementally.

A Spring Boot-based microservice application for managing a bookstore, built with a multi-module Maven structure.

---

## Modules

| Module                 | Port | Description                                                              | Status |
|------------------------|------|--------------------------------------------------------------------------|--------|
| `api-gateway`          | 8989 | Single entry point — routes requests to downstream services              | Done   |
| `catalog-service`      | 8081 | Manages book catalog and inventory                                       | Done   |
| `order-service`        | 8082 | Handles order creation, processing, and events                           | Done   |
| `notification-service` | 8083 | Sends email notifications for order lifecycle events                     | Done   |
| `bookstore-webapp`     | 8080 | Server-side rendered web frontend (Thymeleaf + Alpine.js)                | Done   |

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

### API Gateway
- **Spring Cloud Gateway (WebFlux)** – Reactive API Gateway for routing and CORS
- **SpringDoc OpenAPI WebFlux UI** – Aggregated Swagger UI at the gateway level

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

### Web Frontend (`bookstore-webapp`)
- **Thymeleaf** + **Thymeleaf Layout Dialect** – Server-side HTML templating
- **Thymeleaf Extras Spring Security 6** – Security-aware Thymeleaf expressions (`sec:authorize`)
- **Alpine.js** (via WebJar) – Lightweight reactive JS for dynamic UI
- **Bootstrap 5** (via WebJar) – CSS/component framework
- **jQuery** (via WebJar) – AJAX calls (`$.getJSON`, `$.ajax`)
- **Font Awesome** (via WebJar) – Icons
- **Spring HTTP Interface** (`@GetExchange`, `@PostExchange`) – Declarative HTTP clients (`CatalogServiceClient`, `OrderServiceClient`) for server-side calls to the API gateway

### Testing
- **Testcontainers** – Integration testing with real PostgreSQL and RabbitMQ containers
- **WireMock** (via Testcontainers) – Mocking external HTTP services in integration tests
- **REST Assured** – API-level integration testing
- **MockMvc** – Unit-level controller testing (`@WebMvcTest`)
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
# API Gateway
cd api-gateway && ./mvnw spring-boot:run

# Catalog Service
cd catalog-service && ./mvnw spring-boot:run

# Order Service
cd order-service && ./mvnw spring-boot:run

# Notification Service
cd notification-service && ./mvnw spring-boot:run

# Bookstore Web App
cd bookstore-webapp && ./mvnw spring-boot:run
```

| Service                | URL                          |
|------------------------|------------------------------|
| `bookstore-webapp`     | `http://localhost:8080`      |
| `api-gateway`          | `http://localhost:8989`      |
| `catalog-service`      | `http://localhost:8081`      |
| `order-service`        | `http://localhost:8082`      |
| `notification-service` | `http://localhost:8083`      |
| RabbitMQ UI            | `http://localhost:15672`     |
| MailHog UI             | `http://localhost:8025`      |

---

## API Gateway Routes

All backend services can also be accessed through the API Gateway on port `8989`:

| Route prefix   | Forwards to        | Example                                          |
|----------------|--------------------|--------------------------------------------------|
| `/catalog/**`  | `catalog-service`  | `GET http://localhost:8989/catalog/api/products` |
| `/orders/**`   | `order-service`    | `POST http://localhost:8989/orders/api/orders`   |

The gateway also aggregates OpenAPI docs from all registered services.

---

## Web Frontend Pages

The `bookstore-webapp` provides the following pages:

| Page             | URL                        | Description                                      |
|------------------|----------------------------|--------------------------------------------------|
| Products listing | `http://localhost:8080/`   | Browse paginated product catalog, add to cart    |
| Cart             | `http://localhost:8080/cart` | Review cart, fill in customer & address, place order |
| Orders           | `http://localhost:8080/orders` | View all orders for the logged-in user         |
| Order details    | `http://localhost:8080/orders/{orderNumber}` | Full details of a single order   |

---

## API Documentation (Swagger UI)

| Service                | Swagger UI URL                                  |
|------------------------|-------------------------------------------------|
| `api-gateway`          | `http://localhost:8989/swagger-ui/index.html`   |
| `catalog-service`      | `http://localhost:8081/swagger-ui/index.html`   |
| `order-service`        | `http://localhost:8082/swagger-ui/index.html`   |
| `notification-service` | `http://localhost:8083/swagger-ui/index.html`   |

---

## Configuration

Services use environment variables with sensible defaults for local development.

### bookstore-webapp

| Variable          | Default                   | Description                        |
|-------------------|---------------------------|------------------------------------|
| `API_GATEWAY_URL` | `http://localhost:8989`   | API gateway base URL for server-side HTTP client calls |

### api-gateway

| Variable              | Default                   | Description                |
|-----------------------|---------------------------|----------------------------|
| `CATALOG_SERVICE_URL` | `http://localhost:8081`   | Catalog service base URL   |
| `ORDER_SERVICE_URL`   | `http://localhost:8082`   | Order service base URL     |

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
| `bookstore-webapp`     | `GET localhost:8080/actuator/health`    | `GET localhost:8080/actuator/info`    |
| `api-gateway`          | `GET localhost:8989/actuator/health`    | `GET localhost:8989/actuator/info`    |
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
| `bookstore-webapp`     | `bookstore-webapp/**`     | Build → Test → Build & Push Docker Image   |
| `api-gateway`          | `api-gateway/**`          | Build → Test → Build & Push Docker Image   |
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
│       ├── bookstore-webapp.yml      # CI/CD for bookstore-webapp
│       ├── api-gateway.yml           # CI/CD for api-gateway
│       ├── catalog-service.yml       # CI/CD for catalog-service
│       ├── order-service.yml         # CI/CD for order-service
│       └── notification-service.yml  # CI/CD for notification-service
├── bookstore-webapp/                 # Thymeleaf + Alpine.js web frontend
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── clients/
│   │   │   │   ├── catalog/          # CatalogServiceClient (Spring HTTP Interface)
│   │   │   │   └── orders/           # OrderServiceClient (Spring HTTP Interface) + DTOs
│   │   │   ├── config/               # ClientsConfig — registers HTTP client beans
│   │   │   └── web/controllers/      # ProductController, OrderController, CartController
│   │   └── main/resources/
│   │       ├── static/
│   │       │   ├── css/              # styles.css
│   │       │   ├── images/           # books.png
│   │       │   └── js/               # products.js, cart.js, cartStore.js, orders.js, orderDetails.js
│   │       └── templates/            # Thymeleaf templates (layout, products, cart, orders, order_details)
│   └── pom.xml
├── api-gateway/                      # Spring Cloud Gateway — single entry point
│   ├── src/main/resources/           # application.yml with route definitions
│   └── pom.xml
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
│   │   │   ├── domain/               # Entities, services, repositories, outbox events, SecurityService
│   │   │   ├── jobs/                 # Scheduled jobs (event publishing, order processing)
│   │   │   └── web/                  # REST controllers & global exception handler
│   │   └── test/java/
│   │       ├── web/controllers/      # Integration tests (REST Assured) + Unit tests (MockMvc)
│   │       └── testdata/             # TestDataFactory for test data generation
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
│       └── apps.yml                  # Application services (all 5 microservices)
├── pom.xml                           # Parent POM (multi-module)
└── README.md
```

---

## Key Design Decisions

### Bookstore Web App
The `bookstore-webapp` is a server-side rendered frontend using Thymeleaf templates decorated with a shared `layout.html`. Dynamic behaviour (pagination, cart, order forms) is handled client-side with Alpine.js and jQuery AJAX calls — all targeting the webapp's own backend endpoints (`/api/products`, `/api/orders`), never the API gateway directly (avoiding CORS). The webapp makes server-side HTTP calls to the API gateway via Spring's declarative HTTP Interface clients (`CatalogServiceClient`, `OrderServiceClient`), with a shared `RestClientCustomizer` bean that sets the base URL and configures connect/read timeouts (5s each).

Alpine.js scripts are registered with `Alpine.data()` inside `alpine:init` event listeners. To guarantee these registrations happen before Alpine initialises, all page scripts are loaded **before** the Alpine CDN script tag.

### API Gateway
The `api-gateway` module uses Spring Cloud Gateway (WebFlux) as the single entry point for all client requests. It routes `/catalog/**` to the catalog-service and `/orders/**` to the order-service, stripping the prefix with a `RewritePath` filter. Global CORS configuration is applied at the gateway level. SpringDoc OpenAPI WebFlux UI is configured to aggregate API docs from all downstream services.

### Outbox Pattern
Order events (created, delivered, cancelled, error) are first written to an `order_events` table within the same transaction as the order. A separate scheduled job (`OrderEventsPublishingJob`) then picks them up and publishes them to RabbitMQ. This guarantees no events are lost even if RabbitMQ is temporarily unavailable.

### Idempotent Consumer
The notification-service stores the `eventId` of every processed event in its own `order_events` table. Before processing any incoming message, it checks for a duplicate `eventId` and skips it if already handled. This prevents duplicate emails in case RabbitMQ redelivers a message.

### ShedLock for Distributed Scheduling
Both scheduled jobs (`OrderEventsPublishingJob` and `OrderProcessingJob`) are protected with ShedLock. This ensures that in a multi-instance deployment, only one instance executes the job at a time, preventing duplicate event publishing or duplicate order processing.

### Resilience4j for Inter-Service Communication
The `order-service` communicates with `catalog-service` via HTTP to validate products at order creation time. Resilience4j provides retry (2 attempts, 300ms wait) and circuit breaker (COUNT_BASED, 50% failure threshold) to handle catalog-service downtime gracefully, with a fallback returning an empty result.

### Order Validation
Before creating an order, the `OrderValidator` fetches each item from the catalog-service and verifies that the submitted price matches the actual product price, rejecting orders with price mismatches.

### SecurityService
The `SecurityService` in the order-service abstracts the retrieval of the currently authenticated username, allowing the controller to associate orders with users without coupling directly to the security context.

### Testing Strategy
- **Integration tests** (`@SpringBootTest` + Testcontainers + REST Assured + WireMock): full end-to-end tests against real containers for databases and message brokers, with WireMock mocking the catalog-service HTTP calls in the order-service tests.
- **Unit tests** (`@WebMvcTest` + MockMvc + Mockito): fast, isolated controller-layer tests that verify request validation and HTTP response codes without starting a full context.
