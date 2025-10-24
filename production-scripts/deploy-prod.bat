@echo off
echo ========================================
echo    DEPLOY MANUAL - GLOJAS PRODUÇÃO
echo ========================================
echo.

set /p confirm="Deseja fazer deploy para producao? (s/n): "
if /i not "%confirm%"=="s" (
    echo Deploy cancelado.
    pause
    exit /b 0
)

echo.
echo [1/5] Fazendo backup do banco remoto...
call backup-prod.bat

echo.
echo [2/5] Parando servicos na producao...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 stop all"

echo.
echo [3/5] Fazendo build local...
echo.
echo ATENCAO: Certificue-se de ter Java 17 configurado localmente!
echo Se houver erro, instale Java 17 e configure JAVA_HOME
echo.

cd ..

echo --- Building Business API (precisa Java 17) ---
cd business-api
java -version
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ❌ Erro no build do Business API (verifique se Java 17 está ativo)
    echo Dica: Configure JAVA_HOME para Java 17 antes de executar este script
    pause
    exit /b 1
)

echo --- Building Legacy API (compatível com Java 8+) ---
cd ..\legacy-api
call mvn clean package -DskipTests  
if errorlevel 1 (
    echo ❌ Erro no build do Legacy API
    pause
    exit /b 1
)

echo --- Building Frontend ---
cd ..\frontend
call npm ci
call npm run build
if errorlevel 1 (
    echo ❌ Erro no build do Frontend
    pause
    exit /b 1
)
cd ..\production-scripts

echo.
echo [4/5] Enviando arquivos para VPS...
scp ..\business-api\target\business-api-1.0.0.jar glojas@%VPS_HOST%:/tmp/
scp ..\legacy-api\target\legacy-api-1.0.0.jar glojas@%VPS_HOST%:/tmp/
scp -r ..\frontend\dist glojas@%VPS_HOST%:/tmp/

echo.
echo [5/5] Instalando na producao...
call ssh-prod.bat "sudo mv /tmp/business-api-1.0.0.jar /opt/glojas-modernized/business-api/target/"
call ssh-prod.bat "sudo mv /tmp/legacy-api-1.0.0.jar /opt/glojas-modernized/legacy-api/target/"
call ssh-prod.bat "sudo rm -rf /opt/glojas-modernized/frontend/dist && sudo mv /tmp/dist /opt/glojas-modernized/frontend/"
call ssh-prod.bat "sudo chown -R glojas:glojas /opt/glojas-modernized"

echo.
echo Reiniciando servicos...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 start ecosystem.config.js"

echo.
echo Aguardando servicos subirem...
timeout /t 30 /nobreak >nul

echo.
echo Verificando saude dos servicos...
call status-prod.bat

echo.
echo ==============================
echo   DEPLOY CONCLUIDO COM SUCESSO!
echo ==============================
pause
