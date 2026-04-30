# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copy the Parent POM first
COPY pom.xml .

# 2. Copy ALL service pom files to satisfy dependencies
COPY notification-service/pom.xml notification-service/
COPY order-service/pom.xml order-service/
COPY analytics-service/pom.xml analytics-service/

# 3. Go offline to cache dependencies
RUN mvn dependency:go-offline -B

# 4. Copy the actual source code
COPY . .

# 5. Build the specific service passed via build-arg
ARG SERVICE_NAME
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ARG SERVICE_NAME
# Copy the built jar from the specific service's target folder
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
