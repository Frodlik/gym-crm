# Workload Service

A service for calculating and processing trainer workload. It receives JMS messages about trainings and updates trainer workload data accordingly.

## Prerequisites
Before you build or run the project, make sure the following software is installed on your machine:

| Technology | Minimum Version | Purpose |
|------------|-----------------|---------|
| Java | 21+ | Runtime environment |
| Maven | 3.8+ | Build tool |
| ActiveMQ | latest | Message broker |

## Getting Started

### 1. Infrastructure Setup

**ActiveMQ Setup:**
```bash
# Option 1: Docker (Recommended)
docker run -d \
  --name activemq \
  -e ACTIVEMQ_ADMIN_LOGIN=admin \
  -e ACTIVEMQ_ADMIN_PASSWORD=admin \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq:latest

# Option 2: Local installation
# Download from: https://activemq.apache.org/components/classic/download/
# Start with: ./bin/activemq start
```

### 2. Environment Variables

```bash
# ActiveMQ Configuration
ACTIVEMQ_BROKER_URL=tcp://localhost:61616
ACTIVEMQ_USER=gca
ACTIVEMQ_PASSWORD=gca

# JWT Configuration
JWT_KEY=u8Z4vN3kXxM2qB7eG9TfRjL5cPwYhQsDzVuAiKmNzXtGbHoC
```

### 3. Start the Service

**Prerequisites:** Ensure ActiveMQ and Discovery Service are running

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Start the service
mvn spring-boot:run
```

The service will be available at: **http://localhost:8085**