FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . /app
WORKDIR /app/ms-usuarios
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/ms-usuarios/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]