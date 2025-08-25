@echo off
setlocal enabledelayedexpansion

:: ============================================
:: SCRIPT DE INICIALIZAÇÃO - PROJETO GLOJAS
:: Verifica e inicia PostgreSQL e Redis
:: ============================================

title Projeto Glojas - Inicializando Serviços
color 0A

:: Configurar credenciais PostgreSQL
set PGHOST=localhost
set PGPORT=5432
set PGPASSWORD=F1e0r8n0#1

echo.
echo ========================================
echo     PROJETO GLOJAS - INICIALIZACAO    
echo ========================================
echo.

:: ============================================
:: VERIFICAR POSTGRESQL
:: ============================================
echo [1/4] Verificando PostgreSQL...

:: Verificar se serviço PostgreSQL está rodando
sc query postgresql-x64-15 | findstr "RUNNING" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  PostgreSQL não está rodando. Tentando iniciar...
    net start postgresql-x64-15 >nul 2>&1
    if errorlevel 1 (
        echo ❌ Falha ao iniciar PostgreSQL. Verifique a instalação.
        echo    Serviços disponíveis:
        sc query | findstr "postgresql"
        pause
        exit /b 1
    )
    echo ✅ PostgreSQL iniciado com sucesso
    timeout /t 3 /nobreak >nul
) else (
    echo ✅ PostgreSQL já está rodando
)

:: Testar conexão com o banco
echo    Testando conexão com banco glojas_business...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "SELECT 'OK' as status;" >nul 2>&1
if errorlevel 1 (
    echo ❌ Erro na conexão com PostgreSQL
    echo    Verifique:
    echo    - Usuário glojas_user existe
    echo    - Database glojas_business existe  
    echo    - Credenciais estão corretas
    echo    - Execute INFRA-001 e INFRA-003 primeiro
    pause
    exit /b 1
)
echo ✅ Conexão PostgreSQL OK

echo.

:: ============================================
:: VERIFICAR REDIS
:: ============================================
echo [2/4] Verificando Redis...

:: Verificar se Redis está rodando
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Redis não está rodando. Tentando iniciar...
    
    :: Tentar iniciar como serviço
    net start redis >nul 2>&1
    if errorlevel 1 (
        :: Se falhar, tentar iniciar diretamente
        echo    Tentando iniciar Redis diretamente...
        start "Redis Server" redis-server.exe
        timeout /t 5 /nobreak >nul
        
        :: Verificar novamente
        redis-cli ping >nul 2>&1
        if errorlevel 1 (
            echo ❌ Falha ao iniciar Redis
            echo    Verifique:
            echo    - Redis está instalado corretamente
            echo    - Execute INFRA-002 primeiro
            pause
            exit /b 1
        )
    )
    echo ✅ Redis iniciado com sucesso
) else (
    echo ✅ Redis já está rodando
)

:: Testar operações Redis
echo    Testando operações Redis...
redis-cli set glojas_test "OK" >nul 2>&1
redis-cli get glojas_test | findstr "OK" >nul 2>&1
if errorlevel 1 (
    echo ❌ Erro nas operações Redis
    pause
    exit /b 1
)
redis-cli del glojas_test >nul 2>&1
echo ✅ Operações Redis OK

echo.

:: ============================================
:: VERIFICAR PORTAS
:: ============================================
echo [3/4] Verificando portas...

:: Verificar porta PostgreSQL (5432)
netstat -an | findstr "5432" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo ❌ PostgreSQL não está escutando na porta 5432
    pause
    exit /b 1
)
echo ✅ PostgreSQL porta 5432 OK

:: Verificar porta Redis (6379)
netstat -an | findstr "6379" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo ❌ Redis não está escutando na porta 6379
    pause
    exit /b 1
)
echo ✅ Redis porta 6379 OK

echo.

:: ============================================
:: VERIFICAR ESTRUTURA DO BANCO
:: ============================================
echo [4/4] Verificando estrutura do banco...

:: Verificar se tabelas existem
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -h localhost -p 5432 -U glojas_user -d glojas_business -c "\dt" | findstr "usuarios" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Tabelas não encontradas no banco
    echo    Execute INFRA-003 para criar a estrutura
    echo.
    echo    Deseja continuar mesmo assim? (S/N)
    set /p continue=
    if /i "!continue!" neq "S" (
        echo Execução cancelada pelo usuário
        pause
        exit /b 1
    )
) else (
    echo ✅ Estrutura do banco OK
)

echo.
echo ========================================
echo        TODOS OS SERVIÇOS PRONTOS!      
echo ========================================
echo.
echo 🐘 PostgreSQL: localhost:5432
echo    Database: glojas_business
echo    User: glojas_user
echo.
echo 📦 Redis: localhost:6379
echo    Status: Cache pronto
echo.
echo ========================================
echo        PRÓXIMOS PASSOS                 
echo ========================================
echo.
echo 🔄 Agora você pode:
echo    • Iniciar Legacy API (porta 8082)
echo    • Iniciar Business API (porta 8081)  
echo    • Iniciar Frontend React (porta 3000)
echo.
echo 📝 Scripts disponíveis:
echo    • stop-all.bat - Para parar serviços
echo    • check-services.bat - Verificar status
echo.

:: Opção para abrir ferramentas de monitoramento
echo Deseja abrir ferramentas de monitoramento? (S/N)
set /p opentools=
if /i "!opentools!" equ "S" (
    echo Abrindo pgAdmin e Redis CLI...
    start "" "pgAdmin 4" 2>nul
    start "Redis Monitor" cmd /k "redis-cli monitor"
)

echo.
echo ✅ Inicialização concluída com sucesso!
echo.
pause
