# API de Sincronização de Vendas Mensais

## Visão Geral

Esta funcionalidade implementa a sincronização de vendas mensais, agregando dados da tabela `daily_sells` por mês e loja, e salvando na tabela `monthly_sells`. A API sempre atualiza registros existentes ao invés de duplicar dados.

## Endpoint

```
POST /sync/monthly-sales
```

### Autenticação
- Requer permissão: `sync:execute`
- Mesma permissão usada em `/sync/daily-sales`

### Request Body

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

### Response

```json
{
  "created": 12,
  "updated": 3,
  "processedAt": "2024-01-15T10:30:00",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "storesProcessed": 5,
  "monthsProcessed": 12
}
```

## Como Funciona

### 1. Agregação de Dados
- Busca vendas diárias da tabela `daily_sells` no período especificado
- Agrupa por loja (`store_id`) e mês (`year_month` formato YYYY-MM)
- Soma o campo `total` de cada `DailySell`

### 2. Lógica de Update vs Create
- **Chave composta**: `store_id + year_month`
- Se já existir registro: **UPDATE** (atualiza `total` e `store_name`)
- Se não existir: **INSERT** (cria novo registro)

### 3. Estrutura da Tabela `monthly_sells`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Chave primária |
| `store_code` | VARCHAR(10) | Código da loja |
| `store_id` | UUID | ID da loja |
| `store_name` | VARCHAR(255) | Nome da loja |
| `total` | NUMERIC(15,2) | Total de vendas do mês |
| `year_month` | VARCHAR(7) | Ano/mês (YYYY-MM) |
| `created_at` | TIMESTAMP | Data de criação |
| `updated_at` | TIMESTAMP | Data de atualização |

### 4. Índices Criados
- `idx_monthly_sells_store_code` - Busca por código da loja
- `idx_monthly_sells_store_id` - Busca por ID da loja
- `idx_monthly_sells_created_at` - Busca por data de criação
- `idx_monthly_sells_store_year_month` - **Índice único composto** (store_id, year_month)

## Exemplo de Uso

### 1. Executar Script SQL
```bash
cd business-api/scripts
psql -U glojas_user -d glojas_business -f create-monthly-sells-table.sql
```

### 2. Testar API
```bash
cd business-api
.\test-monthly-sales-sync.ps1
```

### 3. Usar cURL
```bash
curl -X POST http://localhost:8080/sync/monthly-sales \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  }'
```

## Arquivos Criados/Modificados

### Novos Arquivos
- `MonthlySalesSyncRequest.java` - DTO de requisição
- `MonthlySalesSyncResponse.java` - DTO de resposta
- `MonthlySellRepository.java` - Repository para monthly_sells
- `MonthlySalesSyncService.java` - Serviço de sincronização
- `test-monthly-sales-sync.ps1` - Script de teste

### Arquivos Modificados
- `MonthlySell.java` - Adicionado campo `yearMonth`
- `DailySellRepository.java` - Adicionada query de agregação por mês
- `SyncController.java` - Adicionado endpoint `/monthly-sales`
- `create-monthly-sells-table.sql` - Atualizado com campo `year_month`

## Validações

### Request
- `startDate` e `endDate` são obrigatórios
- `endDate` deve ser >= `startDate`
- Datas no formato ISO (YYYY-MM-DD)

### Business Logic
- Só processa lojas que têm dados em `daily_sells`
- Agregação por mês usando `TO_CHAR(date, 'YYYY-MM')`
- Sempre atualiza registros existentes (não duplica)

## Performance

### Otimizações Implementadas
- Query SQL com agregação no banco (não em memória)
- Batch insert/update com `saveAll()`
- Índices para lookup eficiente
- Transação única para toda operação

### Limitações
- Processa todo o período em uma única transação
- Para períodos muito grandes, considere dividir em chunks menores

## Monitoramento

### Logs
- Log de início e fim da sincronização
- Contadores de registros criados/atualizados
- Logs de erro com stack trace completo

### Métricas Retornadas
- Número de registros criados
- Número de registros atualizados
- Número de lojas processadas
- Número de meses processados
- Timestamp do processamento

## Troubleshooting

### Erro 401 (Unauthorized)
- Verificar se usuário tem permissão `sync:execute`
- Verificar se JWT token é válido

### Erro 400 (Bad Request)
- Verificar formato das datas (YYYY-MM-DD)
- Verificar se endDate >= startDate

### Erro 500 (Internal Server Error)
- Verificar se tabela `daily_sells` tem dados
- Verificar se tabela `monthly_sells` foi criada
- Verificar logs da aplicação para detalhes

### Nenhum dado processado
- Verificar se existem dados em `daily_sells` para o período
- Verificar se as lojas estão ativas
- Verificar se as datas estão no formato correto

## Segurança

- Endpoint protegido com Spring Security
- Requer autenticação JWT
- Requer permissão específica (`sync:execute`)
- Validação de entrada com Bean Validation
- Transações para garantir consistência dos dados
