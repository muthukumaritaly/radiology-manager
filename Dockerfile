# =========================================================================
# Stage 1: Build stage
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy dependency definition file first to leverage Docker layer caching
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the production-ready executable jar
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# =========================================================================
# Stage 2: Runtime stage
# =========================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as a non-privileged system user with static UID/GID for Kubernetes compatibility
RUN addgroup -g 10001 -S appgroup && adduser -u 10001 -S appuser -G appgroup
USER appuser

# Copy the built jar from the builder stage
COPY --from=builder /build/target/radiology-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose default Spring Boot application port
EXPOSE 8080

# Configure JVM options for container optimization
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
