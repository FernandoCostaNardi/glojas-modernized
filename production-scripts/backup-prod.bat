@echo off
echo ========================================
echo    BACKUP MANUAL - PRODUÇÃO
echo ========================================
echo.

echo Iniciando backup do banco de dados...
call ssh-prod.bat "cd /opt/glojas-modernized && pg_dump -U glojas_user -h localhost glojas_business > backups/backup_$(date +%Y%m%d_%H%M%S).sql"

echo.
echo Listando backups existentes...
call ssh-prod.bat "ls -la /opt/glojas-modernized/backups/"

echo.
echo ========================================
echo    BACKUP CONCLUÍDO COM SUCESSO!
echo ========================================
pause
