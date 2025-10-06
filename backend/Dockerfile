# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# leverage layer caching for deps
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
# run tests during image build; remove -DskipTests=false to skip
RUN mvn -B -DskipTests=false clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# copy the boot jar (SNAPSHOT name may vary)
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
# Optional JVM tunables via JAVA_OPTS
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
