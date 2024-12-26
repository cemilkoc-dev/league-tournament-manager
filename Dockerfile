FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

ARG VERSION=1.0-SNAPSHOT
ENV JAR_FILE=league-tournament-manager-${VERSION}.jar

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src/

RUN ./mvnw clean package -DskipTests

CMD ["sh", "-c", "java -jar -Dspring.profiles.active=prod /app/target/${JAR_FILE}"]