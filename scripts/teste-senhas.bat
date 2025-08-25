@echo off
echo === TESTE COM SENHAS COMUNS ===
echo.

set PGHOST=localhost
set PGPORT=5432

echo [1] Tentando senha 'postgres'...
set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Senha postgres OK' as resultado;" 2>nul
if not errorlevel 1 (
    echo SUCESSO: Senha postgres funciona!
    goto :create_user
)

echo [2] Tentando senha 'admin'...
set PGPASSWORD=admin
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Senha admin OK' as resultado;" 2>nul
if not errorlevel 1 (
    echo SUCESSO: Senha admin funciona!
    goto :create_user
)

echo [3] Tentando senha '123'...
set PGPASSWORD=123
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Senha 123 OK' as resultado;" 2>nul
if not errorlevel 1 (
    echo SUCESSO: Senha 123 funciona!
    goto :create_user
)

echo [4] Tentando senha '12345'...
set PGPASSWORD=12345
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Senha 12345 OK' as resultado;" 2>nul
if not errorlevel 1 (
    echo SUCESSO: Senha 12345 funciona!
    goto :create_user
)

echo [5] Tentando senha vazia...
set PGPASSWORD=
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Sem senha OK' as resultado;" 2>nul
if not errorlevel 1 (
    echo SUCESSO: Sem senha funciona!
    goto :create_user
)

echo ERRO: Nenhuma senha comum funcionou.
echo Voce precisa descobrir a senha do usuario postgres.
goto :end

:create_user
echo.
echo Criando usuario glojas_user...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE DATABASE IF NOT EXISTS glojas_business;" 2>nul
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "DROP USER IF EXISTS glojas_user;" 2>nul
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE USER glojas_user WITH PASSWORD 'glojas123';" 2>nul
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE glojas_business TO glojas_user;" 2>nul

echo Testando conexao como glojas_user...
set PGPASSWORD=glojas123
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Usuario criado com sucesso!' as resultado;"

:end
echo.
pause