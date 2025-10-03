FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# (Não precisa copiar imagens aqui, mas o build é o mesmo)
CMD ["java", "-cp", "app.jar", "br.com.seunome.consumidor1.ConsumidorRostoApp"]