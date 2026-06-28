FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY request-audit/pom.xml ./request-audit/pom.xml
COPY request-audit/src ./request-audit/src
COPY backend/pom.xml ./backend/pom.xml
COPY backend/src ./backend/src
COPY asr/pom.xml ./asr/pom.xml
COPY asr/src ./asr/src

RUN mvn -q -DskipTests -pl backend -am package

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/backend/target/mealio-backend-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
