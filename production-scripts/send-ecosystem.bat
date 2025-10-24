@echo off
echo ========================================
echo    ENVIAR ECOSYSTEM CONFIG - PRODUÇÃO
echo ========================================
echo.

echo Enviando arquivo ecosystem.config.js para VPS...
scp ecosystem.config.js glojas@%VPS_HOST%:/opt/glojas-modernized/

if errorlevel 1 (
    echo ❌ Erro ao enviar arquivo ecosystem.config.js
    pause
    exit /b 1
)

echo.
echo Ajustando permissoes...
call ssh-prod.bat "sudo chown glojas:glojas /opt/glojas-modernized/ecosystem.config.js"

echo.
echo Verificando arquivo enviado...
call ssh-prod.bat "ls -la /opt/glojas-modernized/ecosystem.config.js"

echo.
echo ✅ Arquivo ecosystem.config.js enviado com sucesso!
echo.
echo Para aplicar as mudancas, execute:
echo 1. stop-prod.bat
echo 2. start-prod.bat
echo.
pause
