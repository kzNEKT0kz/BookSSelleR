# Use JDK 17 runtime
FROM openjdk:17-jdk-slim

# Author info
LABEL authors="gabdu"

# Set working directory inside container
WORKDIR /app

# Copy built JAR into container
COPY build/libs/inventory-api-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
