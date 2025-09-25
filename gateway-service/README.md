# Gateway Service

Spring Boot application that serves as a Gateway Service using Spring Cloud Gateway.

## Prerequisites

| Technology | Minimum Version | Purpose |
|------------|-----------------|---------|
| Java | 21+ | Runtime environment |
| Maven | 3.8+ | Build tool |
| Eureka Discovery | - | Service registry integration |


## Getting Started

### 1. Environment Variables

```bash
# JWT Configuration (for authentication)
JWT_KEY=u8Z4vN3kXxM2qB7eG9TfRjL5cPwYhQsDzVuAiKmNzXtGbHoC
```

### 2. Start the Service

**Prerequisites:** Ensure Discovery Service is running first

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Start the gateway
mvn spring-boot:run
```
The gateway will be available at: **http://localhost:8080**

## Route Configuration

The gateway automatically configures routes for registered services:

| Service | Route Pattern                 | Target |
|---------|-------------------------------|--------|
| **Gym CRM Core** | `/api/v1/trainers/workload/**` | `http://gym-crm-core:8090` |
| **Workload Service** | `/api/v1/**`          | `http://workload-service:8085` |

