@echo off
echo ========================================
echo    REINICIANDO SERVIÇOS - PRODUÇÃO
echo ========================================
echo.

echo Reiniciando todos os servicos...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 restart all"

echo.
echo Aguardando servicos subirem...
timeout /t 30 /nobreak >nul

echo.
echo Verificando status...
call status-prod.bat

echo.
echo ========================================
echo    SERVIÇOS REINICIADOS COM SUCESSO!
echo ========================================
pause
