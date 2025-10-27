# Script de Deploy para Correcao do Context Path Duplicado
# Business API - Correcao Legacy URL

Write-Host "=== DEPLOY BUSINESS API - CORRECAO WEBCLIENT URL ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Yellow
Write-Host ""

# Verificar se o JAR foi compilado
$jarPath = "target\business-api-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "ERRO: JAR nao encontrado em $jarPath" -ForegroundColor Red
    Write-Host "Execute primeiro: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

Write-Host "JAR encontrado: $jarPath" -ForegroundColor Green
$jarSize = (Get-Item $jarPath).Length / 1MB
Write-Host "   Tamanho: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Cyan

# Verificar configuracoes no JAR
Write-Host ""
Write-Host "Verificando configuracoes no JAR..." -ForegroundColor Yellow

# Extrair e verificar application-prod.yml
jar -xf $jarPath BOOT-INF/classes/application-prod.yml
if (Test-Path "BOOT-INF/classes/application-prod.yml") {
    $configContent = Get-Content "BOOT-INF/classes/application-prod.yml" -Raw
    
    if ($configContent -match "base-url: http://localhost:8087" -and $configContent -match "context-path: /api/legacy") {
        Write-Host "Configuracoes corretas encontradas no JAR:" -ForegroundColor Green
        Write-Host "   - base-url: http://localhost:8087" -ForegroundColor Cyan
        Write-Host "   - context-path: /api/legacy" -ForegroundColor Cyan
    } else {
        Write-Host "ERRO: Configuracoes incorretas no JAR!" -ForegroundColor Red
        Write-Host "Recompile o projeto com as configuracoes corretas." -ForegroundColor Yellow
        Remove-Item -Recurse -Force BOOT-INF -ErrorAction SilentlyContinue
        exit 1
    }
    
    # Limpar arquivos extraidos
    Remove-Item -Recurse -Force BOOT-INF -ErrorAction SilentlyContinue
} else {
    Write-Host "ERRO: Nao foi possivel verificar configuracoes no JAR" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "PROXIMOS PASSOS PARA PRODUCAO:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Copie o JAR para o servidor de producao:" -ForegroundColor White
Write-Host "   scp $jarPath user@servidor:/opt/glojas-modernized/business-api/target/" -ForegroundColor Cyan
Write-Host ""
Write-Host "2. No servidor, faca backup do JAR antigo:" -ForegroundColor White
Write-Host "   mv /opt/glojas-modernized/business-api/target/business-api-1.0.0.jar /opt/glojas-modernized/business-api/target/business-api-1.0.0.jar.bak" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Reinicie a Business API via PM2:" -ForegroundColor White
Write-Host "   pm2 restart business-api" -ForegroundColor Cyan
Write-Host ""
Write-Host "4. Verifique os logs:" -ForegroundColor White
Write-Host "   pm2 logs business-api --lines 50" -ForegroundColor Cyan
Write-Host ""
Write-Host "5. Teste o endpoint:" -ForegroundColor White
Write-Host "   curl -X POST http://localhost:8089/api/business/sync/daily-sales" -ForegroundColor Cyan
Write-Host "     -H \"Content-Type: application/json\"" -ForegroundColor Cyan
Write-Host "     -H \"Authorization: Bearer TOKEN\"" -ForegroundColor Cyan
Write-Host "     -d '{\"startDate\": \"2025-10-01\", \"endDate\": \"2025-10-27\"}'" -ForegroundColor Cyan
Write-Host ""
Write-Host "RESULTADO ESPERADO:" -ForegroundColor Green
Write-Host "   - URL correta: http://localhost:8087/api/legacy/sales/store-report-by-day" -ForegroundColor Cyan
Write-Host "   - Sem duplicacao do context path" -ForegroundColor Cyan
Write-Host "   - Resposta HTTP 200" -ForegroundColor Cyan
Write-Host ""
Write-Host "=== DEPLOY PRONTO PARA EXECUCAO ===" -ForegroundColor Green