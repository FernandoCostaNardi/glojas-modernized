@echo off
echo === CONFIGURACAO COMPLETA POSTGRESQL ===
echo Usando senhas corretas...
echo.

set PGHOST=localhost
set PGPORT=5432

echo [1] Testando conexao como postgres...
set PGPASSWORD=postgres123
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "SELECT 'Conexao OK!' as status;"
if errorlevel 1 (
    echo ERRO: Senha do postgres incorreta ou servidor inacessivel
    pause
    exit /b 1
)
echo ✅ Conexao postgres OK!

echo.
echo [2] Criando database glojas_business...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "DROP DATABASE IF EXISTS glojas_business;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE DATABASE glojas_business;"
echo ✅ Database criado!

echo.
echo [3] Criando usuario glojas_user...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "DROP USER IF EXISTS glojas_user;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE USER glojas_user WITH PASSWORD 'F1e0r8n0#1';"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE glojas_business TO glojas_user;"
echo ✅ Usuario criado!

echo.
echo [4] Configurando permissoes...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL ON SCHEMA public TO glojas_user;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO glojas_user;"
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d glojas_business -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO glojas_user;"
echo ✅ Permissoes configuradas!

echo.
echo [5] Executando script de estrutura do banco...
set PGPASSWORD=F1e0r8n0#1
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -f setup-database.sql
if errorlevel 1 (
    echo ERRO: Falha ao executar script SQL
    pause
    exit /b 1
)
echo ✅ Estrutura do banco criada!

echo.
echo [6] Testando conexao final como glojas_user...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Setup completo!' as resultado;"
echo ✅ Conexao glojas_user OK!

echo.
echo [7] Verificando tabelas criadas...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "\dt"

echo.
echo [8] Verificando usuarios criados no sistema...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT count(*) as total_usuarios FROM usuarios;"

echo.
echo === CONFIGURACAO CONCLUIDA COM SUCESSO! ===
echo.
echo Credenciais configuradas:
echo Database: glojas_business
echo Usuario: glojas_user  
echo Senha: F1e0r8n0#1
echo.
pause