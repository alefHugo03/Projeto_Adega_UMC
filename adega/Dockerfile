FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia apenas os arquivos de build para aproveitar cache do Docker.
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw # Garante permissão de execução para o wrapper do Maven.

# Copia o código-fonte e constrói o pacote sem executar testes.
COPY src ./src
RUN ./mvnw -B package -DskipTests

# Imagem final menor apenas com runtime Java.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Porta usada pelo Spring Boot dentro do container.
EXPOSE 8080

# Inicia a aplicação Java.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
