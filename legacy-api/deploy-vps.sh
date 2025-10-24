#!/bin/bash
set -e

echo "=== Deploy Legacy API na VPS ==="
echo ""

# Configurações
CONTAINER_NAME="legacy-api"
IMAGE_NAME="legacy-api:latest"
BACKUP_IMAGE="legacy-api:backup"

# 1. Backup da imagem atual
echo "1. Fazendo backup da imagem atual..."
docker tag $IMAGE_NAME $BACKUP_IMAGE 2>/dev/null || echo "Sem imagem anterior para backup"
echo "✅ Backup concluído"
echo ""

# 2. Build da nova imagem
echo "2. Building nova imagem Docker..."
docker build -t $IMAGE_NAME .
if [ $? -ne 0 ]; then
    echo "❌ Erro no build da imagem"
    exit 1
fi
echo "✅ Imagem buildada com sucesso"
echo ""

# 3. Parar container atual
echo "3. Parando container atual..."
docker stop $CONTAINER_NAME 2>/dev/null || echo "Container não estava rodando"
docker rm $CONTAINER_NAME 2>/dev/null || echo "Container não existia"
echo "✅ Container antigo removido"
echo ""

# 4. Iniciar novo container
echo "4. Iniciando novo container..."
docker run -d \
  --name $CONTAINER_NAME \
  --restart unless-stopped \
  -p 8087:8087 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
  $IMAGE_NAME

if [ $? -ne 0 ]; then
    echo "❌ Erro ao iniciar container"
    echo "Executando rollback..."
    bash rollback-vps.sh
    exit 1
fi
echo "✅ Container iniciado com sucesso"
echo ""

# 5. Aguardar inicialização
echo "5. Aguardando inicialização (15s)..."
sleep 15

# 6. Verificar logs
echo "6. Verificando logs..."
docker logs $CONTAINER_NAME --tail 30 | grep -i "error\|exception" && echo "⚠️  Erros encontrados nos logs" || echo "✅ Sem erros nos logs"
echo ""

# 7. Testar health
echo "7. Testando health check..."
HEALTH=$(curl -s http://localhost:8087/api/legacy/actuator/health)
if echo "$HEALTH" | grep -q "UP"; then
    echo "✅ Health check OK: $HEALTH"
else
    echo "❌ Health check falhou: $HEALTH"
    echo "Executando rollback..."
    bash rollback-vps.sh
    exit 1
fi
echo ""

# 8. Limpar imagens antigas
echo "8. Limpando imagens antigas..."
docker image prune -f
echo "✅ Limpeza concluída"
echo ""

echo "=== Deploy Concluído com Sucesso ==="
echo "🌐 Container: $CONTAINER_NAME"
echo "📦 Imagem: $IMAGE_NAME"
echo "📊 Status: $(docker ps --filter name=$CONTAINER_NAME --format '{{.Status}}')"
echo ""
echo "Comandos úteis:"
echo "  docker logs -f $CONTAINER_NAME     # Ver logs em tempo real"
echo "  docker restart $CONTAINER_NAME     # Reiniciar container"
echo "  docker stats $CONTAINER_NAME       # Ver uso de recursos"
