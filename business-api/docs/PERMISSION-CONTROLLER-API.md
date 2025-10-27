# Documentação PermissionController - Business API

## 📋 Visão Geral

O `PermissionController` é responsável por gerenciar as permissões do sistema na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para consulta de permissões com autenticação JWT obrigatória.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Método**: GET (consulta)

### Estrutura do Response DTO

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "permission:read",
  "resource": "permission",
  "action": "read",
  "description": "Permissão para ler dados de permissões",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Campos:**
- `id`: UUID único da permissão
- `name`: Nome da permissão (ex: permission:read)
- `resource`: Recurso protegido (ex: permission)
- `action`: Ação permitida (ex: read)
- `description`: Descrição da permissão
- `createdAt`: Data de criação da permissão

---

## 🚀 Endpoints Disponíveis

### 1. Listar Todas as Permissões

**Endpoint:** `GET /api/business/permissions`

**Descrição:** Retorna todas as permissões cadastradas no sistema. Requer autenticação JWT e permissão `permission:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/permissions`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista todas as permissões

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "permission:read",
    "resource": "permission",
    "action": "read",
    "description": "Permissão para ler dados de permissões",
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "user:read",
    "resource": "user",
    "action": "read",
    "description": "Permissão para ler dados de usuários",
    "createdAt": "2024-01-15T10:31:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "user:write",
    "resource": "user",
    "action": "write",
    "description": "Permissão para criar/editar usuários",
    "createdAt": "2024-01-15T10:32:00"
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de permissões com dados completos
- Cada permissão tem `id`, `name`, `resource`, `action`, `description`, `createdAt`

##### ❌ Cenário 2: Token ausente

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
```

**Response (401 Unauthorized):**
```http
HTTP/1.1 401 Unauthorized
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token ausente

##### ❌ Cenário 3: Token inválido

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
Authorization: Bearer token_invalido
```

**Response (401 Unauthorized):**
```http
HTTP/1.1 401 Unauthorized
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token inválido

##### ❌ Cenário 4: Token expirado

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado
```

**Response (401 Unauthorized):**
```http
HTTP/1.1 401 Unauthorized
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token expirado

##### ❌ Cenário 5: Sem permissão

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token_sem_permissao}}
```

**Response (403 Forbidden):**
```http
HTTP/1.1 403 Forbidden
```

**Validações:**
- Status Code: `403`
- Erro de autorização por falta de permissão `permission:read`

##### ⚠️ Cenário 6: Erro interno do servidor

**Request:**
```http
GET http://localhost:8089/api/business/permissions
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (500 Internal Server Error):**
```http
HTTP/1.1 500 Internal Server Error
```

**Validações:**
- Status Code: `500`
- Log de erro no console da aplicação

---

### 2. Health Check

**Endpoint:** `GET /api/business/permissions/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/permissions/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/permissions/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
"PermissionController está funcionando"
```

**Validações:**
- Status Code: `200`
- Content-Type: `text/plain`
- Mensagem de status do controller

##### ⚠️ Cenário 2: Erro interno do servidor

**Request:**
```http
GET http://localhost:8089/api/business/permissions/health
Content-Type: application/json
Accept: application/json
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
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `jwt_token_invalid` | `token_invalido` | Token JWT inválido para testes |
| `jwt_token_expired` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado` | Token JWT expirado para testes |

### Collection Structure

```
📁 PermissionController API - Business
├── 📁 Permissions
│   ├── 📄 GET All Permissions
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET All Permissions - Success
    │   └── 📄 GET Health Check - Success
    └── 📁 Error Cases
        ├── 📄 GET All Permissions - No Token
        ├── 📄 GET All Permissions - Invalid Token
        ├── 📄 GET All Permissions - Expired Token
        ├── 📄 GET All Permissions - No Permission
        └── 📄 GET All Permissions - Server Error
```

### Request Templates

#### 1. GET All Permissions

```json
{
  "name": "GET All Permissions",
  "request": {
    "method": "GET",
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
    "url": {
      "raw": "{{base_url}}/permissions",
      "host": ["{{base_url}}"],
      "path": ["permissions"]
    }
  }
}
```

#### 2. GET Health Check

```json
{
  "name": "GET Health Check",
  "request": {
    "method": "GET",
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
      "raw": "{{base_url}}/permissions/health",
      "host": ["{{base_url}}"],
      "path": ["permissions", "health"]
    }
  }
}
```

### Test Scripts (Opcional)

#### Para GET All Permissions:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each permission has required fields", function () {
    const permissions = pm.response.json();
    permissions.forEach(permission => {
        pm.expect(permission).to.have.property('id');
        pm.expect(permission).to.have.property('name');
        pm.expect(permission).to.have.property('resource');
        pm.expect(permission).to.have.property('action');
        pm.expect(permission).to.have.property('description');
        pm.expect(permission).to.have.property('createdAt');
    });
});

pm.test("ID is valid UUID", function () {
    const permissions = pm.response.json();
    permissions.forEach(permission => {
        pm.expect(permission.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
    });
});
```

#### Para GET Health Check:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is string", function () {
    pm.expect(pm.response.text()).to.be.a('string');
});

pm.test("Response contains expected message", function () {
    pm.expect(pm.response.text()).to.include("PermissionController está funcionando");
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Permissões retornadas com sucesso |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão `permission:read` |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sucesso

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Listar permissões**
   - Request: `GET /api/business/permissions`
   - Expected: Status 200, Lista de permissões

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

1. **Sem permissão**
   - Request: Token válido mas sem `permission:read`
   - Expected: Status 403

2. **Com permissão**
   - Request: Token válido com `permission:read`
   - Expected: Status 200

### Teste 4: Health Check

1. **Health check público**
   - Request: `GET /api/business/permissions/health`
   - Expected: Status 200, Mensagem de status

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para buscar todas as permissões
INFO  - Retornando 3 permissões encontradas
```

```
DEBUG - Health check do PermissionController
```

### Logs de Erro

```
ERROR - Erro ao buscar permissões: Connection timeout
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Listar Permissões com Token Válido

```bash
curl -X GET "http://localhost:8089/api/business/permissions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Exemplo 2: Health Check

```bash
curl -X GET "http://localhost:8089/api/business/permissions/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 3: Teste sem Token

```bash
curl -X GET "http://localhost:8089/api/business/permissions" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissão `permission:read`
3. **CORS**: Configurado para `http://localhost:3000`
4. **Read-Only**: Apenas consulta, não permite operações de escrita
5. **Segurança**: Validação de token e permissões

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Connection Pool**: HikariCP com configuração otimizada
- **ORM**: Hibernate com dialect PostgreSQL
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões

### Performance

- **Connection Pool**: Máximo 10 conexões
- **JWT Validation**: Validação de token em cada requisição
- **CORS**: Configurado para frontend React
- **Logging**: INFO para com.sysconard.business

### Diferenças do AuthController

1. **Método**: GET (vs POST do AuthController)
2. **Autenticação**: JWT obrigatório (vs login)
3. **Autorização**: PreAuthorize com `permission:read`
4. **Response**: Lista de permissões (vs token JWT)
5. **Health Check**: Endpoint público para monitoramento

### Segurança

1. **JWT Validation**: Token validado em cada requisição
2. **Permission-based Access**: Controle granular por permissões
3. **CORS**: Restrito ao frontend
4. **PreAuthorize**: Validação de permissões no controller
5. **Logging**: Logs estruturados para auditoria

### Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem `permission:read`
5. **Response**: Receber lista de permissões

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
