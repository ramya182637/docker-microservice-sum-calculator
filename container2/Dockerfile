FROM maven:3.8.1-openjdk-11 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package

FROM openjdk:latest
COPY target/container2-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
CMD ["java", "-jar", "container2-0.0.1-SNAPSHOT.jar"]
