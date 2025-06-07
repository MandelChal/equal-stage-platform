FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

RUN apt update && apt install -y git
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
#./mvnw spring-boot:run