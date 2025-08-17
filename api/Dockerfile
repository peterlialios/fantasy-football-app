# Multi-stage build for Spring Boot API
FROM gradle:8.12.1-jdk17 AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle .
COPY settings.gradle.kts .
COPY gradle/ gradle/

# Download dependencies (cached if no changes to build files)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health check and create non-root user
RUN apk add --no-cache curl && \
    addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]