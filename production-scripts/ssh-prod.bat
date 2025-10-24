@echo off
REM Script auxiliar para executar comandos SSH na VPS

REM ============================================
REM CONFIGURE AQUI OS DADOS DA SUA VPS
REM ============================================
set VPS_HOST=212.85.12.228
set VPS_USER=glojas
set VPS_PORT=22

REM Se não passou comando como parâmetro, abrir shell interativo
if "%~1"=="" (
    echo Conectando ao servidor de producao...
    ssh -p %VPS_PORT% %VPS_USER%@%VPS_HOST%
) else (
    REM Executar comando específico
    ssh -p %VPS_PORT% %VPS_USER%@%VPS_HOST% %*
)
