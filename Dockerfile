# Utiliser une image avec Maven pour compiler le projet
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# Utiliser une image plus légère avec juste le JRE (Java Runtime Environment) pour exécuter l'application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copier le fichier JAR compilé depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Render attend que l'application écoute sur le port 10000 par défaut (ou via la variable PORT)
EXPOSE 10000
ENV PORT=10000

# Commande pour démarrer l'application Spring Boot
CMD ["java", "-jar", "app.jar", "--server.port=${PORT}"]
