# =====================================================
# SCRIPT PARA CORRIGIR USUÁRIO ADMIN - BUSINESS API
# =====================================================
# Este script corrige o usuário admin para usar email correto
# =====================================================

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "CORRIGINDO USUARIO ADMIN - BUSINESS API" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se o PostgreSQL está rodando
Write-Host "Verificando conexão com PostgreSQL..." -ForegroundColor Yellow
try {
    $result = psql -U glojas_user -d glojas_business -c "SELECT version();" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "PostgreSQL conectado com sucesso!" -ForegroundColor Green
    } else {
        throw "Falha na conexão"
    }
} catch {
    Write-Host "ERRO: Não foi possível conectar ao PostgreSQL!" -ForegroundColor Red
    Write-Host "Verifique se:" -ForegroundColor Yellow
    Write-Host "1. O PostgreSQL está rodando na porta 5432" -ForegroundColor White
    Write-Host "2. O banco 'glojas_business' existe" -ForegroundColor White
    Write-Host "3. O usuário 'glojas_user' tem acesso" -ForegroundColor White
    Write-Host ""
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""

# Executar script SQL
Write-Host "Executando correção do usuário admin..." -ForegroundColor Yellow
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptPath "fix-admin-user.sql"

try {
    $result = psql -U glojas_user -d glojas_business -f $sqlFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host "USUARIO ADMIN CORRIGIDO COM SUCESSO!" -ForegroundColor Green
        Write-Host "=====================================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Credenciais para teste:" -ForegroundColor Cyan
        Write-Host "- Username: admin" -ForegroundColor White
        Write-Host "- Email: admin" -ForegroundColor White
        Write-Host "- Senha: admin123" -ForegroundColor White
        Write-Host "- Role: ADMIN" -ForegroundColor White
        Write-Host ""
        Write-Host "Agora você pode testar a API de permissões!" -ForegroundColor Green
        Write-Host ""
    } else {
        throw "Falha na execução"
    }
} catch {
    Write-Host ""
    Write-Host "ERRO: Falha ao executar a correção!" -ForegroundColor Red
    Write-Host "Verifique os logs acima para mais detalhes." -ForegroundColor Yellow
    Write-Host ""
}

Read-Host "Pressione Enter para sair"
