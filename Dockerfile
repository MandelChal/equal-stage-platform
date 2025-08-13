# meaning the docker image that our image will be based on 
FROM eclipse-temurin:21-jdk AS build

# meaning this will be the wroeking directory
WORKDIR /app

RUN apt update && apt install -y git
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

COPY src ./src

# we are misiing EXPOSE 8080 configuration - which port will be exposed?

CMD ["./mvnw", "spring-boot:run"]
#./mvnw spring-boot:run
# than on browser -> http://localhost:8080/swagger-ui/index.html
#./mvnw test -Dset=fakerTest#testUpdateLecturer
#./mvnw test -Dset=<filename>#<functionName>