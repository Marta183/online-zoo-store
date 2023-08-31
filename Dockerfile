#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests
#
#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/online-zoo-store-api.jar online-zoo-store-api.jar
EXPOSE 8088
CMD ["java", "-jar", "online-zoo-store-api.jar"]

#FROM openjdk:11-jre-slim
#WORKDIR /app
#COPY target/online-zoo-store-api.jar /app/online-zoo-store-api.jar
#EXPOSE 8088
#CMD ["java", "-jar", "online-zoo-store-api.jar"]