FROM eclipse-temurin:21-jdk-jammy as build

WORKDIR /app
COPY pom.xml .
COPY src src

COPY .mvn .mvn
COPY mvnw .

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
VOLUME /tmp

COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
