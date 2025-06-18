FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon --refresh-dependencies --stacktrace --info

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]