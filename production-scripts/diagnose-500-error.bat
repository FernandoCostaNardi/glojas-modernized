@echo off
echo ========================================
echo    DIAGNOSTICO ERRO 500 - GLOJAS
echo ========================================
echo.

echo [1/8] Verificando status PM2...
call ssh-prod.bat "pm2 status"
echo.

echo [2/8] Verificando se frontend existe...
call ssh-prod.bat "ls -lah /opt/glojas-modernized/frontend/dist/ | head -20"
echo.

echo [3/8] Verificando permissoes...
call ssh-prod.bat "ls -la /opt/glojas-modernized/"
echo.

echo [4/8] Testando APIs diretamente...
call ssh-prod.bat "curl -s -o /dev/null -w '%%{http_code}' http://localhost:8082/api/business/actuator/health"
echo  ^<-- Business API (deve ser 200^)
call ssh-prod.bat "curl -s -o /dev/null -w '%%{http_code}' http://localhost:8081/api/legacy/actuator/health"
echo  ^<-- Legacy API (deve ser 200^)
echo.

echo [5/8] Verificando configuracao Nginx...
call ssh-prod.bat "sudo nginx -t"
echo.

echo [6/8] Verificando se Nginx esta rodando...
call ssh-prod.bat "sudo systemctl status nginx --no-pager -l"
echo.

echo [7/8] Logs de erro do Nginx (ultimas 20 linhas^)...
call ssh-prod.bat "sudo tail -20 /var/log/nginx/error.log"
echo.

echo [8/8] Logs das aplicacoes (ultimas 20 linhas^)...
echo.
echo === Business API ===
call ssh-prod.bat "tail -20 /opt/glojas-modernized/logs/business-api-error.log 2^>^&1 ^|^| echo 'Sem logs ainda'"
echo.
echo === Legacy API ===
call ssh-prod.bat "tail -20 /opt/glojas-modernized/logs/legacy-api-error.log 2^>^&1 ^|^| echo 'Sem logs ainda'"
echo.

echo ========================================
echo    RESUMO DO DIAGNOSTICO
echo ========================================
echo.
echo Verifique acima se:
echo [✓] PM2 mostra 2 apps online (business-api e legacy-api^)
echo [✓] Frontend /dist/ tem arquivos (index.html, assets/^)
echo [✓] APIs respondem 200 ao teste curl
echo [✓] Nginx -t mostra "test is successful"
echo [✓] Nginx esta "active (running^)"
echo.
echo Se algum [X] falhou, veja as solucoes abaixo.
echo.
pause

echo.
echo ========================================
echo    SOLUCOES RAPIDAS
echo ========================================
echo.
echo [1] Se PM2 nao tem apps rodando:
echo     call start-prod.bat
echo.
echo [2] Se frontend esta vazio:
echo     call deploy-prod.bat
echo.
echo [3] Se APIs nao respondem:
echo     call restart-prod.bat
echo.
echo [4] Se Nginx tem erro:
echo     Ver logs acima e corrigir config
echo.
echo Escolha uma opcao (1-4^) ou 0 para sair:
set /p fix="Opcao: "

if "%fix%"=="1" call start-prod.bat
if "%fix%"=="2" call deploy-prod.bat
if "%fix%"=="3" call restart-prod.bat
if "%fix%"=="4" echo Execute: ssh-prod.bat "sudo nano /etc/nginx/sites-available/glojas"
