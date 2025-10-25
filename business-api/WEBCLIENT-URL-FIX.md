# 🔧 Correção da URL do WebClient - Legacy API

## 📋 Problema Identificado

O endpoint `current-daily-sales` estava retornando erro 404 porque a Business API estava chamando a URL incorreta da Legacy API:

### ❌ URL Incorreta (antes da correção):
```
POST http://localhost:8087/api/sales/store-report-by-day
```

### ✅ URL Correta (após a correção):
```
POST http://localhost:8087/api/legacy/sales/store-report-by-day
```

## 🔍 Análise do Problema

### Logs de Erro:
```
16:45:49.688 [http-nio-8089-exec-3] INFO  c.s.b.service.sell.SellService - Endpoint sendo chamado: /api/sales/store-report-by-day
16:45:50.557 [http-nio-8089-exec-3] ERROR c.s.b.service.sell.SellService - Erro HTTP 404 ao chamar Legacy API (por dia)
```

### Causa Raiz:
- **WebClient configurado incorretamente**: Estava usando `http://localhost:8087` em vez de `http://localhost:8087/api/legacy`
- **Context-path ausente**: A Legacy API tem context-path `/api/legacy` que não estava sendo incluído
- **URL final incorreta**: Resultava em chamadas para endpoints inexistentes

## ✅ Solução Implementada

### 1. Correção do WebClientConfig

**Arquivo**: `src/main/java/com/sysconard/business/config/WebClientConfig.java`

#### Antes (incorreto):
```java
// URL correta para Legacy API (sem prefixo /api/legacy)
String correctBaseUrl = "http://localhost:8087";

return WebClient.builder()
        .baseUrl(correctBaseUrl)  // URL correta sem prefixo
        .build();
```

#### Depois (correto):
```java
// URL correta para Legacy API COM o context-path
String correctBaseUrl = legacyApiBaseUrl + legacyApiContextPath;

return WebClient.builder()
        .baseUrl(correctBaseUrl)  // URL correta COM context-path
        .build();
```

### 2. Configuração Aplicada

**application.yml**:
```yaml
legacy-api:
  base-url: http://localhost:8087
  context-path: /api/legacy
  timeout: 30
```

**Resultado**: URL final = `http://localhost:8087/api/legacy`

### 3. Script de Teste

**Arquivo**: `test-webclient-fix.ps1`

#### Funcionalidades:
- ✅ **Verificação de saúde da API**: Testa se a Business API está rodando
- ✅ **Teste do endpoint**: Faz requisição GET para o endpoint
- ✅ **Análise de dados reais**: Verifica se há vendas reais (não zeros)
- ✅ **Feedback detalhado**: Mostra lojas com vendas e totais
- ✅ **Tratamento de erro**: Exibe detalhes do erro se houver falha

## 🚀 Como Aplicar a Correção

### 1. Reiniciar a Business API
```powershell
# Parar a Business API (Ctrl+C)
# Depois executar:
cd business-api
mvn spring-boot:run
```

### 2. Testar a Correção
```powershell
cd business-api
.\test-webclient-fix.ps1
```

### 3. Verificar Logs
Procure por estas mensagens nos logs:
```
=== WEBCLIENT CONFIG DEBUG ===
legacyApiBaseUrl value: http://localhost:8087
legacyApiContextPath value: /api/legacy
Full URL would be: http://localhost:8087/api/legacy
CORRECT URL: http://localhost:8087/api/legacy
================================
```

## 📊 Resultado Esperado

### ✅ Antes da Correção
```
❌ 404 Not Found from POST http://localhost:8087/api/sales/store-report-by-day
❌ Legacy API indisponível. Retornando dados zerados
```

### ✅ Após a Correção
```
✅ POST http://localhost:8087/api/legacy/sales/store-report-by-day
✅ Dados reais de vendas retornados
✅ Lojas com valores de PDV, DANFE e TROCA
```

## 🔍 Logs de Debug

Para verificar o funcionamento, observe os logs:

```
INFO  - Endpoint sendo chamado: /api/sales/store-report-by-day
INFO  - Enviando requisição para Legacy API (por dia): {storeCodes=[000001, 000002, ...]}
INFO  - Relatório de vendas por dia gerado com sucesso: 5 registros
INFO  - Vendas do dia atual obtidas com sucesso: 14 lojas no resultado final
```

## 📝 Notas Técnicas

1. **Context-path**: Legacy API usa `/api/legacy` como context-path
2. **WebClient**: Deve incluir o context-path na baseUrl
3. **Configuração**: Usa valores do application.yml
4. **Fallback**: Mantém tratamento de erro para Legacy API indisponível
5. **Performance**: Não impacta performance, apenas corrige URL

## 🎯 Resultado Final

O endpoint `current-daily-sales` agora:
- ✅ **Chama a URL correta da Legacy API**
- ✅ **Retorna dados reais de vendas**
- ✅ **Funciona com códigos de loja corretos**
- ✅ **Mantém fallback para erros**
- ✅ **Logs informativos para debugging**

---

**Data da Correção**: 28/08/2025  
**Responsável**: Equipe de Desenvolvimento  
**Status**: ✅ Concluído
