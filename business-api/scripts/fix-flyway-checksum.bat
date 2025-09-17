@echo off
echo ========================================
echo  CORRIGINDO CHECKSUM MISMATCH DO FLYWAY
echo ========================================
echo.

REM Configurar variáveis de ambiente
set FLYWAY_URL=jdbc:postgresql://localhost:5432/glojas_business
set FLYWAY_USER=glojas_user
set FLYWAY_PASSWORD=F1e0r8n0#1
set FLYWAY_SCHEMAS=public
set FLYWAY_LOCATIONS=filesystem:src/main/resources/db/migration

echo [INFO] Conectando ao banco de dados...
echo [INFO] URL: %FLYWAY_URL%
echo [INFO] Schema: %FLYWAY_SCHEMAS%
echo.

REM Verificar se o Flyway CLI está disponível
where flyway >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Flyway CLI não encontrado no PATH
    echo [INFO] Instale o Flyway CLI ou use o Maven plugin
    echo [INFO] Alternativa: Execute via Maven com 'mvn flyway:repair'
    pause
    exit /b 1
)

echo [INFO] Executando flyway repair para corrigir checksums...
flyway repair -url="%FLYWAY_URL%" -user="%FLYWAY_USER%" -password="%FLYWAY_PASSWORD%" -schemas="%FLYWAY_SCHEMAS%" -locations="%FLYWAY_LOCATIONS%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] Checksums corrigidos com sucesso!
    echo [INFO] A aplicação agora deve inicializar normalmente
) else (
    echo.
    echo [ERRO] Falha ao executar flyway repair
    echo [INFO] Verifique a conexão com o banco de dados
    echo [INFO] Alternativa: Execute via Maven com 'mvn flyway:repair'
)

echo.
pause
