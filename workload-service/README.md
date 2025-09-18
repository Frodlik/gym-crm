# Workload Service

A service for calculating and processing trainer workload. It receives JMS messages about trainings and updates trainer workload data accordingly.

----

# Prerequisites
Before you build or run the project, make sure the following software is installed on your machine:

| Technology | Minimum Version |
| ---------- |-----------------|
| Java       | 21              |
| Maven      | 3.8+            |
| ActiveMQ   | latest          |

----

# Environment Variables

```
# ActiveMQ Configuration
ACTIVEMQ_BROKER_URL = tcp://localhost:61616
ACTIVEMQ_USER = gca
ACTIVEMQ_PASSWORD = gca

# JWT Configuration
JWT_KEY=u8Z4vN3kXxM2qB7eG9TfRjL5cPwYhQsDzVuAiKmNzXtGbHoC
```