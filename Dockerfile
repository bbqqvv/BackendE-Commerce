# Stage 1: build app bằng Maven/Gradle
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: chạy app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# mở port app (nếu app bạn cấu hình port khác, đổi ở đây)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
