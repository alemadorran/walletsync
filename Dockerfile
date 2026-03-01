# Etapa 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder

# Seteamos un directorio de trabajo consistente
WORKDIR /build

# 1. Copiamos archivos de configuración de Maven para cachear dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
# Descarga dependencias (solo se repetirá si cambia el pom.xml)
RUN ./mvnw dependency:go-offline -B

# 2. Copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw clean package -DskipTests

# =============================
# Etapa 2: Runtime (Imagen final)
# =============================
FROM eclipse-temurin:21-jre-jammy

# Crear un usuario no-root por seguridad
# No es obligatorio, solo para aumentar seguridad
RUN addgroup --system javauser && adduser --system --group javauser
USER javauser

WORKDIR /app

# Copiamos el JAR usando un wildcard para evitar problemas con la versión
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# Usamos ENTRYPOINT para que el contenedor se comporte como un ejecutable
ENTRYPOINT ["java", "-jar", "app.jar"]

#--------------------------------
# Ejecución de comandos
# docker build -t walletsync-app .
# docker run -d -p 8080:8080 --name walletsync-container walletsync-app