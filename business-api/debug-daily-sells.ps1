# =====================================================
# SCRIPT DE DEBUG PARA VERIFICAR DADOS EM DAILY_SELLS
# =====================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "DEBUG: VERIFICANDO DADOS EM DAILY_SELLS" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Configurações do banco
$DB_NAME = "glojas_business"
$DB_USER = "glojas_user"
$DB_PASSWORD = "F1e0r8n0#1"
$PSQL_PATH = "C:\Program Files\PostgreSQL\15\bin\psql.exe"

# Verificar se o PostgreSQL está rodando
Write-Host "Verificando se o PostgreSQL está rodando..." -ForegroundColor Yellow
$postgresRunning = Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue

if (-not $postgresRunning) {
    Write-Host "ERRO: PostgreSQL não está rodando na porta 5432" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "✓ PostgreSQL está rodando!" -ForegroundColor Green
Write-Host ""

# Configurar senha
$env:PGPASSWORD = $DB_PASSWORD

Write-Host "Executando consultas de debug..." -ForegroundColor Yellow
Write-Host ""

# 1. Verificar se a tabela daily_sells existe
Write-Host "1. Verificando se a tabela daily_sells existe:" -ForegroundColor Cyan
$tableExists = & $PSQL_PATH -U $DB_USER -d $DB_NAME -t -c "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'daily_sells');"
Write-Host "Tabela daily_sells existe: $($tableExists.Trim())" -ForegroundColor White
Write-Host ""

# 2. Contar registros na tabela daily_sells
Write-Host "2. Contando registros na tabela daily_sells:" -ForegroundColor Cyan
$totalRecords = & $PSQL_PATH -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM daily_sells;"
Write-Host "Total de registros: $($totalRecords.Trim())" -ForegroundColor White
Write-Host ""

# 3. Verificar registros para janeiro de 2025
Write-Host "3. Verificando registros para janeiro de 2025:" -ForegroundColor Cyan
$janRecords = & $PSQL_PATH -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM daily_sells WHERE date BETWEEN '2025-01-01' AND '2025-01-31';"
Write-Host "Registros em janeiro/2025: $($janRecords.Trim())" -ForegroundColor White
Write-Host ""

# 4. Mostrar alguns registros de exemplo
Write-Host "4. Mostrando alguns registros de exemplo:" -ForegroundColor Cyan
& $PSQL_PATH -U $DB_USER -d $DB_NAME -c "SELECT store_code, store_name, date, total FROM daily_sells ORDER BY date DESC LIMIT 5;"
Write-Host ""

# 5. Testar a query de agregação
Write-Host "5. Testando query de agregação por mês:" -ForegroundColor Cyan
& $PSQL_PATH -U $DB_USER -d $DB_NAME -c "SELECT store_id, store_code, store_name, TO_CHAR(date, 'YYYY-MM') as yearMonth, SUM(total) as totalSum FROM daily_sells WHERE date BETWEEN '2025-01-01' AND '2025-01-31' GROUP BY store_id, store_code, store_name, TO_CHAR(date, 'YYYY-MM') ORDER BY store_code LIMIT 5;"
Write-Host ""

# 6. Verificar se a tabela monthly_sells existe
Write-Host "6. Verificando se a tabela monthly_sells existe:" -ForegroundColor Cyan
$monthlyTableExists = & $PSQL_PATH -U $DB_USER -d $DB_NAME -t -c "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'monthly_sells');"
Write-Host "Tabela monthly_sells existe: $($monthlyTableExists.Trim())" -ForegroundColor White
Write-Host ""

# 7. Contar registros na tabela monthly_sells
Write-Host "7. Contando registros na tabela monthly_sells:" -ForegroundColor Cyan
$monthlyRecords = & $PSQL_PATH -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM monthly_sells;"
Write-Host "Total de registros em monthly_sells: $($monthlyRecords.Trim())" -ForegroundColor White
Write-Host ""

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "DEBUG CONCLUÍDO" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

Read-Host "Pressione Enter para sair"
