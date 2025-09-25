# Gym crm microservices

### Project Overview
A microservices-based system for managing gym training processes.
The platform provides centralized authentication, workload tracking for trainers, and asynchronous communication using ActiveMQ.

## Services Overview

| Service | Port | Description | Documentation                              |
|---------|------|-------------|--------------------------------------------|
| **🌐 API Gateway** | 8080 | Single entry point, request routing | [📖 README](./gateway-service/README.md)   |
| **🔍 Discovery Service** | 8761 | Service registry (Eureka) | [📖 README](./discovery-service/README.md) |
| **💼 Gym CRM Core** | 8090 | Main business logic and CRM features | [📖 README](./gym-crm-core/README.md)      |
| **⚖️ Workload Service** | 8085 | Trainer workload calculation | [📖 README](./workload-service/README.md)  |

### Infrastructure Components

| Component | Port(s) | Description                                |
|-----------|---------|--------------------------------------------|
| **MySQL** | 3306 | Primary database for Gym CRM core service |
| **ActiveMQ** | 61616, 8161 | Message broker (JMS + Web Console)         |

# Quick Start

### Prerequisites

Ensure you have the following installed:

| Technology | Minimum Version | Purpose                    |
|------------|-----------------|----------------------------|
| Java | 21+ | Runtime environment        |
| Maven | 3.8+ | Build tool                 |
| Docker & Docker Compose | 20.10+ | Containerization           |
| MySQL | 8.0+ | Database                   |
| Git | 2.30+ | Version control            |
| ActiveMQ | latest | Asynchronous communication |

### Local Development

1. **Infrastructure Setup**

**Option 1: Docker (Recommended)**
```bash
# MySQL
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=gym_crm \
  -e MYSQL_USER=gcauser \
  -e MYSQL_PASSWORD=gcauser \
  -p 3306:3306 \
  mysql:8.0

# ActiveMQ
docker run -d \
  --name activemq \
  -e ACTIVEMQ_ADMIN_LOGIN=admin \
  -e ACTIVEMQ_ADMIN_PASSWORD=admin \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq:latest
```

**Option 2: Local Installation**

**MySQL Setup:**
- Install MySQL 8.0+
- Create database and user:
```sql
CREATE DATABASE gym_crm;
CREATE USER 'gcauser'@'localhost' IDENTIFIED BY 'gcauser';
GRANT ALL PRIVILEGES ON gym_crm.* TO 'gcauser'@'localhost';
```

**ActiveMQ Setup:**
- Download from: https://activemq.apache.org/components/classic/download/
- Start with: `./bin/activemq start`
- Web console: http://localhost:8161 (admin/admin)

2. **Services Setup** - Follow individual service documentation:
    - 🌐 [API Gateway Setup](./gateway-service/README.md#getting-started)
    - 💼 [Gym CRM Core Setup](./gym-crm-core/README.md#getting-started-local-setup)
    - ⚖️ [Workload Service Setup](./workload-service/README.md#getting-started)

3. **Start Services in Order**
```bash
# 1. Discovery Service (wait for startup)
cd discovery-service && mvn spring-boot:run &

# 2. Other services (can start in parallel)
cd ../api-gateway && mvn spring-boot:run &
cd ../gym-crm-core && mvn spring-boot:run &
cd ../workload-service && mvn spring-boot:run &
```

## Configuration

### Global Environment Variables

```bash
# Database Configuration
export DB_USERNAME=gcauser
export DB_PASSWORD=gcauser
export DB_URL=jdbc:mysql://localhost:3306/gym_crm

# ActiveMQ Configuration
export ACTIVEMQ_BROKER_URL=tcp://localhost:61616
export ACTIVEMQ_USER=gca
export ACTIVEMQ_PASSWORD=gca

# JWT Configuration
export JWT_KEY=u8Z4vN3kXxM2qB7eG9TfRjL5cPwYhQsDzVuAiKmNzXtGbHoC
```

> 📋 **Service-specific configurations** are documented in individual service README files.