# Documentação RoleController - Business API

## 📋 Visão Geral

O `RoleController` é responsável por gerenciar as roles (funções) do sistema na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para CRUD completo de roles com autenticação JWT obrigatória e controle granular de permissões.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: POST, PUT, PATCH, GET (CRUD completo)

### Estrutura do Response DTO

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "ADMIN",
  "description": "Administrador do sistema com acesso total",
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "permissionNames": ["user:read", "user:write", "role:create", "role:update"]
}
```

**Campos:**
- `id`: UUID único da role
- `name`: Nome da role (ex: ADMIN, USER, MANAGER)
- `description`: Descrição da role
- `active`: Status ativo/inativo da role
- `createdAt`: Data de criação
- `updatedAt`: Data da última atualização
- `permissionNames`: Set com nomes das permissões associadas

---

## 🚀 Endpoints Disponíveis

### 1. Criar Nova Role

**Endpoint:** `POST /api/business/roles/create`

**Descrição:** Cria uma nova role no sistema. Requer autenticação JWT e permissão `role:create`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/roles/create`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Criar role com permissões

**Request:**
```http
POST http://localhost:8089/api/business/roles/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "name": "MANAGER",
  "description": "Gerente com acesso a relatórios e usuários",
  "permissionIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001"
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "MANAGER",
  "description": "Gerente com acesso a relatórios e usuários",
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "permissionNames": ["user:read", "user:write"]
}
```

**Validações:**
- Status Code: `201`
- Content-Type: `application/json`
- Role criada com dados completos
- Permissões associadas corretamente

##### ❌ Cenário 2: Nome inválido (minúsculas)

**Request:**
```http
POST http://localhost:8089/api/business/roles/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "manager",
  "description": "Gerente com acesso a relatórios e usuários",
  "permissionIds": ["550e8400-e29b-41d4-a716-446655440000"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Nome da role deve conter apenas letras maiúsculas e underscore"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por nome inválido

##### ❌ Cenário 3: Nome duplicado

**Request:**
```http
POST http://localhost:8089/api/business/roles/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "ADMIN",
  "description": "Administrador do sistema",
  "permissionIds": ["550e8400-e29b-41d4-a716-446655440000"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Já existe uma role com o nome: ADMIN"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por nome duplicado

##### ❌ Cenário 4: Permissões inválidas

**Request:**
```http
POST http://localhost:8089/api/business/roles/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "MANAGER",
  "description": "Gerente com acesso a relatórios e usuários",
  "permissionIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "00000000-0000-0000-0000-000000000000"
  ]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Algumas permissões especificadas não foram encontradas"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por permissões inválidas

---

### 2. Atualizar Role

**Endpoint:** `PUT /api/business/roles/{roleId}`

**Descrição:** Atualiza uma role existente no sistema. Requer autenticação JWT e permissão `role:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/roles/{{role_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Atualizar role completa

**Request:**
```http
PUT http://localhost:8089/api/business/roles/550e8400-e29b-41d4-a716-446655440002
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "SENIOR_MANAGER",
  "description": "Gerente sênior com acesso expandido",
  "permissionIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002"
  ],
  "active": true
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "SENIOR_MANAGER",
  "description": "Gerente sênior com acesso expandido",
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:35:00",
  "permissionNames": ["user:read", "user:write", "role:create"]
}
```

**Validações:**
- Status Code: `200`
- Role atualizada com novos dados
- `updatedAt` atualizado
- Permissões atualizadas

##### ❌ Cenário 2: Role não encontrada

**Request:**
```http
PUT http://localhost:8089/api/business/roles/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "SENIOR_MANAGER",
  "description": "Gerente sênior com acesso expandido",
  "permissionIds": ["550e8400-e29b-41d4-a716-446655440000"],
  "active": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Role não encontrada com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por role não encontrada

---

### 3. Alterar Status da Role

**Endpoint:** `PATCH /api/business/roles/{roleId}/status`

**Descrição:** Altera apenas o status (ativo/inativo) de uma role existente. Requer autenticação JWT e permissão `role:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PATCH`
- **URL:** `{{base_url}}/roles/{{role_id}}/status`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Ativar role

**Request:**
```http
PATCH http://localhost:8089/api/business/roles/550e8400-e29b-41d4-a716-446655440002/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "active": true
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "MANAGER",
  "description": "Gerente com acesso a relatórios e usuários",
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:40:00",
  "permissionNames": ["user:read", "user:write"]
}
```

**Validações:**
- Status Code: `200`
- Role ativada com sucesso
- `updatedAt` atualizado
- Outros campos inalterados

##### ✅ Cenário 2: Sucesso - Desativar role

**Request:**
```http
PATCH http://localhost:8089/api/business/roles/550e8400-e29b-41d4-a716-446655440002/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "active": false
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "MANAGER",
  "description": "Gerente com acesso a relatórios e usuários",
  "active": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:45:00",
  "permissionNames": ["user:read", "user:write"]
}
```

**Validações:**
- Status Code: `200`
- Role desativada com sucesso
- `updatedAt` atualizado
- Outros campos inalterados

---

### 4. Listar Todas as Roles

**Endpoint:** `GET /api/business/roles`

**Descrição:** Retorna todas as roles cadastradas no sistema. Requer autenticação JWT e permissão `role:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/roles`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista todas as roles

**Request:**
```http
GET http://localhost:8089/api/business/roles
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "ADMIN",
    "description": "Administrador do sistema com acesso total",
    "active": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "permissionNames": ["user:read", "user:write", "role:create", "role:update"]
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "USER",
    "description": "Usuário comum do sistema",
    "active": true,
    "createdAt": "2024-01-15T10:31:00",
    "updatedAt": "2024-01-15T10:31:00",
    "permissionNames": ["user:read"]
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "MANAGER",
    "description": "Gerente com acesso a relatórios e usuários",
    "active": false,
    "createdAt": "2024-01-15T10:32:00",
    "updatedAt": "2024-01-15T10:45:00",
    "permissionNames": ["user:read", "user:write"]
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de roles com dados completos
- Inclui roles ativas e inativas

---

### 5. Listar Roles Ativas

**Endpoint:** `GET /api/business/roles/active`

**Descrição:** Retorna apenas as roles ativas no sistema. Requer autenticação JWT e permissão `role:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/roles/active`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista apenas roles ativas

**Request:**
```http
GET http://localhost:8089/api/business/roles/active
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "ADMIN",
    "description": "Administrador do sistema com acesso total",
    "active": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "permissionNames": ["user:read", "user:write", "role:create", "role:update"]
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "USER",
    "description": "Usuário comum do sistema",
    "active": true,
    "createdAt": "2024-01-15T10:31:00",
    "updatedAt": "2024-01-15T10:31:00",
    "permissionNames": ["user:read"]
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array contendo apenas roles ativas
- Exclui roles inativas

---

### 6. Health Check

**Endpoint:** `GET /api/business/roles/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/roles/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/roles/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
"RoleController está funcionando"
```

**Validações:**
- Status Code: `200`
- Content-Type: `text/plain`
- Mensagem de status do controller

---

### 7. Debug Info

**Endpoint:** `GET /api/business/roles/debug`

**Descrição:** Endpoint de debug para verificar se o roteamento está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/roles/debug`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Debug info

**Request:**
```http
GET http://localhost:8089/api/business/roles/debug
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
"RoleController - Debug: Roteamento funcionando corretamente"
```

**Validações:**
- Status Code: `200`
- Content-Type: `text/plain`
- Mensagem de debug do controller

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `role_id` | `550e8400-e29b-41d4-a716-446655440002` | ID de uma role existente para testes |
| `jwt_token_invalid` | `token_invalido` | Token JWT inválido para testes |
| `jwt_token_expired` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado` | Token JWT expirado para testes |

### Collection Structure

```
📁 RoleController API - Business
├── 📁 Roles CRUD
│   ├── 📄 POST Create Role
│   ├── 📄 PUT Update Role
│   ├── 📄 PATCH Update Role Status
│   ├── 📄 GET All Roles
│   └── 📄 GET Active Roles
├── 📁 Public Endpoints
│   ├── 📄 GET Health Check
│   └── 📄 GET Debug Info
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create Role - Success
    │   ├── 📄 PUT Update Role - Success
    │   ├── 📄 PATCH Update Status - Success
    │   ├── 📄 GET All Roles - Success
    │   └── 📄 GET Active Roles - Success
    └── 📁 Error Cases
        ├── 📄 POST Create Role - Invalid Name
        ├── 📄 POST Create Role - Duplicate Name
        ├── 📄 POST Create Role - Invalid Permissions
        ├── 📄 PUT Update Role - Not Found
        ├── 📄 PATCH Update Status - Not Found
        ├── 📄 GET All Roles - No Token
        ├── 📄 GET All Roles - Invalid Token
        └── 📄 GET All Roles - No Permission
```

### Request Templates

#### 1. POST Create Role

```json
{
  "name": "POST Create Role",
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
      },
      {
        "key": "Authorization",
        "value": "Bearer {{jwt_token}}"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"name\": \"MANAGER\",\n  \"description\": \"Gerente com acesso a relatórios e usuários\",\n  \"permissionIds\": [\n    \"550e8400-e29b-41d4-a716-446655440000\",\n    \"550e8400-e29b-41d4-a716-446655440001\"\n  ]\n}"
    },
    "url": {
      "raw": "{{base_url}}/roles/create",
      "host": ["{{base_url}}"],
      "path": ["roles", "create"]
    }
  }
}
```

#### 2. PUT Update Role

```json
{
  "name": "PUT Update Role",
  "request": {
    "method": "PUT",
    "header": [
      {
        "key": "Content-Type",
        "value": "application/json"
      },
      {
        "key": "Accept",
        "value": "application/json"
      },
      {
        "key": "Authorization",
        "value": "Bearer {{jwt_token}}"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"name\": \"SENIOR_MANAGER\",\n  \"description\": \"Gerente sênior com acesso expandido\",\n  \"permissionIds\": [\n    \"550e8400-e29b-41d4-a716-446655440000\",\n    \"550e8400-e29b-41d4-a716-446655440001\",\n    \"550e8400-e29b-41d4-a716-446655440002\"\n  ],\n  \"active\": true\n}"
    },
    "url": {
      "raw": "{{base_url}}/roles/{{role_id}}",
      "host": ["{{base_url}}"],
      "path": ["roles", "{{role_id}}"]
    }
  }
}
```

#### 3. PATCH Update Role Status

```json
{
  "name": "PATCH Update Role Status",
  "request": {
    "method": "PATCH",
    "header": [
      {
        "key": "Content-Type",
        "value": "application/json"
      },
      {
        "key": "Accept",
        "value": "application/json"
      },
      {
        "key": "Authorization",
        "value": "Bearer {{jwt_token}}"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"active\": true\n}"
    },
    "url": {
      "raw": "{{base_url}}/roles/{{role_id}}/status",
      "host": ["{{base_url}}"],
      "path": ["roles", "{{role_id}}", "status"]
    }
  }
}
```

### Test Scripts (Opcional)

#### Para POST Create Role:

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Role has required fields", function () {
    const role = pm.response.json();
    pm.expect(role).to.have.property('id');
    pm.expect(role).to.have.property('name');
    pm.expect(role).to.have.property('description');
    pm.expect(role).to.have.property('active');
    pm.expect(role).to.have.property('createdAt');
    pm.expect(role).to.have.property('updatedAt');
    pm.expect(role).to.have.property('permissionNames');
});

pm.test("ID is valid UUID", function () {
    const role = pm.response.json();
    pm.expect(role.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
});

pm.test("Role is active by default", function () {
    const role = pm.response.json();
    pm.expect(role.active).to.be.true;
});

// Salvar o ID da role criada para testes subsequentes
if (pm.response.code === 201) {
    const role = pm.response.json();
    pm.environment.set("role_id", role.id);
}
```

#### Para PUT Update Role:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Role has updated data", function () {
    const role = pm.response.json();
    pm.expect(role.name).to.eql("SENIOR_MANAGER");
    pm.expect(role.description).to.eql("Gerente sênior com acesso expandido");
    pm.expect(role.active).to.be.true;
});

pm.test("UpdatedAt is newer than CreatedAt", function () {
    const role = pm.response.json();
    const createdAt = new Date(role.createdAt);
    const updatedAt = new Date(role.updatedAt);
    pm.expect(updatedAt).to.be.greaterThan(createdAt);
});
```

#### Para PATCH Update Role Status:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Role status is updated", function () {
    const role = pm.response.json();
    pm.expect(role).to.have.property('active');
    // Verificar se o status foi alterado conforme esperado
});

pm.test("Other fields remain unchanged", function () {
    const role = pm.response.json();
    // Verificar se name, description e permissionNames não foram alterados
    pm.expect(role).to.have.property('name');
    pm.expect(role).to.have.property('description');
    pm.expect(role).to.have.property('permissionNames');
});
```

#### Para GET All Roles:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each role has required fields", function () {
    const roles = pm.response.json();
    roles.forEach(role => {
        pm.expect(role).to.have.property('id');
        pm.expect(role).to.have.property('name');
        pm.expect(role).to.have.property('description');
        pm.expect(role).to.have.property('active');
        pm.expect(role).to.have.property('createdAt');
        pm.expect(role).to.have.property('updatedAt');
        pm.expect(role).to.have.property('permissionNames');
    });
});

pm.test("ID is valid UUID for each role", function () {
    const roles = pm.response.json();
    roles.forEach(role => {
        pm.expect(role.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
    });
});
```

#### Para GET Active Roles:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("All roles are active", function () {
    const roles = pm.response.json();
    roles.forEach(role => {
        pm.expect(role.active).to.be.true;
    });
});

pm.test("Active roles count is less than or equal to total roles", function () {
    // Este teste requer que você tenha executado GET All Roles primeiro
    const activeRoles = pm.response.json();
    pm.expect(activeRoles.length).to.be.at.least(0);
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Role atualizada, status alterado, roles listadas |
| `201` | Created | Role criada com sucesso |
| `400` | Bad Request | Dados inválidos, nome duplicado, permissões inválidas |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de CRUD

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Criar role**
   - Request: `POST /api/business/roles/create`
   - Expected: Status 201, Role criada

3. **Listar todas as roles**
   - Request: `GET /api/business/roles`
   - Expected: Status 200, Lista incluindo nova role

4. **Atualizar role**
   - Request: `PUT /api/business/roles/{id}`
   - Expected: Status 200, Role atualizada

5. **Alterar status da role**
   - Request: `PATCH /api/business/roles/{id}/status`
   - Expected: Status 200, Status alterado

6. **Listar apenas roles ativas**
   - Request: `GET /api/business/roles/active`
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

1. **Sem permissão role:create**
   - Request: Token válido mas sem `role:create`
   - Expected: Status 403

2. **Sem permissão role:update**
   - Request: Token válido mas sem `role:update`
   - Expected: Status 403

3. **Sem permissão role:read**
   - Request: Token válido mas sem `role:read`
   - Expected: Status 403

### Teste 4: Validação de Dados

1. **Nome inválido (minúsculas)**
   - Request: `name: "manager"`
   - Expected: Status 400

2. **Nome muito curto**
   - Request: `name: "A"`
   - Expected: Status 400

3. **Nome muito longo**
   - Request: `name: "VERY_LONG_ROLE_NAME_THAT_EXCEEDS_LIMIT"`
   - Expected: Status 400

4. **Descrição muito curta**
   - Request: `description: "Desc"`
   - Expected: Status 400

5. **Permissões vazias**
   - Request: `permissionIds: []`
   - Expected: Status 400

### Teste 5: Validação de Negócio

1. **Nome duplicado**
   - Request: `name: "ADMIN"` (já existe)
   - Expected: Status 400

2. **Permissões inválidas**
   - Request: `permissionIds: ["00000000-0000-0000-0000-000000000000"]`
   - Expected: Status 400

3. **Role não encontrada**
   - Request: PUT/PATCH com ID inexistente
   - Expected: Status 400

### Teste 6: Health Check e Debug

1. **Health check público**
   - Request: `GET /api/business/roles/health`
   - Expected: Status 200, Mensagem de status

2. **Debug info público**
   - Request: `GET /api/business/roles/debug`
   - Expected: Status 200, Mensagem de debug

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para criar role: MANAGER
INFO  - Role criada com sucesso: MANAGER (ID: 550e8400-e29b-41d4-a716-446655440002)
INFO  - Recebida requisição para atualizar role: SENIOR_MANAGER (ID: 550e8400-e29b-41d4-a716-446655440002)
INFO  - Role atualizada com sucesso: SENIOR_MANAGER (ID: 550e8400-e29b-41d4-a716-446655440002)
INFO  - Recebida requisição para alterar status da role: ATIVAR (ID: 550e8400-e29b-41d4-a716-446655440002)
INFO  - Status da role alterado com sucesso: SENIOR_MANAGER (ID: 550e8400-e29b-41d4-a716-446655440002) - Status: ATIVO
INFO  - Recebida requisição para buscar todas as roles
INFO  - Retornando 3 roles encontradas
INFO  - Recebida requisição para buscar roles ativas
INFO  - Retornando 2 roles ativas encontradas
```

### Logs de Erro

```
ERROR - Erro ao criar role: Já existe uma role com o nome: ADMIN
ERROR - Erro ao atualizar role 550e8400-e29b-41d4-a716-446655440002: Role não encontrada com ID: 550e8400-e29b-41d4-a716-446655440002
ERROR - Erro ao alterar status da role 550e8400-e29b-41d4-a716-446655440002: Role não encontrada com ID: 550e8400-e29b-41d4-a716-446655440002
ERROR - Erro ao buscar roles: Connection timeout
```

### Logs de Debug

```
DEBUG - Health check do RoleController
INFO  - Endpoint de debug acessado
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Criar Role com Token Válido

```bash
curl -X POST "http://localhost:8089/api/business/roles/create" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "MANAGER",
    "description": "Gerente com acesso a relatórios e usuários",
    "permissionIds": [
      "550e8400-e29b-41d4-a716-446655440000",
      "550e8400-e29b-41d4-a716-446655440001"
    ]
  }'
```

### Exemplo 2: Atualizar Role

```bash
curl -X PUT "http://localhost:8089/api/business/roles/550e8400-e29b-41d4-a716-446655440002" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "SENIOR_MANAGER",
    "description": "Gerente sênior com acesso expandido",
    "permissionIds": [
      "550e8400-e29b-41d4-a716-446655440000",
      "550e8400-e29b-41d4-a716-446655440001",
      "550e8400-e29b-41d4-a716-446655440002"
    ],
    "active": true
  }'
```

### Exemplo 3: Alterar Status da Role

```bash
curl -X PATCH "http://localhost:8089/api/business/roles/550e8400-e29b-41d4-a716-446655440002/status" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "active": false
  }'
```

### Exemplo 4: Listar Todas as Roles

```bash
curl -X GET "http://localhost:8089/api/business/roles" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Exemplo 5: Listar Roles Ativas

```bash
curl -X GET "http://localhost:8089/api/business/roles/active" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Exemplo 6: Health Check

```bash
curl -X GET "http://localhost:8089/api/business/roles/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 7: Debug Info

```bash
curl -X GET "http://localhost:8089/api/business/roles/debug" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`role:create`, `role:update`, `role:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Relacionamentos**: Dependência com permissões existentes

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

### Diferenças dos Controllers Anteriores

1. **Métodos HTTP**: POST, PUT, PATCH, GET (vs apenas GET)
2. **CRUD Completo**: Create, Read, Update (vs apenas Read)
3. **Validação Complexa**: Pattern, Size, NotEmpty (vs validações simples)
4. **Relacionamentos**: Set<UUID> de permissões (vs dados simples)
5. **Operações Específicas**: PATCH para status (vs operações genéricas)
6. **Validações de Negócio**: Nome duplicado, permissões válidas (vs validações básicas)
7. **Response Complexo**: Set<String> de permissões (vs dados simples)

### Segurança

1. **JWT Validation**: Token validado em cada requisição
2. **Permission-based Access**: Controle granular por permissões
3. **CORS**: Restrito ao frontend
4. **PreAuthorize**: Validação de permissões no controller
5. **Logging**: Logs estruturados para auditoria
6. **Validation**: Bean Validation com regras específicas

### Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem permissões adequadas
5. **Response**: Receber dados da role ou lista de roles

### Validações de Negócio

1. **Nome Único**: Não permite roles com mesmo nome
2. **Permissões Válidas**: Todas as permissões devem existir
3. **Status**: Controle de ativo/inativo
4. **Relacionamentos**: Dependência com permissões
5. **Integridade**: Validação de dados antes de persistir

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
