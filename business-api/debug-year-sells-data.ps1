# =====================================================
# SCRIPT PARA DEBUGAR DADOS NA TABELA YEAR_SELLS
# =====================================================
# Este script verifica se há dados na tabela year_sells
# Execute: .\debug-year-sells-data.ps1
# =====================================================

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "DEBUGANDO DADOS NA TABELA YEAR_SELLS" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Configurações do banco
$host = "localhost"
$port = "5432"
$database = "glojas_business"
$username = "glojas_user"

Write-Host "Conectando ao banco: $database" -ForegroundColor Yellow
Write-Host ""

try {
    # Query para verificar dados na tabela year_sells
    $query = @"
SELECT 
    store_id,
    store_code,
    store_name,
    year,
    total,
    created_at,
    updated_at
FROM year_sells 
WHERE year = 2025
ORDER BY store_code;
"@

    Write-Host "Executando query de verificação..." -ForegroundColor Yellow
    Write-Host "Query: $query" -ForegroundColor Gray
    Write-Host ""

    # Executar query usando psql
    $result = psql -h $host -p $port -U $username -d $database -c $query 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "=================================================" -ForegroundColor Green
        Write-Host "DADOS ENCONTRADOS NA TABELA YEAR_SELLS" -ForegroundColor Green
        Write-Host "=================================================" -ForegroundColor Green
        Write-Host $result -ForegroundColor White
        
        # Contar registros
        $countQuery = "SELECT COUNT(*) as total FROM year_sells WHERE year = 2025;"
        $countResult = psql -h $host -p $port -U $username -d $database -c $countQuery -t 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "Total de registros para 2025: $countResult" -ForegroundColor Cyan
        }
        
    } else {
        Write-Host "=================================================" -ForegroundColor Red
        Write-Host "ERRO AO EXECUTAR QUERY" -ForegroundColor Red
        Write-Host "=================================================" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    }

} catch {
    Write-Host "=================================================" -ForegroundColor Red
    Write-Host "ERRO DE CONEXÃO" -ForegroundColor Red
    Write-Host "=================================================" -ForegroundColor Red
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "DEBUG CONCLUÍDO" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Instruções adicionais
Write-Host ""
Write-Host "Se houver dados na tabela, o problema está na lógica de separação." -ForegroundColor Yellow
Write-Host "Se não houver dados, o problema pode estar na query de busca." -ForegroundColor Yellow
Write-Host ""
Write-Host "Para limpar os dados de teste:" -ForegroundColor Green
Write-Host "DELETE FROM year_sells WHERE year = 2025;" -ForegroundColor White
