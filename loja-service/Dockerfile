FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY loja-service/pom.xml ./loja-service/
COPY loja-service/src ./loja-service/src

RUN chmod +x mvnw && ./mvnw clean package -pl loja-service -am -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/loja-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
