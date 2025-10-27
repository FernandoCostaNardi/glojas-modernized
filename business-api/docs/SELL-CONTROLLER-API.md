# Documentação SellController - Business API

## 📋 Visão Geral

O `SellController` é responsável por gerenciar relatórios de vendas no sistema da Business API. Este controller consome dados da Legacy API e oferece endpoints para relatórios agregados de vendas por loja, por dia, vendas diárias e vendas do dia atual em tempo real. Todos os endpoints requerem autenticação JWT obrigatória.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, GET
- **Integração**: Consome dados da Legacy API
- **DTOs**: Records para DTOs simples (≤5 campos)

### Estrutura dos Response DTOs

#### StoreReportResponse
```json
{
  "storeName": "Loja Centro",
  "storeCode": "001",
  "danfe": 15000.00,
  "pdv": 8000.00,
  "troca": 500.00
}
```

#### StoreReportByDayResponse
```json
{
  "storeName": "Loja Centro",
  "storeCode": "001",
  "reportDate": "2024-01-15",
  "danfe": 5000.00,
  "pdv": 3000.00,
  "troca": 200.00
}
```

#### DailySalesReportResponse
```json
{
  "storeName": "Loja Centro",
  "pdv": 8000.00,
  "danfe": 15000.00,
  "exchange": 500.00,
  "total": 22500.00
}
```

### Tipos de Vendas

- **DANFE**: Vendas via Nota Fiscal Eletrônica
- **PDV**: Vendas via Ponto de Venda
- **TROCA/EXCHANGE**: Trocas de produtos (reduzem o faturamento)
- **TOTAL**: DANFE + PDV - TROCA

---

## 🚀 Endpoints Disponíveis

### 1. Relatório de Vendas por Loja

**Endpoint:** `POST /api/business/sales/store-report`

**Descrição:** Obtém relatório de vendas agregadas por loja para um período específico. Consome dados da Legacy API e agrega valores de DANFE, PDV e TROCA3. Requer autenticação JWT e permissão `sell:read`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/sales/store-report`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Relatório de uma loja

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "danfe": 15000.00,
    "pdv": 8000.00,
    "troca": 500.00
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista com dados agregados por loja
- Valores numéricos válidos

##### ✅ Cenário 2: Sucesso - Relatório de múltiplas lojas

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001", "002", "003"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "danfe": 15000.00,
    "pdv": 8000.00,
    "troca": 500.00
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "danfe": 12000.00,
    "pdv": 6000.00,
    "troca": 300.00
  },
  {
    "storeName": "Loja Sul",
    "storeCode": "003",
    "danfe": 18000.00,
    "pdv": 9000.00,
    "troca": 700.00
  }
]
```

**Validações:**
- Status Code: `200`
- Lista com múltiplas lojas
- Dados agregados por loja

##### ❌ Cenário 3: Data de início ausente

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "endDate": "2024-01-31",
  "storeCodes": ["001"]
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

##### ❌ Cenário 4: Lista de lojas vazia

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": []
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Lista de códigos de lojas não pode estar vazia"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação

##### ❌ Cenário 5: Data de início posterior à data de fim

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-31",
  "endDate": "2024-01-01",
  "storeCodes": ["001"]
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
- Erro de validação de lógica de negócio

---

### 2. Relatório de Vendas por Loja e por Dia

**Endpoint:** `POST /api/business/sales/store-report-by-day`

**Descrição:** Obtém relatório de vendas agregadas por loja e por dia para um período específico. Consome dados da Legacy API e agrega valores de DANFE, PDV e TROCA3 separados por dia. Requer autenticação JWT e permissão `sell:read`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/sales/store-report-by-day`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Relatório por dia de uma loja

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report-by-day
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-03",
  "storeCodes": ["001"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-01",
    "danfe": 5000.00,
    "pdv": 3000.00,
    "troca": 200.00
  },
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-02",
    "danfe": 5500.00,
    "pdv": 2500.00,
    "troca": 150.00
  },
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-03",
    "danfe": 4500.00,
    "pdv": 2500.00,
    "troca": 150.00
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista com dados separados por dia
- Cada registro com data específica

##### ✅ Cenário 2: Sucesso - Relatório por dia de múltiplas lojas

**Request:**
```http
POST http://localhost:8089/api/business/sales/store-report-by-day
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-02",
  "storeCodes": ["001", "002"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-01",
    "danfe": 5000.00,
    "pdv": 3000.00,
    "troca": 200.00
  },
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-02",
    "danfe": 5500.00,
    "pdv": 2500.00,
    "troca": 150.00
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "reportDate": "2024-01-01",
    "danfe": 4000.00,
    "pdv": 2000.00,
    "troca": 100.00
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "reportDate": "2024-01-02",
    "danfe": 4500.00,
    "pdv": 2500.00,
    "troca": 150.00
  }
]
```

**Validações:**
- Status Code: `200`
- Lista com dados de múltiplas lojas por dia
- Dados organizados por loja e data

---

### 3. Relatório de Vendas Diárias

**Endpoint:** `GET /api/business/sales/daily-sales`

**Descrição:** Obtém relatório de vendas diárias agregadas por loja para um período específico. Retorna dados consolidados de vendas. Requer autenticação JWT e permissão `sell:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/sales/daily-sales`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Relatório de vendas diárias

**Request:**
```http
GET http://localhost:8089/api/business/sales/daily-sales?startDate=2024-01-01&endDate=2024-01-31
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "pdv": 8000.00,
    "danfe": 15000.00,
    "exchange": 500.00,
    "total": 22500.00
  },
  {
    "storeName": "Loja Norte",
    "pdv": 6000.00,
    "danfe": 12000.00,
    "exchange": 300.00,
    "total": 17700.00
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista com dados agregados por loja
- Total calculado automaticamente

##### ❌ Cenário 2: Data de início ausente

**Request:**
```http
GET http://localhost:8089/api/business/sales/daily-sales?endDate=2024-01-31
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "startDate é obrigatório"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação

---

### 4. Vendas do Dia Atual (Tempo Real)

**Endpoint:** `GET /api/business/sales/current-daily-sales`

**Descrição:** Obtém vendas do dia atual em tempo real. Busca dados diretamente da Legacy API sem usar cache, garantindo informações sempre atualizadas. Requer autenticação JWT e permissão `sell:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/sales/current-daily-sales`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Vendas do dia atual

**Request:**
```http
GET http://localhost:8089/api/business/sales/current-daily-sales
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "pdv": 2500.00,
    "danfe": 5000.00,
    "exchange": 100.00,
    "total": 7400.00
  },
  {
    "storeName": "Loja Norte",
    "pdv": 1800.00,
    "danfe": 3500.00,
    "exchange": 50.00,
    "total": 5250.00
  }
]
```

**Response Headers:**
```
Cache-Control: no-cache, no-store, must-revalidate
Pragma: no-cache
Expires: 0
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Headers anti-cache configurados
- Dados sempre atualizados (sem cache)

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `start_date` | `2024-01-01` | Data de início para testes |
| `end_date` | `2024-01-31` | Data de fim para testes |
| `store_code_001` | `001` | Código da loja 1 |
| `store_code_002` | `002` | Código da loja 2 |
| `store_code_003` | `003` | Código da loja 3 |

### Collection Structure

```
📁 SellController API - Business
├── 📁 Sales Reports
│   ├── 📄 POST Store Report
│   ├── 📄 POST Store Report By Day
│   ├── 📄 GET Daily Sales Report
│   └── 📄 GET Current Daily Sales
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Store Report - Single Store
    │   ├── 📄 POST Store Report - Multiple Stores
    │   ├── 📄 POST Store Report By Day - Single Store
    │   ├── 📄 POST Store Report By Day - Multiple Stores
    │   ├── 📄 GET Daily Sales Report - Success
    │   └── 📄 GET Current Daily Sales - Success
    └── 📁 Error Cases
        ├── 📄 POST Store Report - Missing Start Date
        ├── 📄 POST Store Report - Empty Store List
        ├── 📄 POST Store Report - Invalid Date Range
        ├── 📄 GET Daily Sales Report - Missing Start Date
        ├── 📄 GET Current Daily Sales - No Token
        └── 📄 GET Current Daily Sales - Invalid Token
```

### Test Scripts

#### Para POST Store Report:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each store has required fields", function () {
    const stores = pm.response.json();
    stores.forEach(store => {
        pm.expect(store).to.have.property('storeName');
        pm.expect(store).to.have.property('storeCode');
        pm.expect(store).to.have.property('danfe');
        pm.expect(store).to.have.property('pdv');
        pm.expect(store).to.have.property('troca');
    });
});

pm.test("Numeric values are valid", function () {
    const stores = pm.response.json();
    stores.forEach(store => {
        pm.expect(store.danfe).to.be.a('number');
        pm.expect(store.pdv).to.be.a('number');
        pm.expect(store.troca).to.be.a('number');
    });
});
```

#### Para GET Current Daily Sales:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Anti-cache headers are present", function () {
    pm.expect(pm.response.headers.get('Cache-Control')).to.include('no-cache');
    pm.expect(pm.response.headers.get('Pragma')).to.equal('no-cache');
    pm.expect(pm.response.headers.get('Expires')).to.equal('0');
});

pm.test("Each store has total calculated", function () {
    const stores = pm.response.json();
    stores.forEach(store => {
        pm.expect(store).to.have.property('total');
        pm.expect(store.total).to.be.a('number');
    });
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Relatório gerado com sucesso |
| `400` | Bad Request | Dados inválidos, datas inválidas |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor ou Legacy API indisponível |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Relatórios

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Relatório por loja**
   - Request: `POST /api/business/sales/store-report`
   - Expected: Status 200, Dados agregados

3. **Relatório por loja e dia**
   - Request: `POST /api/business/sales/store-report-by-day`
   - Expected: Status 200, Dados por dia

4. **Relatório de vendas diárias**
   - Request: `GET /api/business/sales/daily-sales`
   - Expected: Status 200, Dados consolidados

5. **Vendas do dia atual**
   - Request: `GET /api/business/sales/current-daily-sales`
   - Expected: Status 200, Dados em tempo real

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

### Teste 3: Validação de Lojas

1. **Lista de lojas vazia**
   - Request: storeCodes = []
   - Expected: Status 400

2. **Lista de lojas com código nulo**
   - Request: storeCodes = [null]
   - Expected: Status 400

3. **Lista de lojas com código vazio**
   - Request: storeCodes = [""]
   - Expected: Status 400

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida solicitação de relatório de vendas por loja: startDate=2024-01-01, endDate=2024-01-31, storeCodes=[001, 002]
INFO  - Relatório de vendas processado com sucesso: 2 lojas retornadas
INFO  - Recebida solicitação de relatório de vendas por loja e por dia: startDate=2024-01-01, endDate=2024-01-03, storeCodes=[001]
INFO  - Relatório de vendas por dia processado com sucesso: 3 registros retornados
INFO  - Recebida solicitação de relatório de vendas diárias: startDate=2024-01-01, endDate=2024-01-31
INFO  - Relatório de vendas diárias processado com sucesso: 2 lojas retornadas
INFO  - Recebida solicitação de vendas do dia atual em tempo real
INFO  - Vendas do dia atual processadas com sucesso: 2 lojas retornadas
```

### Logs de Erro

```
ERROR - Erro ao processar relatório de vendas: Connection timeout
ERROR - Erro ao processar relatório de vendas por dia: Legacy API indisponível
ERROR - Erro ao processar relatório de vendas diárias: Data de início não pode ser posterior à data de fim
ERROR - Erro ao processar vendas do dia atual: Legacy API retornou erro 500
```

### Logs de Warning

```
WARN  - Parâmetros inválidos para relatório de vendas diárias: Data de início é obrigatória
WARN  - Parâmetros inválidos para relatório de vendas diárias: Lista de códigos de lojas não pode estar vazia
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Relatório por Loja com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sales/store-report" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "storeCodes": ["001", "002"]
  }'
```

### Exemplo 2: Relatório por Loja e Dia com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sales/store-report-by-day" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-03",
    "storeCodes": ["001"]
  }'
```

### Exemplo 3: Vendas Diárias com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sales/daily-sales?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 4: Vendas do Dia Atual com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sales/current-daily-sales" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissão `sell:read`
3. **CORS**: Configurado para `http://localhost:3000`
4. **Integração**: Depende da disponibilidade da Legacy API
5. **Datas**: Formato ISO (YYYY-MM-DD)
6. **Cache**: Endpoint `/current-daily-sales` não usa cache

### Dependências

- **Legacy API**: Fonte de dados de vendas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **Integração externa**: Consome dados da Legacy API
2. **Múltiplos tipos de relatório**: 4 endpoints diferentes
3. **Mix de métodos**: POST e GET
4. **Dados financeiros**: BigDecimal para valores monetários
5. **Tempo real**: Endpoint sem cache para dados atualizados
6. **Headers especiais**: Anti-cache headers no endpoint de tempo real
7. **Validação de datas**: Lógica customizada de validação
8. **Agregação de dados**: Calcula totais automaticamente

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
