# =====================================================
# SCRIPT PARA POPULAR DADOS DE TESTE - BUSINESS API
# =====================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "SCRIPT PARA POPULAR DADOS DE TESTE - BUSINESS API" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

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

# Configurações do banco
$DB_NAME = "glojas_business"
$DB_USER = "glojas_user"
$DB_PASSWORD = "F1e0r8n0#1"
$SCRIPT_FILE = "populate-test-data.sql"

Write-Host "Configurações do banco:" -ForegroundColor Yellow
Write-Host "- Database: $DB_NAME" -ForegroundColor White
Write-Host "- Usuário: $DB_USER" -ForegroundColor White
Write-Host "- Script: $SCRIPT_FILE" -ForegroundColor White
Write-Host ""

# Verificar se o arquivo de script existe
if (-not (Test-Path $SCRIPT_FILE)) {
    Write-Host "ERRO: Arquivo $SCRIPT_FILE não encontrado!" -ForegroundColor Red
    Write-Host "Certifique-se de que o arquivo está no diretório atual" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Executando script de dados de teste..." -ForegroundColor Yellow
Write-Host ""

# Executar o script SQL
try {
    $env:PGPASSWORD = $DB_PASSWORD
    $PSQL_PATH = "C:\Program Files\PostgreSQL\15\bin\psql.exe"
    $result = & $PSQL_PATH -U $DB_USER -d $DB_NAME -f $SCRIPT_FILE 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host "SUCESSO! Dados de teste inseridos com sucesso!" -ForegroundColor Green
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Usuários criados:" -ForegroundColor Yellow
        Write-Host "- admin/admin123 (ADMIN)" -ForegroundColor White
        Write-Host "- joao.silva/admin123 (TESTER)" -ForegroundColor White
        Write-Host "- maria.santos/admin123 (DEVELOPER)" -ForegroundColor White
        Write-Host "- pedro.oliveira/admin123 (ANALYST)" -ForegroundColor White
        Write-Host "- ana.costa/admin123 (MANAGER)" -ForegroundColor White
        Write-Host "- carlos.ferreira/admin123 (USER)" -ForegroundColor White
        Write-Host "- lucia.rodriguez/admin123 (DEVELOPER + TESTER)" -ForegroundColor White
        Write-Host ""
        Write-Host "Roles criadas: ADMIN, USER, MANAGER, TESTER, DEVELOPER, ANALYST" -ForegroundColor Yellow
        Write-Host "Permissões criadas: Todas as permissões básicas + permissões de teste" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Tokens de reset de senha criados:" -ForegroundColor Yellow
        Write-Host "- test-token-joao-silva-12345 (expira em 1 hora)" -ForegroundColor White
        Write-Host "- test-token-maria-santos-67890 (expira em 30 minutos)" -ForegroundColor White
        Write-Host ""
    } else {
        throw "Falha na execução do script"
    }
} catch {
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host "ERRO! Falha ao executar o script" -ForegroundColor Red
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possíveis causas:" -ForegroundColor Yellow
    Write-Host "- Banco de dados não existe" -ForegroundColor White
    Write-Host "- Usuário ou senha incorretos" -ForegroundColor White
    Write-Host "- Tabelas não foram criadas pelo Hibernate" -ForegroundColor White
    Write-Host "- PostgreSQL não está rodando" -ForegroundColor White
    Write-Host ""
    Write-Host "Detalhes do erro:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
}

# Limpar variável de ambiente
Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue

Read-Host "Pressione Enter para sair"
