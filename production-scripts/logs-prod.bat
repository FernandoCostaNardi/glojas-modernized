@echo off
echo ========================================
echo    LOGS EM TEMPO REAL - PRODUÇÃO
echo ========================================
echo.

echo Opcoes de logs:
echo [1] Business API
echo [2] Legacy API  
echo [3] Todos os servicos (PM2)
echo [4] Nginx Error Logs
echo [5] PostgreSQL Logs
echo.

set /p choice="Escolha uma opcao (1-5): "

if "%choice%"=="1" (
    call ssh-prod.bat "tail -f /opt/glojas-modernized/logs/business-api-combined.log"
) else if "%choice%"=="2" (
    call ssh-prod.bat "tail -f /opt/glojas-modernized/logs/legacy-api-combined.log"  
) else if "%choice%"=="3" (
    call ssh-prod.bat "pm2 logs"
) else if "%choice%"=="4" (
    call ssh-prod.bat "sudo tail -f /var/log/nginx/error.log"
) else if "%choice%"=="5" (
    call ssh-prod.bat "sudo tail -f /var/log/postgresql/postgresql-*-main.log"
) else (
    echo Opcao invalida.
)

pause
