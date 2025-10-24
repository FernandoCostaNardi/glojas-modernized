@echo off
echo ========================================
echo    CONFIGURAR JAVA 17 LOCAL - WINDOWS
echo ========================================
echo.

echo Este script ajuda a configurar Java 17 localmente para builds.
echo.

echo Verificando versao atual do Java...
java -version
echo.

echo === Opcoes de Configuracao ===
echo [1] Verificar se Java 17 esta instalado
echo [2] Baixar Java 17 (link oficial)  
echo [3] Configurar JAVA_HOME temporariamente
echo [4] Configurar JAVA_HOME permanentemente
echo [0] Sair
echo.

set /p choice="Escolha uma opcao: "

if "%choice%"=="1" (
    echo.
    echo Procurando instalacoes do Java...
    dir "C:\Program Files\Java\" 2>nul
    dir "C:\Program Files\Eclipse Adoptium\" 2>nul
    dir "C:\Program Files\Microsoft\" 2>nul | findstr openjdk
    echo.
    echo Se nao encontrou Java 17, escolha opcao 2 para baixar.
    
) else if "%choice%"=="2" (
    echo.
    echo Abrindo pagina de download do Java 17...
    start https://adoptium.net/download/
    echo.
    echo Baixe o Java 17 LTS (.msi) e instale.
    echo Depois execute este script novamente.
    
) else if "%choice%"=="3" (
    echo.
    set /p java_path="Digite o caminho do Java 17 (ex: C:\Program Files\Eclipse Adoptium\jdk-17.0.8.101-hotspot): "
    if exist "%java_path%\bin\java.exe" (
        set JAVA_HOME=%java_path%
        set PATH=%java_path%\bin;%PATH%
        echo.
        echo ✅ JAVA_HOME configurado temporariamente para: %java_path%
        echo.
        echo Verificando versao...
        java -version
        echo.
        echo NOTA: Esta configuracao e temporaria. Para permanente, use opcao 4.
    ) else (
        echo ❌ Caminho invalido ou Java nao encontrado.
    )
    
) else if "%choice%"=="4" (
    echo.
    set /p java_path="Digite o caminho do Java 17 (ex: C:\Program Files\Eclipse Adoptium\jdk-17.0.8.101-hotspot): "
    if exist "%java_path%\bin\java.exe" (
        echo.
        echo Configurando JAVA_HOME permanentemente...
        setx JAVA_HOME "%java_path%"
        setx PATH "%java_path%\bin;%PATH%"
        echo.
        echo ✅ JAVA_HOME configurado permanentemente!
        echo.
        echo IMPORTANTE: Feche e reabra o terminal para aplicar as mudancas.
        echo Ou execute: set JAVA_HOME=%java_path%
        echo            set PATH=%java_path%\bin;%PATH%
    ) else (
        echo ❌ Caminho invalido ou Java nao encontrado.
    )
    
) else if "%choice%"=="0" (
    exit /b 0
) else (
    echo Opcao invalida.
)

echo.
pause
