## Integration Tests

This directory contains integration tests for the project. Integration tests are designed to test the interaction between different components of the system to ensure they work together as expected.

## Prerequisites

Before running the tests, make sure you have:

| Technology | Minimum Version | Purpose             |
|------------|-----------------|---------------------|
| Java | 21+ | Runtime environment |
| Maven | 3.8+ | Build tool          |
| Docker | 20+ | Сontainerization    |

> ⚠️ **Important**: Docker must be running before starting test execution!

## Setup

### 1. Start Docker Containers

Before running tests, you need to start the test environment using Docker Compose:

```bash
docker-compose -f docker-compose.test.yml up -d
```

This command will start all required services (databases, ActiveMQ, etc.) in detached mode.

### 2. Verify Container Status

Ensure all containers are running successfully:

```bash
docker-compose -f docker-compose.test.yml ps
```

All services should have `Up` or `running` status.

## Running Tests

To run the integration tests, use the following Maven command:

```bash
mvn test
```
This command will execute all integration tests defined in the project.

```bash
mvn test '-Dcucumber.filter.tags=@PositiveScenario'

mvn test '-Dcucumber.filter.tags=@NegativeScenario'
```
Run only positive scenarios or negative scenarios

## Functionality Overview

### Main Feature Files:

- **gym-crm-core.feature** - Trainee profile management tests (create, update, delete, deactivate)
- **login.feature** - User registration and login tests
- **permission.feature** - Profile access permission tests
- **integration.feature** - Integration tests between Training and Workload services via JMS
- **workload.feature** - Trainer workload management tests

## Cleanup After Testing

After completing tests, stop and remove containers:

```bash
docker-compose -f docker-compose.test.yml down
```

For complete cleanup including volumes:

```bash
docker-compose -f docker-compose.test.yml down -v
```