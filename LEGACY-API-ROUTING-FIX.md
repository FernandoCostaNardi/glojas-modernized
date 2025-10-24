# Correção do Roteamento Legacy API

## Problema Identificado

A rota `/api/stores-legacy` estava retornando HTML do frontend em vez de dados da API devido a problemas de configuração no Nginx e Business API.

## Correções Aplicadas

### 1. Business API - SecurityConfig.java
**Arquivo:** `business-api/src/main/java/com/sysconard/business/config/SecurityConfig.java`

**Mudança:** Adicionada exceção para `/stores-legacy/**` no Spring Security
```java
.requestMatchers("/stores-legacy/**").permitAll() // Liberar acesso para stores-legacy
```

### 2. Business API - StoreLegacyController.java
**Arquivo:** `business-api/src/main/java/com/sysconard/business/controller/store/StoreLegacyController.java`

**Mudança:** Renomeado mapeamento de `/stores-legacy` para `/legacy/stores`
```java
@RequestMapping("/legacy/stores")  // Era: @RequestMapping("/stores-legacy")
```

### 3. Business API - WebClientConfig.java
**Arquivo:** `business-api/src/main/java/com/sysconard/business/config/WebClientConfig.java`

**Mudança:** Corrigida base URL do WebClient para Legacy API
```java
String correctBaseUrl = "http://localhost:8087";  // Era: "http://localhost:8087/api/legacy"
```

### 4. Nginx - Configuração
**Arquivo:** `/etc/nginx/sites-available/glojas`

**Mudanças:**
- Adicionado `stores-legacy` e `legacy` no regex de rotas permitidas
- Removida rota direta para Legacy API (porta 8087)
- Todas as rotas `/api/legacy/*` agora passam pela Business API (porta 8089)

## Arquitetura Corrigida

```
Cliente → Nginx (443) → Business API (8089) → Legacy API (8087)
                     ↓
                  Frontend (servido estaticamente)
```

**Rotas:**
- `/api/business/*` → Business API (porta 8089)
- `/api/legacy/*` → Business API (porta 8089) → Legacy API (porta 8087)
- `/*` → Frontend (arquivos estáticos)

## Scripts de Deploy

### 1. fix-nginx-legacy-routing.sh
Script bash para aplicar correções no Nginx na VPS.

### 2. fix-nginx-legacy-routing.ps1
Script PowerShell para aplicar correções no Nginx na VPS.

### 3. validate-legacy-api-fix.sh
Script para validar se as correções funcionaram.

## Como Aplicar as Correções

### 1. Na VPS (como root):
```bash
# Aplicar correções do Nginx
sudo ./fix-nginx-legacy-routing.sh

# Ou usando PowerShell:
sudo pwsh ./fix-nginx-legacy-routing.ps1
```

### 2. Rebuild e Deploy da Business API:
```bash
# Na máquina de desenvolvimento
cd business-api
mvn clean package
# Deploy para produção
```

### 3. Validar correções:
```bash
# Na VPS
./validate-legacy-api-fix.sh
```

## URLs de Teste

### Antes (não funcionava):
- `https://gestaosmarteletron.com.br/api/stores-legacy` → Retornava HTML do frontend

### Depois (funcionando):
- `https://gestaosmarteletron.com.br/api/legacy/stores` → Retorna dados JSON da Legacy API

## Frontend - Atualização Necessária

O frontend precisa ser atualizado para usar a nova rota:

```typescript
// Antes
const response = await fetch('/api/stores-legacy');

// Depois
const response = await fetch('/api/legacy/stores');
```

## Logs para Monitoramento

### Business API:
```bash
pm2 logs business-api --lines 50
```

### Nginx:
```bash
tail -f /var/log/nginx/glojas-access.log | grep legacy
tail -f /var/log/nginx/glojas-error.log
```

## Validação de Sucesso

A correção foi bem-sucedida se:

1. ✅ `curl https://gestaosmarteletron.com.br/api/legacy/stores` retorna JSON válido
2. ✅ Não retorna HTML do frontend
3. ✅ Logs da Business API mostram comunicação com Legacy API
4. ✅ Nginx logs mostram proxy correto para porta 8089

## Rollback (se necessário)

Se algo der errado, restaure o backup do Nginx:
```bash
cp /etc/nginx/sites-available/glojas.backup.* /etc/nginx/sites-available/glojas
systemctl reload nginx
```

---

**Data da Correção:** $(date)  
**Responsável:** Equipe de Desenvolvimento  
**Status:** ✅ Implementado
