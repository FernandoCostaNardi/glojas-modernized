#!/bin/bash

echo "=== Deploy Legacy API no Docker ==="

# 1. Build do JAR
echo "1. Building JAR..."
mvn clean package -DskipTests

# 2. Build da imagem Docker
echo "2. Building Docker image..."
docker build -t legacy-api:latest .

# 3. Parar container antigo (se existir)
echo "3. Stopping old container..."
docker stop legacy-api 2>/dev/null || true
docker rm legacy-api 2>/dev/null || true

# 4. Iniciar novo container
echo "4. Starting new container..."
docker-compose up -d

# 5. Verificar logs
echo "5. Checking logs..."
sleep 5
docker logs legacy-api --tail 50

echo ""
echo "✅ Deploy concluído!"
echo "🌐 URL: http://localhost:8087/api/legacy"
echo "📊 Health: http://localhost:8087/api/legacy/actuator/health"
echo ""
echo "Para ver logs em tempo real:"
echo "  docker logs -f legacy-api"
