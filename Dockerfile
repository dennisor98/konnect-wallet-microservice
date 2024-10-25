# Stage 1: Build the application
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install Redis
RUN apt-get update && \
    apt-get install -y redis-server && \
    rm -rf /var/lib/apt/lists/*

# Copy application JAR
COPY build/libs/wallet-backend-0.0.1-SNAPSHOT.jar wallet-dev.jar

# Expose ports for the app and Redis
EXPOSE 8081 6379

# Start Redis and the application
CMD redis-server --daemonize yes && java -jar wallet-dev.jar
