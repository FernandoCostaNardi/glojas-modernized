# Documentação UserController - Business API

## 📋 Visão Geral

O `UserController` é responsável por gerenciar os usuários do sistema na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para CRUD completo de usuários, alteração de senha, busca avançada com filtros e paginação, e gerenciamento de status com autenticação JWT obrigatória.

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
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "joinDate": "2024-01-15T10:30:00",
  "lastLoginDate": "2024-01-20T14:30:00",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER"],
  "message": "Usuário criado com sucesso",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Campos:**
- `id`: UUID único do usuário
- `name`: Nome completo do usuário
- `username`: Nome de usuário único
- `email`: Email do usuário
- `profileImageUrl`: URL da imagem de perfil
- `joinDate`: Data de cadastro
- `lastLoginDate`: Data do último login
- `isActive`: Status ativo/inativo
- `isNotLocked`: Status bloqueado/não bloqueado
- `roles`: Set com nomes das roles do usuário
- `message`: Mensagem de resposta
- `updatedAt`: Data da última atualização

---

## 🚀 Endpoints Disponíveis

### 1. Criar Novo Usuário

**Endpoint:** `POST /api/business/users/create`

**Descrição:** Cria um novo usuário no sistema. Requer autenticação JWT e permissão `user:create`.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/users/create`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Criar usuário com dados completos

**Request:**
```http
POST http://localhost:8089/api/business/users/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body:**
```json
{
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "password": "MinhaSenh@123",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "roles": ["USER", "MANAGER"]
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "joinDate": "2024-01-15T10:30:00",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER"],
  "message": "Usuário criado com sucesso"
}
```

**Validações:**
- Status Code: `201`
- Content-Type: `application/json`
- Usuário criado com dados completos
- Roles associadas corretamente

##### ❌ Cenário 2: Username inválido (caracteres especiais)

**Request:**
```http
POST http://localhost:8089/api/business/users/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva",
  "username": "joao@silva",
  "email": "joao@example.com",
  "password": "MinhaSenh@123",
  "roles": ["USER"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Username deve conter apenas letras, números e underscore"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por username inválido

##### ❌ Cenário 3: Email inválido

**Request:**
```http
POST http://localhost:8089/api/business/users/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva",
  "username": "joao.silva",
  "email": "email-invalido",
  "password": "MinhaSenh@123",
  "roles": ["USER"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Email deve ser válido"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por email inválido

##### ❌ Cenário 4: Senha muito curta

**Request:**
```http
POST http://localhost:8089/api/business/users/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "password": "123",
  "roles": ["USER"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Senha deve ter entre 6 e 100 caracteres"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por senha muito curta

##### ❌ Cenário 5: Roles vazias

**Request:**
```http
POST http://localhost:8089/api/business/users/create
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "password": "MinhaSenh@123",
  "roles": []
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Pelo menos uma role deve ser especificada"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por roles vazias

---

### 2. Verificar Disponibilidade de Username

**Endpoint:** `GET /api/business/users/check-username/{username}`

**Descrição:** Verifica se um username está disponível. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users/check-username/{{username}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Username disponível

**Request:**
```http
GET http://localhost:8089/api/business/users/check-username/novo.usuario
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "username": "novo.usuario",
  "available": true,
  "message": "Username disponível"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Username disponível

##### ✅ Cenário 2: Username já existe

**Request:**
```http
GET http://localhost:8089/api/business/users/check-username/admin
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "username": "admin",
  "available": false,
  "message": "Username já está em uso"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Username não disponível

---

### 3. Verificar Disponibilidade de Email

**Endpoint:** `GET /api/business/users/check-email/{email}`

**Descrição:** Verifica se um email está disponível. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users/check-email/{{email}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Email disponível

**Request:**
```http
GET http://localhost:8089/api/business/users/check-email/novo@example.com
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "email": "novo@example.com",
  "available": true,
  "message": "Email disponível"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Email disponível

##### ✅ Cenário 2: Email já existe

**Request:**
```http
GET http://localhost:8089/api/business/users/check-email/admin@example.com
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "email": "admin@example.com",
  "available": false,
  "message": "Email já está em uso"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Email não disponível

---

### 4. Atualizar Usuário

**Endpoint:** `PUT /api/business/users/{userId}`

**Descrição:** Atualiza um usuário existente no sistema. Requer autenticação JWT e permissão `user:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/users/{{user_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Atualizar usuário completo

**Request:**
```http
PUT http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva Santos",
  "email": "joao.santos@example.com",
  "profileImageUrl": "https://example.com/new-avatar.jpg",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER", "ADMIN"]
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva Santos",
  "username": "joao.silva",
  "email": "joao.santos@example.com",
  "profileImageUrl": "https://example.com/new-avatar.jpg",
  "joinDate": "2024-01-15T10:30:00",
  "lastLoginDate": "2024-01-20T14:30:00",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER", "ADMIN"],
  "message": "Usuário atualizado com sucesso",
  "updatedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Usuário atualizado com novos dados
- `updatedAt` atualizado
- Roles atualizadas

##### ❌ Cenário 2: Usuário não encontrado

**Request:**
```http
PUT http://localhost:8089/api/business/users/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "name": "João Silva Santos",
  "email": "joao.santos@example.com",
  "profileImageUrl": "https://example.com/new-avatar.jpg",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER"]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Usuário não encontrado com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por usuário não encontrado

---

### 5. Alterar Senha

**Endpoint:** `PUT /api/business/users/{userId}/change-password`

**Descrição:** Altera a senha de um usuário. Requer autenticação JWT e permissão `user:change-password`.

#### Configuração no Postman

**Request:**
- **Method:** `PUT`
- **URL:** `{{base_url}}/users/{{user_id}}/change-password`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Alterar senha

**Request:**
```http
PUT http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "currentPassword": "MinhaSenh@123",
  "newPassword": "NovaSenh@456",
  "confirmNewPassword": "NovaSenh@456"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao.silva",
  "message": "Senha alterada com sucesso",
  "changedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Senha alterada com sucesso
- Timestamp da alteração

##### ❌ Cenário 2: Senha atual incorreta

**Request:**
```http
PUT http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "currentPassword": "SenhaIncorreta",
  "newPassword": "NovaSenh@456",
  "confirmNewPassword": "NovaSenh@456"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Senha atual incorreta"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por senha atual incorreta

##### ❌ Cenário 3: Nova senha não atende aos critérios

**Request:**
```http
PUT http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "currentPassword": "MinhaSenh@123",
  "newPassword": "123456",
  "confirmNewPassword": "123456"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Nova senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por senha não atender aos critérios

##### ❌ Cenário 4: Confirmação de senha não confere

**Request:**
```http
PUT http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "currentPassword": "MinhaSenh@123",
  "newPassword": "NovaSenh@456",
  "confirmNewPassword": "NovaSenh@789"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Confirmação da nova senha não confere"
}
```

**Validações:**
- Status Code: `400`
- Erro de validação por confirmação não conferir

---

### 6. Buscar Usuário por ID

**Endpoint:** `GET /api/business/users/{userId}`

**Descrição:** Busca um usuário pelo ID. Requer autenticação JWT e permissão `user:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users/{{user_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar usuário por ID

**Request:**
```http
GET http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "username": "joao.silva",
  "email": "joao@example.com",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "joinDate": "2024-01-15T10:30:00",
  "lastLoginDate": "2024-01-20T14:30:00",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER"],
  "message": "Usuário encontrado",
  "updatedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Usuário encontrado com dados completos

##### ❌ Cenário 2: Usuário não encontrado

**Request:**
```http
GET http://localhost:8089/api/business/users/00000000-0000-0000-0000-000000000000
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation failed",
  "message": "Usuário não encontrado com ID: 00000000-0000-0000-0000-000000000000"
}
```

**Validações:**
- Status Code: `400`
- Erro de negócio por usuário não encontrado

---

### 7. Buscar Usuários com Filtros e Paginação

**Endpoint:** `GET /api/business/users`

**Descrição:** Busca usuários com filtros e paginação. Requer autenticação JWT e permissão `user:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users`
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
GET http://localhost:8089/api/business/users?name=João&isActive=true&page=0&size=10&sortBy=name&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "João Silva",
      "username": "joao.silva",
      "email": "joao@example.com",
      "profileImageUrl": "https://example.com/avatar.jpg",
      "joinDate": "2024-01-15T10:30:00",
      "lastLoginDate": "2024-01-20T14:30:00",
      "isActive": true,
      "isNotLocked": true,
      "roles": ["USER", "MANAGER"],
      "message": "Usuário encontrado",
      "updatedAt": "2024-01-21T10:30:00"
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
    "totalActive": 5,
    "totalInactive": 2,
    "totalBlocked": 1,
    "totalUsers": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista de usuários filtrados
- Informações de paginação
- Totalizadores de usuários

##### ✅ Cenário 2: Sucesso - Buscar com filtro por roles

**Request:**
```http
GET http://localhost:8089/api/business/users?roles=ADMIN,MANAGER&page=0&size=5&sortBy=name&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Admin User",
      "username": "admin",
      "email": "admin@example.com",
      "profileImageUrl": "https://example.com/admin-avatar.jpg",
      "joinDate": "2024-01-10T10:30:00",
      "lastLoginDate": "2024-01-21T09:30:00",
      "isActive": true,
      "isNotLocked": true,
      "roles": ["ADMIN"],
      "message": "Usuário encontrado",
      "updatedAt": "2024-01-21T09:30:00"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 5,
    "hasNext": false,
    "hasPrevious": false
  },
  "counts": {
    "totalActive": 5,
    "totalInactive": 2,
    "totalBlocked": 1,
    "totalUsers": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Lista filtrada por roles
- Informações de paginação
- Totalizadores

##### ✅ Cenário 3: Sucesso - Buscar com filtro por status

**Request:**
```http
GET http://localhost:8089/api/business/users?isActive=false&isNotLocked=true&page=0&size=10&sortBy=name&sortDir=asc
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
{
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "Usuário Inativo",
      "username": "usuario.inativo",
      "email": "inativo@example.com",
      "profileImageUrl": null,
      "joinDate": "2024-01-05T10:30:00",
      "lastLoginDate": null,
      "isActive": false,
      "isNotLocked": true,
      "roles": ["USER"],
      "message": "Usuário encontrado",
      "updatedAt": "2024-01-15T10:30:00"
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
    "totalActive": 5,
    "totalInactive": 2,
    "totalBlocked": 1,
    "totalUsers": 8
  }
}
```

**Validações:**
- Status Code: `200`
- Lista filtrada por status
- Informações de paginação
- Totalizadores

---

### 8. Buscar Todos os Usuários

**Endpoint:** `GET /api/business/users/all`

**Descrição:** Busca todos os usuários cadastrados no sistema (sem filtros). Requer autenticação JWT e permissão `user:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users/all`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista todos os usuários

**Request:**
```http
GET http://localhost:8089/api/business/users/all
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "João Silva",
    "username": "joao.silva",
    "email": "joao@example.com",
    "profileImageUrl": "https://example.com/avatar.jpg",
    "joinDate": "2024-01-15T10:30:00",
    "lastLoginDate": "2024-01-20T14:30:00",
    "isActive": true,
    "isNotLocked": true,
    "roles": ["USER", "MANAGER"],
    "message": "Usuário encontrado",
    "updatedAt": "2024-01-21T10:30:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Admin User",
    "username": "admin",
    "email": "admin@example.com",
    "profileImageUrl": "https://example.com/admin-avatar.jpg",
    "joinDate": "2024-01-10T10:30:00",
    "lastLoginDate": "2024-01-21T09:30:00",
    "isActive": true,
    "isNotLocked": true,
    "roles": ["ADMIN"],
    "message": "Usuário encontrado",
    "updatedAt": "2024-01-21T09:30:00"
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de todos os usuários
- Dados completos de cada usuário

---

### 9. Alterar Status Ativo/Inativo

**Endpoint:** `PATCH /api/business/users/{userId}/status`

**Descrição:** Altera o status ativo/inativo de um usuário. Requer autenticação JWT e permissão `user:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PATCH`
- **URL:** `{{base_url}}/users/{{user_id}}/status`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Ativar usuário

**Request:**
```http
PATCH http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "isActive": true,
  "comment": "Usuário reativado pelo administrador"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao.silva",
  "previousStatus": false,
  "newStatus": true,
  "message": "Status do usuário alterado com sucesso",
  "changedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Status alterado com sucesso
- Timestamp da alteração

##### ✅ Cenário 2: Sucesso - Desativar usuário

**Request:**
```http
PATCH http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/status
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "isActive": false,
  "comment": "Usuário desativado por violação de política"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao.silva",
  "previousStatus": true,
  "newStatus": false,
  "message": "Status do usuário alterado com sucesso",
  "changedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Status alterado com sucesso
- Timestamp da alteração

---

### 10. Alterar Status de Bloqueio

**Endpoint:** `PATCH /api/business/users/{userId}/lock`

**Descrição:** Altera o status de bloqueio de um usuário. Requer autenticação JWT e permissão `user:update`.

#### Configuração no Postman

**Request:**
- **Method:** `PATCH`
- **URL:** `{{base_url}}/users/{{user_id}}/lock`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Desbloquear usuário

**Request:**
```http
PATCH http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/lock
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "isNotLocked": true,
  "comment": "Usuário desbloqueado após verificação"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao.silva",
  "previousLockStatus": false,
  "newLockStatus": true,
  "message": "Status de bloqueio do usuário alterado com sucesso",
  "changedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Status de bloqueio alterado com sucesso
- Timestamp da alteração

##### ✅ Cenário 2: Sucesso - Bloquear usuário

**Request:**
```http
PATCH http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/lock
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Body:**
```json
{
  "isNotLocked": false,
  "comment": "Usuário bloqueado por tentativas de login suspeitas"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao.silva",
  "previousLockStatus": true,
  "newLockStatus": false,
  "message": "Status de bloqueio do usuário alterado com sucesso",
  "changedAt": "2024-01-21T10:30:00"
}
```

**Validações:**
- Status Code: `200`
- Status de bloqueio alterado com sucesso
- Timestamp da alteração

---

### 11. Health Check

**Endpoint:** `GET /api/business/users/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/users/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/users/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "service": "UserController",
  "timestamp": "2024-01-21T10:30:00",
  "version": "1.0"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Status de saúde do controller
- Informações de versão e timestamp

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `{{jwt_token}}` | Token JWT válido (preenchido pelo AuthController) |
| `user_id` | `550e8400-e29b-41d4-a716-446655440000` | ID de um usuário existente para testes |
| `username` | `joao.silva` | Username para testes de disponibilidade |
| `email` | `joao@example.com` | Email para testes de disponibilidade |
| `jwt_token_invalid` | `token_invalido` | Token JWT inválido para testes |
| `jwt_token_expired` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado` | Token JWT expirado para testes |

### Collection Structure

```
📁 UserController API - Business
├── 📁 Users CRUD
│   ├── 📄 POST Create User
│   ├── 📄 PUT Update User
│   ├── 📄 PUT Change Password
│   ├── 📄 PATCH Update User Status
│   ├── 📄 PATCH Update User Lock
│   ├── 📄 GET User by ID
│   ├── 📄 GET All Users with Filters
│   └── 📄 GET All Users
├── 📁 Public Endpoints
│   ├── 📄 GET Check Username Availability
│   ├── 📄 GET Check Email Availability
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create User - Success
    │   ├── 📄 PUT Update User - Success
    │   ├── 📄 PUT Change Password - Success
    │   ├── 📄 PATCH Update Status - Success
    │   ├── 📄 PATCH Update Lock - Success
    │   ├── 📄 GET User by ID - Success
    │   ├── 📄 GET All Users with Filters - Success
    │   └── 📄 GET All Users - Success
    └── 📁 Error Cases
        ├── 📄 POST Create User - Invalid Data
        ├── 📄 PUT Update User - Not Found
        ├── 📄 PUT Change Password - Invalid Current Password
        ├── 📄 PATCH Update Status - Not Found
        ├── 📄 PATCH Update Lock - Not Found
        ├── 📄 GET User by ID - Not Found
        ├── 📄 GET All Users - No Token
        ├── 📄 GET All Users - Invalid Token
        └── 📄 GET All Users - No Permission
```

### Request Templates

#### 1. POST Create User

```json
{
  "name": "POST Create User",
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
      "raw": "{\n  \"name\": \"João Silva\",\n  \"username\": \"joao.silva\",\n  \"email\": \"joao@example.com\",\n  \"password\": \"MinhaSenh@123\",\n  \"profileImageUrl\": \"https://example.com/avatar.jpg\",\n  \"roles\": [\"USER\", \"MANAGER\"]\n}"
    },
    "url": {
      "raw": "{{base_url}}/users/create",
      "host": ["{{base_url}}"],
      "path": ["users", "create"]
    }
  }
}
```

#### 2. PUT Update User

```json
{
  "name": "PUT Update User",
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
      "raw": "{\n  \"name\": \"João Silva Santos\",\n  \"email\": \"joao.santos@example.com\",\n  \"profileImageUrl\": \"https://example.com/new-avatar.jpg\",\n  \"isActive\": true,\n  \"isNotLocked\": true,\n  \"roles\": [\"USER\", \"MANAGER\", \"ADMIN\"]\n}"
    },
    "url": {
      "raw": "{{base_url}}/users/{{user_id}}",
      "host": ["{{base_url}}"],
      "path": ["users", "{{user_id}}"]
    }
  }
}
```

#### 3. PUT Change Password

```json
{
  "name": "PUT Change Password",
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
      "raw": "{\n  \"currentPassword\": \"MinhaSenh@123\",\n  \"newPassword\": \"NovaSenh@456\",\n  \"confirmNewPassword\": \"NovaSenh@456\"\n}"
    },
    "url": {
      "raw": "{{base_url}}/users/{{user_id}}/change-password",
      "host": ["{{base_url}}"],
      "path": ["users", "{{user_id}}", "change-password"]
    }
  }
}
```

#### 4. GET All Users with Filters

```json
{
  "name": "GET All Users with Filters",
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
      "raw": "{{base_url}}/users?name=João&isActive=true&page=0&size=10&sortBy=name&sortDir=asc",
      "host": ["{{base_url}}"],
      "path": ["users"],
      "query": [
        {
          "key": "name",
          "value": "João"
        },
        {
          "key": "isActive",
          "value": "true"
        },
        {
          "key": "page",
          "value": "0"
        },
        {
          "key": "size",
          "value": "10"
        },
        {
          "key": "sortBy",
          "value": "name"
        },
        {
          "key": "sortDir",
          "value": "asc"
        }
      ]
    }
  }
}
```

### Test Scripts (Opcional)

#### Para POST Create User:

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("User has required fields", function () {
    const user = pm.response.json();
    pm.expect(user).to.have.property('id');
    pm.expect(user).to.have.property('name');
    pm.expect(user).to.have.property('username');
    pm.expect(user).to.have.property('email');
    pm.expect(user).to.have.property('isActive');
    pm.expect(user).to.have.property('isNotLocked');
    pm.expect(user).to.have.property('roles');
    pm.expect(user).to.have.property('joinDate');
});

pm.test("ID is valid UUID", function () {
    const user = pm.response.json();
    pm.expect(user.id).to.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i);
});

pm.test("User is active by default", function () {
    const user = pm.response.json();
    pm.expect(user.isActive).to.be.true;
});

pm.test("User is not locked by default", function () {
    const user = pm.response.json();
    pm.expect(user.isNotLocked).to.be.true;
});

// Salvar o ID do usuário criado para testes subsequentes
if (pm.response.code === 201) {
    const user = pm.response.json();
    pm.environment.set("user_id", user.id);
}
```

#### Para PUT Update User:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("User has updated data", function () {
    const user = pm.response.json();
    pm.expect(user.name).to.eql("João Silva Santos");
    pm.expect(user.email).to.eql("joao.santos@example.com");
    pm.expect(user.isActive).to.be.true;
    pm.expect(user.isNotLocked).to.be.true;
});

pm.test("UpdatedAt is newer than JoinDate", function () {
    const user = pm.response.json();
    const joinDate = new Date(user.joinDate);
    const updatedAt = new Date(user.updatedAt);
    pm.expect(updatedAt).to.be.greaterThan(joinDate);
});
```

#### Para PUT Change Password:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Password change response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('userId');
    pm.expect(response).to.have.property('username');
    pm.expect(response).to.have.property('message');
    pm.expect(response).to.have.property('changedAt');
});

pm.test("ChangedAt is valid timestamp", function () {
    const response = pm.response.json();
    const changedAt = new Date(response.changedAt);
    pm.expect(changedAt).to.be.a('date');
});
```

#### Para GET All Users with Filters:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required structure", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('users');
    pm.expect(response).to.have.property('pagination');
    pm.expect(response).to.have.property('counts');
});

pm.test("Users is an array", function () {
    const response = pm.response.json();
    pm.expect(response.users).to.be.an('array');
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
    pm.expect(counts).to.have.property('totalActive');
    pm.expect(counts).to.have.property('totalInactive');
    pm.expect(counts).to.have.property('totalBlocked');
    pm.expect(counts).to.have.property('totalUsers');
});
```

#### Para GET Check Username Availability:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('username');
    pm.expect(response).to.have.property('available');
    pm.expect(response).to.have.property('message');
});

pm.test("Available is boolean", function () {
    const response = pm.response.json();
    pm.expect(response.available).to.be.a('boolean');
});
```

#### Para GET Check Email Availability:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('email');
    pm.expect(response).to.have.property('available');
    pm.expect(response).to.have.property('message');
});

pm.test("Available is boolean", function () {
    const response = pm.response.json();
    pm.expect(response.available).to.be.a('boolean');
});
```

#### Para GET Health Check:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('status');
    pm.expect(response).to.have.property('service');
    pm.expect(response).to.have.property('timestamp');
    pm.expect(response).to.have.property('version');
});

pm.test("Status is UP", function () {
    const response = pm.response.json();
    pm.expect(response.status).to.eql("UP");
});

pm.test("Service is UserController", function () {
    const response = pm.response.json();
    pm.expect(response.service).to.eql("UserController");
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Usuário atualizado, senha alterada, status alterado, usuários listados |
| `201` | Created | Usuário criado com sucesso |
| `400` | Bad Request | Dados inválidos, senha incorreta, confirmação não confere |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de CRUD

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Verificar disponibilidade**
   - Request: `GET /api/business/users/check-username/novo.usuario`
   - Expected: Status 200, Username disponível

3. **Criar usuário**
   - Request: `POST /api/business/users/create`
   - Expected: Status 201, Usuário criado

4. **Buscar usuário por ID**
   - Request: `GET /api/business/users/{id}`
   - Expected: Status 200, Dados do usuário

5. **Atualizar usuário**
   - Request: `PUT /api/business/users/{id}`
   - Expected: Status 200, Usuário atualizado

6. **Alterar senha**
   - Request: `PUT /api/business/users/{id}/change-password`
   - Expected: Status 200, Senha alterada

7. **Alterar status**
   - Request: `PATCH /api/business/users/{id}/status`
   - Expected: Status 200, Status alterado

8. **Alterar bloqueio**
   - Request: `PATCH /api/business/users/{id}/lock`
   - Expected: Status 200, Bloqueio alterado

9. **Buscar com filtros**
   - Request: `GET /api/business/users?name=João&isActive=true`
   - Expected: Status 200, Lista filtrada

10. **Buscar todos**
    - Request: `GET /api/business/users/all`
    - Expected: Status 200, Lista completa

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

1. **Sem permissão user:create**
   - Request: Token válido mas sem `user:create`
   - Expected: Status 403

2. **Sem permissão user:update**
   - Request: Token válido mas sem `user:update`
   - Expected: Status 403

3. **Sem permissão user:read**
   - Request: Token válido mas sem `user:read`
   - Expected: Status 403

4. **Sem permissão user:change-password**
   - Request: Token válido mas sem `user:change-password`
   - Expected: Status 403

### Teste 4: Validação de Dados

1. **Nome inválido (muito curto)**
   - Request: `name: "A"`
   - Expected: Status 400

2. **Username inválido (caracteres especiais)**
   - Request: `username: "joao@silva"`
   - Expected: Status 400

3. **Email inválido**
   - Request: `email: "email-invalido"`
   - Expected: Status 400

4. **Senha muito curta**
   - Request: `password: "123"`
   - Expected: Status 400

5. **Senha não atende aos critérios**
   - Request: `password: "123456"`
   - Expected: Status 400

6. **Roles vazias**
   - Request: `roles: []`
   - Expected: Status 400

### Teste 5: Validação de Negócio

1. **Username duplicado**
   - Request: `username: "admin"` (já existe)
   - Expected: Status 400

2. **Email duplicado**
   - Request: `email: "admin@example.com"` (já existe)
   - Expected: Status 400

3. **Usuário não encontrado**
   - Request: PUT/PATCH com ID inexistente
   - Expected: Status 400

4. **Senha atual incorreta**
   - Request: `currentPassword: "SenhaIncorreta"`
   - Expected: Status 400

5. **Confirmação de senha não confere**
   - Request: `confirmNewPassword: "SenhaDiferente"`
   - Expected: Status 400

### Teste 6: Endpoints Públicos

1. **Check username disponível**
   - Request: `GET /api/business/users/check-username/novo.usuario`
   - Expected: Status 200, Username disponível

2. **Check username já existe**
   - Request: `GET /api/business/users/check-username/admin`
   - Expected: Status 200, Username não disponível

3. **Check email disponível**
   - Request: `GET /api/business/users/check-email/novo@example.com`
   - Expected: Status 200, Email disponível

4. **Check email já existe**
   - Request: `GET /api/business/users/check-email/admin@example.com`
   - Expected: Status 200, Email não disponível

5. **Health check**
   - Request: `GET /api/business/users/health`
   - Expected: Status 200, Status de saúde

### Teste 7: Busca com Filtros

1. **Filtro por nome**
   - Request: `GET /api/business/users?name=João`
   - Expected: Status 200, Lista filtrada

2. **Filtro por roles**
   - Request: `GET /api/business/users?roles=ADMIN,MANAGER`
   - Expected: Status 200, Lista filtrada

3. **Filtro por status ativo**
   - Request: `GET /api/business/users?isActive=true`
   - Expected: Status 200, Lista filtrada

4. **Filtro por status bloqueado**
   - Request: `GET /api/business/users?isNotLocked=false`
   - Expected: Status 200, Lista filtrada

5. **Filtro combinado**
   - Request: `GET /api/business/users?name=João&isActive=true&roles=USER`
   - Expected: Status 200, Lista filtrada

### Teste 8: Paginação

1. **Primeira página**
   - Request: `GET /api/business/users?page=0&size=5`
   - Expected: Status 200, Primeira página

2. **Segunda página**
   - Request: `GET /api/business/users?page=1&size=5`
   - Expected: Status 200, Segunda página

3. **Ordenação por nome**
   - Request: `GET /api/business/users?sortBy=name&sortDir=asc`
   - Expected: Status 200, Lista ordenada

4. **Ordenação por data**
   - Request: `GET /api/business/users?sortBy=joinDate&sortDir=desc`
   - Expected: Status 200, Lista ordenada

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para criar usuário: joao.silva
INFO  - Usuário criado com sucesso: joao.silva
INFO  - Recebida requisição para atualizar usuário: João Silva Santos (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Usuário atualizado com sucesso: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para alterar senha do usuário: 550e8400-e29b-41d4-a716-446655440000
INFO  - Senha alterada com sucesso para usuário: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Recebida requisição para alterar status do usuário: ativo (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Status do usuário alterado com sucesso: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000) - ativo -> ativo
INFO  - Recebida requisição para alterar status de bloqueio do usuário: não bloqueado (ID: 550e8400-e29b-41d4-a716-446655440000)
INFO  - Status de bloqueio do usuário alterado com sucesso: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000) - não bloqueado -> não bloqueado
INFO  - Recebida requisição para buscar usuários com filtros: name=João, roles=null, isActive=true, isNotLocked=null, page=0, size=10, sortBy=name, sortDir=asc
INFO  - Retornando 1 usuários da página 1 de 1 total, com totalizadores: ativos=5, inativos=2, bloqueados=1
INFO  - Recebida requisição para buscar todos os usuários sem filtros
INFO  - Retornando 8 usuários encontrados
```

### Logs de Erro

```
ERROR - Erro ao criar usuário: Username já está em uso
ERROR - Erro ao atualizar usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao alterar senha do usuário 550e8400-e29b-41d4-a716-446655440000: Senha atual incorreta
ERROR - Erro ao alterar status do usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao alterar status de bloqueio do usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao buscar usuários: Connection timeout
```

### Logs de Debug

```
DEBUG - Verificando disponibilidade do username: novo.usuario
DEBUG - Verificando disponibilidade do email: novo@example.com
DEBUG - Recebida requisição para buscar usuário: 550e8400-e29b-41d4-a716-446655440000
DEBUG - Usuário encontrado: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000)
DEBUG - Health check do UserController
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Criar Usuário com Token Válido

```bash
curl -X POST "http://localhost:8089/api/business/users/create" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "João Silva",
    "username": "joao.silva",
    "email": "joao@example.com",
    "password": "MinhaSenh@123",
    "profileImageUrl": "https://example.com/avatar.jpg",
    "roles": ["USER", "MANAGER"]
  }'
```

### Exemplo 2: Atualizar Usuário

```bash
curl -X PUT "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "João Silva Santos",
    "email": "joao.santos@example.com",
    "profileImageUrl": "https://example.com/new-avatar.jpg",
    "isActive": true,
    "isNotLocked": true,
    "roles": ["USER", "MANAGER", "ADMIN"]
  }'
```

### Exemplo 3: Alterar Senha

```bash
curl -X PUT "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "currentPassword": "MinhaSenh@123",
    "newPassword": "NovaSenh@456",
    "confirmNewPassword": "NovaSenh@456"
  }'
```

### Exemplo 4: Buscar Usuários com Filtros

```bash
curl -X GET "http://localhost:8089/api/business/users?name=João&isActive=true&page=0&size=10&sortBy=name&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Exemplo 5: Verificar Disponibilidade de Username

```bash
curl -X GET "http://localhost:8089/api/business/users/check-username/novo.usuario" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 6: Verificar Disponibilidade de Email

```bash
curl -X GET "http://localhost:8089/api/business/users/check-email/novo@example.com" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 7: Alterar Status do Usuário

```bash
curl -X PATCH "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/status" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "isActive": false,
    "comment": "Usuário desativado por violação de política"
  }'
```

### Exemplo 8: Alterar Status de Bloqueio

```bash
curl -X PATCH "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/lock" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "isNotLocked": false,
    "comment": "Usuário bloqueado por tentativas de login suspeitas"
  }'
```

### Exemplo 9: Health Check

```bash
curl -X GET "http://localhost:8089/api/business/users/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`user:create`, `user:update`, `user:read`, `user:change-password`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Relacionamentos**: Dependência com roles existentes

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

### Diferenças dos Controllers Anteriores

1. **Controller mais complexo**: 11 endpoints (vs 2-7 dos anteriores)
2. **Múltiplos métodos HTTP**: POST, PUT, PATCH, GET (vs apenas GET)
3. **CRUD completo**: Create, Read, Update com operações específicas
4. **Busca avançada**: Filtros, paginação e totalizadores
5. **Validações complexas**: Pattern para senha, Email, etc
6. **Endpoints públicos**: Verificação de disponibilidade
7. **Response complexo**: Estruturas aninhadas (pagination, counts)
8. **Relacionamentos**: Set<String> de roles
9. **Query parameters**: Filtros e paginação
10. **Operações específicas**: Senha, status, bloqueio

### Segurança

1. **JWT Validation**: Token validado em cada requisição
2. **Permission-based Access**: Controle granular por permissões
3. **CORS**: Restrito ao frontend
4. **PreAuthorize**: Validação de permissões no controller
5. **Logging**: Logs estruturados para auditoria
6. **Validation**: Bean Validation com regras específicas
7. **Password Security**: Critérios complexos para senhas
8. **Data Protection**: Senhas não expostas em logs

### Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem permissões adequadas
5. **Response**: Receber dados do usuário ou lista de usuários

### Validações de Negócio

1. **Username Único**: Não permite usuários com mesmo username
2. **Email Único**: Não permite usuários com mesmo email
3. **Roles Válidas**: Todas as roles devem existir
4. **Status**: Controle de ativo/inativo e bloqueado/não bloqueado
5. **Relacionamentos**: Dependência com roles
6. **Integridade**: Validação de dados antes de persistir
7. **Password Security**: Critérios complexos para senhas
8. **Data Consistency**: Validação de dados em operações de atualização

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
