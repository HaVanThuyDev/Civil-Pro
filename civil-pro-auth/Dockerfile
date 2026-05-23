# ================================================================
# DOCKERFILE - CIVIL-PRO MICROSERVICE
# Multi-stage build: giảm image size tối đa
# Stage 1: Build với Maven
# Stage 2: Run với JRE minimal
# ================================================================

# ---- STAGE 1: BUILD ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy pom.xml trước để tận dụng Docker layer cache
# (chỉ re-download dependencies khi pom.xml thay đổi)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached nếu pom.xml không đổi)
RUN ./mvnw dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ---- STAGE 2: RUNTIME ----
FROM eclipse-temurin:21-jre-alpine AS runtime

# Tạo non-root user (security best practice)
RUN addgroup -S civil && adduser -S civil -G civil

WORKDIR /app

# Copy JAR từ build stage
COPY --from=builder /app/target/*.jar app.jar

# Chuyển sang non-root user
USER civil

# JVM tuning cho container:
# - UseContainerSupport: đọc CPU/Memory limit từ cgroup
# - MaxRAMPercentage: dùng tối đa 75% RAM được cấp
# - G1GC: GC phù hợp cho microservice
# - timezone: Việt Nam
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+OptimizeStringConcat \
               -Duser.timezone=Asia/Ho_Chi_Minh \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]