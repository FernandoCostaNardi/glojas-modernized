# ========================================
#  CORRIGINDO CHECKSUM MISMATCH DO FLYWAY
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " CORRIGINDO CHECKSUM MISMATCH DO FLYWAY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar variáveis de ambiente
$env:FLYWAY_URL = "jdbc:postgresql://localhost:5432/glojas_business"
$env:FLYWAY_USER = "glojas_user"
$env:FLYWAY_PASSWORD = "F1e0r8n0#1"
$env:FLYWAY_SCHEMAS = "public"
$env:FLYWAY_LOCATIONS = "filesystem:src/main/resources/db/migration"

Write-Host "[INFO] Conectando ao banco de dados..." -ForegroundColor Yellow
Write-Host "[INFO] URL: $env:FLYWAY_URL" -ForegroundColor Gray
Write-Host "[INFO] Schema: $env:FLYWAY_SCHEMAS" -ForegroundColor Gray
Write-Host ""

# Verificar se o Flyway CLI está disponível
try {
    $flywayVersion = flyway -v 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[INFO] Flyway CLI encontrado: $flywayVersion" -ForegroundColor Green
    } else {
        throw "Flyway CLI não encontrado"
    }
} catch {
    Write-Host "[ERRO] Flyway CLI não encontrado no PATH" -ForegroundColor Red
    Write-Host "[INFO] Instale o Flyway CLI ou use o Maven plugin" -ForegroundColor Yellow
    Write-Host "[INFO] Alternativa: Execute via Maven com 'mvn flyway:repair'" -ForegroundColor Yellow
    Read-Host "Pressione Enter para continuar"
    exit 1
}

Write-Host "[INFO] Executando flyway repair para corrigir checksums..." -ForegroundColor Yellow

# Executar flyway repair
$repairCommand = @(
    "flyway", "repair",
    "-url=`"$env:FLYWAY_URL`"",
    "-user=`"$env:FLYWAY_USER`"",
    "-password=`"$env:FLYWAY_PASSWORD`"",
    "-schemas=`"$env:FLYWAY_SCHEMAS`"",
    "-locations=`"$env:FLYWAY_LOCATIONS`""
)

try {
    & $repairCommand[0] $repairCommand[1..($repairCommand.Length-1)]
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "[SUCESSO] Checksums corrigidos com sucesso!" -ForegroundColor Green
        Write-Host "[INFO] A aplicação agora deve inicializar normalmente" -ForegroundColor Green
    } else {
        throw "Falha ao executar flyway repair"
    }
} catch {
    Write-Host ""
    Write-Host "[ERRO] Falha ao executar flyway repair" -ForegroundColor Red
    Write-Host "[INFO] Verifique a conexão com o banco de dados" -ForegroundColor Yellow
    Write-Host "[INFO] Alternativa: Execute via Maven com 'mvn flyway:repair'" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Pressione Enter para continuar"
