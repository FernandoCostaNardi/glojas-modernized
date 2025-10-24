@echo off
echo ========================================
echo INICIANDO LEGACY API NA PORTA 8087
echo ========================================

echo Configurando Java 8...
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_211
set PATH=%JAVA_HOME%\bin;%PATH%

echo Verificando versao do Java:
java -version

echo.
echo Verificando versao do Maven:
mvn -v

echo.
echo Navegando para o diretorio legacy-api...
cd /d "%~dp0..\legacy-api"

echo.
echo Limpando e compilando...
mvn clean compile

echo.
echo Iniciando Legacy API na porta 8087...
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.tls.client.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dserver.port=8087"

pause
