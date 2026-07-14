# Stage 1: Compile the source code inside a Java 24 environment
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Stage 2: Create the lightweight production container
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]