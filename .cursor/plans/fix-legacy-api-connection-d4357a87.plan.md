<!-- d4357a87-9f52-420e-b6b2-286b05428730 880a0fa1-200b-40fe-9070-c085c7cc7760 -->
# Tutorial: Atualização da Legacy API em Produção

## Visão Geral

Este tutorial ensina como atualizar a Legacy API em produção após fazer alterações no código.

## Arquivos a Criar

1. **`legacy-api/deploy-production.ps1`** - Script PowerShell para Windows (automatiza tudo)
2. **`legacy-api/deploy-vps.sh`** - Script Bash para VPS (deploy automatizado)
3. **`legacy-api/DEPLOY-GUIDE.md`** - Guia passo a passo manual
4. **`legacy-api/rollback-vps.sh`** - Script de rollback em caso de erro

## Conteúdo dos Arquivos

### 1. Script PowerShell (Windows) - Automatizado

**Arquivo:** `legacy-api/deploy-production.ps1`

```powershell
# Script de Deploy Automatizado - Legacy API
# Uso: .\deploy-production.ps1

$ErrorActionPreference = "Stop"

Write-Host "=== Deploy Legacy API em Produção ===" -ForegroundColor Cyan
Write-Host ""

# Configurações
$VPS_HOST = "212.85.12.228"
$VPS_USER = "root"
$VPS_PATH = "/opt/glojas-modernized/legacy-api"
$LOCAL_PATH = "G:\olisystem\glojas-modernized\legacy-api"

# 1. Build do JAR
Write-Host "1. Building JAR..." -ForegroundColor Yellow
Set-Location $LOCAL_PATH
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro no build do JAR" -ForegroundColor Red
    exit 1
}
Write-Host "✅ JAR buildado com sucesso" -ForegroundColor Green
Write-Host ""

# 2. Verificar se JAR foi criado
if (-not (Test-Path "target\legacy-api-1.0.0.jar")) {
    Write-Host "❌ JAR não encontrado em target\" -ForegroundColor Red
    exit 1
}
$jarSize = (Get-Item "target\legacy-api-1.0.0.jar").Length / 1MB
Write-Host "📦 JAR: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Cyan
Write-Host ""

# 3. Upload do JAR para VPS
Write-Host "2. Uploading JAR para VPS..." -ForegroundColor Yellow
scp target\legacy-api-1.0.0.jar ${VPS_USER}@${VPS_HOST}:${VPS_PATH}/target/
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro no upload do JAR" -ForegroundColor Red
    exit 1
}
Write-Host "✅ JAR enviado com sucesso" -ForegroundColor Green
Write-Host ""

# 4. Deploy na VPS
Write-Host "3. Executando deploy na VPS..." -ForegroundColor Yellow
ssh ${VPS_USER}@${VPS_HOST} "cd ${VPS_PATH} && bash deploy-vps.sh"
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro no deploy" -ForegroundColor Red
    Write-Host "Execute rollback: ssh ${VPS_USER}@${VPS_HOST} 'cd ${VPS_PATH} && bash rollback-vps.sh'" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 5. Validação
Write-Host "4. Validando deploy..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

$healthCheck = ssh ${VPS_USER}@${VPS_HOST} "curl -s http://localhost:8087/api/legacy/actuator/health"
if ($healthCheck -like "*UP*") {
    Write-Host "✅ Health check OK: $healthCheck" -ForegroundColor Green
} else {
    Write-Host "⚠️  Health check falhou: $healthCheck" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Deploy Concluído ===" -ForegroundColor Cyan
Write-Host "🌐 URL: http://glojas.com.br/api/legacy" -ForegroundColor Cyan
Write-Host "📊 Health: http://glojas.com.br/api/legacy/actuator/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "Comandos úteis:" -ForegroundColor White
Write-Host "  ssh ${VPS_USER}@${VPS_HOST} 'docker logs -f legacy-api'  # Ver logs" -ForegroundColor Gray
Write-Host "  ssh ${VPS_USER}@${VPS_HOST} 'docker restart legacy-api'  # Reiniciar" -ForegroundColor Gray
```

### 2. Script Bash (VPS) - Deploy Automatizado

**Arquivo:** `legacy-api/deploy-vps.sh`

```bash
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
```

### 3. Script de Rollback (VPS)

**Arquivo:** `legacy-api/rollback-vps.sh`

```bash
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
```

### 4. Guia Manual (Markdown)

**Arquivo:** `legacy-api/DEPLOY-GUIDE.md`

````markdown
# Guia de Deploy - Legacy API em Produção

## 📋 Pré-requisitos

- Maven instalado no Windows
- Acesso SSH à VPS como root
- Docker rodando na VPS

## 🚀 Deploy Automatizado (Recomendado)

### Windows

```powershell
cd G:\olisystem\glojas-modernized\legacy-api
.\deploy-production.ps1
````

O script automatiza:

1. Build do JAR
2. Upload para VPS
3. Build da imagem Docker
4. Deploy do container
5. Validação automática

## 📝 Deploy Manual (Passo a Passo)

### Passo 1: Build Local (Windows)

```powershell
cd G:\olisystem\glojas-modernized\legacy-api
mvn clean package -DskipTests
```

Verifique se o JAR foi criado:

```powershell
ls target\legacy-api-1.0.0.jar
```

### Passo 2: Upload para VPS

```powershell
scp target\legacy-api-1.0.0.jar root@212.85.12.228:/opt/glojas-modernized/legacy-api/target/
```

### Passo 3: Deploy na VPS

Conecte na VPS:

```bash
ssh root@212.85.12.228
```

Execute o deploy:

```bash
cd /opt/glojas-modernized/legacy-api
bash deploy-vps.sh
```

### Passo 4: Validação

```bash
# Ver logs
docker logs -f legacy-api

# Testar health
curl http://localhost:8087/api/legacy/actuator/health

# Testar endpoint
curl http://localhost:8087/api/legacy/stores
```

## 🔄 Rollback

Se algo der errado:

```bash
ssh root@212.85.12.228
cd /opt/glojas-modernized/legacy-api
bash rollback-vps.sh
```

## 🛠️ Comandos Úteis

### Monitoramento

```bash
# Ver logs em tempo real
docker logs -f legacy-api

# Ver últimas 100 linhas
docker logs legacy-api --tail 100

# Ver uso de recursos
docker stats legacy-api --no-stream

# Ver status do container
docker ps | grep legacy-api
```

### Manutenção

```bash
# Reiniciar container
docker restart legacy-api

# Parar container
docker stop legacy-api

# Ver variáveis de ambiente
docker exec legacy-api env | grep SPRING

# Entrar no container
docker exec -it legacy-api sh
```

### Debug

```bash
# Verificar conectividade SQL Server
docker exec legacy-api timeout 5 sh -c 'cat < /dev/null > /dev/tcp/45.174.189.210/1433' && echo "OK" || echo "FALHOU"

# Ver configuração de rede
docker inspect legacy-api | grep -A 20 NetworkSettings

# Ver healthcheck
docker inspect legacy-api | grep -A 10 Health
```

## ⚠️ Troubleshooting

### Container não inicia

```bash
# Ver logs de erro
docker logs legacy-api

# Ver eventos do Docker
docker events --since 5m

# Verificar porta em uso
netstat -tulpn | grep 8087
```

### Erro de conexão SQL Server

```bash
# Testar de dentro do container
docker exec legacy-api ping -c 3 45.174.189.210

# Ver logs de SQL Server
docker logs legacy-api | grep -i "sqlserver\|prelogin\|timeout"
```

### Rollback não funciona

```bash
# Parar container
docker stop legacy-api
docker rm legacy-api

# Listar imagens disponíveis
docker images | grep legacy-api

# Iniciar manualmente com imagem de backup
docker run -d --name legacy-api --restart unless-stopped -p 8087:8087 -e SPRING_PROFILES_ACTIVE=prod legacy-api:backup
```

## 📊 Checklist de Deploy

- [ ] Build do JAR sem erros
- [ ] Upload do JAR para VPS
- [ ] Build da imagem Docker
- [ ] Container iniciado
- [ ] Health check retorna UP
- [ ] Endpoints respondem corretamente
- [ ] Logs sem erros de SQL Server
- [ ] Backup da versão anterior criado

## 🎯 URLs de Produção

- **API Base**: http://glojas.com.br/api/legacy
- **Health Check**: http://glojas.com.br/api/legacy/actuator/health
- **Stores**: http://glojas.com.br/api/legacy/stores
- **Products**: http://glojas.com.br/api/legacy/products/registered

## 📞 Suporte

Em caso de problemas:

1. Verificar logs: `docker logs legacy-api`
2. Executar rollback: `bash rollback-vps.sh`
3. Verificar conectividade SQL Server
4. Contatar equipe de desenvolvimento

```

## Resumo dos Arquivos

1. **deploy-production.ps1** - Script Windows automatizado
2. **deploy-vps.sh** - Script VPS para deploy
3. **rollback-vps.sh** - Script VPS para rollback
4. **DEPLOY-GUIDE.md** - Guia completo manual

### To-dos

- [x] Criar script de deploy automatizado