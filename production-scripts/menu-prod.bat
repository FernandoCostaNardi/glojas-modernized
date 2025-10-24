@echo off
:menu
cls
echo ========================================
echo     GLOJAS - GERENCIAMENTO PRODUÇÃO
echo         (Java 8 + Java 17)
echo ========================================
echo.
echo === OPERAÇÕES BÁSICAS ===
echo [1] Iniciar Servicos
echo [2] Parar Servicos  
echo [3] Reiniciar Servicos
echo [4] Status dos Servicos
echo [5] Ver Logs
echo.
echo === DEPLOY E BACKUP ===
echo [6] Fazer Backup
echo [7] Deploy Manual
echo [8] Atualizar via Git
echo.
echo === CONFIGURAÇÃO JAVA ===
echo [9] Verificar Versões Java (VPS)
echo [10] Configurar Java 17 Local
echo [11] Enviar Ecosystem Config
echo [12] Setup Multi-Java (VPS)
echo.
echo === OUTROS ===
echo [13] Monitoramento
echo [14] SSH Interativo
echo [0] Sair
echo.
set /p choice="Escolha uma opcao: "

if "%choice%"=="1" call start-prod.bat
if "%choice%"=="2" call stop-prod.bat  
if "%choice%"=="3" call restart-prod.bat
if "%choice%"=="4" call status-prod.bat
if "%choice%"=="5" call logs-prod.bat
if "%choice%"=="6" call backup-prod.bat
if "%choice%"=="7" call deploy-prod.bat
if "%choice%"=="8" call update-prod.bat  
if "%choice%"=="9" call check-java-versions.bat
if "%choice%"=="10" call config-java17-local.bat
if "%choice%"=="11" call send-ecosystem.bat
if "%choice%"=="12" call config-multi-java.bat
if "%choice%"=="13" call monitor-prod.bat
if "%choice%"=="14" call ssh-prod.bat
if "%choice%"=="0" exit

goto menu
