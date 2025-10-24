@echo off
echo ========================================
echo    PARANDO SERVIÇOS - PRODUÇÃO  
echo ========================================
echo.

set /p confirm="Deseja parar todos os servicos? (s/n): "
if /i not "%confirm%"=="s" (
    echo Operacao cancelada.
    pause
    exit /b 0
)

echo.
echo Parando todos os servicos na producao...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 stop all"

echo.
echo ========================================
echo    SERVIÇOS PARADOS COM SUCESSO!
echo ========================================
pause
