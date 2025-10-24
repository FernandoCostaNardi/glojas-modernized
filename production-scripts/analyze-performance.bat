@echo off
echo ========================================
echo    ANALISE DE PERFORMANCE - GLOJAS
echo ========================================
echo.

set report_file=performance-report-%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%.txt
set report_file=%report_file: =0%

echo Gerando relatorio de performance...
echo ======================================== > %report_file%
echo   RELATORIO DE PERFORMANCE - GLOJAS >> %report_file%
echo   Data: %date% %time% >> %report_file%
echo ======================================== >> %report_file%
echo. >> %report_file%

echo [1/12] Analisando uso de CPU...
echo === USO DE CPU === >> %report_file%
call ssh-prod.bat "mpstat 1 5 2>&1 || top -b -n 1 | head -20" >> %report_file%
echo. >> %report_file%

echo [2/12] Analisando uso de Memoria...
echo === USO DE MEMORIA === >> %report_file%
call ssh-prod.bat "free -h" >> %report_file%
echo. >> %report_file%
call ssh-prod.bat "vmstat -s" >> %report_file%
echo. >> %report_file%

echo [3/12] Analisando uso de Disco...
echo === USO DE DISCO === >> %report_file%
call ssh-prod.bat "df -h" >> %report_file%
echo. >> %report_file%
call ssh-prod.bat "du -sh /opt/glojas-modernized/*" >> %report_file%
echo. >> %report_file%

echo [4/12] Analisando processos Java...
echo === PROCESSOS JAVA === >> %report_file%
call ssh-prod.bat "ps aux | grep java | grep -v grep" >> %report_file%
echo. >> %report_file%

echo [5/12] Status PM2...
echo === STATUS PM2 === >> %report_file%
call ssh-prod.bat "pm2 status && pm2 describe business-api && pm2 describe legacy-api" >> %report_file%
echo. >> %report_file%

echo [6/12] Analisando conexoes PostgreSQL...
echo === CONEXOES POSTGRESQL === >> %report_file%
call ssh-prod.bat "sudo -u postgres psql -c 'SELECT count(*) as total_connections FROM pg_stat_activity;'" >> %report_file%
call ssh-prod.bat "sudo -u postgres psql -c 'SELECT datname, count(*) FROM pg_stat_activity GROUP BY datname;'" >> %report_file%
echo. >> %report_file%

echo [7/12] Analisando queries lentas PostgreSQL...
echo === QUERIES LENTAS === >> %report_file%
call ssh-prod.bat "sudo -u postgres psql -d glojas_business -c 'SELECT query, calls, total_time, mean_time FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;' 2>&1 || echo 'pg_stat_statements nao instalado'" >> %report_file%
echo. >> %report_file%

echo [8/12] Analisando requisicoes HTTP...
echo === REQUISICOES HTTP (ultimos 1000) === >> %report_file%
call ssh-prod.bat "sudo tail -1000 /var/log/nginx/access.log | awk '{print $9}' | sort | uniq -c | sort -rn | head -20" >> %report_file%
echo. >> %report_file%

echo [9/12] Analisando endpoints mais acessados...
echo === ENDPOINTS MAIS ACESSADOS === >> %report_file%
call ssh-prod.bat "sudo tail -1000 /var/log/nginx/access.log | awk '{print $7}' | sort | uniq -c | sort -rn | head -20" >> %report_file%
echo. >> %report_file%

echo [10/12] Analisando tempo de resposta...
echo === TEMPO DE RESPOSTA (media ultimas 100 req) === >> %report_file%
call ssh-prod.bat "sudo tail -100 /var/log/nginx/access.log | awk '{print $NF}' | awk '{sum+=$1; count++} END {print \"Media: \" sum/count \" segundos\"}'" >> %report_file%
echo. >> %report_file%

echo [11/12] Verificando erros recentes...
echo === ERROS RECENTES === >> %report_file%
call ssh-prod.bat "sudo grep -i error /var/log/nginx/error.log | tail -50" >> %report_file%
echo. >> %report_file%

echo [12/12] Configuracoes JVM...
echo === CONFIGURACOES JVM === >> %report_file%
call ssh-prod.bat "pm2 env business-api | grep -i java" >> %report_file%
call ssh-prod.bat "pm2 env legacy-api | grep -i java" >> %report_file%
echo. >> %report_file%

echo.
echo ========================================
echo   RELATORIO GERADO COM SUCESSO!
echo ========================================
echo.
echo Arquivo: %report_file%
echo.
echo Abrindo relatorio...
start notepad %report_file%
echo.
pause
