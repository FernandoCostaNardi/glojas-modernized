@echo off
echo ========================================
echo    VERIFICAR VERSÕES JAVA - PRODUÇÃO
echo ========================================
echo.

echo Verificando versoes do Java instaladas na VPS...
echo.

echo === Java 8 ===
call ssh-prod.bat "/usr/lib/jvm/java-8-openjdk-amd64/bin/java -version"

echo.
echo === Java 17 ===
call ssh-prod.bat "/usr/lib/jvm/java-17-openjdk-amd64/bin/java -version"

echo.
echo === Maven ===
call ssh-prod.bat "mvn -version"

echo.
echo === Processos Java Rodando ===
call ssh-prod.bat "ps aux | grep java | grep -v grep"

echo.
echo === Diretórios JVM ===
call ssh-prod.bat "ls -la /usr/lib/jvm/ | grep openjdk"

echo.
pause
