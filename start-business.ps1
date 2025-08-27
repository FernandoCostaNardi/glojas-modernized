# Script para iniciar Business API com Java 17
Write-Host "========================================" -ForegroundColor Blue
Write-Host "INICIANDO BUSINESS API COM JAVA 17" -ForegroundColor Blue
Write-Host "========================================" -ForegroundColor Blue

# Configurar Java 17
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.2"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Java configurado:" -ForegroundColor Yellow
java -version

# Entrar no diretório business
Set-Location business-api

Write-Host "`nLimpando projeto..." -ForegroundColor Yellow
mvn clean

Write-Host "`nIniciando aplicação..." -ForegroundColor Yellow
mvn spring-boot:run
