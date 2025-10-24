@echo off
echo =====================================================
echo SCRIPT PARA POPULAR DADOS DE TESTE - BUSINESS API
echo =====================================================
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

REM Configurações do banco
set DB_NAME=glojas_business
set DB_USER=glojas_user
set DB_PASSWORD=F1e0r8n0#1
set SCRIPT_FILE=populate-test-data.sql

echo Configurações do banco:
echo - Database: %DB_NAME%
echo - Usuário: %DB_USER%
echo - Script: %SCRIPT_FILE%
echo.

REM Verificar se o arquivo de script existe
if not exist "%SCRIPT_FILE%" (
    echo ERRO: Arquivo %SCRIPT_FILE% não encontrado!
    echo Certifique-se de que o arquivo está no diretório atual
    pause
    exit /b 1
)

echo Executando script de dados de teste...
echo.

REM Executar o script SQL
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U %DB_USER% -d %DB_NAME% -f %SCRIPT_FILE%

if %errorlevel% equ 0 (
    echo.
    echo =====================================================
    echo SUCESSO! Dados de teste inseridos com sucesso!
    echo =====================================================
    echo.
    echo Usuários criados:
    echo - admin/admin123 (ADMIN)
    echo - joao.silva/admin123 (TESTER)
    echo - maria.santos/admin123 (DEVELOPER)
    echo - pedro.oliveira/admin123 (ANALYST)
    echo - ana.costa/admin123 (MANAGER)
    echo - carlos.ferreira/admin123 (USER)
    echo - lucia.rodriguez/admin123 (DEVELOPER + TESTER)
    echo.
    echo Roles criadas: ADMIN, USER, MANAGER, TESTER, DEVELOPER, ANALYST
    echo Permissões criadas: Todas as permissões básicas + permissões de teste
    echo.
) else (
    echo.
    echo =====================================================
    echo ERRO! Falha ao executar o script
    echo =====================================================
    echo.
    echo Possíveis causas:
    echo - Banco de dados não existe
    echo - Usuário ou senha incorretos
    echo - Tabelas não foram criadas pelo Hibernate
    echo - PostgreSQL não está rodando
    echo.
)

pause
