# =====================================================
# SCRIPT PARA ATUALIZAR DADOS DE LOJAS COM CÓDIGOS REAIS
# =====================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "ATUALIZANDO DADOS DE LOJAS COM CÓDIGOS REAIS" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Configurações do banco
$DB_NAME = "glojas_business"
$DB_USER = "glojas_user"
$DB_PASSWORD = "F1e0r8n0#1"
$SCRIPT_FILE = "scripts/populate-test-data.sql"

Write-Host "Configurações do banco:" -ForegroundColor Yellow
Write-Host "- Database: $DB_NAME" -ForegroundColor White
Write-Host "- Usuário: $DB_USER" -ForegroundColor White
Write-Host "- Script: $SCRIPT_FILE" -ForegroundColor White
Write-Host ""

# Verificar se o arquivo de script existe
if (-not (Test-Path $SCRIPT_FILE)) {
    Write-Host "ERRO: Arquivo $SCRIPT_FILE não encontrado!" -ForegroundColor Red
    Write-Host "Certifique-se de que o arquivo está no diretório correto" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Executando script de dados atualizado..." -ForegroundColor Yellow
Write-Host ""

# Executar o script SQL
try {
    $env:PGPASSWORD = $DB_PASSWORD
    $result = & "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U $DB_USER -d $DB_NAME -f $SCRIPT_FILE
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host "SUCESSO! Dados de lojas atualizados com sucesso!" -ForegroundColor Green
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Lojas criadas com códigos reais da Legacy API:" -ForegroundColor Yellow
        Write-Host "- 000001: JAB MATRIZ" -ForegroundColor White
        Write-Host "- 000002: CD JANGURUSSU" -ForegroundColor White
        Write-Host "- 000003: SMART ANT. SALES" -ForegroundColor White
        Write-Host "- 000004: SMART MARACANAU" -ForegroundColor White
        Write-Host "- 000005: SMART IGUATEMI" -ForegroundColor White
        Write-Host "- 000006: SMART MESSEJANA" -ForegroundColor White
        Write-Host "- 000007: SMART IANDE" -ForegroundColor White
        Write-Host "- 000008: SMART VIA SUL" -ForegroundColor White
        Write-Host "- 000009: JAB CD 2" -ForegroundColor White
        Write-Host "- 000010: SMART NORTH" -ForegroundColor White
        Write-Host "- 000011: SMART PARANGABA" -ForegroundColor White
        Write-Host "- 000012: SMART RIOMAR KENNEDY" -ForegroundColor White
        Write-Host "- 000013: SMART RIOMAR FORTALE" -ForegroundColor White
        Write-Host "- 000014: SMART JOQUEI" -ForegroundColor White
        Write-Host ""
        Write-Host "Agora o endpoint current-daily-sales deve retornar dados reais!" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "=====================================================" -ForegroundColor Red
        Write-Host "ERRO ao executar script!" -ForegroundColor Red
        Write-Host "=====================================================" -ForegroundColor Red
        Write-Host ""
        Write-Host "Verifique se:" -ForegroundColor Yellow
        Write-Host "1. PostgreSQL está rodando" -ForegroundColor White
        Write-Host "2. Banco de dados 'glojas_business' existe" -ForegroundColor White
        Write-Host "3. Usuário 'glojas_user' tem permissões" -ForegroundColor White
        Write-Host ""
        Write-Host "Saída do comando:" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    }
} catch {
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host "ERRO ao executar script!" -ForegroundColor Red
    Write-Host "=====================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "SCRIPT CONCLUÍDO" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
