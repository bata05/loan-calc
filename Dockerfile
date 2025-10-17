# Multi-platform: Works on M1/M2/M3 + Windows + Intel
FROM maven:3.8.5-openjdk-17 AS build

RUN mkdir app

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Production: ARM64 + AMD64 NATIVE
FROM maven:3.8.5-openjdk-17

# RUN addgroup -gid 1001 appgroup && \
#     adduser -D -s /bin/sh -u 1001 -G appgroup appuser

RUN mkdir app    

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar"]