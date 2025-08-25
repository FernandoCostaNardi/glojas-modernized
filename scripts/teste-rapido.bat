@echo off
echo === TESTE RAPIDO GLOJAS ===
echo.

:: Configurar credenciais PostgreSQL
set PGHOST=localhost
set PGPORT=5432
set PGPASSWORD=F1e0r8n0#1

echo [1] Verificando PostgreSQL...
sc query postgresql-x64-15 2>nul | findstr "RUNNING" >nul 2>&1
if errorlevel 1 (
    echo POSTGRESQL: PARADO
) else (
    echo POSTGRESQL: RODANDO
)

echo [2] Verificando Redis...
"C:\Program Files\Redis\redis-cli.exe" ping >nul 2>&1
if errorlevel 1 (
    echo REDIS: PARADO
) else (
    echo REDIS: RODANDO
)

echo [3] Verificando conexao PostgreSQL...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo CONEXAO PG: FALHA
) else (
    echo CONEXAO PG: OK
)

echo [4] Verificando tabelas...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "\dt" 2>nul | findstr "usuarios" >nul 2>&1
if errorlevel 1 (
    echo TABELAS: NAO ENCONTRADAS
) else (
    echo TABELAS: OK
)

echo.
echo === FIM DO TESTE ===
pause