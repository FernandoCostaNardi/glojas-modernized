# Documentação StoreController - Business API

## 📋 Visão Geral

O `StoreController` é responsável por gerenciar as lojas do sistema na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para CRUD completo de lojas, busca com filtros avançados e paginação, e gerenciamento com autenticação JWT obrigatória.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, PUT, GET, DELETE (CRUD completo)

### Estrutura do Response DTO

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Campos:**
- `id`: UUID único da loja
- `code`: Código único da loja (6 caracteres alfanuméricos maiúsculos)
- `name`: Nome da loja
- `city`: Cidade onde a loja está localizada
- `status`: Status da loja (true = ativa, false = inativa)
- `createdAt`: Data e hora de criação
- `updatedAt`: Data e hora da última atualização

---

## 🚀 Endpoints Disponíveis

### 1. Criar Nova Loja

**Endpoint:** `POST /api/business/stores`

**Descrição:** Cria uma nova loja no sistema. Requer autenticação JWT e permissão `store:create`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/stores`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Criar loja

**Request:**
```http
POST http://localhost:8089/api/business/stores
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Validações:**
- Status Code: `201`
- Content-Type: `application/json`
- Loja criada com dados completos
- ID gerado automaticamente

##### ❌ Cenário 2: Código inválido (menos de 6 caracteres)

**Request:**
```http
POST http://localhost:8089/api/business/stores
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "LJ01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Código deve ter exatamente 6 caracteres"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação

##### ❌ Cenário 3: Código com caracteres minúsculos

**Request:**
```http
POST http://localhost:8089/api/business/stores
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "loja01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Código deve conter apenas letras maiúsculas e números"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por padrão

##### ❌ Cenário 4: Nome ausente

**Request:**
```http
POST http://localhost:8089/api/business/stores
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "code": "LOJA01",
  "city": "São Paulo",
  "status": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Nome da loja é obrigatório"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação

---

### 2. Atualizar Loja

**Endpoint:** `PUT /api/business/stores/{storeId}`

**Descrição:** Atualiza uma loja existente no sistema. O código não pode ser alterado. Requer autenticação JWT e permissão `store:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/stores/{{store_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Atualizar loja

**Request:**
```http
PUT http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "Loja Centro - Atualizada",
  "city": "São Paulo - SP",
  "status": true
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "LOJA01",
  "name": "Loja Centro - Atualizada",
  "city": "São Paulo - SP",
  "status": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Validações:**
- Status Code: `200`
- Loja atualizada com novos dados
- `updatedAt` atualizado
- `code` permanece inalterado

##### ❌ Cenário 2: Loja não encontrada

**Request:**
```http
PUT http://localhost:8089/api/business/stores/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "Loja Teste",
  "city": "São Paulo",
  "status": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Loja não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por loja não encontrada

---

### 3. Buscar Loja por ID

**Endpoint:** `GET /api/business/stores/{storeId}`

**Descrição:** Busca uma loja pelo ID. Requer autenticação JWT e permissão `store:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores/{{store_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar loja por ID

**Request:**
```http
GET http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T14:30:00"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Loja encontrada com dados completos

##### ❌ Cenário 2: Loja não encontrada

**Request:**
```http
GET http://localhost:8089/api/business/stores/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Loja não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por loja não encontrada

---

### 4. Buscar Todas as Lojas com Paginação

**Endpoint:** `GET /api/business/stores`

**Descrição:** Busca todas as lojas cadastradas no sistema com paginação. Requer autenticação JWT e permissão `store:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar todas as lojas

**Request:**
```http
GET http://localhost:8089/api/business/stores?page=0&size=10&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "LOJA01",
      "name": "Loja Centro",
      "city": "São Paulo",
      "status": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-20T14:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Página de lojas com informações de paginação

---

### 5. Buscar Lojas com Filtros

**Endpoint:** `GET /api/business/stores/search`

**Descrição:** Busca lojas com filtros e paginação. Permite filtrar por código, nome, cidade e status. Requer autenticação JWT e permissão `store:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores/search`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar com filtro por cidade

**Request:**
```http
GET http://localhost:8089/api/business/stores/search?city=São Paulo&page=0&size=10&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "LOJA01",
      "name": "Loja Centro",
      "city": "São Paulo",
      "status": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-20T14:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

**Validações:**
- Status Code: `200`
- Lista filtrada por cidade
- Informações de paginação

##### ✅ Cenário 2: Sucesso - Buscar com múltiplos filtros

**Request:**
```http
GET http://localhost:8089/api/business/stores/search?code=LOJA&name=Centro&city=São Paulo&status=true&page=0&size=10&sortBy=code&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "LOJA01",
      "name": "Loja Centro",
      "city": "São Paulo",
      "status": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-20T14:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

**Validações:**
- Status Code: `200`
- Lista filtrada por múltiplos critérios

---

### 6. Remover Loja

**Endpoint:** `DELETE /api/business/stores/{storeId}`

**Descrição:** Remove uma loja do sistema. Requer autenticação JWT e permissão `store:delete`.

#### Configuração no Postman

**Request:**
- **Method:** `DELETE`
- **URL:** `{{base_url}}/stores/{{store_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Remover loja

**Request:**
```http
DELETE http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (204 No Content):**
```
(sem corpo de resposta)
```

**Validações:**
- Status Code: `204`
- Sem corpo de resposta
- Loja removida com sucesso

##### ❌ Cenário 2: Loja não encontrada

**Request:**
```http
DELETE http://localhost:8089/api/business/stores/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Loja não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por loja não encontrada

---

### 7. Health Check

**Endpoint:** `GET /api/business/stores/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/stores/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```
StoreController está funcionando
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
| `store_id` | `550e8400-e29b-41d4-a716-446655440000` | ID de uma loja existente para testes |
| `store_code` | `LOJA01` | Código de uma loja para testes |
| `store_name` | `Loja Centro` | Nome de uma loja para testes |
| `store_city` | `São Paulo` | Cidade para testes |

### Collection Structure

```
📁 StoreController API - Business
├── 📁 Stores CRUD
│   ├── 📄 POST Create Store
│   ├── 📄 PUT Update Store
│   ├── 📄 GET Store by ID
│   ├── 📄 GET All Stores
│   ├── 📄 GET Search Stores
│   ├── 📄 DELETE Remove Store
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create Store - Success
    │   ├── 📄 PUT Update Store - Success
    │   ├── 📄 GET Store by ID - Success
    │   ├── 📄 GET All Stores - Success
    │   ├── 📄 GET Search Stores - By City
    │   ├── 📄 GET Search Stores - Multiple Filters
    │   └── 📄 DELETE Remove Store - Success
    └── 📁 Error Cases
        ├── 📄 POST Create Store - Invalid Code Length
        ├── 📄 POST Create Store - Invalid Code Pattern
        ├── 📄 POST Create Store - Missing Name
        ├── 📄 PUT Update Store - Not Found
        ├── 📄 GET Store by ID - Not Found
        ├── 📄 DELETE Remove Store - Not Found
        ├── 📄 GET All Stores - No Token
        └── 📄 GET All Stores - Invalid Token
```

### Test Scripts

#### Para POST Create Store:

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Store has required fields", function () {
    const store = pm.response.json();
    pm.expect(store).to.have.property('id');
    pm.expect(store).to.have.property('code');
    pm.expect(store).to.have.property('name');
    pm.expect(store).to.have.property('city');
    pm.expect(store).to.have.property('status');
    pm.expect(store).to.have.property('createdAt');
    pm.expect(store).to.have.property('updatedAt');
});

pm.test("ID is valid UUID", function () {
    const store = pm.response.json();
    pm.expect(store.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
});

pm.test("Code has 6 characters", function () {
    const store = pm.response.json();
    pm.expect(store.code).to.have.lengthOf(6);
});

// Salvar o ID da loja criada para testes subsequentes
if (pm.response.code === 201) {
    const store = pm.response.json();
    pm.environment.set("store_id", store.id);
}
```

#### Para GET Search Stores:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has pagination structure", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('content');
    pm.expect(response).to.have.property('pageable');
    pm.expect(response).to.have.property('totalElements');
    pm.expect(response).to.have.property('totalPages');
});

pm.test("Content is an array", function () {
    const response = pm.response.json();
    pm.expect(response.content).to.be.an('array');
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Loja atualizada, encontrada, listada |
| `201` | Created | Loja criada com sucesso |
| `204` | No Content | Loja removida com sucesso |
| `400` | Bad Request | Dados inválidos, loja não encontrada |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de CRUD

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Criar loja**
   - Request: `POST /api/business/stores`
   - Expected: Status 201, Loja criada

3. **Buscar loja por ID**
   - Request: `GET /api/business/stores/{id}`
   - Expected: Status 200, Dados da loja

4. **Atualizar loja**
   - Request: `PUT /api/business/stores/{id}`
   - Expected: Status 200, Loja atualizada

5. **Buscar com filtros**
   - Request: `GET /api/business/stores/search?city=São Paulo`
   - Expected: Status 200, Lista filtrada

6. **Remover loja**
   - Request: `DELETE /api/business/stores/{id}`
   - Expected: Status 204, Loja removida

### Teste 2: Validação de Código

1. **Código muito curto**
   - Request: `code: "LJ01"`
   - Expected: Status 400

2. **Código muito longo**
   - Request: `code: "LOJA001"`
   - Expected: Status 400

3. **Código com minúsculas**
   - Request: `code: "loja01"`
   - Expected: Status 400

4. **Código com caracteres especiais**
   - Request: `code: "LOJA@1"`
   - Expected: Status 400

### Teste 3: Validação de Campos Obrigatórios

1. **Nome ausente**
   - Request: Sem `name`
   - Expected: Status 400

2. **Cidade ausente**
   - Request: Sem `city`
   - Expected: Status 400

3. **Código ausente**
   - Request: Sem `code`
   - Expected: Status 400

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para criar loja: LOJA01
INFO  - Loja criada com sucesso: LOJA01 (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para atualizar loja: Loja Centro (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Loja atualizada com sucesso: LOJA01 (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para buscar todas as lojas com paginação: page=0, size=10, sortBy=code, sortDir=asc
INFO  - Retornando 1 lojas da página 1 de 1 total
INFO  - Recebida requisição para remover loja: 550e8400-e29b-41d4-a716-446655440000
INFO  - Loja removida com sucesso: 550e8400-e29b-41d4-a716-446655440000
```

### Logs de Erro

```
ERROR - Erro ao criar loja: Código já existe
ERROR - Erro ao atualizar loja 550e8400-e29b-41d4-a716-446655440000: Loja não encontrada
ERROR - Erro ao buscar loja 550e8400-e29b-41d4-a716-446655440000: Loja não encontrada
ERROR - Erro ao remover loja 550e8400-e29b-41d4-a716-446655440000: Loja não encontrada
```

### Logs de Debug

```
DEBUG - Recebida requisição para buscar loja: 550e8400-e29b-41d4-a716-446655440000
DEBUG - Loja encontrada: LOJA01 (ID: 550e8400-e29b-41d4-a716-446655440000)
DEBUG - Health check do StoreController
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Criar Loja com cURL

```bash
curl -X POST "http://localhost:8089/api/business/stores" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "code": "LOJA01",
    "name": "Loja Centro",
    "city": "São Paulo",
    "status": true
  }'
```

### Exemplo 2: Atualizar Loja com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "name": "Loja Centro - Atualizada",
    "city": "São Paulo - SP",
    "status": true
  }'
```

### Exemplo 3: Buscar Lojas com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/stores/search?city=São Paulo&status=true&page=0&size=10&sortBy=code&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 4: Remover Loja com cURL

```bash
curl -X DELETE "http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`store:create`, `store:update`, `store:read`, `store:delete`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Código único**: Não permite lojas com mesmo código
6. **Código imutável**: Não pode ser alterado após criação

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **CRUD completo**: POST, PUT, GET, DELETE (vs apenas POST, PUT, GET)
2. **Busca avançada**: Endpoint separado `/search` com múltiplos filtros
3. **Código com padrão rígido**: 6 caracteres alfanuméricos maiúsculos
4. **Código imutável**: Não pode ser alterado no PUT
5. **Status boolean**: Ativa/inativa (vs enum)
6. **Timestamps**: createdAt e updatedAt
7. **Paginação Spring**: Usa Page do Spring Data
8. **Filtros opcionais**: Todos os filtros são opcionais

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
