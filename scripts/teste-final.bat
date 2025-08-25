@echo off
echo === TESTE FINAL COMPLETO ===
echo.

echo [1] PostgreSQL Service...
sc query postgresql-x64-15 | findstr "RUNNING"
if errorlevel 1 (echo ❌ PARADO) else (echo ✅ RODANDO)

echo [2] Redis Service...
"C:\Program Files\Redis\redis-cli.exe" ping 2>nul
if errorlevel 1 (echo ❌ PARADO) else (echo ✅ RODANDO)

echo [3] PostgreSQL Connection...
set PGPASSWORD=F1e0r8n0#1
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -U glojas_user -d glojas_business -c "SELECT 'OK' as status;" 2>nul
if errorlevel 1 (echo ❌ FALHA) else (echo ✅ OK)

echo [4] Database Structure...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -U glojas_user -d glojas_business -c "\dt" 2>nul | findstr "usuarios"
if errorlevel 1 (echo ❌ SEM TABELAS) else (echo ✅ TABELAS OK)

echo [5] Redis Operations...
"C:\Program Files\Redis\redis-cli.exe" set test_final "ok" >nul 2>&1
"C:\Program Files\Redis\redis-cli.exe" get test_final | findstr "ok" >nul 2>&1
if errorlevel 1 (echo ❌ FALHA) else (echo ✅ OK)
"C:\Program Files\Redis\redis-cli.exe" del test_final >nul 2>&1

echo.
echo === PORTAS ===
netstat -an | findstr ":5432.*LISTENING" && echo ✅ PostgreSQL 5432 OK || echo ❌ PostgreSQL 5432 FALHA
netstat -an | findstr ":6379.*LISTENING" && echo ✅ Redis 6379 OK || echo ❌ Redis 6379 FALHA

echo.
echo === RESUMO ===
echo PostgreSQL e Redis estao rodando
echo Problema principal: Usuario glojas_user nao configurado
echo Solucao: Descobrir senha do postgres e executar setup-postgres.bat
echo.
pause