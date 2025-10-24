# Script para diagnosticar erro 500 na Business API
# Execute na VPS

Write-Host "=== DIAGNOSTICANDO ERRO 500 NA BUSINESS API ===" -ForegroundColor Red

Write-Host "1. Verificando status das APIs..." -ForegroundColor Yellow
Write-Host "Business API (8089):"
netstat -tlnp | grep -E '8089'
Write-Host "Legacy API (8087):"
netstat -tlnp | grep -E '8087'

Write-Host ""
Write-Host "2. Testando Legacy API diretamente..." -ForegroundColor Yellow
Write-Host "Testando: http://localhost:8087/stores"
$legacyTest = curl -v http://localhost:8087/stores 2>&1
Write-Host "Resposta Legacy API:"
Write-Host $legacyTest

Write-Host ""
Write-Host "3. Testando health check da Business API..." -ForegroundColor Yellow
Write-Host "Testando: http://localhost:8089/api/business/legacy/stores/health"
$healthTest = curl -v http://localhost:8089/api/business/legacy/stores/health 2>&1
Write-Host "Resposta Health Check:"
Write-Host $healthTest

Write-Host ""
Write-Host "4. Verificando logs da Business API..." -ForegroundColor Yellow
Write-Host "Últimas 20 linhas dos logs:"
pm2 logs business-api --lines 20

Write-Host ""
Write-Host "5. Verificando logs de erro da Business API..." -ForegroundColor Yellow
if (Test-Path "/opt/glojas-modernized/logs/business-api-error.log") {
    Write-Host "Últimas 10 linhas do log de erro:"
    Get-Content "/opt/glojas-modernized/logs/business-api-error.log" -Tail 10
} else {
    Write-Host "Log de erro não encontrado"
}

Write-Host ""
Write-Host "6. Testando com timeout maior..." -ForegroundColor Yellow
Write-Host "Testando com timeout de 30 segundos:"
$timeoutTest = curl -v --max-time 30 http://localhost:8089/api/business/legacy/stores 2>&1
Write-Host "Resposta com timeout:"
Write-Host $timeoutTest

Write-Host ""
Write-Host "=== DIAGNÓSTICO CONCLUÍDO ===" -ForegroundColor Green
Write-Host "Analise os resultados acima para identificar a causa do erro 500" -ForegroundColor Yellow
