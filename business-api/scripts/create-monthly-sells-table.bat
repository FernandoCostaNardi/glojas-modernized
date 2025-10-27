@echo off
echo =====================================================
echo CRIAR TABELA MONTHLY_SELLS - BUSINESS API
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
set SCRIPT_FILE=create-monthly-sells-table.sql

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

echo Executando script de criação da tabela monthly_sells...
echo.

REM Executar o script SQL
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U %DB_USER% -d %DB_NAME% -f %SCRIPT_FILE%

if %errorlevel% equ 0 (
    echo.
    echo =====================================================
    echo SUCESSO! Tabela monthly_sells criada com sucesso!
    echo =====================================================
    echo.
    echo A tabela está pronta para uso pela aplicação.
    echo.
    echo Estrutura da tabela:
    echo - id (UUID) - Chave primária
    echo - store_code (VARCHAR) - Código da loja
    echo - store_id (UUID) - ID da loja
    echo - store_name (VARCHAR) - Nome da loja
    echo - total (NUMERIC) - Valor total das vendas mensais
    echo - created_at (TIMESTAMP) - Data de criação
    echo - updated_at (TIMESTAMP) - Data de atualização
    echo.
    echo Índices criados:
    echo - idx_monthly_sells_store_code
    echo - idx_monthly_sells_store_id
    echo - idx_monthly_sells_created_at
    echo.
) else (
    echo.
    echo =====================================================
    echo ERRO! Falha ao criar a tabela monthly_sells
    echo =====================================================
    echo.
    echo Verifique os logs acima para mais detalhes.
    echo.
)

pause

