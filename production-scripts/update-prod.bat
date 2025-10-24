@echo off
echo ========================================
echo    ATUALIZAÇÃO VIA GIT - PRODUÇÃO
echo ========================================
echo.

set /p confirm="Deseja atualizar via Git? (s/n): "
if /i not "%confirm%"=="s" (
    echo Atualizacao cancelada.
    pause
    exit /b 0
)

echo.
echo Fazendo backup antes da atualizacao...
call backup-prod.bat

echo.
echo Parando servicos...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 stop all"

echo.
echo Atualizando codigo do Git...
call ssh-prod.bat "cd /opt/glojas-modernized && git pull origin main"

echo.
echo Fazendo build das aplicacoes...
call ssh-prod.bat "cd /opt/glojas-modernized/business-api && mvn clean package -DskipTests"
call ssh-prod.bat "cd /opt/glojas-modernized/legacy-api && mvn clean package -DskipTests"
call ssh-prod.bat "cd /opt/glojas-modernized/frontend && npm ci && npm run build"

echo.
echo Reiniciando servicos...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 start ecosystem.config.js"

echo.
echo Aguardando servicos subirem...
timeout /t 30 /nobreak >nul

echo.
echo Verificando status final...
call status-prod.bat

echo.
echo ========================================
echo    ATUALIZAÇÃO CONCLUÍDA COM SUCESSO!
echo ========================================
pause
