# Stage 1: Build the JAR file
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /home/ahdev/application/wallet-dev/build/libs/wallet-backend-0.0.1-SNAPSHOT.jar wallet-dev.jar
EXPOSE 8081
CMD ["java", "-jar", "/wallet.jar"]
