@echo off
echo === CONFIGURANDO POSTGRESQL ===
echo.

echo [1] Testando conexao como postgres...
echo Sera solicitada a senha do usuario postgres.
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT version();"
if errorlevel 1 (
    echo ERRO: Nao foi possivel conectar como postgres
    pause
    exit /b 1
)

echo.
echo [2] Verificando se database glojas_business existe...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "\l" | findstr "glojas_business"
if errorlevel 1 (
    echo Database nao existe. Criando...
    "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE DATABASE glojas_business;"
) else (
    echo Database ja existe.
)

echo.
echo [3] Verificando se usuario glojas_user existe...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "\du" | findstr "glojas_user"
if errorlevel 1 (
    echo Usuario nao existe. Criando...
    "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE USER glojas_user WITH PASSWORD 'glojas123';"
    "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE glojas_business TO glojas_user;"
) else (
    echo Usuario ja existe. Resetando senha...
    "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "ALTER USER glojas_user WITH PASSWORD 'glojas123';"
)

echo.
echo [4] Configurando permissoes...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL ON SCHEMA public TO glojas_user;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO glojas_user;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO glojas_user;"

echo.
echo [5] Testando conexao como glojas_user...
echo Senha: glojas123
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Conexao OK!' as resultado;"

echo.
echo === CONFIGURACAO CONCLUIDA ===
pause