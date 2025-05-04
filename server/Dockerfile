FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests -pl server -am

FROM amazoncorretto:18-alpine
WORKDIR /app
COPY --from=build /app/server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]