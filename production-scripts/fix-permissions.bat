@echo off
echo ========================================
echo    CORRIGIR PERMISSOES - PRODUCAO
echo ========================================
echo.

echo Este script corrige o erro: "permission denied"
echo.

echo [1/8] Parando processos PM2...
call ssh-prod.bat "pm2 stop all && pm2 delete all"
echo.

echo [2/8] Corrigindo proprietario de todos os arquivos...
call ssh-prod.bat "sudo chown -R glojas:glojas /opt/glojas-modernized"
echo.

echo [3/8] Ajustando permissoes dos diretorios...
call ssh-prod.bat "sudo chmod -R 755 /opt/glojas-modernized"
echo.

echo [4/8] Garantindo que diretorio logs existe...
call ssh-prod.bat "mkdir -p /opt/glojas-modernized/logs"
echo.

echo [5/8] Removendo logs antigos com problema...
call ssh-prod.bat "rm -f /opt/glojas-modernized/logs/*.log"
echo.

echo [6/8] Verificando permissoes corrigidas...
call ssh-prod.bat "ls -la /opt/glojas-modernized/"
echo.

echo [7/8] Iniciando PM2 novamente...
call ssh-prod.bat "cd /opt/glojas-modernized && pm2 start ecosystem.config.js"
echo.

echo [8/8] Salvando configuracao PM2...
call ssh-prod.bat "pm2 save"
echo.

echo ========================================
echo    VERIFICACAO FINAL
echo ========================================
echo.
call ssh-prod.bat "pm2 status"
echo.

echo Se mostrar "online" para ambas as APIs:
echo ✅ CORRIGIDO! Teste no navegador agora
echo.
echo Se ainda mostrar erro:
echo ❌ Execute os comandos manualmente via SSH
echo.
pause
