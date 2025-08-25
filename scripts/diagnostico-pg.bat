@echo off
echo === DIAGNOSTICO POSTGRESQL ===
echo.

echo [1] Testando conexao sem especificar database...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -c "SELECT 1;" 2>&1
echo Exit code: %errorlevel%

echo.
echo [2] Testando conexao como postgres sem database...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U postgres -c "SELECT 1;" 2>&1
echo Exit code: %errorlevel%

echo.
echo [3] Verificando se database glojas_business existe...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -l 2>&1 | findstr "glojas_business"
if errorlevel 1 (
    echo Database NAO EXISTE
) else (
    echo Database EXISTE
)

echo.
echo [4] Verificando usuarios existentes...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "\du" 2>&1 | findstr "glojas_user"
if errorlevel 1 (
    echo Usuario NAO EXISTE
) else (
    echo Usuario EXISTE
)

echo.
echo === FIM DIAGNOSTICO ===
pause