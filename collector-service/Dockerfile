FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY loja-service/pom.xml ./loja-service/
COPY loja-service/src ./loja-service/src
COPY collector-service/pom.xml ./collector-service/
COPY collector-service/src ./collector-service/src

RUN chmod +x mvnw && ./mvnw clean package -pl collector-service -am -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/collector-service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
