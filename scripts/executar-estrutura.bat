@echo off
echo === EXECUTANDO SCRIPT DE ESTRUTURA ===
echo.

cd /d G:\olisystem\glojas-modernized\scripts

set PGHOST=localhost
set PGPORT=5432
set PGPASSWORD=F1e0r8n0#1

echo [1] Verificando se arquivo existe...
if not exist "setup-database.sql" (
    echo ERRO: Arquivo setup-database.sql nao encontrado
    dir *.sql
    pause
    exit /b 1
)
echo ✅ Arquivo encontrado!

echo.
echo [2] Executando script de estrutura...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -f setup-database.sql
if errorlevel 1 (
    echo ERRO: Falha ao executar script
    pause
    exit /b 1
)
echo ✅ Script executado com sucesso!

echo.
echo [3] Verificando tabelas criadas...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "\dt"

echo.
echo [4] Verificando dados inseridos...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Permissoes: ' || count(*)::text FROM permissoes;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Usuarios: ' || count(*)::text FROM usuarios;"

echo.
echo === ESTRUTURA CRIADA COM SUCESSO ===
pause