@echo off
echo =====================================================
echo SETUP COMPLETO DO BANCO DE DADOS - BUSINESS API
echo =====================================================
echo.

REM Configurações
set DB_NAME=glojas_business
set DB_USER=glojas_user
set DB_PASSWORD=F1e0r8n0#1
set PSQL_PATH="C:\Program Files\PostgreSQL\15\bin\psql.exe"

echo Configurações:
echo - Database: %DB_NAME%
echo - Usuário: %DB_USER%
echo - Senha: %DB_PASSWORD%
echo.

REM Verificar se o PostgreSQL está rodando
echo Verificando se o PostgreSQL está rodando...
netstat -an | findstr :5432 >nul
if %errorlevel% neq 0 (
    echo ERRO: PostgreSQL não está rodando na porta 5432
    echo Por favor, inicie o PostgreSQL e tente novamente
    pause
    exit /b 1
)

echo PostgreSQL está rodando!
echo.

REM Tentar conectar como postgres (usuário padrão)
echo Tentando conectar como usuário postgres...
%PSQL_PATH% -U postgres -d postgres -c "SELECT 'Conexão OK' as status;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Não foi possível conectar como postgres
    echo Verifique se o PostgreSQL está configurado corretamente
    pause
    exit /b 1
)

echo Conexão como postgres OK!
echo.

REM Criar usuário se não existir
echo Verificando/criando usuário %DB_USER%...
%PSQL_PATH% -U postgres -d postgres -c "SELECT 1 FROM pg_roles WHERE rolname='%DB_USER%';" | findstr "1" >nul
if %errorlevel% neq 0 (
    echo Criando usuário %DB_USER%...
    %PSQL_PATH% -U postgres -d postgres -c "CREATE USER %DB_USER% WITH PASSWORD '%DB_PASSWORD%';"
    if %errorlevel% neq 0 (
        echo ERRO: Falha ao criar usuário
        pause
        exit /b 1
    )
    echo Usuário %DB_USER% criado com sucesso!
) else (
    echo Usuário %DB_USER% já existe!
)

REM Criar banco se não existir
echo Verificando/criando banco %DB_NAME%...
%PSQL_PATH% -U postgres -d postgres -c "SELECT 1 FROM pg_database WHERE datname='%DB_NAME%';" | findstr "1" >nul
if %errorlevel% neq 0 (
    echo Criando banco %DB_NAME%...
    %PSQL_PATH% -U postgres -d postgres -c "CREATE DATABASE %DB_NAME% OWNER %DB_USER%;"
    if %errorlevel% neq 0 (
        echo ERRO: Falha ao criar banco
        pause
        exit /b 1
    )
    echo Banco %DB_NAME% criado com sucesso!
) else (
    echo Banco %DB_NAME% já existe!
)

REM Conceder privilégios
echo Concedendo privilégios...
%PSQL_PATH% -U postgres -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE %DB_NAME% TO %DB_USER%;"
%PSQL_PATH% -U postgres -d %DB_NAME% -c "GRANT ALL ON SCHEMA public TO %DB_USER%;"
%PSQL_PATH% -U postgres -d %DB_NAME% -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %DB_USER%;"
%PSQL_PATH% -U postgres -d %DB_NAME% -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %DB_USER%;"

echo Privilégios concedidos!
echo.

REM Testar conexão com o novo usuário
echo Testando conexão com %DB_USER%...
%PSQL_PATH% -U %DB_USER% -d %DB_NAME% -c "SELECT 'Conexão OK' as status;" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Falha ao conectar com %DB_USER%
    echo Verifique as configurações
    pause
    exit /b 1
)

echo Conexão com %DB_USER% OK!
echo.

echo =====================================================
echo SETUP CONCLUÍDO COM SUCESSO!
echo =====================================================
echo.
echo Banco de dados configurado:
echo - Database: %DB_NAME%
echo - Usuário: %DB_USER%
echo - Senha: %DB_PASSWORD%
echo.
echo Agora você pode executar os dados de teste.
echo.

pause
