# Script para iniciar Legacy API com Java 8
Write-Host "========================================" -ForegroundColor Green
Write-Host "INICIANDO LEGACY API COM JAVA 8" -ForegroundColor Green  
Write-Host "========================================" -ForegroundColor Green

# Configurar Java 8
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_211"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Java configurado:" -ForegroundColor Yellow
java -version

# Entrar no diretório legacy
Set-Location legacy-api

Write-Host "`nLimpando projeto..." -ForegroundColor Yellow
mvn clean

Write-Host "`nIniciando aplicação com configurações TLS para SQL Server legado..." -ForegroundColor Yellow
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.tls.client.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"
