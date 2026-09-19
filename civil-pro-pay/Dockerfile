FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
    echo "Asia/Ho_Chi_Minh" > /etc/timezone

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8089 9089

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
