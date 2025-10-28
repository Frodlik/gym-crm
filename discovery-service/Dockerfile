FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .

COPY discovery-service ./discovery-service
COPY gateway-service ./gateway-service
COPY gym-crm-core ./gym-crm-core
COPY workload-service ./workload-service
COPY integration-tests ./integration-tests

RUN mvn -pl discovery-service -am clean package spring-boot:repackage -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/discovery-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]