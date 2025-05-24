# Use a lightweight OpenJDK image
FROM openjdk:17-jdk-slim

# Create a directory for the app
WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
