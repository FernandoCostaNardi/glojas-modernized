# Documentação SyncController - Business API

## 📋 Visão Geral

O `SyncController` é responsável por gerenciar operações de sincronização de dados no sistema da Business API. Este controller fornece endpoints para sincronização manual de vendas diárias e verificação de status do serviço de sincronização. Todos os endpoints requerem autenticação JWT obrigatória e permissões específicas.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, GET
- **DTOs**: Records para DTOs simples (≤5 campos)

### Estrutura dos Response DTOs

#### DailySalesSyncResponse
```json
{
  "created": 15,
  "updated": 8,
  "processedAt": "2024-01-15T10:30:00",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storesProcessed": 5
}
```

#### SyncStatusResponse
```json
{
  "serviceName": "DailySales Sync Service",
  "status": "HEALTHY",
  "version": "1.0",
  "lastCheck": "2024-01-15T10:30:00",
  "error": null
}
```

---

## 🚀 Endpoints Disponíveis

### 1. Sincronização Manual de Vendas Diárias

**Endpoint:** `POST /api/business/sync/daily-sales`

**Descrição:** Executa sincronização manual de vendas diárias para o período especificado. Busca dados da Legacy API e sincroniza com o banco de dados local. Requer autenticação JWT e permissão `sync:execute`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/sync/daily-sales`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Sincronização de período mensal

**Request:**
```http
POST http://localhost:8089/api/business/sync/daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31"
}
```

**Response (200 OK):**
```json
{
  "created": 15,
  "updated": 8,
  "processedAt": "2024-01-15T10:30:00",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storesProcessed": 5
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Estatísticas de sincronização
- Total processado = created + updated

##### ✅ Cenário 2: Sucesso - Sincronização de período semanal

**Request:**
```http
POST http://localhost:8089/api/business/sync/daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-07"
}
```

**Response (200 OK):**
```json
{
  "created": 5,
  "updated": 2,
  "processedAt": "2024-01-15T10:35:00",
  "startDate": "2024-01-01",
  "endDate": "2024-01-07",
  "storesProcessed": 3
}
```

**Validações:**
- Status Code: `200`
- Período menor = menos registros processados

##### ❌ Cenário 3: Data de início ausente

**Request:**
```http
POST http://localhost:8089/api/business/sync/daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "endDate": "2024-01-31"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Data de início é obrigatória"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação

##### ❌ Cenário 4: Data de início posterior à data de fim

**Request:**
```http
POST http://localhost:8089/api/business/sync/daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-31",
  "endDate": "2024-01-01"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Data de início não pode ser posterior à data de fim"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação de lógica

##### ❌ Cenário 5: Data futura

**Request:**
```http
POST http://localhost:8089/api/business/sync/daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2025-01-01",
  "endDate": "2025-01-31"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Data de início não pode ser futura"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação de data futura

---

### 2. Status do Serviço de Sincronização

**Endpoint:** `GET /api/business/sync/status`

**Descrição:** Verifica o status e saúde do serviço de sincronização. Útil para monitoramento e verificação da disponibilidade. Requer autenticação JWT e permissão `sync:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/sync/status`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Serviço saudável

**Request:**
```http
GET http://localhost:8089/api/business/sync/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "serviceName": "DailySales Sync Service",
  "status": "HEALTHY",
  "version": "1.0",
  "lastCheck": "2024-01-15T10:30:00",
  "error": null
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Status: HEALTHY
- Error: null

##### ❌ Cenário 2: Serviço com problemas

**Request:**
```http
GET http://localhost:8089/api/business/sync/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (503 Service Unavailable):**
```json
{
  "serviceName": "DailySales Sync Service",
  "status": "UNHEALTHY",
  "version": "1.0",
  "lastCheck": "2024-01-15T10:30:00",
  "error": "Database connection failed"
}
```

**Validações:**
- Status Code: `503`
- Status: UNHEALTHY
- Error: mensagem de erro

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `sync_start_date` | `2024-01-01` | Data de início para sincronização |
| `sync_end_date` | `2024-01-31` | Data de fim para sincronização |

### Collection Structure

```
📁 SyncController API - Business
├── 📁 Sync Operations
│   ├── 📄 POST Sync Daily Sales
│   └── 📄 GET Sync Status
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Sync Daily Sales - Monthly Period
    │   ├── 📄 POST Sync Daily Sales - Weekly Period
    │   └── 📄 GET Sync Status - Healthy
    └── 📁 Error Cases
        ├── 📄 POST Sync Daily Sales - Missing Start Date
        ├── 📄 POST Sync Daily Sales - Invalid Date Range
        ├── 📄 POST Sync Daily Sales - Future Date
        ├── 📄 GET Sync Status - Unhealthy
        ├── 📄 POST Sync Daily Sales - No Token
        └── 📄 POST Sync Daily Sales - Invalid Token
```

### Test Scripts

#### Para POST Sync Daily Sales:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('created');
    pm.expect(response).to.have.property('updated');
    pm.expect(response).to.have.property('processedAt');
    pm.expect(response).to.have.property('startDate');
    pm.expect(response).to.have.property('endDate');
    pm.expect(response).to.have.property('storesProcessed');
});

pm.test("Numeric values are valid", function () {
    const response = pm.response.json();
    pm.expect(response.created).to.be.a('number');
    pm.expect(response.updated).to.be.a('number');
    pm.expect(response.storesProcessed).to.be.a('number');
    pm.expect(response.created).to.be.at.least(0);
    pm.expect(response.updated).to.be.at.least(0);
    pm.expect(response.storesProcessed).to.be.at.least(0);
});

pm.test("Total processed is correct", function () {
    const response = pm.response.json();
    const totalProcessed = response.created + response.updated;
    pm.expect(totalProcessed).to.be.at.least(0);
});
```

#### Para GET Sync Status:

```javascript
pm.test("Status code is 200 or 503", function () {
    pm.expect([200, 503]).to.include(pm.response.code);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('serviceName');
    pm.expect(response).to.have.property('status');
    pm.expect(response).to.have.property('version');
    pm.expect(response).to.have.property('lastCheck');
});

pm.test("Status is valid", function () {
    const response = pm.response.json();
    pm.expect(response.status).to.be.oneOf(['HEALTHY', 'UNHEALTHY']);
});

pm.test("Service name is correct", function () {
    const response = pm.response.json();
    pm.expect(response.serviceName).to.equal('DailySales Sync Service');
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Sincronização executada com sucesso, status saudável |
| `400` | Bad Request | Dados inválidos, datas inválidas |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |
| `503` | Service Unavailable | Serviço de sincronização com problemas |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sincronização

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Verificar status do serviço**
   - Request: `GET /api/business/sync/status`
   - Expected: Status 200, Status HEALTHY

3. **Executar sincronização**
   - Request: `POST /api/business/sync/daily-sales`
   - Expected: Status 200, Estatísticas de sincronização

4. **Verificar resultados**
   - Validar created >= 0
   - Validar updated >= 0
   - Validar storesProcessed >= 0

### Teste 2: Validação de Datas

1. **Data de início ausente**
   - Request: Sem startDate
   - Expected: Status 400

2. **Data de fim ausente**
   - Request: Sem endDate
   - Expected: Status 400

3. **Data de início posterior à data de fim**
   - Request: startDate > endDate
   - Expected: Status 400

4. **Data futura**
   - Request: startDate ou endDate no futuro
   - Expected: Status 400

### Teste 3: Validação de Autorização

1. **Sem permissão sync:execute**
   - Request: Token válido mas sem `sync:execute`
   - Expected: Status 403

2. **Sem permissão sync:read**
   - Request: Token válido mas sem `sync:read`
   - Expected: Status 403

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida solicitação de sincronização de vendas diárias: startDate=2024-01-01, endDate=2024-01-31
INFO  - Sincronização de vendas diárias concluída: criados=15, atualizados=8, lojas=5
DEBUG - Verificando status do serviço de sincronização
```

### Logs de Erro

```
ERROR - Erro durante sincronização de vendas diárias: Connection timeout
ERROR - Erro ao verificar status do serviço: Database connection failed
```

### Logs de Warning

```
WARN  - Dados inválidos para sincronização: Data de início não pode ser posterior à data de fim
WARN  - Dados inválidos para sincronização: Data de início não pode ser futura
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Sincronização com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sync/daily-sales" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  }'
```

### Exemplo 2: Status do Serviço com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sync/status" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissões específicas (`sync:execute`, `sync:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação de datas**: Não permite datas futuras
5. **Período**: startDate não pode ser posterior a endDate
6. **Integração**: Depende da disponibilidade da Legacy API

### Dependências

- **Legacy API**: Fonte de dados de vendas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Lógica de Negócio

1. **Busca dados da Legacy API**: Para o período especificado
2. **Compara com dados locais**: Identifica registros novos e existentes
3. **Cria novos registros**: Para vendas não existentes
4. **Atualiza registros existentes**: Para vendas já cadastradas
5. **Retorna estatísticas**: Created, updated, storesProcessed

### Diferenças dos Controllers Anteriores

1. **Operação de sincronização**: Processa dados em lote (vs operações unitárias)
2. **Estatísticas de processamento**: Retorna contadores de criação/atualização
3. **Validação de datas**: Não permite datas futuras
4. **Endpoint de status**: Verifica saúde do serviço (vs health check simples)
5. **Status 503**: Pode retornar Service Unavailable
6. **Processamento assíncrono**: Pode demorar mais tempo
7. **Records internos**: SyncStatusResponse definido no controller
8. **Permissões específicas**: sync:execute e sync:read

### Casos de Uso

1. **Sincronização manual**: Executar sincronização sob demanda
2. **Recuperação de dados**: Sincronizar períodos específicos
3. **Auditoria**: Verificar quantos registros foram criados/atualizados
4. **Monitoramento**: Verificar saúde do serviço de sincronização
5. **Troubleshooting**: Identificar problemas de integração

---

## 🔄 Fluxo de Sincronização

### Diagrama de Fluxo

```
1. Cliente → POST /api/business/sync/daily-sales
2. SyncController → DailySalesSyncService.syncDailySales()
3. DailySalesSyncService → Legacy API (buscar vendas)
4. DailySalesSyncService → Database (comparar registros)
5. DailySalesSyncService → Database (criar novos registros)
6. DailySalesSyncService → Database (atualizar registros existentes)
7. DailySalesSyncService → SyncController (estatísticas)
8. SyncController → Cliente (DailySalesSyncResponse)
```

### Exemplo de Processamento

**Período**: 2024-01-01 a 2024-01-31

**Dados da Legacy API**: 50 registros de vendas

**Dados no Database Local**: 35 registros já existentes

**Resultado:**
- **Created**: 15 (novos registros)
- **Updated**: 35 (registros atualizados)
- **StoresProcessed**: 5 (lojas processadas)
- **Total Processed**: 50 (created + updated)

---

## 📈 Métricas e Performance

### Tempo de Resposta Esperado

- **Status Check**: < 100ms
- **Sincronização Diária**: Variável (depende do período e volume de dados)
  - 1 dia: ~500ms
  - 1 semana: ~2000ms
  - 1 mês: ~5000ms

### Otimizações

- **Batch processing**: Processa múltiplos registros de uma vez
- **Transações**: Usa transações para garantir consistência
- **Validação prévia**: Valida datas antes de processar
- **Logging detalhado**: Facilita troubleshooting

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
