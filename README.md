# Pet API

A simple REST application built with **Spring Boot** for managing pets. It exposes **REST APIs** to create, retrieve, update, and delete (**CRUD**) pet information using a relational database, with the codebase structured to allow a future switch to a non-relational backend.

## Requirements Met

- **Java** — The specification required Java 17+; this project uses **Java 25** (as defined in `pom.xml`).
- **Relational database** — Uses **PostgreSQL** by default; **H2** (in-memory) is available for development and tests. Data access is abstracted behind a repository interface.
- **REST APIs** — Implemented with **Spring Web** (Spring MVC via `spring-boot-starter-webmvc`) and follow RESTful conventions.

The design is **prepared for a non-relational database**: a single `IPetRepository` interface is used for persistence, with implementations for relational (JPA) and a stub for non-relational backends. A MongoDB profile is defined in configuration for future use.

## Pet Entity

| Property    | Type   | Required | Constraints                          |
|-------------|--------|----------|--------------------------------------|
| `id`        | Long   | —        | Auto-generated                       |
| `name`      | String | Yes      | Not empty                            |
| `species`   | String | Yes      | Not empty (e.g. "Dog", "Cat")        |
| `age`       | Integer| No       | ≥ 0 when provided                    |
| `ownerName` | String | No       | —                                    |

## API Overview

Base path: **`/api/pets`**

| Method   | Path         | Description |
|----------|--------------|-------------|
| `GET`    | `/api/pets`  | List all pets, with optional query filters: `species`, `ownerName`, `name`, `age` (exact), or `minAge` / `maxAge` (range). |
| `GET`    | `/api/pets/{id}` | Get a single pet by ID. |
| `POST`   | `/api/pets`  | Create a new pet. Request body: `name`, `species`, `age` (optional), `ownerName` (optional). |
| `PUT`    | `/api/pets/{id}` | Update an existing pet by ID. |
| `DELETE` | `/api/pets/{id}` | Delete a pet by ID. |

Responses use JSON. Validation and not found entity errors are handled via a global exception handler and return appropriate HTTP status codes (e.g. 400, 404).

## Tech Stack & Dependencies

The following are defined in the project’s `pom.xml`:

| Component        | Version / artifact |
|------------------|--------------------|
| **Java**         | **25**             |
| **Spring Boot**  | 4.0.3              |
| **Build**        | Maven              |

**Main dependencies:**

- `spring-boot-starter-webmvc` — REST API (Spring Web)
- `spring-boot-starter-data-jpa` — Relational persistence
- `spring-boot-starter-validation` — Bean Validation (Jakarta)
- `postgresql` (runtime) — PostgreSQL JDBC driver
- `h2` (runtime) — H2 in-memory database for tests and optional profile

**Test dependencies:** `spring-boot-starter-test`, `spring-boot-starter-webmvc-test`, `jackson-databind`, and related Spring Test modules.

## Running the Application

### Prerequisites

- **Java 25** (matches project `java.version` in `pom.xml`)
- **Maven 3.6+**
- For default profile: **PostgreSQL** (e.g. local instance with database `pet` and credentials matching `application.yaml`)

### Build

```bash
mvn clean install
```

### Run with PostgreSQL (default)

Ensure PostgreSQL is running and the `pet` database exists (or is created automatically, depending on your setup). Then:

```bash
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/petapi-0.0.1-SNAPSHOT.jar
```

### Run with H2 (in-memory, no PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

The app will use an in-memory H2 database; no external DB is required.

### Configuration

Main settings live in `src/main/resources/application.yaml`:

- **Default / `postgresql` profile**: PostgreSQL URL, user, password, and JPA options.
- **`h2` profile**: In-memory H2, no external DB.
- **`mongodb` profile**: Placeholder for a future MongoDB backend (implementation not included).

Override as needed (e.g., via environment variables or `application-local.yaml`).

## Project Structure (high level)

- **`controller`** — REST endpoints and DTOs (request/response, filters).
- **`service`** — Business logic for pet CRUD and filtering.
- **`repository`** — Persistence: `IPetRepository` plus JPA (relational) and NoSQL stub implementations.
- **`model`** — Domain model `Pet` (persistence-agnostic).
- **`config`** — Database type and properties.
- **`exception`** — Global exception handling and error responses.

## Tests

- **Unit / slice tests**: `PetApiMockMvcTest` (MockMvc, mocked service).
- **Integration with H2**: `PetApiH2Test`.
- **Integration with PostgreSQL**: `PetApiPostgresTest` (requires a running PostgreSQL instance with test DB).

Run all tests:

```bash
mvn test
```
