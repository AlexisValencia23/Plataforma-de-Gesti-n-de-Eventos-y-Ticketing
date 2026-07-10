#Compilar la aplicación usando Maven y JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven (pom.xml) de la raíz si es necesario,
# o compilar desde el pom local del microservicio.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar el .jar
COPY src ./src
RUN mvn clean package -DskipTests

#Crear la imagen ligera para ejecutar la aplicación
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]