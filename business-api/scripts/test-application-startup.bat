@echo off
echo ========================================
echo  TESTANDO INICIALIZAÇÃO DA APLICAÇÃO
echo ========================================
echo.

echo [INFO] Navegando para o diretório business-api...
cd /d "%~dp0.."

echo [INFO] Verificando se o Maven está disponível...
mvn --version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Maven não encontrado no PATH
    pause
    exit /b 1
)

echo [INFO] Compilando a aplicação...
mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Falha na compilação
    pause
    exit /b 1
)

echo [INFO] Testando inicialização da aplicação...
echo [INFO] A aplicação será iniciada em modo de teste
echo [INFO] Pressione Ctrl+C para parar após verificar se inicia corretamente
echo.

mvn spring-boot:run

echo.
echo [INFO] Teste de inicialização concluído
pause
