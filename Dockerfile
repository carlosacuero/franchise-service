# Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -q -DskipTests package

# Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /build/target/*.jar /app/app.jar
EXPOSE 8081
ENV MONGODB_URI=mongodb://localhost:27017/franquicias_db
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
