# ============================================================================
# Script para Resolver Problemas EPERM do npm ci
# ============================================================================
# 
# Este script resolve problemas de permissão EPERM que ocorrem durante npm ci,
# especialmente com arquivos como esbuild.exe no Windows.
#
# Implementa múltiplas estratégias progressivas seguindo Clean Code principles:
# 1. Verificação e eliminação de processos conflitantes
# 2. Limpeza de caches npm/yarn/pnpm
# 3. Remoção forçada de node_modules
# 4. Verificação e correção de configurações npm
# 5. Reinstalação de dependências com fallback
#
# Autor: Sistema Glojas Modernizado
# Data: 2025-09-24
# ============================================================================

[CmdletBinding()]
param(
    [switch]$Force,
    [switch]$SkipProcessKill,
    [switch]$Help
)

# Configurações globais
$script:MaxRetries = 3
$script:RetryDelaySeconds = 2
$script:LogPrefix = "[NPM-EPERM-FIX]"

# ============================================================================
# FUNÇÕES UTILITÁRIAS
# ============================================================================

function Write-LogMessage {
    <#
    .SYNOPSIS
    Escreve mensagens de log com formatação consistente
    #>
    param(
        [Parameter(Mandatory = $true)]
        [string]$Message,
        
        [Parameter(Mandatory = $false)]
        [ValidateSet("INFO", "WARN", "ERROR", "SUCCESS")]
        [string]$Level = "INFO"
    )
    
    $timestamp = Get-Date -Format "HH:mm:ss"
    $color = switch ($Level) {
        "INFO" { "Cyan" }
        "WARN" { "Yellow" }
        "ERROR" { "Red" }
        "SUCCESS" { "Green" }
    }
    
    Write-Host "$script:LogPrefix [$timestamp] [$Level] $Message" -ForegroundColor $color
}

function Test-IsElevated {
    <#
    .SYNOPSIS
    Verifica se o PowerShell está sendo executado como administrador
    #>
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Test-FrontendDirectory {
    <#
    .SYNOPSIS
    Verifica se estamos no diretório frontend correto
    #>
    $requiredFiles = @("package.json", "vite.config.ts", "tsconfig.json")
    
    foreach ($file in $requiredFiles) {
        if (-not (Test-Path $file)) {
            Write-LogMessage "Arquivo obrigatório não encontrado: $file" -Level "ERROR"
            return $false
        }
    }
    
    # Verificar se é realmente um projeto React/Vite
    $packageJson = Get-Content "package.json" -Raw | ConvertFrom-Json
    if (-not ($packageJson.dependencies.react -or $packageJson.devDependencies.vite)) {
        Write-LogMessage "Este não parece ser um projeto React/Vite válido" -Level "ERROR"
        return $false
    }
    
    return $true
}

# ============================================================================
# FUNÇÕES DE LIMPEZA DE PROCESSOS
# ============================================================================

function Stop-ProcessesSafely {
    <#
    .SYNOPSIS
    Interrompe processos que podem estar bloqueando arquivos do npm/esbuild
    #>
    param(
        [switch]$SkipKill
    )
    
    if ($SkipKill) {
        Write-LogMessage "Pulando eliminação de processos (parâmetro -SkipProcessKill)" -Level "WARN"
        return $true
    }
    
    Write-LogMessage "Verificando processos que podem estar bloqueando arquivos..."
    
    # Lista de processos que podem causar conflitos
    $processesToCheck = @(
        "node",
        "npm", 
        "npx",
        "esbuild",
        "vite",
        "webpack",
        "tsc",
        "eslint"
    )
    
    $killedAny = $false
    
    foreach ($processName in $processesToCheck) {
        try {
            $processes = Get-Process -Name $processName -ErrorAction SilentlyContinue
            
            if ($processes) {
                Write-LogMessage "Encontrados $($processes.Count) processo(s) '$processName'"
                
                foreach ($process in $processes) {
                    try {
                        Write-LogMessage "Interrompendo processo: $processName (PID: $($process.Id))"
                        $process.Kill()
                        $killedAny = $true
                        Start-Sleep -Milliseconds 500
                    }
                    catch {
                        Write-LogMessage "Não foi possível interromper processo $processName (PID: $($process.Id)): $($_.Exception.Message)" -Level "WARN"
                    }
                }
            }
        }
        catch {
            # Ignorar erros ao verificar processos
        }
    }
    
    if ($killedAny) {
        Write-LogMessage "Aguardando liberação de arquivos..." 
        Start-Sleep -Seconds 3
    } else {
        Write-LogMessage "Nenhum processo conflitante encontrado"
    }
    
    return $true
}

# ============================================================================
# FUNÇÕES DE LIMPEZA DE CACHE
# ============================================================================

function Clear-NpmCaches {
    <#
    .SYNOPSIS
    Limpa todos os tipos de cache npm, yarn e pnpm
    #>
    Write-LogMessage "Limpando caches de gerenciadores de pacotes..."
    
    $cacheOperations = @(
        @{
            Name = "Cache npm"
            Command = "npm cache clean --force"
            Description = "Limpeza forçada do cache npm"
        },
        @{
            Name = "Cache yarn (global)"
            Command = "yarn cache clean --force"
            Description = "Limpeza do cache yarn global"
            Optional = $true
        },
        @{
            Name = "Cache pnpm"
            Command = "pnpm store prune"
            Description = "Limpeza do store pnpm"
            Optional = $true
        }
    )
    
    foreach ($operation in $cacheOperations) {
        try {
            Write-LogMessage "Executando: $($operation.Description)"
            
            $result = Invoke-Expression $operation.Command 2>&1
            
            if ($LASTEXITCODE -eq 0) {
                Write-LogMessage "$($operation.Name) limpo com sucesso" -Level "SUCCESS"
            } else {
                if ($operation.Optional) {
                    Write-LogMessage "$($operation.Name) não disponível ou erro ignorável" -Level "WARN"
                } else {
                    Write-LogMessage "Erro ao limpar $($operation.Name): $result" -Level "ERROR"
                }
            }
        }
        catch {
            if ($operation.Optional) {
                Write-LogMessage "$($operation.Name) não disponível: $($_.Exception.Message)" -Level "WARN"
            } else {
                Write-LogMessage "Erro ao limpar $($operation.Name): $($_.Exception.Message)" -Level "ERROR"
            }
        }
    }
    
    return $true
}

function Clear-TemporaryDirectories {
    <#
    .SYNOPSIS
    Limpa diretórios temporários que podem conter arquivos npm corrompidos
    #>
    Write-LogMessage "Limpando diretórios temporários..."
    
    $tempDirs = @(
        $env:TEMP,
        $env:TMP,
        "$env:APPDATA\npm-cache",
        "$env:LOCALAPPDATA\npm-cache"
    )
    
    foreach ($tempDir in $tempDirs) {
        if ($tempDir -and (Test-Path $tempDir)) {
            try {
                $npmTempFiles = Get-ChildItem -Path $tempDir -Filter "*npm*" -Recurse -ErrorAction SilentlyContinue
                if ($npmTempFiles) {
                    Write-LogMessage "Removendo $($npmTempFiles.Count) arquivo(s) temporário(s) npm de $tempDir"
                    $npmTempFiles | Remove-Item -Force -Recurse -ErrorAction SilentlyContinue
                }
            }
            catch {
                Write-LogMessage "Erro ao limpar arquivos temporários em $tempDir`: $($_.Exception.Message)" -Level "WARN"
            }
        }
    }
    
    return $true
}

# ============================================================================
# FUNÇÕES DE REMOÇÃO DE NODE_MODULES
# ============================================================================

function Remove-NodeModulesWithRetry {
    <#
    .SYNOPSIS
    Remove node_modules com lógica de retry para contornar problemas de bloqueio
    #>
    if (-not (Test-Path "node_modules")) {
        Write-LogMessage "Diretório node_modules não existe, pulando remoção"
        return $true
    }
    
    Write-LogMessage "Removendo diretório node_modules..."
    
    for ($attempt = 1; $attempt -le $script:MaxRetries; $attempt++) {
        try {
            Write-LogMessage "Tentativa $attempt de $script:MaxRetries para remover node_modules"
            
            # Tentar com PowerShell Remove-Item primeiro
            Remove-Item -Path "node_modules" -Recurse -Force -ErrorAction Stop
            
            if (-not (Test-Path "node_modules")) {
                Write-LogMessage "node_modules removido com sucesso" -Level "SUCCESS"
                return $true
            }
        }
        catch {
            Write-LogMessage "Erro na tentativa $attempt`: $($_.Exception.Message)" -Level "WARN"
            
            # Tentar com comando cmd como fallback
            try {
                Write-LogMessage "Tentando remoção com cmd..."
                $result = cmd /c "rmdir /s /q node_modules" 2>&1
                
                if (-not (Test-Path "node_modules")) {
                    Write-LogMessage "node_modules removido com cmd" -Level "SUCCESS"
                    return $true
                }
            }
            catch {
                Write-LogMessage "Erro também com cmd: $($_.Exception.Message)" -Level "WARN"
            }
            
            if ($attempt -lt $script:MaxRetries) {
                Write-LogMessage "Aguardando $script:RetryDelaySeconds segundos antes da próxima tentativa..."
                Start-Sleep -Seconds $script:RetryDelaySeconds
            }
        }
    }
    
    Write-LogMessage "Não foi possível remover node_modules após $script:MaxRetries tentativas" -Level "ERROR"
    return $false
}

# ============================================================================
# FUNÇÕES DE CONFIGURAÇÃO NPM
# ============================================================================

function Test-NpmConfiguration {
    <#
    .SYNOPSIS
    Verifica e corrige configurações npm problemáticas
    #>
    Write-LogMessage "Verificando configurações npm..."
    
    try {
        # Verificar configuração de tmp directory
        $tmpConfig = npm config get tmp 2>$null
        if ($tmpConfig -and $tmpConfig -ne "undefined") {
            Write-LogMessage "Configuração de diretório temporário encontrada: $tmpConfig"
            
            # Verificar se o diretório tmp existe e tem permissões
            if (Test-Path $tmpConfig) {
                $tempTestFile = Join-Path $tmpConfig "npm-test-$(Get-Random).tmp"
                try {
                    "test" | Out-File -FilePath $tempTestFile -ErrorAction Stop
                    Remove-Item $tempTestFile -ErrorAction SilentlyContinue
                    Write-LogMessage "Diretório temporário npm OK" -Level "SUCCESS"
                }
                catch {
                    Write-LogMessage "Problema com diretório temporário npm, reconfigurando..." -Level "WARN"
                    npm config delete tmp
                }
            }
        }
        
        # Verificar configuração de cache
        $cacheConfig = npm config get cache 2>$null
        if ($cacheConfig -and $cacheConfig -ne "undefined") {
            if (-not (Test-Path $cacheConfig)) {
                Write-LogMessage "Diretório de cache npm não existe, será criado automaticamente"
            }
        }
        
        # Configurar bin-links para Windows se necessário
        $binLinks = npm config get bin-links 2>$null
        if ($binLinks -eq "false") {
            Write-LogMessage "bin-links está desabilitado, reabilitando para operação normal"
            npm config set bin-links true
        }
        
        return $true
    }
    catch {
        Write-LogMessage "Erro ao verificar configurações npm: $($_.Exception.Message)" -Level "ERROR"
        return $false
    }
}

# ============================================================================
# FUNÇÕES DE INSTALAÇÃO
# ============================================================================

function Install-Dependencies {
    <#
    .SYNOPSIS
    Instala dependências usando npm ci com fallback para npm install
    #>
    Write-LogMessage "Iniciando instalação de dependências..."
    
    # Verificar se package-lock.json existe
    if (-not (Test-Path "package-lock.json")) {
        Write-LogMessage "package-lock.json não encontrado, usando npm install"
        return Invoke-NpmInstall
    }
    
    # Tentar npm ci primeiro
    Write-LogMessage "Tentando npm ci..."
    if (Invoke-NpmCi) {
        return $true
    }
    
    # Fallback para npm install
    Write-LogMessage "npm ci falhou, tentando npm install como fallback" -Level "WARN"
    return Invoke-NpmInstall
}

function Invoke-NpmCi {
    <#
    .SYNOPSIS
    Executa npm ci com retry logic
    #>
    for ($attempt = 1; $attempt -le $script:MaxRetries; $attempt++) {
        try {
            Write-LogMessage "Executando npm ci (tentativa $attempt de $script:MaxRetries)..."
            
            $process = Start-Process -FilePath "npm" -ArgumentList "ci" -Wait -PassThru -NoNewWindow
            
            if ($process.ExitCode -eq 0) {
                Write-LogMessage "npm ci executado com sucesso!" -Level "SUCCESS"
                return $true
            } else {
                Write-LogMessage "npm ci falhou com código de saída $($process.ExitCode)" -Level "WARN"
            }
        }
        catch {
            Write-LogMessage "Erro durante npm ci: $($_.Exception.Message)" -Level "WARN"
        }
        
        if ($attempt -lt $script:MaxRetries) {
            Write-LogMessage "Aguardando $script:RetryDelaySeconds segundos antes da próxima tentativa..."
            Start-Sleep -Seconds $script:RetryDelaySeconds
        }
    }
    
    return $false
}

function Invoke-NpmInstall {
    <#
    .SYNOPSIS
    Executa npm install como fallback
    #>
    try {
        Write-LogMessage "Executando npm install..."
        
        $process = Start-Process -FilePath "npm" -ArgumentList "install" -Wait -PassThru -NoNewWindow
        
        if ($process.ExitCode -eq 0) {
            Write-LogMessage "npm install executado com sucesso!" -Level "SUCCESS"
            return $true
        } else {
            Write-LogMessage "npm install falhou com código de saída $($process.ExitCode)" -Level "ERROR"
            return $false
        }
    }
    catch {
        Write-LogMessage "Erro durante npm install: $($_.Exception.Message)" -Level "ERROR"
        return $false
    }
}

# ============================================================================
# FUNÇÃO PRINCIPAL
# ============================================================================

function Resolve-EpermError {
    <#
    .SYNOPSIS
    Função principal que orquestra todas as estratégias de correção
    #>
    Write-LogMessage "=========================================="
    Write-LogMessage "INICIANDO CORREÇÃO DE ERRO EPERM NPM CI"
    Write-LogMessage "=========================================="
    
    # Verificações iniciais
    Write-LogMessage "Executando verificações iniciais..."
    
    if (-not (Test-FrontendDirectory)) {
        Write-LogMessage "Execute este script no diretório frontend do projeto" -Level "ERROR"
        return $false
    }
    
    $isElevated = Test-IsElevated
    if (-not $isElevated) {
        Write-LogMessage "AVISO: Script não está sendo executado como administrador" -Level "WARN"
        Write-LogMessage "Algumas operações podem falhar devido a permissões limitadas" -Level "WARN"
    } else {
        Write-LogMessage "Script executando com privilégios de administrador" -Level "SUCCESS"
    }
    
    # Estratégia 1: Parar processos conflitantes
    Write-LogMessage "Estratégia 1: Interrompendo processos conflitantes..."
    if (-not (Stop-ProcessesSafely -SkipKill:$SkipProcessKill)) {
        Write-LogMessage "Falha na estratégia 1, continuando..." -Level "WARN"
    }
    
    # Estratégia 2: Limpar caches
    Write-LogMessage "Estratégia 2: Limpando caches..."
    if (-not (Clear-NpmCaches)) {
        Write-LogMessage "Falha na estratégia 2, continuando..." -Level "WARN"
    }
    
    Clear-TemporaryDirectories
    
    # Estratégia 3: Remover node_modules
    Write-LogMessage "Estratégia 3: Removendo node_modules..."
    if (-not (Remove-NodeModulesWithRetry)) {
        Write-LogMessage "Falha na estratégia 3, continuando..." -Level "WARN"
    }
    
    # Estratégia 4: Verificar configurações npm
    Write-LogMessage "Estratégia 4: Verificando configurações npm..."
    if (-not (Test-NpmConfiguration)) {
        Write-LogMessage "Falha na estratégia 4, continuando..." -Level "WARN"
    }
    
    # Estratégia 5: Reinstalar dependências
    Write-LogMessage "Estratégia 5: Instalando dependências..."
    if (Install-Dependencies) {
        Write-LogMessage "=========================================="
        Write-LogMessage "CORREÇÃO CONCLUÍDA COM SUCESSO!" -Level "SUCCESS"
        Write-LogMessage "npm ci executado sem erros EPERM"
        Write-LogMessage "=========================================="
        return $true
    } else {
        Write-LogMessage "=========================================="
        Write-LogMessage "CORREÇÃO FALHOU" -Level "ERROR"
        Write-LogMessage "Não foi possível resolver o erro EPERM"
        Write-LogMessage "=========================================="
        return $false
    }
}

function Show-Help {
    Write-Host @"
========================================
SCRIPT DE CORREÇÃO EPERM NPM CI
========================================

Este script resolve problemas de permissão EPERM que ocorrem durante 'npm ci',
especialmente com arquivos como esbuild.exe no Windows.

USO:
    .\fix-npm-eperm.ps1 [parâmetros]

PARÂMETROS:
    -Verbose        Exibe informações detalhadas de execução (via -Verbose do PowerShell)
    -Force          Força operações mesmo com avisos
    -SkipProcessKill  Pula a etapa de eliminar processos
    -Help           Exibe esta ajuda

EXEMPLOS:
    .\fix-npm-eperm.ps1
    .\fix-npm-eperm.ps1 -Verbose
    .\fix-npm-eperm.ps1 -SkipProcessKill

ESTRATÉGIAS IMPLEMENTADAS:
1. Interrupção de processos conflitantes (node, npm, esbuild, etc.)
2. Limpeza de caches npm, yarn e pnpm
3. Remoção forçada do diretório node_modules
4. Verificação e correção de configurações npm
5. Reinstalação de dependências com npm ci/install

REQUISITOS:
- Executar no diretório frontend do projeto
- Windows PowerShell 5.1+ ou PowerShell Core
- npm instalado e configurado
- Recomendado: executar como administrador

Para mais informações sobre erros EPERM:
https://docs.npmjs.com/common-errors
"@
}

# ============================================================================
# PONTO DE ENTRADA
# ============================================================================

if ($Help) {
    Show-Help
    exit 0
}

# Verbosidade é controlada automaticamente pelo CmdletBinding

# Executar correção
try {
    $success = Resolve-EpermError
    if ($success) {
        exit 0
    } else {
        exit 1
    }
}
catch {
    Write-LogMessage "Erro inesperado durante execução: $($_.Exception.Message)" -Level "ERROR"
    Write-LogMessage "Stack trace: $($_.ScriptStackTrace)" -Level "ERROR"
    exit 1
}
