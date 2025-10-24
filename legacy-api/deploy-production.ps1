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
ssh ${VPS_USER}@${VPS_HOST} "cd ${VPS_PATH}; bash deploy-vps.sh"
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro no deploy" -ForegroundColor Red
    Write-Host "Execute rollback: ssh ${VPS_USER}@${VPS_HOST} 'cd ${VPS_PATH}; bash rollback-vps.sh'" -ForegroundColor Yellow
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