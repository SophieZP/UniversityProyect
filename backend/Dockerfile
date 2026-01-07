# ETAPA 1: Construcción
FROM maven:3.9-eclipse-temurin-25 AS build
# Al estar en el Root Directory correcto, COPY . . copiará el pom.xml
COPY . .
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:25-jdk
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]