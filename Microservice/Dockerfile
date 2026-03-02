FROM maven:3.8.6-eclipse-temurin-17-alpine AS builder
WORKDIR /microservice
COPY pom.xml .
RUN mvn dependency:resolve -f pom.xml
COPY ./src ./src
RUN mvn package -DskipTests -f pom.xml

FROM eclipse-temurin:17-jre-alpine
WORKDIR /microservice
COPY --from=builder /microservice/target/Microservice-*.jar /microservice/microservice.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/microservice/microservice.jar"]

