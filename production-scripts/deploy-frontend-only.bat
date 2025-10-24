@echo off
echo ========================================
echo    ENVIAR FRONTEND - PRODUCAO
echo ========================================
echo.

echo [1/4] Fazendo build do frontend localmente...
cd ..
cd frontend
call npm ci
if errorlevel 1 (
    echo ❌ Erro ao instalar dependencias
    pause
    exit /b 1
)

call npm run build
if errorlevel 1 (
    echo ❌ Erro ao fazer build do frontend
    pause
    exit /b 1
)

cd ..\production-scripts
echo ✅ Build concluido!
echo.

echo [2/4] Enviando para VPS...
scp -r ..\frontend\dist glojas@%VPS_HOST%:/tmp/
echo.

echo [3/4] Instalando na VPS...
call ssh-prod.bat "sudo rm -rf /opt/glojas-modernized/frontend/dist"
call ssh-prod.bat "sudo mv /tmp/dist /opt/glojas-modernized/frontend/"
call ssh-prod.bat "sudo chown -R glojas:glojas /opt/glojas-modernized/frontend"
call ssh-prod.bat "sudo chmod -R 755 /opt/glojas-modernized/frontend/dist"
echo.

echo [4/4] Recarregando Nginx...
call ssh-prod.bat "sudo nginx -t && sudo systemctl reload nginx"
echo.

echo ========================================
echo    FRONTEND ENVIADO COM SUCESSO!
echo ========================================
echo.
echo Teste agora em: https://gestaosmarteletron.com.br
echo.
pause
