# Documentação OperationController - Business API

## 📋 Visão Geral

O `OperationController` é responsável por gerenciar as operações do sistema na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para CRUD completo de operações, busca avançada com filtros e paginação, e gerenciamento com autenticação JWT obrigatória.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, PUT, GET (CRUD completo)

### Estrutura do Response DTO

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Campos:**
- `id`: UUID único da operação
- `code`: Código único da operação (letras maiúsculas, números, underscore e hífen)
- `description`: Descrição da operação
- `operationSource`: Fonte da operação (SELL, EXCHANGE)
- `createdAt`: Data e hora de criação
- `updatedAt`: Data e hora da última atualização

### Enum OperationSource

- **SELL**: Operações de venda
- **EXCHANGE**: Operações de troca

---

## 🚀 Endpoints Disponíveis

### 1. Criar Nova Operação

**Endpoint:** `POST /api/business/operations`

**Descrição:** Cria uma nova operação no sistema. Requer autenticação JWT e permissão `operation:create`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/operations`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Criar operação de venda

**Request:**
```http
POST http://localhost:8089/api/business/operations
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Validações:**
- Status Code: `201`
- Content-Type: `application/json`
- Operação criada com dados completos
- ID gerado automaticamente

##### ✅ Cenário 2: Sucesso - Criar operação de troca

**Request:**
```http
POST http://localhost:8089/api/business/operations
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "TROCA-001",
  "description": "Operação de Troca de Produtos",
  "operationSource": "EXCHANGE"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "code": "TROCA-001",
  "description": "Operação de Troca de Produtos",
  "operationSource": "EXCHANGE",
  "createdAt": "2024-01-15T10:35:00",
  "updatedAt": "2024-01-15T10:35:00"
}
```

**Validações:**
- Status Code: `201`
- operationSource: `EXCHANGE`

##### ❌ Cenário 3: Código inválido (caracteres especiais)

**Request:**
```http
POST http://localhost:8089/api/business/operations
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "venda@001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Código deve conter apenas letras maiúsculas, números, underscore e hífen"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por código inválido

##### ❌ Cenário 4: Descrição muito curta

**Request:**
```http
POST http://localhost:8089/api/business/operations
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "VENDA-001",
  "description": "Op",
  "operationSource": "SELL"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Descrição deve ter entre 3 e 255 caracteres"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por descrição muito curta

##### ❌ Cenário 5: Fonte da operação ausente

**Request:**
```http
POST http://localhost:8089/api/business/operations
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Fonte da operação é obrigatória"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por fonte ausente

---

### 2. Atualizar Operação

**Endpoint:** `PUT /api/business/operations/{operationId}`

**Descrição:** Atualiza uma operação existente no sistema. Permite atualização parcial. Requer autenticação JWT e permissão `operation:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/operations/{{operation_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Atualizar operação completa

**Request:**
```http
PUT http://localhost:8089/api/business/operations/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "VENDA-001-UPDATED",
  "description": "Operação de Venda de Produtos Atualizada",
  "operationSource": "SELL"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "VENDA-001-UPDATED",
  "description": "Operação de Venda de Produtos Atualizada",
  "operationSource": "SELL",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Validações:**
- Status Code: `200`
- Operação atualizada com novos dados
- `updatedAt` atualizado

##### ✅ Cenário 2: Sucesso - Atualizar apenas descrição

**Request:**
```http
PUT http://localhost:8089/api/business/operations/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "description": "Nova Descrição da Operação"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "VENDA-001",
  "description": "Nova Descrição da Operação",
  "operationSource": "SELL",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:35:00"
}
```

**Validações:**
- Status Code: `200`
- Apenas descrição atualizada
- Código e fonte mantidos

##### ❌ Cenário 3: Operação não encontrada

**Request:**
```http
PUT http://localhost:8089/api/business/operations/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "description": "Nova Descrição"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Operação não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por operação não encontrada

---

### 3. Buscar Operação por ID

**Endpoint:** `GET /api/business/operations/{operationId}`

**Descrição:** Busca uma operação pelo ID. Requer autenticação JWT e permissão `operation:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operations/{{operation_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar operação por ID

**Request:**
```http
GET http://localhost:8089/api/business/operations/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Operação encontrada com dados completos

##### ❌ Cenário 2: Operação não encontrada

**Request:**
```http
GET http://localhost:8089/api/business/operations/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Operação não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por operação não encontrada

---

### 4. Buscar Operações com Filtros e Paginação

**Endpoint:** `GET /api/business/operations`

**Descrição:** Busca operações com filtros e paginação. Requer autenticação JWT e permissão `operation:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operations`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar com filtros básicos

**Request:**
```http
GET http://localhost:8089/api/business/operations?code=VENDA&operationSource=SELL&page=0&size=10&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "operations": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "VENDA-001",
      "description": "Operação de Venda de Produtos",
      "operationSource": "SELL",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-20T14:30:00"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 1,
    "hasNext": false,
    "hasPrevious": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10,
  "hasNext": false,
  "hasPrevious": false,
  "counts": {
    "totalSell": 5,
    "totalExchange": 3,
    "totalOperations": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista de operações filtradas
- Informações de paginação
- Totalizadores por tipo

##### ✅ Cenário 2: Sucesso - Buscar com filtro por fonte

**Request:**
```http
GET http://localhost:8089/api/business/operations?operationSource=EXCHANGE&page=0&size=10&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "operations": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "code": "TROCA-001",
      "description": "Operação de Troca de Produtos",
      "operationSource": "EXCHANGE",
      "createdAt": "2024-01-15T10:35:00",
      "updatedAt": "2024-01-15T10:35:00"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 1,
    "hasNext": false,
    "hasPrevious": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10,
  "hasNext": false,
  "hasPrevious": false,
  "counts": {
    "totalSell": 5,
    "totalExchange": 3,
    "totalOperations": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Lista filtrada por fonte EXCHANGE
- Informações de paginação
- Totalizadores

##### ✅ Cenário 3: Sucesso - Buscar com paginação

**Request:**
```http
GET http://localhost:8089/api/business/operations?page=1&size=5&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "operations": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440005",
      "code": "VENDA-006",
      "description": "Operação de Venda 6",
      "operationSource": "SELL",
      "createdAt": "2024-01-16T10:30:00",
      "updatedAt": "2024-01-16T10:30:00"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 5,
    "totalPages": 2,
    "totalElements": 8,
    "hasNext": false,
    "hasPrevious": true
  },
  "totalElements": 8,
  "totalPages": 2,
  "currentPage": 1,
  "pageSize": 5,
  "hasNext": false,
  "hasPrevious": true,
  "counts": {
    "totalSell": 5,
    "totalExchange": 3,
    "totalOperations": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Segunda página
- hasNext: false
- hasPrevious: true

---

### 5. Health Check

**Endpoint:** `GET /api/business/operations/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operations/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/operations/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```
OperationController está funcionando
```

**Validações:**
- Status Code: `200`
- Content-Type: `text/plain`
- Mensagem de saúde do controller

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `operation_id` | `550e8400-e29b-41d4-a716-446655440000` | ID de uma operação existente para testes |
| `operation_code` | `VENDA-001` | Código de uma operação para testes |
| `operation_source_sell` | `SELL` | Fonte de operação SELL |
| `operation_source_exchange` | `EXCHANGE` | Fonte de operação EXCHANGE |

### Collection Structure

```
📁 OperationController API - Business
├── 📁 Operations CRUD
│   ├── 📄 POST Create Operation
│   ├── 📄 PUT Update Operation
│   ├── 📄 GET Operation by ID
│   ├── 📄 GET All Operations with Filters
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create Operation - SELL
    │   ├── 📄 POST Create Operation - EXCHANGE
    │   ├── 📄 PUT Update Operation - Full
    │   ├── 📄 PUT Update Operation - Partial
    │   ├── 📄 GET Operation by ID - Success
    │   ├── 📄 GET All Operations - With Filters
    │   └── 📄 GET All Operations - With Pagination
    └── 📁 Error Cases
        ├── 📄 POST Create Operation - Invalid Code
        ├── 📄 POST Create Operation - Short Description
        ├── 📄 POST Create Operation - Missing Source
        ├── 📄 PUT Update Operation - Not Found
        ├── 📄 GET Operation by ID - Not Found
        ├── 📄 GET All Operations - No Token
        └── 📄 GET All Operations - Invalid Token
```

### Test Scripts

#### Para POST Create Operation:

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Operation has required fields", function () {
    const operation = pm.response.json();
    pm.expect(operation).to.have.property('id');
    pm.expect(operation).to.have.property('code');
    pm.expect(operation).to.have.property('description');
    pm.expect(operation).to.have.property('operationSource');
    pm.expect(operation).to.have.property('createdAt');
    pm.expect(operation).to.have.property('updatedAt');
});

pm.test("ID is valid UUID", function () {
    const operation = pm.response.json();
    pm.expect(operation.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
});

pm.test("OperationSource is valid", function () {
    const operation = pm.response.json();
    pm.expect(operation.operationSource).to.be.oneOf(['SELL', 'EXCHANGE']);
});

// Salvar o ID da operação criada para testes subsequentes
if (pm.response.code === 201) {
    const operation = pm.response.json();
    pm.environment.set("operation_id", operation.id);
}
```

#### Para PUT Update Operation:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Operation has updated data", function () {
    const operation = pm.response.json();
    pm.expect(operation).to.have.property('updatedAt');
});

pm.test("UpdatedAt is newer than CreatedAt", function () {
    const operation = pm.response.json();
    const createdAt = new Date(operation.createdAt);
    const updatedAt = new Date(operation.updatedAt);
    pm.expect(updatedAt).to.be.greaterThanOrEqual(createdAt);
});
```

#### Para GET All Operations with Filters:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required structure", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('operations');
    pm.expect(response).to.have.property('pagination');
    pm.expect(response).to.have.property('counts');
});

pm.test("Operations is an array", function () {
    const response = pm.response.json();
    pm.expect(response.operations).to.be.an('array');
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
    pm.expect(counts).to.have.property('totalSell');
    pm.expect(counts).to.have.property('totalExchange');
    pm.expect(counts).to.have.property('totalOperations');
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Operação atualizada, operação encontrada, operações listadas |
| `201` | Created | Operação criada com sucesso |
| `400` | Bad Request | Dados inválidos, operação não encontrada |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de CRUD

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Criar operação**
   - Request: `POST /api/business/operations`
   - Expected: Status 201, Operação criada

3. **Buscar operação por ID**
   - Request: `GET /api/business/operations/{id}`
   - Expected: Status 200, Dados da operação

4. **Atualizar operação**
   - Request: `PUT /api/business/operations/{id}`
   - Expected: Status 200, Operação atualizada

5. **Buscar com filtros**
   - Request: `GET /api/business/operations?code=VENDA`
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

1. **Sem permissão operation:create**
   - Request: Token válido mas sem `operation:create`
   - Expected: Status 403

2. **Sem permissão operation:update**
   - Request: Token válido mas sem `operation:update`
   - Expected: Status 403

3. **Sem permissão operation:read**
   - Request: Token válido mas sem `operation:read`
   - Expected: Status 403

### Teste 4: Validação de Dados

1. **Código inválido**
   - Request: `code: "venda@001"`
   - Expected: Status 400

2. **Descrição muito curta**
   - Request: `description: "Op"`
   - Expected: Status 400

3. **Fonte ausente**
   - Request: Sem `operationSource`
   - Expected: Status 400

### Teste 5: Validação de Negócio

1. **Operação não encontrada**
   - Request: PUT/GET com ID inexistente
   - Expected: Status 400

2. **Código duplicado**
   - Request: `code: "VENDA-001"` (já existe)
   - Expected: Status 400

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para criar operação: VENDA-001
INFO  - Operação criada com sucesso: VENDA-001 (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para atualizar operação: VENDA-001 (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Operação atualizada com sucesso: VENDA-001 (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para buscar operações com filtros: code=VENDA, operationSource=SELL, page=0, size=10, sortBy=code, sortDir=asc
INFO  - Retornando 1 operações da página 1 de 1 total
```

### Logs de Erro

```
ERROR - Erro ao criar operação: Código já existe
ERROR - Erro ao atualizar operação 550e8400-e29b-41d4-a716-446655440000: Operação não encontrada
ERROR - Erro ao buscar operação 550e8400-e29b-41d4-a716-446655440000: Operação não encontrada
ERROR - Erro ao buscar operações: Connection timeout
```

### Logs de Debug

```
DEBUG - Recebida requisição para buscar operação: 550e8400-e29b-41d4-a716-446655440000
DEBUG - Operação encontrada: VENDA-001 (ID: 550e8400-e29b-41d4-a716-446655440000)
DEBUG - Health check do OperationController
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Criar Operação com cURL

```bash
curl -X POST "http://localhost:8089/api/business/operations" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "code": "VENDA-001",
    "description": "Operação de Venda de Produtos",
    "operationSource": "SELL"
  }'
```

### Exemplo 2: Atualizar Operação com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/operations/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "description": "Nova Descrição da Operação"
  }'
```

### Exemplo 3: Buscar Operações com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/operations?code=VENDA&operationSource=SELL&page=0&size=10&sortBy=code&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 4: Health Check com cURL

```bash
curl -X GET "http://localhost:8089/api/business/operations/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`operation:create`, `operation:update`, `operation:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Código único**: Não permite operações com mesmo código

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Connection Pool**: HikariCP com configuração otimizada
- **ORM**: Hibernate com dialect PostgreSQL
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Performance

- **Connection Pool**: Máximo 10 conexões
- **JWT Validation**: Validação de token em cada requisição
- **CORS**: Configurado para frontend React
- **Logging**: INFO para com.sysconard.business
- **Transactions**: @Transactional para operações de escrita
- **Paginação**: Suporte a paginação com filtros
- **Busca**: Filtros otimizados com índices

### Segurança

- **JWT Validation**: Token validado em cada requisição
- **Permission-based Access**: Controle granular por permissões
- **CORS**: Restrito ao frontend
- **PreAuthorize**: Validação de permissões no controller
- **Logging**: Logs estruturados para auditoria
- **Validation**: Bean Validation com regras específicas

### Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem permissões adequadas
5. **Response**: Receber dados da operação ou lista de operações

### Validações de Negócio

1. **Código Único**: Não permite operações com mesmo código
2. **Fonte Válida**: Apenas SELL ou EXCHANGE
3. **Atualização Parcial**: Permite atualizar apenas campos específicos
4. **Timestamps**: createdAt e updatedAt gerenciados automaticamente
5. **Integridade**: Validação de dados antes de persistir

### Diferenças dos Controllers Anteriores

1. **Controller intermediário**: 5 endpoints (vs 1 do OperationKindController e 11 do UserController)
2. **CRUD completo**: POST, PUT, GET
3. **Busca avançada**: Filtros, paginação e totalizadores
4. **Validações com Pattern**: Código com letras maiúsculas, números, underscore e hífen
5. **Enum**: OperationSource (SELL, EXCHANGE)
6. **Response com timestamps**: createdAt e updatedAt
7. **Totalizadores**: Por tipo de operação (SELL, EXCHANGE)
8. **Atualização parcial**: Permite atualizar apenas campos específicos
9. **Query parameters**: Filtros por código e fonte

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
