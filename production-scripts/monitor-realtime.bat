@echo off
echo ========================================
echo    MONITORAMENTO TEMPO REAL - GLOJAS
echo ========================================
echo.

:menu
cls
echo ========================================
echo    MONITORAMENTO TEMPO REAL
echo ========================================
echo.
echo [1] CPU e Memoria (atualiza a cada 2s)
echo [2] Logs PM2 em tempo real
echo [3] Logs Nginx em tempo real
echo [4] Logs PostgreSQL em tempo real
echo [5] Top 10 processos por CPU
echo [6] Top 10 processos por Memoria
echo [7] Monitorar conexoes de rede
echo [8] Estatisticas de requisicoes HTTP
echo [9] Voltar ao menu principal
echo [0] Sair
echo.
set /p choice="Escolha uma opcao: "

if "%choice%"=="1" goto cpu_memory
if "%choice%"=="2" goto pm2_logs
if "%choice%"=="3" goto nginx_logs
if "%choice%"=="4" goto postgresql_logs
if "%choice%"=="5" goto top_cpu
if "%choice%"=="6" goto top_memory
if "%choice%"=="7" goto network
if "%choice%"=="8" goto http_stats
if "%choice%"=="9" goto menu
if "%choice%"=="0" exit
goto menu

:cpu_memory
cls
echo === CPU e Memoria (Ctrl+C para parar) ===
echo.
call ssh-prod.bat "watch -n 2 'free -h && echo && top -b -n 1 | head -20'"
goto menu

:pm2_logs
cls
echo === Logs PM2 em Tempo Real (Ctrl+C para parar) ===
echo.
call ssh-prod.bat "pm2 logs --lines 100"
goto menu

:nginx_logs
cls
echo === Logs Nginx em Tempo Real (Ctrl+C para parar) ===
echo.
call ssh-prod.bat "sudo tail -f /var/log/nginx/access.log /var/log/nginx/error.log"
goto menu

:postgresql_logs
cls
echo === Logs PostgreSQL em Tempo Real (Ctrl+C para parar) ===
echo.
call ssh-prod.bat "sudo tail -f /var/log/postgresql/postgresql-*.log"
goto menu

:top_cpu
cls
echo === Top 10 Processos por CPU ===
echo.
call ssh-prod.bat "ps aux --sort=-%cpu | head -11"
echo.
pause
goto menu

:top_memory
cls
echo === Top 10 Processos por Memoria ===
echo.
call ssh-prod.bat "ps aux --sort=-%mem | head -11"
echo.
pause
goto menu

:network
cls
echo === Conexoes de Rede Ativas ===
echo.
call ssh-prod.bat "netstat -tuln | grep LISTEN && echo && ss -s"
echo.
pause
goto menu

:http_stats
cls
echo === Estatisticas HTTP (ultimos 1000 requests) ===
echo.
call ssh-prod.bat "sudo tail -1000 /var/log/nginx/access.log | awk '{print $9}' | sort | uniq -c | sort -rn"
echo.
pause
goto menu
