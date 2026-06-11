# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy POMs first for better layer caching
COPY pom.xml .
COPY relic-common/pom.xml relic-common/
COPY relic-pojo/pom.xml relic-pojo/
COPY relic-server/pom.xml relic-server/

RUN mvn dependency:go-offline -B -pl relic-server -am -DskipTests || true

COPY relic-common relic-common
COPY relic-pojo relic-pojo
COPY relic-server relic-server

RUN mvn clean package -B -pl relic-server -am -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

COPY --from=build /app/relic-server/target/relic-server-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
