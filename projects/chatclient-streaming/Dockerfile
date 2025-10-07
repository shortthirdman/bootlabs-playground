# ------------------
# 1. Build Stage
# ------------------
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory
WORKDIR /app

# Copy the pre-built layered JAR (assuming 'mvn package' has been run locally)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

# Extract the JAR layers
RUN java -Djarmode=layertools -jar application.jar extract

# ------------------
# 2. Runtime Stage
# ------------------
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy layers from the builder stage in order of change frequency (less frequent first)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Expose the application port
EXPOSE 8080

# Specify the entry point using the Spring Boot JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]