@echo off
echo === CORRIGINDO SENHA DO GLOJAS_USER ===
echo.

set PGHOST=localhost
set PGPORT=5432
set PGPASSWORD=postgres123

echo [1] Resetando senha do glojas_user...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "ALTER USER glojas_user WITH PASSWORD 'F1e0r8n0#1';"
if errorlevel 1 (
    echo ERRO: Falha ao alterar senha
    pause
    exit /b 1
)
echo ✅ Senha alterada!

echo.
echo [2] Testando conexao com nova senha...
set PGPASSWORD=F1e0r8n0#1
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -c "SELECT 'Conexao OK com nova senha!' as teste;"
if errorlevel 1 (
    echo ERRO: Ainda nao consegue conectar
    pause
    exit /b 1
)
echo ✅ Conexao OK!

echo.
echo [3] Executando script de estrutura...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U glojas_user -d glojas_business -f setup-database.sql
if errorlevel 1 (
    echo ERRO: Falha ao executar script
    pause
    exit /b 1
)
echo ✅ Script executado!

echo.
echo === CORRECAO CONCLUIDA ===
pause