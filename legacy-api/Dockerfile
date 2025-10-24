FROM openjdk:8-jre-alpine

# Metadados
LABEL maintainer="glojas-modernized"
LABEL description="Legacy API - Read-only SQL Server access"

# Criar diretório da aplicação
WORKDIR /app

# Copiar JAR
COPY target/legacy-api-1.0.0.jar app.jar

# Expor porta
EXPOSE 8087

# Variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8087/api/legacy/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
