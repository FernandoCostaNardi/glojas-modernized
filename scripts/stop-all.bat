@echo off
setlocal enabledelayedexpansion

:: ============================================
:: SCRIPT DE PARADA - PROJETO GLOJAS
:: Para PostgreSQL, Redis e APIs
:: ============================================

title Projeto Glojas - Parando Serviços
color 0C

echo.
echo ========================================
echo     PROJETO GLOJAS - FINALIZANDO     
echo ========================================
echo.

:: ============================================
:: PARAR APLICAÇÕES (se estiverem rodando)
:: ============================================
echo [1/4] Parando aplicações Java...

:: Procurar e parar processos Java relacionados ao projeto
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq java.exe" /fo csv ^| findstr "java.exe"') do (
    set pid=%%i
    set pid=!pid:"=!
    wmic process where "processid=!pid!" get commandline /format:csv | findstr "spring-boot\|glojas" >nul 2>&1
    if not errorlevel 1 (
        echo    Parando processo Java PID: !pid!
        taskkill /pid !pid! /f >nul 2>&1
    )
)
echo ✅ Aplicações Java finalizadas

echo.

:: ============================================
:: PARAR NODEJS (FRONTEND)
:: ============================================
echo [2/4] Parando aplicações Node.js...

:: Parar processos Node relacionados ao projeto
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq node.exe" /fo csv ^| findstr "node.exe"') do (
    set pid=%%i
    set pid=!pid:"=!
    wmic process where "processid=!pid!" get commandline /format:csv | findstr "react\|npm\|glojas" >nul 2>&1
    if not errorlevel 1 (
        echo    Parando processo Node PID: !pid!
        taskkill /pid !pid! /f >nul 2>&1
    )
)
echo ✅ Aplicações Node.js finalizadas

echo.

:: ============================================
:: PARAR REDIS
:: ============================================
echo [3/4] Parando Redis...

:: Verificar se Redis está rodando
redis-cli ping >nul 2>&1
if not errorlevel 1 (
    echo    Redis está rodando, finalizando...
    
    :: Tentar shutdown gracioso
    redis-cli shutdown >nul 2>&1
    timeout /t 2 /nobreak >nul
    
    :: Verificar se parou
    redis-cli ping >nul 2>&1
    if not errorlevel 1 (
        echo    Forçando parada do Redis...
        taskkill /im redis-server.exe /f >nul 2>&1
        net stop redis >nul 2>&1
    )
    echo ✅ Redis finalizado
) else (
    echo ✅ Redis já estava parado
)

echo.

:: ============================================
:: PARAR POSTGRESQL (OPCIONAL)
:: ============================================
echo [4/4] PostgreSQL...

echo    PostgreSQL pode continuar rodando para outros projetos
echo    Deseja parar o PostgreSQL também? (S/N)
set /p stoppg=
if /i "!stoppg!" equ "S" (
    echo    Parando PostgreSQL...
    net stop postgresql-x64-15 >nul 2>&1
    if errorlevel 1 (
        echo ⚠️  Falha ao parar PostgreSQL (pode não estar rodando)
    ) else (
        echo ✅ PostgreSQL finalizado
    )
) else (
    echo ✅ PostgreSQL mantido em execução
)

echo.

:: ============================================
:: VERIFICAÇÃO FINAL
:: ============================================
echo ========================================
echo         VERIFICAÇÃO FINAL             
echo ========================================
echo.

:: Verificar processos Java restantes
set javacount=0
for /f %%i in ('tasklist /fi "imagename eq java.exe" 2^>nul ^| find /c "java.exe"') do set javacount=%%i
echo 📊 Processos Java restantes: !javacount!

:: Verificar processos Node restantes  
set nodecount=0
for /f %%i in ('tasklist /fi "imagename eq node.exe" 2^>nul ^| find /c "node.exe"') do set nodecount=%%i
echo 📊 Processos Node.js restantes: !nodecount!

:: Verificar Redis
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo 📦 Redis: ✅ Parado
) else (
    echo 📦 Redis: ⚠️  Ainda rodando
)

:: Verificar PostgreSQL
sc query postgresql-x64-15 | findstr "RUNNING" >nul 2>&1
if errorlevel 1 (
    echo 🐘 PostgreSQL: ✅ Parado
) else (
    echo 🐘 PostgreSQL: ✅ Rodando (mantido)
)

echo.
echo ========================================
echo           LIMPEZA CONCLUÍDA           
echo ========================================
echo.
echo 📝 Para reiniciar tudo: start-all.bat
echo 🔍 Para verificar status: check-services.bat
echo.
echo ✅ Finalização concluída!
echo.
pause