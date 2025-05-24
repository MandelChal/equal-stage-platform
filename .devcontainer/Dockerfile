# Base image: Maven + JDK 17
FROM maven:3.9.3-eclipse-temurin-17

# Set working directory inside container
WORKDIR /workspace

# Copy your entire project into the container
COPY . .

# Pre-install dependencies to speed up dev startup
RUN mvn clean install -DskipTests=true

# Set default command for interactive dev sessions
CMD ["mvn", "spring-boot:run"]
