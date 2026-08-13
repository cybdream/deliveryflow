# DeliveryFlow

[한국어](README-ko.md)

DeliveryFlow is an operations-focused delivery management API. It manages the delivery workflow from order reception and driver assignment to status updates and completion-history tracking.

The project modernizes previous delivery-reception domain experience with Spring Boot, emphasizing operational reliability, access control, and auditability.

## Project Goals

- Implement order reception and driver assignment workflows as REST APIs.
- Prevent invalid delivery status changes and retain a history of every change.
- Separate administrator and driver permissions.
- Build a production-minded backend with tests, API documentation, Docker, and CI/CD.

## Key Features

- Order creation and lookup
- Driver assignment and reassignment
- Delivery status updates
- Delivery history and operational notes
- Administrator delivery dashboard
- Public delivery tracking

## Delivery Status Flow

```mermaid
flowchart LR
    A[RECEIVED<br/>Order received] --> B[ASSIGNED<br/>Driver assigned]
    B --> C[IN_DELIVERY<br/>Delivery started]
    C --> D[DELIVERED<br/>Delivery completed]
    B --> E[ON_HOLD<br/>Delivery on hold]
    C --> E
    E --> B
    E --> C
    A --> F[CANCELLED<br/>Delivery cancelled]
    B --> F
    E --> F
```

`DELIVERED` and `CANCELLED` are final statuses and cannot be changed afterward.

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL |
| Data Access | Spring Data JPA |
| Security | Spring Security, JWT |
| API Documentation | Swagger / OpenAPI |
| Test | JUnit 5, Mockito |
| Deployment | Docker, GitHub Actions |

## Development Roadmap

| Phase | Scope | Status |
|---|---|---|
| 1 | Project setup; order creation and lookup API | Complete |
| 2 | Driver registration and lookup; driver assignment | Complete |
| 3 | Delivery status updates and delivery history | Complete |
| 4 | JWT login | Complete |
| 5 | Request authentication and role authorization | Complete |
| 6 | Delivery list and search | Complete |
| 7 | Dashboard and standardized error responses | Next |
| 5 | Tests, Docker, CI/CD, and deployment | Planned |

## Documentation

- [Project Specification (English)](docs/deliveryflow-portfolio-spec-en.md)
- [프로젝트 한글 명세서](docs/deliveryflow-portfolio-spec-ko.md)
- [Implementation Status (Korean)](docs/implementation-status-ko.md)
- [Login API (Korean)](docs/login-api-ko.md)
- [JWT Authentication and Authorization (Korean)](docs/jwt-authorization-ko.md)
- [Delivery List and Search API (Korean)](docs/delivery-list-api-ko.md)

## Running the Application

The current order and driver APIs can be run with the following command. Database connection settings are kept in the Git-ignored `application-local.properties` file:

```bash
./gradlew bootRun
```

## Planned Package Structure

```text
src/main/java/com/deliveryflow
├── auth          # Authentication and authorization
├── user          # Users and drivers
├── order         # Order reception
├── delivery      # Deliveries, status updates, and history
└── common        # Shared configuration, exceptions, and responses
```

## Core Design Principles

- Drivers can access and modify only deliveries assigned to them.
- Every status change records the previous status, new status, actor, timestamp, and reason.
- A reason is required when a delivery is put on hold or cancelled.
- Optimistic locking is used to reduce data conflicts caused by concurrent updates.

## Future Improvements

- Daily workload statistics by driver
- Analysis of delivery-failure reasons
- Notification capability
- Customer-facing tracking UI
- Deployment monitoring and log management

