@echo off
echo ========================================
echo    STATUS DOS SERVIÇOS - PRODUÇÃO
echo ========================================
echo.

echo Verificando status dos servicos...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 status"

echo.
echo Verificando saude das APIs...
call ssh-prod.bat "curl -f -s http://localhost:8089/api/business/actuator/health > /dev/null && echo '✅ Business API: OK' || echo '❌ Business API: FAIL'"
call ssh-prod.bat "curl -f -s http://localhost:8087/api/legacy/actuator/health > /dev/null && echo '✅ Legacy API: OK' || echo '❌ Legacy API: FAIL'"

echo.
echo Uso de memoria e disco...
call ssh-prod.bat "free -h && echo && df -h /opt/glojas-modernized"

echo.
pause
