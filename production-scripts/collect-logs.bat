@echo off
echo ========================================
echo    COLETAR LOGS - ANALISE COMPLETA
echo ========================================
echo.

set timestamp=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set timestamp=%timestamp: =0%
set log_dir=logs-collected-%timestamp%

echo Criando diretorio local para logs: %log_dir%
mkdir %log_dir% 2>nul

echo.
echo [1/10] Coletando logs do PM2...
call ssh-prod.bat "pm2 logs --lines 500 --nostream" > %log_dir%\pm2-all-logs.txt

echo.
echo [2/10] Coletando logs Business API...
call ssh-prod.bat "tail -500 /opt/glojas-modernized/logs/business-api-error.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\business-api-error.txt
call ssh-prod.bat "tail -500 /opt/glojas-modernized/logs/business-api-out.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\business-api-out.txt

echo.
echo [3/10] Coletando logs Legacy API...
call ssh-prod.bat "tail -500 /opt/glojas-modernized/logs/legacy-api-error.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\legacy-api-error.txt
call ssh-prod.bat "tail -500 /opt/glojas-modernized/logs/legacy-api-out.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\legacy-api-out.txt

echo.
echo [4/10] Coletando logs Nginx...
call ssh-prod.bat "sudo tail -500 /var/log/nginx/error.log" > %log_dir%\nginx-error.txt
call ssh-prod.bat "sudo tail -500 /var/log/nginx/access.log" > %log_dir%\nginx-access.txt
call ssh-prod.bat "sudo tail -500 /var/log/nginx/glojas-error.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\nginx-glojas-error.txt
call ssh-prod.bat "sudo tail -500 /var/log/nginx/glojas-access.log 2>&1 || echo 'Arquivo nao existe'" > %log_dir%\nginx-glojas-access.txt

echo.
echo [5/10] Coletando logs do Sistema (syslog)...
call ssh-prod.bat "sudo tail -500 /var/log/syslog" > %log_dir%\syslog.txt

echo.
echo [6/10] Coletando logs do PostgreSQL...
call ssh-prod.bat "sudo tail -500 /var/log/postgresql/postgresql-*.log 2>&1 || echo 'Erro ao acessar logs PostgreSQL'" > %log_dir%\postgresql.txt

echo.
echo [7/10] Coletando metricas de CPU e Memoria...
call ssh-prod.bat "top -b -n 1 | head -20" > %log_dir%\top-snapshot.txt
call ssh-prod.bat "free -h" > %log_dir%\memory-usage.txt
call ssh-prod.bat "df -h" > %log_dir%\disk-usage.txt

echo.
echo [8/10] Coletando status dos processos...
call ssh-prod.bat "pm2 status" > %log_dir%\pm2-status.txt
call ssh-prod.bat "ps aux --sort=-%mem | head -20" > %log_dir%\processes-by-memory.txt
call ssh-prod.bat "ps aux --sort=-%cpu | head -20" > %log_dir%\processes-by-cpu.txt

echo.
echo [9/10] Coletando estatisticas Java...
call ssh-prod.bat "pm2 jlist" > %log_dir%\pm2-jlist.txt 2>&1
call ssh-prod.bat "pgrep -f 'business-api' | xargs -I {} ps -o pid,vsz,rss,cmd -p {} 2>&1" > %log_dir%\business-api-memory.txt
call ssh-prod.bat "pgrep -f 'legacy-api' | xargs -I {} ps -o pid,vsz,rss,cmd -p {} 2>&1" > %log_dir%\legacy-api-memory.txt

echo.
echo [10/10] Coletando conexoes de rede...
call ssh-prod.bat "netstat -tuln | grep LISTEN" > %log_dir%\network-ports.txt
call ssh-prod.bat "ss -s" > %log_dir%\socket-summary.txt

echo.
echo ========================================
echo    COLETA CONCLUIDA!
echo ========================================
echo.
echo Logs salvos em: %log_dir%\
echo.
echo Arquivos coletados:
dir /b %log_dir%
echo.
echo Envie a pasta %log_dir% para analise ou
echo comprima em ZIP para facilitar o envio.
echo.
pause
