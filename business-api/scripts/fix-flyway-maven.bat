@echo off
echo ========================================
echo  CORRIGINDO CHECKSUM MISMATCH DO FLYWAY
echo  USANDO MAVEN PLUGIN
echo ========================================
echo.

echo [INFO] Navegando para o diretório business-api...
cd /d "%~dp0.."

echo [INFO] Verificando se o Maven está disponível...
mvn --version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Maven não encontrado no PATH
    echo [INFO] Instale o Maven ou use o Flyway CLI diretamente
    pause
    exit /b 1
)

echo [INFO] Executando flyway repair via Maven...
echo [INFO] Isso irá corrigir os checksums das migrações aplicadas
echo.

mvn flyway:repair

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] Checksums corrigidos com sucesso via Maven!
    echo [INFO] A aplicação agora deve inicializar normalmente
    echo.
    echo [INFO] Para testar, execute: mvn spring-boot:run
) else (
    echo.
    echo [ERRO] Falha ao executar flyway repair via Maven
    echo [INFO] Verifique a conexão com o banco de dados
    echo [INFO] Certifique-se de que o PostgreSQL está rodando
)

echo.
pause
