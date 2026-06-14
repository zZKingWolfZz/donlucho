# Build stage using Maven and Alpine JDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage using a lightweight JRE 17 Alpine image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/donlucho-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override the port using the PORT env variable at runtime)
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:8080}"]
