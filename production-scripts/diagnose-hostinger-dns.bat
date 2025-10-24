@echo off
echo ========================================
echo    DIAGNOSTICO DNS - HOSTINGER VPS
echo ========================================
echo.

set domain=gestaosmarteletron.com.br

echo [1/5] Verificando IP atual do dominio...
nslookup %domain%
echo.

echo [2/5] IPs da Hostinger (hospedagem web):
echo - 84.32.84.32
echo - 212.85.12.228
echo.
echo Se o dominio aponta para um desses IPs:
echo ❌ ERRADO - Esta apontando para hospedagem web, nao VPS
echo.

echo [3/5] Descobrindo IP da sua VPS...
echo Entre com o IP da VPS ou pressione Enter para continuar
set /p vps_ip="IP da VPS (ex: 123.45.67.89): "
echo.

if not "%vps_ip%"=="" (
    echo [4/5] Testando conectividade com VPS...
    ping -n 2 %vps_ip%
    echo.
)

echo [5/5] Verificando nameservers...
nslookup -type=ns %domain%
echo.

echo ========================================
echo    RESUMO DO DIAGNOSTICO
echo ========================================
echo.
echo Dominio: %domain%
echo.
echo PASSOS PARA CORRIGIR:
echo.
echo 1. Acesse: https://hpanel.hostinger.com
echo 2. Va em VPS e copie o IP da sua VPS
echo 3. Va em Dominios ^> %domain% ^> DNS/Nameservers
echo 4. DELETE registros A que apontam para 84.32.84.32
echo 5. DELETE registros A que apontam para 212.85.12.228
echo 6. ADICIONE registro A com @ apontando para IP da VPS
echo 7. ADICIONE registro A com www apontando para IP da VPS
echo 8. Aguarde 15 minutos e teste novamente
echo.
pause
