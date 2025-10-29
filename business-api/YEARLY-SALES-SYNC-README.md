# API de Sincronização de Vendas Anuais

## Visão Geral

A API de sincronização de vendas anuais permite agregar dados da tabela `monthly_sells` por ano e loja, salvando o resultado na tabela `year_sells`. Esta API completa a hierarquia de agregação de vendas do sistema.

## Endpoint

```
POST /sync/yearly-sales
```

## Parâmetros de Entrada

### Request Body (JSON)

```json
{
  "year": 2025
}
```

| Campo | Tipo | Obrigatório | Validação | Descrição |
|-------|------|-------------|-----------|-----------|
| `year` | Integer | Sim | 2000 ≤ year ≤ 2030 | Ano para sincronização |

## Resposta

### Response Body (JSON)

```json
{
  "created": 14,
  "updated": 6,
  "processedAt": "2025-10-27T15:30:00",
  "year": 2025,
  "storesProcessed": 20
}
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `created` | Integer | Número de registros criados |
| `updated` | Integer | Número de registros atualizados |
| `processedAt` | String (ISO DateTime) | Timestamp do processamento |
| `year` | Integer | Ano processado |
| `storesProcessed` | Integer | Número de lojas processadas |

## Autenticação

- **Método**: Bearer Token
- **Header**: `Authorization: Bearer <token>`
- **Permissão**: `sync:execute`

## Exemplos de Uso

### 1. cURL

```bash
curl -X POST "http://localhost:8080/sync/yearly-sales" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"year": 2025}'
```

### 2. PowerShell

```powershell
$headers = @{
    "Content-Type" = "application/json"
    "Authorization" = "Bearer YOUR_TOKEN"
}

$body = @{
    year = 2025
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/sync/yearly-sales" -Method POST -Headers $headers -Body $body
```

### 3. Postman

- **Method**: POST
- **URL**: `http://localhost:8080/sync/yearly-sales`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer YOUR_TOKEN`
- **Body** (raw JSON):
  ```json
  {
    "year": 2025
  }
  ```

### 4. Script PowerShell (Incluído)

```powershell
.\test-yearly-sales-sync.ps1
```

## Fluxo de Processamento

### 1. Validação de Entrada
- Verificar se o ano é válido (2000-2030)
- Validar token de autenticação
- Verificar permissões do usuário

### 2. Agregação de Dados
- Buscar dados de `monthly_sells` para o ano especificado
- Agrupar por `store_id`, `store_code`, `store_name` e `year`
- Somar o campo `total` para cada loja

### 3. Verificação de Existentes
- Para cada loja, verificar se já existe registro em `year_sells`
- Separar dados em duas listas: criar vs atualizar

### 4. Persistência
- **Criar**: Novos registros para lojas sem dados anuais
- **Atualizar**: Registros existentes com novos totais
- Usar `saveAll()` para operações em lote

### 5. Resposta
- Calcular estatísticas do processamento
- Retornar contadores e metadados

## Query SQL de Agregação

```sql
SELECT 
    m.store_id,
    m.store_code,
    m.store_name,
    CAST(SUBSTRING(m.year_month, 1, 4) AS INTEGER) as year,
    SUM(m.total) as total
FROM monthly_sells m
WHERE CAST(SUBSTRING(m.year_month, 1, 4) AS INTEGER) = :year
GROUP BY m.store_id, m.store_code, m.store_name, year
ORDER BY m.store_code
```

## Tratamento de Erros

### Códigos de Status HTTP

| Código | Descrição | Causa |
|--------|-----------|-------|
| 200 | OK | Sincronização executada com sucesso |
| 400 | Bad Request | Ano inválido ou dados malformados |
| 401 | Unauthorized | Token inválido ou ausente |
| 403 | Forbidden | Usuário sem permissão `sync:execute` |
| 500 | Internal Server Error | Erro interno do servidor |

### Exemplos de Erro

#### 400 Bad Request
```json
{
  "timestamp": "2025-10-27T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Ano deve ser maior ou igual a 2000",
  "path": "/sync/yearly-sales"
}
```

#### 403 Forbidden
```json
{
  "timestamp": "2025-10-27T15:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Acesso negado",
  "path": "/sync/yearly-sales"
}
```

## Considerações de Performance

### Otimizações Implementadas

1. **Query SQL Nativa**: Agregação eficiente no banco de dados
2. **Índice Único**: `(store_id, year)` para busca rápida de existentes
3. **Persistência em Lote**: `saveAll()` para operações otimizadas
4. **Transações Controladas**: `@Transactional` para consistência

### Estimativas de Performance

| Cenário | Registros monthly_sells | Lojas | Tempo Estimado |
|---------|------------------------|-------|----------------|
| Ano completo | 240 (20 lojas × 12 meses) | 20 | < 1 segundo |
| Ano parcial | 120 (20 lojas × 6 meses) | 20 | < 500ms |
| Sem dados | 0 | 0 | < 100ms |

## Hierarquia de Agregação

### Fluxo Completo

```
daily_sells (dados diários)
    ↓ POST /sync/monthly-sales
monthly_sells (dados mensais)
    ↓ POST /sync/yearly-sales
year_sells (dados anuais)
```

### Endpoints de Sincronização

1. **`POST /sync/daily-sales`**
   - Sincroniza dados diários da Legacy API
   - Fonte: Legacy API → `daily_sells`

2. **`POST /sync/monthly-sales`**
   - Agrega vendas diárias por mês
   - Fonte: `daily_sells` → `monthly_sells`

3. **`POST /sync/yearly-sales`** ✨ **NOVO**
   - Agrega vendas mensais por ano
   - Fonte: `monthly_sells` → `year_sells`

## Monitoramento e Logs

### Logs Estruturados

```
INFO  - Iniciando sincronização de vendas anuais: year=2025
INFO  - Buscando dados agregados de vendas mensais para o ano: 2025
INFO  - Dados agregados obtidos: 20 registros
INFO  - Dados separados: criar=14, atualizar=6
INFO  - Registros criados com sucesso: 14
INFO  - Registros atualizados com sucesso: 6
INFO  - Sincronização de vendas anuais concluída: criados=14, atualizados=6, lojas=20
```

### Métricas Importantes

- **Tempo de processamento**: Monitorar duração das operações
- **Taxa de sucesso**: Criados vs atualizados
- **Volume de dados**: Lojas processadas por execução
- **Erros**: Falhas de validação ou persistência

## Troubleshooting

### Problemas Comuns

#### 1. "Nenhum dado encontrado"
- **Causa**: Não há dados em `monthly_sells` para o ano
- **Solução**: Executar primeiro `/sync/monthly-sales` para o período

#### 2. "Ano inválido"
- **Causa**: Ano fora do range 2000-2030
- **Solução**: Usar ano válido na requisição

#### 3. "Acesso negado"
- **Causa**: Usuário sem permissão `sync:execute`
- **Solução**: Verificar permissões do usuário

#### 4. "Erro de conexão com banco"
- **Causa**: Problemas de conectividade ou permissões
- **Solução**: Verificar configuração do banco de dados

### Verificações de Diagnóstico

```sql
-- Verificar dados em monthly_sells para o ano
SELECT COUNT(*) FROM monthly_sells 
WHERE CAST(SUBSTRING(year_month, 1, 4) AS INTEGER) = 2025;

-- Verificar dados em year_sells
SELECT COUNT(*) FROM year_sells WHERE year = 2025;

-- Verificar índices
SELECT indexname, indexdef FROM pg_indexes 
WHERE tablename = 'year_sells';
```

## Segurança

### Validações de Entrada
- Ano deve estar no range 2000-2030
- Token JWT válido e não expirado
- Usuário com permissão `sync:execute`

### Auditoria
- Logs de todas as operações
- Timestamp de processamento
- Contadores de registros afetados

### Isolamento
- Transações isoladas para consistência
- Rollback automático em caso de erro
- Validação de dados antes da persistência

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento
