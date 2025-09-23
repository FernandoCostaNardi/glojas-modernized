# 🔄 API de Sincronização de Vendas Diárias

## 📋 Visão Geral

A API de Sincronização de Vendas Diárias é uma funcionalidade completa do Business API que permite sincronizar dados de vendas de todas as lojas ativas através de operações manuais ou automáticas agendadas.

## 🚀 Funcionalidades Implementadas

### 1. **Sincronização Manual**
- Endpoint REST para sincronização sob demanda
- Validação de período de datas
- Estatísticas detalhadas de processamento

### 2. **Sincronização Automática**
- Agendamento diário às 01:00 AM
- Sincronização semanal opcional (domingos às 02:00 AM)
- Configuração via `application.yml`

### 3. **Otimização de Performance**
- Consultas em lote (batch operations)
- Validação inteligente para evitar duplicatas
- Separação eficiente entre criação e atualização

## 📊 Arquitetura

### Componentes Implementados

```
┌─────────────────────────────────────────────────────────────┐
│                    Sync Architecture                         │
├─────────────────────────────────────────────────────────────┤
│  Controller: SyncController                                 │
│  ├─ POST /api/business/sync/daily-sales                     │
│  └─ GET  /api/business/sync/status                          │
├─────────────────────────────────────────────────────────────┤
│  Service: DailySalesSyncService                             │
│  ├─ fetchExternalData()                                     │
│  ├─ separateCreateUpdateData()                              │
│  └─ persistData()                                           │
├─────────────────────────────────────────────────────────────┤
│  Repository: DailySellRepository                            │
│  ├─ findExistingRecords()                                   │
│  ├─ saveAll() (batch operations)                            │
│  └─ Custom queries otimizadas                               │
├─────────────────────────────────────────────────────────────┤
│  Scheduler: DailySalesScheduler                             │
│  ├─ syncYesterdayData() - Diário às 01:00                  │
│  └─ syncWeeklyCorrection() - Semanal às 02:00              │
├─────────────────────────────────────────────────────────────┤
│  Entity: DailySell                                          │
│  ├─ UUID id, storeId                                        │
│  ├─ String storeCode, storeName                             │
│  ├─ LocalDate date                                          │
│  ├─ BigDecimal danfe, pdv, exchange, total                  │
│  └─ Auditoria: createdAt, updatedAt                         │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Configuração

### Application.yml

```yaml
# Configurações para sincronização de vendas diárias
sync:
  daily-sales:
    enabled: true                    # Habilita/desabilita a funcionalidade
    batch-size: 1000                 # Tamanho do lote para operações em massa
    retry-attempts: 3                # Tentativas de retry em caso de erro
    timeout: 30s                     # Timeout para operações
    schedule:
      enabled: true                  # Habilita/desabilita agendamento automático
      cron: "0 0 1 * * *"           # Todo dia às 01:00 AM
    weekly:
      enabled: false                 # Sincronização semanal de correção
      cron: "0 0 2 * * SUN"         # Todo domingo às 02:00 AM
```

## 📡 API Endpoints

### 1. Sincronização Manual

**POST** `/api/business/sync/daily-sales`

```json
{
  "startDate": "2025-09-18",
  "endDate": "2025-09-19"
}
```

**Resposta:**
```json
{
  "created": 15,
  "updated": 8,
  "processedAt": "2025-09-19T10:30:00",
  "startDate": "2025-09-18",
  "endDate": "2025-09-19",
  "storesProcessed": 12
}
```

### 2. Status do Serviço

**GET** `/api/business/sync/status`

**Resposta:**
```json
{
  "serviceName": "DailySales Sync Service",
  "status": "HEALTHY",
  "version": "1.0",
  "lastCheck": "2025-09-19T10:30:00",
  "error": null
}
```

## 🔄 Fluxo de Sincronização

### Passo 1: Obtenção de Dados Externos
1. **Busca lojas ativas**: `storeService.getAllActiveStores()`
2. **Extrai códigos das lojas**: Lista de `storeCodes`
3. **Chama relatório de vendas**: `sellService.getStoreReportByDay(request)`
4. **Mapeia para DailySell**: Conversão dos dados externos

### Passo 2: Validação Inteligente
1. **Consulta registros existentes**: Uma única query com `IN` clause
2. **Cria mapa de lookup**: HashMap com chave `storeId|date`
3. **Separa operações**: 
   - `toCreate[]`: Registros novos
   - `toUpdate[]`: Registros existentes

### Passo 3: Persistência Otimizada
1. **Batch Insert**: `repository.saveAll(toCreate)`
2. **Batch Update**: `repository.saveAll(toUpdate)`
3. **Retorna estatísticas**: Contadores de criados/atualizados

## 🔍 Monitoramento e Logs

### Logs Estruturados

```log
INFO  - Iniciando sincronização de vendas diárias: startDate=2025-09-18, endDate=2025-09-19
DEBUG - Lojas ativas encontradas: 12
DEBUG - Registros de vendas obtidos da API externa: 24
DEBUG - Dados separados: criar=15, atualizar=8, lojas únicas=12
DEBUG - Registros criados: 15
DEBUG - Registros atualizados: 8
INFO  - Sincronização concluída com sucesso: criados=15, atualizados=8, lojas=12
```

### Métricas de Monitoramento

```log
SYNC_METRICS: date=2025-09-18, created=15, updated=8, total_processed=23, stores=12, success=true
SYNC_METRICS: date=2025-09-19, success=false, error_type=RuntimeException, error_message=Connection timeout
```

## 🛡️ Segurança e Validação

### Controle de Acesso
- **Sincronização Manual**: `@PreAuthorize("hasAuthority('sync:execute')")`
- **Status do Serviço**: `@PreAuthorize("hasAuthority('sync:read')")`

### Validação de Dados
- **Datas obrigatórias**: `@NotNull` nas DTOs
- **Validação de período**: `startDate <= endDate`
- **Datas não futuras**: Validação customizada
- **Bean Validation**: Aplicada em todos os endpoints

## 🔨 Testes

### Exemplo de Teste Manual

```bash
# 1. Obter token JWT
curl -X POST http://localhost:8082/api/business/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Executar sincronização
curl -X POST http://localhost:8082/api/business/sync/daily-sales \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "startDate": "2025-09-18",
    "endDate": "2025-09-18"
  }'

# 3. Verificar status
curl -X GET http://localhost:8082/api/business/sync/status \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📈 Performance e Otimizações

### Consultas Otimizadas
- **Índice composto**: `(store_id, date)` na tabela `daily_sells`
- **Batch queries**: Minimiza round-trips ao banco
- **Connection pooling**: Configurado via Hikari

### Operações em Lote
- **Batch size configurável**: Default 1000 registros
- **saveAll()**: Utiliza batch insert/update do Hibernate
- **Lookup eficiente**: HashMap O(1) para verificação de duplicatas

## 🚨 Tratamento de Erros

### Tipos de Exceções
- **IllegalArgumentException**: Dados inválidos (400 Bad Request)
- **RuntimeException**: Erros de sincronização (500 Internal Server Error)
- **ExternalServiceException**: Falhas na Legacy API

### Retry Strategy
- **Tentativas configuráveis**: Default 3 tentativas
- **Timeout configurável**: Default 30 segundos
- **Logs detalhados**: Para debugging e monitoramento

## 📋 Checklist de Implementação

- [✅] DTOs de request/response criados
- [✅] Repository com queries otimizadas
- [✅] Service com lógica de sincronização
- [✅] Controller REST com validação
- [✅] Scheduler automático configurável
- [✅] Configurações no application.yml
- [✅] Logs estruturados implementados
- [✅] Tratamento de erros robusto
- [✅] Documentação completa
- [✅] Segurança e autorização
- [✅] Validação de dados
- [✅] Performance otimizada

## 🔮 Próximos Passos (Opcional)

### Melhorias Futuras
1. **Métricas com Micrometer**: Para monitoring avançado
2. **Alertas automáticos**: Email/Slack em caso de falhas
3. **Dashboard de monitoramento**: Interface web para acompanhamento
4. **Backup automático**: Antes de operações de atualização em massa
5. **Compressão de dados**: Para otimizar transferência de grandes volumes

---

**Versão**: 1.0  
**Data**: 19/09/2025  
**Responsável**: Business API Team
