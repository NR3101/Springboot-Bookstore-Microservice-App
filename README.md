# Bookstore Microservice App

> ⚠️ **Work in Progress** — This project is actively under development. New microservices and features are being added incrementally.

A Spring Boot-based microservice application for managing a bookstore, built with a multi-module Maven structure.

---

## Modules

| Module            | Port | Description                        | Status         |
|-------------------|------|------------------------------------|----------------|
| `catalog-service` | 8081 | Manages book catalog and inventory | 🚧 In Progress |

---

## Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA** with PostgreSQL
- **Flyway** – Database migrations
- **Spring Boot Actuator** – Health & info endpoints
- **Testcontainers** – Integration testing with real PostgreSQL
- **Spotless** – Code formatting (Palantir Java Format)
- **Docker Compose** – Local infrastructure

---

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.9+ (or use the included `mvnw` wrapper)

---

## Getting Started

### 1. Start Infrastructure

```bash
docker compose -f deployment/docker-compose/infra.yml up -d
```

### 2. Build the Project

```bash
./mvnw clean package -DskipTests
```

### 3. Run a Service

```bash
cd catalog-service
./mvnw spring-boot:run
```

`catalog-service` will be available at: `http://localhost:8081`

---

## Configuration

Services use environment variables with sensible defaults for local development:

### catalog-service

| Variable      | Default                                        | Description         |
|---------------|------------------------------------------------|---------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:15432/catalog_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres`                                     | DB username         |
| `DB_PASSWORD` | `password`                                     | DB password         |

---

## Actuator Endpoints

| Service           | Health                               | Info                               |
|-------------------|--------------------------------------|------------------------------------|
| `catalog-service` | `GET localhost:8081/actuator/health` | `GET localhost:8081/actuator/info` |

---

## Running Tests

```bash
# Run tests for all modules
./mvnw test

# Run tests for a specific module
cd catalog-service && ./mvnw test
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

## Project Structure

```
bookstore-microservice-app/
├── catalog-service/          # Book catalog microservice
├── deployment/
│   └── docker-compose/
│       └── infra.yml         # Local infrastructure (databases, etc.)
├── pom.xml                   # Parent POM (multi-module)
└── README.md
```
