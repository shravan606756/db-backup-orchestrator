# DB Backup Orchestrator Engine

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=spring)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)
![Build Status](https://img.shields.io/badge/Build-Stable-success?style=for-the-badge)

**Artifact:** Backend Service
**Architecture:** Layered Event-Driven Microservice
**Runtime:** Java 17 / Spring Boot 3.2

## 1. Architectural Overview

The **DB Backup Orchestrator** is an enterprise-grade backend system designed to automate, audit, and securely manage database backup lifecycles. The system is engineered as a high-fidelity, containerized micro-architecture that enforces a strict **"Append-Only" security model**, ensuring data immutability and audit compliance.

The core design philosophy prioritizes **Extensibility** (via the Strategy Pattern) and **Non-Blocking Performance** (via Event-Driven Design), ensuring the system remains stable while adapting to heterogeneous database environments.

## 2. System Architecture & Design Patterns

The application structure strictly adheres to established software engineering patterns, evident in the package organization (`com.shravan.resilientdb_engine`):

### 2.1. Strategy Pattern (Polymorphic Execution)
* **Component:** `backup.strategy.BackupStrategy` & `backup.strategy.impl.OthersBackupStrategy`
* **Design Principle:** Open/Closed Principle (OCP).
* **Implementation:** The system defines a generic interface for backup operations. Concrete implementations (e.g., `OthersBackupStrategy`) encapsulate vendor-specific CLI logic (e.g., `pg_dump`). This allows new database support to be injected without modifying the core service logic.

### 2.2. Event-Driven Architecture (Observer Pattern)
* **Component:** `backup.event.BackupStatusChangedEvent` & `BackupStatusEventListener`
* **Design Principle:** Decoupling of Concerns.
* **Implementation:** State transitions in backup jobs (e.g., `PENDING` -> `COMPLETED`) are broadcast via the internal Spring Application Event Bus. Listeners react asynchronously to handle side effects—such as WebSocket notifications or audit logging—without blocking the primary transaction.

### 2.3. Asynchronous Worker Pattern
* **Component:** `backup.processor.BackupJobProcessorService`
* **Design Principle:** Resource Optimization.
* **Implementation:** Long-running I/O operations (file compression, encryption, transfer) are offloaded to managed worker threads. This prevents thread starvation on the Servlet container (Tomcat), ensuring the API remains responsive under load.

## 3. Engineering Best Practices

The development lifecycle enforces rigorous standards to ensure maintainability and robustness:

* **Separation of Concerns (SoC):**
    * **DTO Pattern (`backup.dto`):** Data Transfer Objects decouple internal JPA entities (`BackupJob`) from the external API contract, preventing over-posting vulnerabilities.
    * **Service Isolation (`backup.service`):** Business logic is encapsulated entirely within the service layer, keeping Controllers "thin."
* **Global Exception Handling:**
    * **Component:** `common.exception.GlobalExceptionHandler`
    * **Implementation:** A centralized `@ControllerAdvice` captures runtime exceptions and transforms them into standardized, RFC-7807 compliant JSON error responses.
* **Transactional Integrity:** Critical metadata updates are wrapped in `@Transactional` boundaries to ensure ACID compliance.

## 4. Infrastructure & Topology

The system is deployed as a multi-container topology orchestrating three distinct services via Docker Compose:

1.  **Application Container (Spring Boot):**
    * **Port:** `8080` (API Gateway)
    * **Persistence:** Mounts a host volume (`./uploads:/uploads`) to ensure backup artifacts persist across container restarts.
    * **Network:** Acts as the only entry point, proxying requests to internal services.
2.  **Persistence Layer (PostgreSQL 15):**
    * **Port:** `5432`
    * **Role:** Stores relational data including Job Metadata, Configuration Profiles, and Audit Logs.
    * **Security:** Isolated within the internal Docker bridge network.
3.  **Distributed Cache (Redis 7):**
    * **Port:** `6379`
    * **Role:** Provides sub-millisecond access to ephemeral job status data, significantly reducing read load on the primary database during high-frequency polling.

## 5. Project Structure

```
src/main/java/com/shravan/resilientdb_engine/
├── backup/
│   ├── controller/      # REST Interface Layer (API Endpoints)
│   ├── dto/             # Data Transfer Objects (Request/Response)
│   ├── entity/          # JPA Entities (Database Schema)
│   ├── event/           # Event Bus (Observer Pattern)
│   ├── processor/       # Async Job Processing Logic
│   ├── repository/      # Data Access Layer (Spring Data JPA)
│   ├── service/         # Business Logic & Transaction Management
│   └── strategy/        # Strategy Pattern Implementations
└── common/
    └── exception/       # Global Error Handling
```
## 6. Quality Assurance & Testing

Quality assurance is integrated into the build lifecycle (`src/test/java`), ensuring reliability across the service layer and application context.

### Testing Scope

**Unit Testing**

- `BackupServiceImplTest` utilizes Mockito to isolate business logic.
- Verifies state transitions and strategy selection.
- External dependencies (Repositories, File System) are mocked.

**Integration Testing**

- `ResilientDbEngineApplicationTests` validates the Inversion of Control (IoC) container.
- Ensures correct bean wiring.
- Verifies configuration property loading.

### Execution

To execute the test suite without packaging the application:

```bash
./mvnw test
```

## 7. Deployment & Execution

### Prerequisites

- Docker Engine 24.0+

### Quick Start (No Java Required)

The system is designed for one-click deployment.

#### Clone the Repository

```bash
git clone https://github.com/shravan606756/db-backup-orchestrator.git
cd db-backup-orchestrator
```

### Start the Stack

This command builds the image and starts the containers in detached mode.

```bash
docker compose up -d --build
```
## 8. API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/backups` | Retrieve all backup jobs |
| POST | `/api/backups/upload` | Upload a backup file and create a backup job (multipart/form-data: `file`, `jobName`, `databaseType`) |
| GET | `/api/backups/download/{id}` | Download the backup file for a given job ID |


## Future Roadmap & Scalability

The current architecture is designed as a foundational unit for enterprise data security. The following enhancements are planned to scale the system for multi-cloud and petabyte-scale environments:

### Distributed Storage Integration (Hadoop/HDFS)

Transition from local Docker volumes to Apache Hadoop (HDFS) for long-term data archival. This will allow the system to handle massive backup files (petabyte scale) by distributing chunks across a commodity cluster, decoupling storage capacity from the application server. This also enables downstream data analytics on backup logs using Apache Spark.

### Cloud-Native Disaster Recovery (DR)

Implement cloud storage adapters for AWS S3 (Glacier) and Google Cloud Storage. This hybrid-cloud strategy will automatically replicate Gold Standard backups to off-site immutable cloud buckets to ensure business continuity even in the event of a total data center failure.

### Zero-Trust Security Enhancements

Integrate HashiCorp Vault for dynamic database credential rotation and implement blockchain notarization to create a tamper-proof cryptographic proof of every backup's existence and integrity.

