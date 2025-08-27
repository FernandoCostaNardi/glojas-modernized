@echo off
echo Configurando Java 8 para Legacy API (compatibilidade TLS)...
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_211
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME configurado para: %JAVA_HOME%
java -version
echo.
echo Java 8 ativo! Compativel com TLS legado. Agora voce pode executar:
echo mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.tls.client.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"
echo.
