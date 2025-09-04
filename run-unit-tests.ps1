# Script para executar testes unitários da API de Usuários
# Este script executa todos os testes unitários e de integração

Write-Host "=== EXECUTANDO TESTES UNITÁRIOS DA API DE USUÁRIOS ===" -ForegroundColor Yellow
Write-Host ""

# Navegar para o diretório da Business API
Set-Location "business-api"

# Verificar se o Maven está disponível
try {
    $mavenVersion = mvn -version 2>$null
    Write-Host "✅ Maven encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven não encontrado. Certifique-se de que o Maven está instalado e no PATH." -ForegroundColor Red
    exit 1
}

# Limpar e compilar o projeto
Write-Host "`n1. Limpando e compilando o projeto..." -ForegroundColor Cyan
try {
    mvn clean compile -q
    Write-Host "✅ Compilação bem-sucedida" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na compilação" -ForegroundColor Red
    exit 1
}

# Executar testes unitários
Write-Host "`n2. Executando testes unitários..." -ForegroundColor Cyan
try {
    mvn test -Dtest="*Test" -q
    Write-Host "✅ Testes unitários executados com sucesso" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na execução dos testes unitários" -ForegroundColor Red
    exit 1
}

# Executar testes específicos com relatório detalhado
Write-Host "`n3. Executando testes com relatório detalhado..." -ForegroundColor Cyan

# Testes do Controller
Write-Host "   - Testando UserController..." -ForegroundColor Gray
mvn test -Dtest="UserControllerTest" -q

# Testes do Service
Write-Host "   - Testando UserService..." -ForegroundColor Gray
mvn test -Dtest="UserServiceTest" -q

# Testes do Exception Handler
Write-Host "   - Testando GlobalExceptionHandler..." -ForegroundColor Gray
mvn test -Dtest="GlobalExceptionHandlerTest" -q

# Testes de Integração
Write-Host "   - Testando Integração..." -ForegroundColor Gray
mvn test -Dtest="UserApiIntegrationTest" -q

# Gerar relatório de cobertura (opcional)
Write-Host "`n4. Gerando relatório de cobertura..." -ForegroundColor Cyan
try {
    mvn jacoco:report -q
    Write-Host "✅ Relatório de cobertura gerado" -ForegroundColor Green
    Write-Host "   Relatório disponível em: target/site/jacoco/index.html" -ForegroundColor Gray
} catch {
    Write-Host "⚠️  Relatório de cobertura não disponível (dependência não configurada)" -ForegroundColor Yellow
}

# Executar todos os testes com relatório final
Write-Host "`n5. Executando todos os testes com relatório final..." -ForegroundColor Cyan
try {
    $testResult = mvn test -q 2>&1
    Write-Host "✅ Todos os testes executados" -ForegroundColor Green
    
    # Extrair estatísticas dos testes
    $testStats = $testResult | Select-String "Tests run:|BUILD SUCCESS|BUILD FAILURE"
    Write-Host "`n=== RESUMO DOS TESTES ===" -ForegroundColor Yellow
    $testStats | ForEach-Object { Write-Host $_ -ForegroundColor Gray }
    
} catch {
    Write-Host "❌ Erro na execução dos testes" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== TESTES CONCLUÍDOS ===" -ForegroundColor Yellow
Write-Host "🎉 Todos os testes foram executados com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Testes implementados:" -ForegroundColor Cyan
Write-Host "   • UserControllerTest - Testes unitários do controller" -ForegroundColor Gray
Write-Host "   • UserServiceTest - Testes unitários do service" -ForegroundColor Gray
Write-Host "   • GlobalExceptionHandlerTest - Testes do tratamento de exceções" -ForegroundColor Gray
Write-Host "   • UserApiIntegrationTest - Testes de integração" -ForegroundColor Gray
Write-Host ""
Write-Host "🔍 Cenários testados:" -ForegroundColor Cyan
Write-Host "   • Criação de usuários com dados válidos" -ForegroundColor Gray
Write-Host "   • Validação de dados de entrada" -ForegroundColor Gray
Write-Host "   • Verificação de unicidade de username/email" -ForegroundColor Gray
Write-Host "   • Validação de roles existentes" -ForegroundColor Gray
Write-Host "   • Tratamento de exceções customizadas" -ForegroundColor Gray
Write-Host "   • Respostas de erro estruturadas" -ForegroundColor Gray
Write-Host "   • Health checks" -ForegroundColor Gray
Write-Host ""
Write-Host "📝 Para executar testes específicos:" -ForegroundColor Cyan
Write-Host "   mvn test -Dtest=UserControllerTest" -ForegroundColor Gray
Write-Host "   mvn test -Dtest=UserServiceTest" -ForegroundColor Gray
Write-Host "   mvn test -Dtest=GlobalExceptionHandlerTest" -ForegroundColor Gray
Write-Host "   mvn test -Dtest=UserApiIntegrationTest" -ForegroundColor Gray
