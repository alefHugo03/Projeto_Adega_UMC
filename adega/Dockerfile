# Estágio 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Otimização: Baixa as dependências primeiro para aproveitar o cache do Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e os recursos (incluindo o keystore.p12)
COPY src ./src
RUN mvn clean package -DskipTests -Pprod

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]