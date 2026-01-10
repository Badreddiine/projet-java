# Étape 1: Build de l'application
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY src ./src

# Construire l'application (skip les tests pour un build plus rapide)
# Ajouter l'option pour gérer l'encodage
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8

# Étape 2: Image d'exécution
FROM eclipse-temurin:17-jre-alpine

# Créer un utilisateur non-root pour plus de sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Changer la propriété du fichier
RUN chown spring:spring app.jar

# Utiliser l'utilisateur non-root
USER spring:spring

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement par défaut (peuvent être surchargées)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage avec les options JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1