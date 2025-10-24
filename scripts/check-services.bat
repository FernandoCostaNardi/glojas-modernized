@echo off
setlocal enabledelayedexpansion

:: ============================================
:: SCRIPT DE VERIFICAÇÃO - PROJETO GLOJAS
:: Verifica status de todos os serviços
:: ============================================

title Projeto Glojas - Status dos Serviços
color 0B

:: Configurar credenciais PostgreSQL
set PGHOST=localhost
set PGPORT=5432
set PGPASSWORD=F1e0r8n0#1

echo.
echo ========================================
echo    PROJETO GLOJAS - STATUS SERVIÇOS   
echo ========================================
echo.

:: ============================================
:: VERIFICAR POSTGRESQL
:: ============================================
echo 🐘 POSTGRESQL
echo ----------------------------------------

:: Verificar serviço Windows
sc query postgresql-x64-15 2>nul | findstr "RUNNING" >nul 2>&1
if errorlevel 1 (
    echo Status Serviço: ❌ PARADO
    set pg_service=PARADO
) else (
    echo Status Serviço: ✅ RODANDO
    set pg_service=RODANDO
)

:: Verificar porta
netstat -an | findstr "5432" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo Porta 5432: ❌ NÃO ESTÁ ESCUTANDO
    set pg_port=FECHADA
) else (
    echo Porta 5432: ✅ ESCUTANDO
    set pg_port=ABERTA
)

:: Testar conexão
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "SELECT version();" >nul 2>&1
if errorlevel 1 (
    echo Conexão BD: ❌ FALHA (verifique credenciais)
    set pg_connection=FALHA
) else (
    echo Conexão BD: ✅ OK
    set pg_connection=OK
)

:: Verificar estrutura (tabelas)
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "\dt" 2>nul | findstr "usuarios" >nul 2>&1
if errorlevel 1 (
    echo Estrutura: ❌ TABELAS NÃO ENCONTRADAS
    set pg_structure=SEM_TABELAS
) else (
    echo Estrutura: ✅ TABELAS OK
    set pg_structure=OK
)

echo.

:: ============================================
:: VERIFICAR REDIS
:: ============================================
echo 📦 REDIS
echo ----------------------------------------

:: Verificar se Redis responde
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo Status: ❌ NÃO RESPONDE
    set redis_status=PARADO
) else (
    echo Status: ✅ RESPONDENDO
    set redis_status=RODANDO
)

:: Verificar porta
netstat -an | findstr "6379" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo Porta 6379: ❌ NÃO ESTÁ ESCUTANDO  
    set redis_port=FECHADA
) else (
    echo Porta 6379: ✅ ESCUTANDO
    set redis_port=ABERTA
)

:: Testar operações
if "!redis_status!" equ "RODANDO" (
    redis-cli set test_check "ok" >nul 2>&1
    redis-cli get test_check | findstr "ok" >nul 2>&1
    if errorlevel 1 (
        echo Operações: ❌ FALHA NAS OPERAÇÕES
        set redis_ops=FALHA
    ) else (
        echo Operações: ✅ OK
        set redis_ops=OK
        redis-cli del test_check >nul 2>&1
    )
) else (
    echo Operações: ❌ N/A (Redis parado)
    set redis_ops=NA
)

:: Verificar memória (se estiver rodando)
if "!redis_status!" equ "RODANDO" (
    for /f "tokens=2" %%i in ('redis-cli info memory ^| findstr "used_memory_human"') do (
        echo Uso Memória: %%i
    )
)

echo.

:: ============================================
:: VERIFICAR PROCESSOS APLICAÇÕES
:: ============================================
echo 🚀 APLICAÇÕES
echo ----------------------------------------

:: Verificar processos Java (APIs)
set javacount=0
for /f %%i in ('tasklist /fi "imagename eq java.exe" 2^>nul ^| find /c "java.exe"') do set javacount=%%i

if !javacount! gtr 0 (
    echo Aplicações Java: ✅ !javacount! processo(s) rodando
    
    :: Tentar identificar quais APIs estão rodando
    curl -s http://localhost:8089/api/business/health >nul 2>&1
    if not errorlevel 1 (
        echo   • Business API: ✅ Rodando (porta 8089)
    ) else (
        echo   • Business API: ❌ Não responde (porta 8087)
    )
    
    curl -s http://localhost:8087/api/legacy/health >nul 2>&1
    if not errorlevel 1 (
        echo   • Legacy API: ✅ Rodando (porta 8087)
    ) else (
        echo   • Legacy API: ❌ Não responde (porta 8087)
    )
    
) else (
    echo Aplicações Java: ❌ Nenhuma rodando
    echo   • Business API: ❌ Não iniciada
    echo   • Legacy API: ❌ Não iniciada
)

:: Verificar processos Node.js (Frontend)
set nodecount=0
for /f %%i in ('tasklist /fi "imagename eq node.exe" 2^>nul ^| find /c "node.exe"') do set nodecount=%%i

if !nodecount! gtr 0 (
    echo Frontend React: ✅ !nodecount! processo(s) Node rodando
    
    :: Verificar se frontend responde
    curl -s http://localhost:3000 >nul 2>&1
    if not errorlevel 1 (
        echo   • Frontend: ✅ Respondendo (porta 3000)
    ) else (
        echo   • Frontend: ⚠️  Processo rodando mas não responde
    )
) else (
    echo Frontend React: ❌ Não iniciado
)

echo.

:: ============================================
:: RESUMO GERAL
:: ============================================
echo ========================================
echo              RESUMO GERAL             
echo ========================================
echo.

:: Calcular score geral
set score=0
if "!pg_service!" equ "RODANDO" set /a score+=1
if "!pg_port!" equ "ABERTA" set /a score+=1  
if "!pg_connection!" equ "OK" set /a score+=1
if "!pg_structure!" equ "OK" set /a score+=1
if "!redis_status!" equ "RODANDO" set /a score+=1
if "!redis_port!" equ "ABERTA" set /a score+=1
if "!redis_ops!" equ "OK" set /a score+=1

echo 📊 Score de Saúde: !score!/7

if !score! geq 6 (
    echo 🎯 Status Geral: ✅ EXCELENTE - Pronto para desenvolvimento
) else if !score! geq 4 (
    echo 🎯 Status Geral: ⚠️  BOM - Alguns ajustes necessários
) else (
    echo 🎯 Status Geral: ❌ CRÍTICO - Requer atenção
)

echo.
echo 📝 Recomendações:
if "!pg_service!" neq "RODANDO" echo   • Execute start-all.bat para iniciar PostgreSQL
if "!pg_structure!" neq "OK" echo   • Execute INFRA-003 para criar estrutura do banco  
if "!redis_status!" neq "RODANDO" echo   • Execute start-all.bat para iniciar Redis
if !javacount! equ 0 echo   • Inicie as APIs Java (Legacy e Business)
if !nodecount! equ 0 echo   • Inicie o Frontend React

echo.
echo 🔄 Scripts disponíveis:
echo   • start-all.bat - Iniciar serviços
echo   • stop-all.bat - Parar serviços  
echo   • check-services.bat - Este script
echo.
pause