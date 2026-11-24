FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B verify && cp target/thindesk-*.jar /app/thindesk.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/thindesk.jar ./thindesk.jar
ENTRYPOINT ["java", "-jar", "/app/thindesk.jar"]
