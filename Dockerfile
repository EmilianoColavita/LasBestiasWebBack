# ---- Etapa 1: Build del proyecto ----
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# Copiamos solo los archivos necesarios para cachear dependencias
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
RUN gradle dependencies --no-daemon || return 0

# Copiamos el código fuente
COPY . /app

# Compilamos el JAR
RUN gradle clean build -x test --no-daemon

# ---- Etapa 2: Imagen final ----
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/build/libs/*.jar app.jar

# Configuramos variables de entorno por defecto (Render las puede sobrescribir)
ENV PORT=8080
EXPOSE 8080

# Ejecutamos la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
