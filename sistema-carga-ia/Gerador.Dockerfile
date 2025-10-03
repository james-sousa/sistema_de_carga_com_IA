# Estágio de Build
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Estágio de Execução
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# MUDANÇA AQUI: Copia a pasta de imagens para dentro do container
COPY src/main/resources/imagens_gerador /data
CMD ["java", "-cp", "app.jar", "br.com.seunome.gerador.GeradorApp"]