@echo off
echo ========================================
echo INICIANDO LEGACY API COM JAVA 8
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
echo Limpando e compilando...
mvn clean compile

echo.
echo Iniciando aplicacao com Java 8 e TLS legado na porta 8087...
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.tls.client.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dserver.port=8087"

pause
