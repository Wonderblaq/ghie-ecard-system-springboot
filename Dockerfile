# Use a lightweight Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set the directory inside the container
WORKDIR /app

#  run "./mvnw clean package" first! to generate jar file
COPY target/*.jar app.jar

# Expose the port your Spring app runs on
EXPOSE 8080

# The command to start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
