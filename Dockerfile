FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src/

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "target/league-tournament-manager.jar"]