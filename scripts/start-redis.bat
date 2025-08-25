@echo off
echo === INICIANDO REDIS ===
echo.

echo [1] Verificando se Redis ja esta rodando...
"C:\Program Files\Redis\redis-cli.exe" ping >nul 2>&1
if not errorlevel 1 (
    echo Redis ja esta rodando!
    goto :end
)

echo [2] Tentando iniciar Redis como servico...
net start redis >nul 2>&1
if not errorlevel 1 (
    echo Redis iniciado como servico!
    goto :test
)

echo [3] Iniciando Redis diretamente...
start "Redis Server" "C:\Program Files\Redis\redis-server.exe" "C:\Program Files\Redis\redis.windows.conf"
timeout /t 3 /nobreak >nul

:test
echo [4] Testando Redis...
"C:\Program Files\Redis\redis-cli.exe" ping
if errorlevel 1 (
    echo ERRO: Redis nao responde
) else (
    echo Redis OK!
)

:end
pause