# =====================================================
# SCRIPT POWERSHELL PARA CRIAR TABELA YEAR_SELLS
# =====================================================
# Este script executa o SQL de criação da tabela year_sells
# Execute: .\run-create-year-sells-table.ps1
# =====================================================

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "CRIANDO TABELA YEAR_SELLS - BUSINESS API" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Verificar se o arquivo SQL existe
$sqlFile = "create-year-sells-table.sql"
if (-not (Test-Path $sqlFile)) {
    Write-Host "ERRO: Arquivo $sqlFile não encontrado!" -ForegroundColor Red
    Write-Host "Certifique-se de estar no diretório business-api/scripts" -ForegroundColor Yellow
    exit 1
}

Write-Host "Arquivo SQL encontrado: $sqlFile" -ForegroundColor Green

# Executar o script SQL
Write-Host "Executando script SQL..." -ForegroundColor Yellow

try {
    # Executar psql com o arquivo SQL
    psql -U glojas_user -d glojas_business -f $sqlFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "=================================================" -ForegroundColor Green
        Write-Host "TABELA YEAR_SELLS CRIADA COM SUCESSO!" -ForegroundColor Green
        Write-Host "=================================================" -ForegroundColor Green
        Write-Host "A tabela está pronta para ser utilizada pela aplicação." -ForegroundColor Green
    } else {
        Write-Host "ERRO: Falha ao executar o script SQL!" -ForegroundColor Red
        Write-Host "Código de saída: $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERRO: Exceção ao executar psql: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "Script concluído com sucesso!" -ForegroundColor Green
