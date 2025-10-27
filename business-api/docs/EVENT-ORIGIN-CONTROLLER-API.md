# Documentação EventOriginController - Business API

## 📋 Visão Geral

O `EventOriginController` é responsável por gerenciar as origens de eventos (EventOrigin) no sistema da Business API. Este controller segue os princípios de Clean Code e oferece endpoints para CRUD completo de origens de eventos, busca com filtros e paginação, e gerenciamento com autenticação JWT obrigatória.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, PUT, GET (CRUD completo)
- **DTOs**: Records para DTOs simples (≤5 campos)

### Estrutura do Response DTO

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```

**Campos:**
- `id`: UUID único da origem do evento
- `eventSource`: Fonte do evento (PDV, EXCHANGE, DANFE)
- `sourceCode`: Código da fonte (máximo 50 caracteres)

### Enum EventSource

- **PDV**: Origem de eventos do PDV (Ponto de Venda)
- **EXCHANGE**: Origem de eventos de troca
- **DANFE**: Origem de eventos de DANFE (Documento Auxiliar da Nota Fiscal Eletrônica)

---

## 🚀 Endpoints Disponíveis

### 1. Criar Nova Origem de Evento

**Endpoint:** `POST /api/business/event-origins`

**Descrição:** Cria uma nova origem de evento no sistema. Requer autenticação JWT e permissão `origin:create`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/event-origins`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Criar origem PDV

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```

**Validações:**
- Status Code: `201`
- Content-Type: `application/json`
- Origem criada com dados completos
- ID gerado automaticamente

##### ✅ Cenário 2: Sucesso - Criar origem EXCHANGE

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "EXCHANGE",
  "sourceCode": "EXCHANGE-001"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "eventSource": "EXCHANGE",
  "sourceCode": "EXCHANGE-001"
}
```

**Validações:**
- Status Code: `201`
- eventSource: `EXCHANGE`

##### ✅ Cenário 3: Sucesso - Criar origem DANFE

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "DANFE",
  "sourceCode": "DANFE-001"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "eventSource": "DANFE",
  "sourceCode": "DANFE-001"
}
```

**Validações:**
- Status Code: `201`
- eventSource: `DANFE`

##### ❌ Cenário 4: EventSource ausente

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "sourceCode": "PDV-001"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "EventSource é obrigatório"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por eventSource ausente

##### ❌ Cenário 5: SourceCode ausente

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "PDV"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "SourceCode é obrigatório"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por sourceCode ausente

##### ❌ Cenário 6: SourceCode muito longo

**Request:**
```http
POST http://localhost:8089/api/business/event-origins
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001-CODIGO-MUITO-LONGO-QUE-EXCEDE-O-LIMITE-DE-CINQUENTA-CARACTERES"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "SourceCode deve ter no máximo 50 caracteres"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por sourceCode muito longo

---

### 2. Atualizar Origem de Evento

**Endpoint:** `PUT /api/business/event-origins/{id}`

**Descrição:** Atualiza uma origem de evento existente no sistema. Requer autenticação JWT e permissão `origin:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/event-origins/{{event_origin_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Atualizar origem

**Request:**
```http
PUT http://localhost:8089/api/business/event-origins/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001-UPDATED"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "eventSource": "PDV",
  "sourceCode": "PDV-001-UPDATED"
}
```

**Validações:**
- Status Code: `200`
- Origem atualizada com novos dados

##### ❌ Cenário 2: Origem não encontrada

**Request:**
```http
PUT http://localhost:8089/api/business/event-origins/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "EventOrigin não encontrado com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por origem não encontrada

---

### 3. Buscar Origem de Evento por ID

**Endpoint:** `GET /api/business/event-origins/{id}`

**Descrição:** Busca uma origem de evento pelo ID. Requer autenticação JWT e permissão `origin:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/event-origins/{{event_origin_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar origem por ID

**Request:**
```http
GET http://localhost:8089/api/business/event-origins/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Origem encontrada com dados completos

##### ❌ Cenário 2: Origem não encontrada

**Request:**
```http
GET http://localhost:8089/api/business/event-origins/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "EventOrigin não encontrado com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por origem não encontrada

---

### 4. Buscar Origens de Evento com Filtros e Paginação

**Endpoint:** `GET /api/business/event-origins`

**Descrição:** Busca origens de evento com filtros e paginação. Requer autenticação JWT e permissão `origin:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/event-origins`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar com filtro por fonte

**Request:**
```http
GET http://localhost:8089/api/business/event-origins?eventSource=PDV&page=0&size=10&sortBy=sourceCode&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "eventOrigins": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "eventSource": "PDV",
      "sourceCode": "PDV-001"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "hasNext": false,
    "hasPrevious": false
  },
  "counts": {
    "totalPdv": 5,
    "totalExchange": 3,
    "totalDanfe": 2,
    "totalEventOrigins": 10
  }
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista de origens filtradas
- Informações de paginação
- Totalizadores por tipo

##### ✅ Cenário 2: Sucesso - Buscar todas as origens

**Request:**
```http
GET http://localhost:8089/api/business/event-origins?page=0&size=20&sortBy=sourceCode&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "eventOrigins": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "eventSource": "PDV",
      "sourceCode": "PDV-001"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "eventSource": "EXCHANGE",
      "sourceCode": "EXCHANGE-001"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "eventSource": "DANFE",
      "sourceCode": "DANFE-001"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 3,
    "pageSize": 20,
    "hasNext": false,
    "hasPrevious": false
  },
  "counts": {
    "totalPdv": 5,
    "totalExchange": 3,
    "totalDanfe": 2,
    "totalEventOrigins": 10
  }
}
```

**Validações:**
- Status Code: `200`
- Lista completa de origens
- Informações de paginação
- Totalizadores

##### ✅ Cenário 3: Sucesso - Buscar com paginação

**Request:**
```http
GET http://localhost:8089/api/business/event-origins?page=1&size=5&sortBy=sourceCode&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "eventOrigins": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440005",
      "eventSource": "PDV",
      "sourceCode": "PDV-006"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 2,
    "totalElements": 10,
    "pageSize": 5,
    "hasNext": false,
    "hasPrevious": true
  },
  "counts": {
    "totalPdv": 5,
    "totalExchange": 3,
    "totalDanfe": 2,
    "totalEventOrigins": 10
  }
}
```

**Validações:**
- Status Code: `200`
- Segunda página
- hasNext: false
- hasPrevious: true

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `event_origin_id` | `550e8400-e29b-41d4-a716-446655440000` | ID de uma origem existente para testes |
| `event_source_pdv` | `PDV` | Fonte de evento PDV |
| `event_source_exchange` | `EXCHANGE` | Fonte de evento EXCHANGE |
| `event_source_danfe` | `DANFE` | Fonte de evento DANFE |

### Collection Structure

```
📁 EventOriginController API - Business
├── 📁 Event Origins CRUD
│   ├── 📄 POST Create Event Origin
│   ├── 📄 PUT Update Event Origin
│   ├── 📄 GET Event Origin by ID
│   └── 📄 GET All Event Origins with Filters
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create Event Origin - PDV
    │   ├── 📄 POST Create Event Origin - EXCHANGE
    │   ├── 📄 POST Create Event Origin - DANFE
    │   ├── 📄 PUT Update Event Origin - Success
    │   ├── 📄 GET Event Origin by ID - Success
    │   ├── 📄 GET All Event Origins - With Filter
    │   └── 📄 GET All Event Origins - With Pagination
    └── 📁 Error Cases
        ├── 📄 POST Create Event Origin - Missing EventSource
        ├── 📄 POST Create Event Origin - Missing SourceCode
        ├── 📄 POST Create Event Origin - Long SourceCode
        ├── 📄 PUT Update Event Origin - Not Found
        ├── 📄 GET Event Origin by ID - Not Found
        ├── 📄 GET All Event Origins - No Token
        └── 📄 GET All Event Origins - Invalid Token
```

### Test Scripts

#### Para POST Create Event Origin:

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Event Origin has required fields", function () {
    const eventOrigin = pm.response.json();
    pm.expect(eventOrigin).to.have.property('id');
    pm.expect(eventOrigin).to.have.property('eventSource');
    pm.expect(eventOrigin).to.have.property('sourceCode');
});

pm.test("ID is valid UUID", function () {
    const eventOrigin = pm.response.json();
    pm.expect(eventOrigin.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
});

pm.test("EventSource is valid", function () {
    const eventOrigin = pm.response.json();
    pm.expect(eventOrigin.eventSource).to.be.oneOf(['PDV', 'EXCHANGE', 'DANFE']);
});

// Salvar o ID da origem criada para testes subsequentes
if (pm.response.code === 201) {
    const eventOrigin = pm.response.json();
    pm.environment.set("event_origin_id", eventOrigin.id);
}
```

#### Para GET All Event Origins with Filters:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required structure", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('eventOrigins');
    pm.expect(response).to.have.property('pagination');
    pm.expect(response).to.have.property('counts');
});

pm.test("Event Origins is an array", function () {
    const response = pm.response.json();
    pm.expect(response.eventOrigins).to.be.an('array');
});

pm.test("Pagination has required fields", function () {
    const response = pm.response.json();
    const pagination = response.pagination;
    pm.expect(pagination).to.have.property('currentPage');
    pm.expect(pagination).to.have.property('totalPages');
    pm.expect(pagination).to.have.property('totalElements');
    pm.expect(pagination).to.have.property('pageSize');
    pm.expect(pagination).to.have.property('hasNext');
    pm.expect(pagination).to.have.property('hasPrevious');
});

pm.test("Counts has required fields", function () {
    const response = pm.response.json();
    const counts = response.counts;
    pm.expect(counts).to.have.property('totalPdv');
    pm.expect(counts).to.have.property('totalExchange');
    pm.expect(counts).to.have.property('totalDanfe');
    pm.expect(counts).to.have.property('totalEventOrigins');
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Origem atualizada, origem encontrada, origens listadas |
| `201` | Created | Origem criada com sucesso |
| `400` | Bad Request | Dados inválidos, origem não encontrada |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de CRUD

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Criar origem**
   - Request: `POST /api/business/event-origins`
   - Expected: Status 201, Origem criada

3. **Buscar origem por ID**
   - Request: `GET /api/business/event-origins/{id}`
   - Expected: Status 200, Dados da origem

4. **Atualizar origem**
   - Request: `PUT /api/business/event-origins/{id}`
   - Expected: Status 200, Origem atualizada

5. **Buscar com filtros**
   - Request: `GET /api/business/event-origins?eventSource=PDV`
   - Expected: Status 200, Lista filtrada

### Teste 2: Validação de Autenticação

1. **Token ausente**
   - Request: Sem header Authorization
   - Expected: Status 401

2. **Token inválido**
   - Request: Authorization com token inválido
   - Expected: Status 401

3. **Token expirado**
   - Request: Authorization com token expirado
   - Expected: Status 401

### Teste 3: Validação de Autorização

1. **Sem permissão origin:create**
   - Request: Token válido mas sem `origin:create`
   - Expected: Status 403

2. **Sem permissão origin:update**
   - Request: Token válido mas sem `origin:update`
   - Expected: Status 403

3. **Sem permissão origin:read**
   - Request: Token válido mas sem `origin:read`
   - Expected: Status 403

### Teste 4: Validação de Dados

1. **EventSource ausente**
   - Request: Sem `eventSource`
   - Expected: Status 400

2. **SourceCode ausente**
   - Request: Sem `sourceCode`
   - Expected: Status 400

3. **SourceCode muito longo**
   - Request: `sourceCode` com mais de 50 caracteres
   - Expected: Status 400

### Teste 5: Validação de Negócio

1. **Origem não encontrada**
   - Request: PUT/GET com ID inexistente
   - Expected: Status 400

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para criar EventOrigin: eventSource=PDV, sourceCode=PDV-001
INFO  - EventOrigin criado com sucesso: id=550e8400-e29b-41d4-a716-446655440000
INFO  - Recebida requisição para atualizar EventOrigin: id=550e8400-e29b-41d4-a716-446655440000, eventSource=PDV, sourceCode=PDV-001-UPDATED
INFO  - EventOrigin atualizado com sucesso: id=550e8400-e29b-41d4-a716-446655440000
```

### Logs de Erro

```
ERROR - Erro ao criar EventOrigin: EventSource é obrigatório
ERROR - Erro ao atualizar EventOrigin 550e8400-e29b-41d4-a716-446655440000: EventOrigin não encontrado
ERROR - Erro ao buscar EventOrigin 550e8400-e29b-41d4-a716-446655440000: EventOrigin não encontrado
```

### Logs de Debug

```
DEBUG - Recebida requisição para buscar EventOrigin por ID: 550e8400-e29b-41d4-a716-446655440000
DEBUG - EventOrigin encontrado: id=550e8400-e29b-41d4-a716-446655440000, sourceCode=PDV-001
DEBUG - Recebida requisição para buscar EventOrigins: eventSource=PDV, page=0, size=10, sortBy=sourceCode, sortDir=asc
DEBUG - EventOrigins encontrados: 1 de 10
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Criar Origem com cURL

```bash
curl -X POST "http://localhost:8089/api/business/event-origins" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "eventSource": "PDV",
    "sourceCode": "PDV-001"
  }'
```

### Exemplo 2: Atualizar Origem com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/event-origins/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "eventSource": "PDV",
    "sourceCode": "PDV-001-UPDATED"
  }'
```

### Exemplo 3: Buscar Origens com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/event-origins?eventSource=PDV&page=0&size=10&sortBy=sourceCode&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissões específicas (`origin:create`, `origin:update`, `origin:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **SourceCode**: Máximo 50 caracteres

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **Controller simples**: 4 endpoints (vs 5 do OperationController)
2. **DTOs com Records**: Uso de Records para DTOs simples (≤5 campos)
3. **Enum EventSource**: PDV, EXCHANGE, DANFE
4. **Totalizadores**: Por tipo de fonte de evento
5. **Busca simples**: Filtro apenas por eventSource
6. **Sem atualização parcial**: Todos os campos são obrigatórios

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
