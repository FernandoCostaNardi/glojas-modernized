@echo off
echo ========================================
echo    MONITORAMENTO - PRODUÇÃO
echo ========================================
echo.

echo Opcoes de monitoramento:
echo [1] PM2 Monitor (visual)
echo [2] Status dos servicos
echo [3] Uso de CPU/Memoria
echo [4] Conexoes ativas
echo [5] Disk Usage
echo.

set /p choice="Escolha uma opcao (1-5): "

if "%choice%"=="1" (
    call ssh-prod.bat "pm2 monit"
) else if "%choice%"=="2" (
    call status-prod.bat
) else if "%choice%"=="3" (
    call ssh-prod.bat "top -bn1 | head -20"
) else if "%choice%"=="4" (
    call ssh-prod.bat "netstat -an | grep LISTEN"
) else if "%choice%"=="5" (
    call ssh-prod.bat "df -h && echo && du -sh /opt/glojas-modernized/*"
) else (
    echo Opcao invalida.
)

pause
