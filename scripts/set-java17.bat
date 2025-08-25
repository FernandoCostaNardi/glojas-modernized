@echo off
echo Configurando Java 17 para esta sessao...
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.2
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME configurado para: %JAVA_HOME%
java -version
echo.
echo Java 17 ativo! Para projetos modernos.
echo.
