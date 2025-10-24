@echo off
echo ========================================
echo    INICIANDO SERVIÇOS - PRODUÇÃO
echo ========================================
echo.

echo Iniciando todos os servicos na producao...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 start ecosystem.config.js"

echo.
echo Aguardando servicos subirem...
timeout /t 20 /nobreak >nul

echo.
echo Verificando status...
call status-prod.bat

echo.
echo ========================================
echo    SERVIÇOS INICIADOS COM SUCESSO!
echo ========================================
pause
