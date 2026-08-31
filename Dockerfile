# Multi-Stage Dockerfile for Employee Helpdesk System (Aditya Singh - Roll No: 23102B0010)

# =========================================================================
# STAGE 1: Build & Package Spring Boot Application
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build executable JAR without running tests (tests executed in CI quality gate stage)
RUN mvn clean package -DskipTests

# =========================================================================
# STAGE 2: Production JRE Lightweight Runtime Container
# =========================================================================
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Aditya Singh <aditya.singh@company.com>"
LABEL project="Employee Helpdesk System - DevOps SEM-7"
LABEL version="1.0.0"

# Create non-root system user for container security
RUN addgroup -S helpdesk && adduser -S helpdesk -G helpdesk

WORKDIR /app

# Copy compiled JAR from builder stage
COPY --from=builder /build/target/employee-helpdesk-system-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown -R helpdesk:helpdesk /app

# Switch to non-root user
USER helpdesk

# Expose HTTP application port
EXPOSE 8082

# Environment variables
ENV PORT=8082 \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Container Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Execute Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
