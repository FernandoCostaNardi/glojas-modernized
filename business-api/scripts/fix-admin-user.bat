@echo off
REM =====================================================
REM SCRIPT PARA CORRIGIR USUÁRIO ADMIN - BUSINESS API
REM =====================================================
REM Este script corrige o usuário admin para usar email correto
REM =====================================================

echo.
echo =====================================================
echo CORRIGINDO USUARIO ADMIN - BUSINESS API
echo =====================================================
echo.

REM Verificar se o PostgreSQL está rodando
echo Verificando conexão com PostgreSQL...
psql -U glojas_user -d glojas_business -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Não foi possível conectar ao PostgreSQL!
    echo Verifique se:
    echo 1. O PostgreSQL está rodando na porta 5432
    echo 2. O banco 'glojas_business' existe
    echo 3. O usuário 'glojas_user' tem acesso
    echo.
    pause
    exit /b 1
)

echo PostgreSQL conectado com sucesso!
echo.

REM Executar script SQL
echo Executando correção do usuário admin...
psql -U glojas_user -d glojas_business -f "%~dp0fix-admin-user.sql"

if %errorlevel% equ 0 (
    echo.
    echo =====================================================
    echo USUARIO ADMIN CORRIGIDO COM SUCESSO!
    echo =====================================================
    echo.
    echo Credenciais para teste:
    echo - Username: admin
    echo - Email: admin
    echo - Senha: admin123
    echo - Role: ADMIN
    echo.
    echo Agora você pode testar a API de permissões!
    echo.
) else (
    echo.
    echo ERRO: Falha ao executar a correção!
    echo Verifique os logs acima para mais detalhes.
    echo.
)

pause
