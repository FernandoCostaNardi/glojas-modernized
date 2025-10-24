#!/bin/bash
set -e

echo "=== Rollback Legacy API ==="
echo ""

CONTAINER_NAME="legacy-api"
IMAGE_NAME="legacy-api:latest"
BACKUP_IMAGE="legacy-api:backup"

# 1. Verificar se existe backup
if ! docker images | grep -q "$BACKUP_IMAGE"; then
    echo "❌ Nenhum backup encontrado"
    exit 1
fi

echo "1. Backup encontrado, iniciando rollback..."
echo ""

# 2. Parar container atual
echo "2. Parando container atual..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true
echo "✅ Container removido"
echo ""

# 3. Restaurar imagem de backup
echo "3. Restaurando imagem de backup..."
docker tag $BACKUP_IMAGE $IMAGE_NAME
echo "✅ Imagem restaurada"
echo ""

# 4. Iniciar container com versão antiga
echo "4. Iniciando container com versão anterior..."
docker run -d \
  --name $CONTAINER_NAME \
  --restart unless-stopped \
  -p 8087:8087 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
  $IMAGE_NAME

if [ $? -ne 0 ]; then
    echo "❌ Erro ao iniciar container com backup"
    exit 1
fi
echo "✅ Container iniciado"
echo ""

# 5. Aguardar e verificar
echo "5. Aguardando inicialização..."
sleep 10

HEALTH=$(curl -s http://localhost:8087/api/legacy/actuator/health)
if echo "$HEALTH" | grep -q "UP"; then
    echo "✅ Rollback concluído com sucesso"
    echo "📊 Health: $HEALTH"
else
    echo "❌ Rollback falhou"
    exit 1
fi
