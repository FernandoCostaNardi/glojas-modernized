@echo off
echo ========================================
echo    CONFIGURAÇÃO INICIAL VPS - MULTI JAVA
echo ========================================
echo.

echo ATENCAO: Execute este script apenas na primeira instalacao!
echo Este script instalara Java 8 E Java 17 na VPS
echo.
set /p confirm="Continuar com configuracao inicial? (s/n): "
if /i not "%confirm%"=="s" (
    echo Configuracao cancelada.
    pause
    exit /b 0
)

echo.
echo [1/10] Atualizando sistema...
call ssh-prod.bat "sudo apt update && sudo apt upgrade -y"

echo.
echo [2/10] Instalando Java 8...
call ssh-prod.bat "sudo apt install openjdk-8-jdk -y"

echo.
echo [3/10] Instalando Java 17...
call ssh-prod.bat "sudo apt install openjdk-17-jdk -y"

echo.
echo [4/10] Configurando alternatives para Java...
call ssh-prod.bat "sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-8-openjdk-amd64/bin/java 1"
call ssh-prod.bat "sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 2"

echo.  
echo [5/10] Instalando PostgreSQL...
call ssh-prod.bat "sudo apt install postgresql postgresql-contrib -y"

echo.
echo [6/10] Instalando Node.js...
call ssh-prod.bat "curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - && sudo apt install -y nodejs"

echo.
echo [7/10] Instalando Nginx...
call ssh-prod.bat "sudo apt install nginx -y"

echo.
echo [8/10] Instalando Maven...
call ssh-prod.bat "sudo apt install maven -y"

echo.
echo [9/10] Instalando PM2...
call ssh-prod.bat "sudo npm install -g pm2"

echo.
echo [10/10] Criando estrutura de diretorios...
call ssh-prod.bat "sudo mkdir -p /opt/glojas-modernized/{logs,backups,scripts} && sudo chown -R glojas:glojas /opt/glojas-modernized"

echo.
echo Verificando instalacao das versoes do Java...
call ssh-prod.bat "/usr/lib/jvm/java-8-openjdk-amd64/bin/java -version"
echo.
call ssh-prod.bat "/usr/lib/jvm/java-17-openjdk-amd64/bin/java -version"

echo.
echo ========================================
echo    CONFIGURAÇÃO MULTI-JAVA CONCLUÍDA!
echo ========================================
echo.
echo Versoes instaladas:
echo - Java 8: /usr/lib/jvm/java-8-openjdk-amd64/bin/java
echo - Java 17: /usr/lib/jvm/java-17-openjdk-amd64/bin/java
echo.
echo Proximos passos:
echo 1. Configure o PostgreSQL (usuario e banco)
echo 2. Configure o Nginx  
echo 3. Copie o ecosystem.config.js atualizado
echo 4. Faça o primeiro deploy
echo.
pause
