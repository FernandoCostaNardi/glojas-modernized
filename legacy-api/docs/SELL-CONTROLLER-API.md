# Documentação SellController - Legacy API

## 📋 Visão Geral

O `SellController` é responsável por gerenciar relatórios de vendas do sistema na Legacy API. Este controller segue os princípios de Clean Code e oferece endpoints para geração de relatórios de vendas por loja com agregações de DANFE, PDV e TROCA3.

### Informações Técnicas

- **Base URL**: `http://localhost:8087/api/legacy`
- **Context Path**: `/api/legacy`
- **Porta**: `8087`
- **Tecnologia**: Spring Boot 2.7.x (Java 8)
- **Padrão**: REST API (Read-Only)
- **Autenticação**: Não requerida (API pública)
- **Método**: POST (com request body)

### Estrutura do Request DTO

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001", "002", "003"],
  "danfeOrigin": ["DANFE1", "DANFE2"],
  "pdvOrigin": ["PDV1", "PDV2"],
  "exchangeOrigin": ["TROCA1", "TROCA2"],
  "sellOperation": ["VENDA1", "VENDA2"],
  "exchangeOperation": ["TROCA1", "TROCA2"]
}
```

### Estrutura do Response DTO (Store Report)

```json
{
  "storeName": "Loja Centro",
  "storeCode": "001",
  "danfe": 15000.50,
  "pdv": 8500.25,
  "troca3": 1200.75
```

### Estrutura do Response DTO (Store Report By Day)

```json
{
  "storeName": "Loja Centro",
  "storeCode": "001",
  "reportDate": "2024-01-15",
  "danfe": 500.00,
  "pdv": 300.25,
  "troca3": 50.75
}
```

---

## 🚀 Endpoints Disponíveis

### 1. Relatório de Vendas por Loja

**Endpoint:** `POST /api/legacy/sales/store-report`

**Descrição:** Gera relatório de vendas por loja com agregações de DANFE, PDV e TROCA3.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/sales/store-report`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```
- **Body:** JSON com todos os parâmetros obrigatórios

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Relatório gerado

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json
Accept: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001", "002", "003"],
  "danfeOrigin": ["DANFE1", "DANFE2"],
  "pdvOrigin": ["PDV1", "PDV2"],
  "exchangeOrigin": ["TROCA1", "TROCA2"],
  "sellOperation": ["VENDA1", "VENDA2"],
  "exchangeOperation": ["TROCA1", "TROCA2"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "danfe": 15000.50,
    "pdv": 8500.25,
    "troca3": 1200.75
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "danfe": 12000.00,
    "pdv": 7000.00,
    "troca3": 800.50
  },
  {
    "storeName": "Loja Sul",
    "storeCode": "003",
    "danfe": 0.00,
    "pdv": 0.00,
    "troca3": 0.00
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de lojas com dados agregados
- Valores em BigDecimal (monetários)
- Lojas sem vendas aparecem com valores zerados

##### ❌ Cenário 2: Data de início ausente

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json

{
  "endDate": "2024-01-31",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por campo obrigatório ausente

##### ❌ Cenário 3: Formato de data inválido

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json

{
  "startDate": "01/01/2024",
  "endDate": "31/01/2024",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por formato de data inválido

##### ❌ Cenário 4: Lista de códigos de loja vazia

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": [],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por lista vazia

##### ❌ Cenário 5: Lista de origem DANFE vazia

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001"],
  "danfeOrigin": [],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por lista vazia

##### ⚠️ Cenário 6: Erro interno do servidor

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (500 Internal Server Error):**
```http
HTTP/1.1 500 Internal Server Error
```

**Validações:**
- Status Code: `500`
- Log de erro no console da aplicação

---

### 2. Relatório de Vendas por Loja e por Dia

**Endpoint:** `POST /api/legacy/sales/store-report-by-day`

**Descrição:** Gera relatório de vendas por loja e por dia com agregações de DANFE, PDV e TROCA3.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/sales/store-report-by-day`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```
- **Body:** JSON com todos os parâmetros obrigatórios

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Relatório por dia gerado

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report-by-day
Content-Type: application/json
Accept: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-03",
  "storeCodes": ["001", "002"],
  "danfeOrigin": ["DANFE1", "DANFE2"],
  "pdvOrigin": ["PDV1", "PDV2"],
  "exchangeOrigin": ["TROCA1", "TROCA2"],
  "sellOperation": ["VENDA1", "VENDA2"],
  "exchangeOperation": ["TROCA1", "TROCA2"]
}
```

**Response (200 OK):**
```json
[
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-01",
    "danfe": 500.00,
    "pdv": 300.25,
    "troca3": 50.75
  },
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-02",
    "danfe": 750.50,
    "pdv": 450.00,
    "troca3": 25.25
  },
  {
    "storeName": "Loja Centro",
    "storeCode": "001",
    "reportDate": "2024-01-03",
    "danfe": 0.00,
    "pdv": 0.00,
    "troca3": 0.00
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "reportDate": "2024-01-01",
    "danfe": 0.00,
    "pdv": 0.00,
    "troca3": 0.00
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "reportDate": "2024-01-02",
    "danfe": 1200.00,
    "pdv": 800.50,
    "troca3": 100.25
  },
  {
    "storeName": "Loja Norte",
    "storeCode": "002",
    "reportDate": "2024-01-03",
    "danfe": 0.00,
    "pdv": 0.00,
    "troca3": 0.00
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de registros por loja e por dia
- Todas as combinações de loja + data aparecem
- Dias sem vendas aparecem com valores zerados
- Ordenação por nome da loja e depois por data

##### ❌ Cenário 2: Data de fim ausente

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report-by-day
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por campo obrigatório ausente

##### ❌ Cenário 3: Lista de operações de venda vazia

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report-by-day
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": [],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por lista vazia

##### ⚠️ Cenário 4: Erro interno do servidor

**Request:**
```http
POST http://localhost:8087/api/legacy/sales/store-report-by-day
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001"],
  "danfeOrigin": ["DANFE1"],
  "pdvOrigin": ["PDV1"],
  "exchangeOrigin": ["TROCA1"],
  "sellOperation": ["VENDA1"],
  "exchangeOperation": ["TROCA1"]
}
```

**Response (500 Internal Server Error):**
```http
HTTP/1.1 500 Internal Server Error
```

**Validações:**
- Status Code: `500`
- Log de erro no console da aplicação

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8087/api/legacy` | URL base da API |
| `start_date` | `2024-01-01` | Data de início para testes |
| `end_date` | `2024-01-31` | Data de fim para testes |
| `store_codes` | `["001", "002", "003"]` | Códigos de loja para testes |
| `danfe_origin` | `["DANFE1", "DANFE2"]` | Códigos de origem DANFE |
| `pdv_origin` | `["PDV1", "PDV2"]` | Códigos de origem PDV |
| `exchange_origin` | `["TROCA1", "TROCA2"]` | Códigos de origem de troca |
| `sell_operation` | `["VENDA1", "VENDA2"]` | Códigos de operação de venda |
| `exchange_operation` | `["TROCA1", "TROCA2"]` | Códigos de operação de troca |

### Collection Structure

```
📁 SellController API
├── 📁 Sales Reports
│   ├── 📄 POST Store Sales Report
│   └── 📄 POST Store Sales Report By Day
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Store Report - Success
    │   └── 📄 POST Store Report By Day - Success
    └── 📁 Error Cases
        ├── 📄 POST Store Report - Missing Fields
        ├── 📄 POST Store Report - Invalid Date Format
        ├── 📄 POST Store Report - Empty Lists
        └── 📄 POST Store Report - Server Error
```

### Request Templates

#### 1. POST Store Sales Report

```json
{
  "name": "POST Store Sales Report",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Content-Type",
        "value": "application/json"
      },
      {
        "key": "Accept",
        "value": "application/json"
      }
    ],
    "url": {
      "raw": "{{base_url}}/sales/store-report",
      "host": ["{{base_url}}"],
      "path": ["sales", "store-report"]
    },
    "body": {
      "mode": "raw",
      "raw": "{\n  \"startDate\": \"{{start_date}}\",\n  \"endDate\": \"{{end_date}}\",\n  \"storeCodes\": {{store_codes}},\n  \"danfeOrigin\": {{danfe_origin}},\n  \"pdvOrigin\": {{pdv_origin}},\n  \"exchangeOrigin\": {{exchange_origin}},\n  \"sellOperation\": {{sell_operation}},\n  \"exchangeOperation\": {{exchange_operation}}\n}"
    }
  }
}
```

#### 2. POST Store Sales Report By Day

```json
{
  "name": "POST Store Sales Report By Day",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Content-Type",
        "value": "application/json"
      },
      {
        "key": "Accept",
        "value": "application/json"
      }
    ],
    "url": {
      "raw": "{{base_url}}/sales/store-report-by-day",
      "host": ["{{base_url}}"],
      "path": ["sales", "store-report-by-day"]
    },
    "body": {
      "mode": "raw",
      "raw": "{\n  \"startDate\": \"{{start_date}}\",\n  \"endDate\": \"{{end_date}}\",\n  \"storeCodes\": {{store_codes}},\n  \"danfeOrigin\": {{danfe_origin}},\n  \"pdvOrigin\": {{pdv_origin}},\n  \"exchangeOrigin\": {{exchange_origin}},\n  \"sellOperation\": {{sell_operation}},\n  \"exchangeOperation\": {{exchange_operation}}\n}"
    }
  }
}
```

### Test Scripts (Opcional)

#### Para POST Store Sales Report:

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
        pm.expect(store).to.have.property('troca3');
    });
});
```

#### Para POST Store Sales Report By Day:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each record has required fields", function () {
    const records = pm.response.json();
    records.forEach(record => {
        pm.expect(record).to.have.property('storeName');
        pm.expect(record).to.have.property('storeCode');
        pm.expect(record).to.have.property('reportDate');
        pm.expect(record).to.have.property('danfe');
        pm.expect(record).to.have.property('pdv');
        pm.expect(record).to.have.property('troca3');
    });
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Relatório gerado com sucesso |
| `400` | Bad Request | Validação de Bean Validation falhou |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sucesso

1. **Relatório de vendas por loja**
   - Request: `POST /api/legacy/sales/store-report`
   - Expected: Status 200, Array de lojas com dados agregados

2. **Relatório de vendas por loja e por dia**
   - Request: `POST /api/legacy/sales/store-report-by-day`
   - Expected: Status 200, Array de registros por loja e por dia

### Teste 2: Validação de Campos Obrigatórios

1. **Data de início ausente**
   - Request: Sem campo `startDate`
   - Expected: Status 400

2. **Data de fim ausente**
   - Request: Sem campo `endDate`
   - Expected: Status 400

3. **Lista de códigos de loja vazia**
   - Request: `storeCodes: []`
   - Expected: Status 400

### Teste 3: Validação de Formato

1. **Formato de data inválido**
   - Request: `startDate: "01/01/2024"`
   - Expected: Status 400

2. **Formato de data correto**
   - Request: `startDate: "2024-01-01"`
   - Expected: Status 200

### Teste 4: Validação de Listas

1. **Lista de origem DANFE vazia**
   - Request: `danfeOrigin: []`
   - Expected: Status 400

2. **Lista de origem PDV vazia**
   - Request: `pdvOrigin: []`
   - Expected: Status 400

3. **Lista de operações de venda vazia**
   - Request: `sellOperation: []`
   - Expected: Status 400

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Solicitando relatório de vendas por loja: startDate=2024-01-01, endDate=2024-01-31, storeCodes=[001, 002, 003]
INFO  - Relatório de vendas gerado com sucesso: 3 lojas
```

```
INFO  - Solicitando relatório de vendas por loja e por dia: startDate=2024-01-01, endDate=2024-01-31, storeCodes=[001, 002, 003]
INFO  - Relatório de vendas por dia gerado com sucesso: 6 registros
```

### Logs de Erro

```
ERROR - Erro de validação nos parâmetros: Data de início é obrigatória
ERROR - Erro interno ao gerar relatório de vendas: Connection timeout
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Relatório de Vendas por Loja

```bash
curl -X POST "http://localhost:8087/api/legacy/sales/store-report" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "storeCodes": ["001", "002", "003"],
    "danfeOrigin": ["DANFE1", "DANFE2"],
    "pdvOrigin": ["PDV1", "PDV2"],
    "exchangeOrigin": ["TROCA1", "TROCA2"],
    "sellOperation": ["VENDA1", "VENDA2"],
    "exchangeOperation": ["TROCA1", "TROCA2"]
  }'
```

### Exemplo 2: Relatório de Vendas por Loja e por Dia

```bash
curl -X POST "http://localhost:8087/api/legacy/sales/store-report-by-day" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-03",
    "storeCodes": ["001", "002"],
    "danfeOrigin": ["DANFE1", "DANFE2"],
    "pdvOrigin": ["PDV1", "PDV2"],
    "exchangeOrigin": ["TROCA1", "TROCA2"],
    "sellOperation": ["VENDA1", "VENDA2"],
    "exchangeOperation": ["TROCA1", "TROCA2"]
  }'
```

### Exemplo 3: Teste de Validação

```bash
curl -X POST "http://localhost:8087/api/legacy/sales/store-report" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "endDate": "2024-01-31",
    "storeCodes": ["001"],
    "danfeOrigin": ["DANFE1"],
    "pdvOrigin": ["PDV1"],
    "exchangeOrigin": ["TROCA1"],
    "sellOperation": ["VENDA1"],
    "exchangeOperation": ["TROCA1"]
  }'
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Read-Only**: A API é somente leitura, não permite operações de escrita
2. **Sem Autenticação**: Endpoints públicos, sem controle de acesso
3. **Validação Rigorosa**: Bean Validation com múltiplas validações
4. **Formato de Data**: Apenas formato YYYY-MM-DD aceito
5. **Listas Obrigatórias**: Todas as listas devem ter pelo menos um elemento
6. **Legacy**: Compatível com Java 8 e Spring Boot 2.7.x

### Dependências

- **Database**: SQL Server (45.174.189.210:1433)
- **Connection Pool**: HikariCP com configuração read-only
- **ORM**: Hibernate com dialect SQL Server 2012
- **Validation**: Bean Validation 2.x

### Performance

- **Connection Pool**: Máximo 1 conexão (read-only)
- **Lazy Loading**: Habilitado para otimização
- **Query Otimizada**: Filtro de data no banco para melhor performance
- **Agregação**: Dados já agregados no SQL Server

### Diferenças dos Controllers Anteriores

1. **Método HTTP**: POST (não GET)
2. **Request Body**: JSON complexo com múltiplas validações
3. **Response**: Múltiplos campos BigDecimal (monetários)
4. **Validação**: Bean Validation com @Valid
5. **Endpoints**: Dois endpoints relacionados (agregado e por dia)
6. **Lógica**: Processamento de dados agregados do banco
7. **Formato**: Datas em formato ISO (YYYY-MM-DD)

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Legacy API
