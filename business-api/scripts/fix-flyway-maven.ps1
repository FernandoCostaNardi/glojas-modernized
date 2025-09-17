# ========================================
#  CORRIGINDO CHECKSUM MISMATCH DO FLYWAY
#  USANDO MAVEN PLUGIN
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " CORRIGINDO CHECKSUM MISMATCH DO FLYWAY" -ForegroundColor Cyan
Write-Host " USANDO MAVEN PLUGIN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Navegar para o diretório business-api
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$businessApiPath = Join-Path $scriptPath ".."
Set-Location $businessApiPath

Write-Host "[INFO] Diretório atual: $(Get-Location)" -ForegroundColor Gray
Write-Host ""

Write-Host "[INFO] Verificando se o Maven está disponível..." -ForegroundColor Yellow

try {
    $mavenVersion = mvn --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[INFO] Maven encontrado" -ForegroundColor Green
    } else {
        throw "Maven não encontrado"
    }
} catch {
    Write-Host "[ERRO] Maven não encontrado no PATH" -ForegroundColor Red
    Write-Host "[INFO] Instale o Maven ou use o Flyway CLI diretamente" -ForegroundColor Yellow
    Read-Host "Pressione Enter para continuar"
    exit 1
}

Write-Host "[INFO] Executando flyway repair via Maven..." -ForegroundColor Yellow
Write-Host "[INFO] Isso irá corrigir os checksums das migrações aplicadas" -ForegroundColor Gray
Write-Host ""

try {
    mvn flyway:repair
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "[SUCESSO] Checksums corrigidos com sucesso via Maven!" -ForegroundColor Green
        Write-Host "[INFO] A aplicação agora deve inicializar normalmente" -ForegroundColor Green
        Write-Host ""
        Write-Host "[INFO] Para testar, execute: mvn spring-boot:run" -ForegroundColor Cyan
    } else {
        throw "Falha ao executar flyway repair"
    }
} catch {
    Write-Host ""
    Write-Host "[ERRO] Falha ao executar flyway repair via Maven" -ForegroundColor Red
    Write-Host "[INFO] Verifique a conexão com o banco de dados" -ForegroundColor Yellow
    Write-Host "[INFO] Certifique-se de que o PostgreSQL está rodando" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Pressione Enter para continuar"
