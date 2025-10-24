@echo off
echo ========================================
echo    VERIFICAR PROPAGAÇÃO DNS
echo ========================================
echo.

set /p domain="Digite o dominio (ex: glojas.com.br): "
echo.

echo [1/6] Verificando DNS local...
nslookup %domain%
echo.

echo [2/6] Verificando Google DNS (8.8.8.8)...
nslookup %domain% 8.8.8.8
echo.

echo [3/6] Verificando Cloudflare DNS (1.1.1.1)...
nslookup %domain% 1.1.1.1
echo.

echo [4/6] Verificando OpenDNS (208.67.222.222)...
nslookup %domain% 208.67.222.222
echo.

echo [5/6] Testando conectividade (ping)...
ping -n 4 %domain%
echo.

echo [6/6] Testando WWW...
nslookup www.%domain%
echo.

echo ========================================
echo    RESUMO
echo ========================================
echo.
echo Se todos os testes mostraram o mesmo IP:
echo ✅ DNS PROPAGADO! Pode continuar o deploy
echo.
echo Se alguns falharam:
echo ⏳ DNS AINDA PROPAGANDO. Aguarde 15-30 min
echo.
echo Ferramentas online para verificar:
echo https://www.whatsmydns.net
echo https://dnschecker.org
echo.
pause
