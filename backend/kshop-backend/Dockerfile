# Étape 1 : Build de l'application avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Création de l'image finale légère avec JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copie le jar généré à l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Expose le port (Render utilise dynamiquement le port)
EXPOSE 8080

# Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]