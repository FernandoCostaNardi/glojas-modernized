# =====================================================
# SCRIPT POWERSHELL PARA CRIAR TABELA MONTHLY_SELLS
# =====================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "CRIAR TABELA MONTHLY_SELLS - BUSINESS API" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Configurações do banco
$DB_NAME = "glojas_business"
$DB_USER = "glojas_user"
$DB_PASSWORD = "F1e0r8n0#1"
$SCRIPT_FILE = "create-monthly-sells-table.sql"
$PSQL_PATH = "C:\Program Files\PostgreSQL\15\bin\psql.exe"

# Verificar se o PostgreSQL está rodando
Write-Host "Verificando se o PostgreSQL está rodando..." -ForegroundColor Yellow
$postgresRunning = Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue

if (-not $postgresRunning) {
    Write-Host "ERRO: PostgreSQL não está rodando na porta 5432" -ForegroundColor Red
    Write-Host "Por favor, inicie o PostgreSQL e tente novamente" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "PostgreSQL está rodando!" -ForegroundColor Green
Write-Host ""

# Exibir configurações
Write-Host "Configurações do banco:" -ForegroundColor Cyan
Write-Host "- Database: $DB_NAME"
Write-Host "- Usuário: $DB_USER"
Write-Host "- Script: $SCRIPT_FILE"
Write-Host ""

# Verificar se o arquivo de script existe
if (-not (Test-Path $SCRIPT_FILE)) {
    Write-Host "ERRO: Arquivo $SCRIPT_FILE não encontrado!" -ForegroundColor Red
    Write-Host "Certifique-se de que o arquivo está no diretório atual" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Verificar se o psql existe
if (-not (Test-Path $PSQL_PATH)) {
    Write-Host "ERRO: psql não encontrado em $PSQL_PATH" -ForegroundColor Red
    Write-Host "Verifique se o PostgreSQL está instalado corretamente" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Executar o script SQL
Write-Host "Executando script de criação da tabela monthly_sells..." -ForegroundColor Yellow
Write-Host ""

$env:PGPASSWORD = $DB_PASSWORD
& $PSQL_PATH -U $DB_USER -d $DB_NAME -f $SCRIPT_FILE

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Green
    Write-Host "SUCESSO! Tabela monthly_sells criada com sucesso!" -ForegroundColor Green
    Write-Host "=====================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "A tabela está pronta para uso pela aplicação." -ForegroundColor Green
    Write-Host ""
    Write-Host "Estrutura da tabela:" -ForegroundColor Cyan
    Write-Host "- id (UUID) - Chave primária"
    Write-Host "- store_code (VARCHAR) - Código da loja"
    Write-Host "- store_id (UUID) - ID da loja"
    Write-Host "- store_name (VARCHAR) - Nome da loja"
    Write-Host "- total (NUMERIC) - Valor total das vendas mensais"
    Write-Host "- created_at (TIMESTAMP) - Data de criação"
    Write-Host "- updated_at (TIMESTAMP) - Data de atualização"
    Write-Host ""
    Write-Host "Índices criados:" -ForegroundColor Cyan
    Write-Host "- idx_monthly_sells_store_code"
    Write-Host "- idx_monthly_sells_store_id"
    Write-Host "- idx_monthly_sells_created_at"
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host "ERRO! Falha ao criar a tabela monthly_sells" -ForegroundColor Red
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifique os logs acima para mais detalhes." -ForegroundColor Red
    Write-Host ""
}

Read-Host "Pressione Enter para sair"

