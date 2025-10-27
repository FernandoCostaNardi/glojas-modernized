# Guia de Configuração do Postman - UserController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Usuários (`UserController`) da Business API usando o Postman. O controller oferece 11 endpoints para CRUD completo de usuários, alteração de senha, busca avançada com filtros e paginação, e gerenciamento de status com autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Selecione o arquivo `UserController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `UserController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"UserController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `user_id`: ID de um usuário existente para testes
- `username`: Username para testes de disponibilidade
- `email`: Email para testes de disponibilidade

#### Variáveis de Teste
- `test_username`: Username para testes de criação
- `test_email`: Email para testes de criação
- `test_password`: Senha para testes de criação
- `test_name`: Nome para testes de criação
- `test_roles`: Roles para testes de criação

#### Variáveis de Validação
- `invalid_username`: Username inválido para testes
- `invalid_email`: Email inválido para testes
- `short_password`: Senha muito curta para testes
- `weak_password`: Senha fraca para testes
- `strong_password`: Senha forte para testes

## 🔐 Autenticação JWT

### 1. Obter Token JWT

**IMPORTANTE**: Antes de testar os endpoints protegidos, você deve obter um token JWT válido usando o `AuthController`.

#### Passo 1: Fazer Login
1. Use o `AuthController` para fazer login
2. Endpoint: `POST /api/business/auth/login`
3. Body:
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

#### Passo 2: Copiar Token
1. Na resposta do login, copie o valor do campo `token`
2. Cole o token na variável `jwt_token` do environment

#### Passo 3: Verificar Token
1. Execute o endpoint `GET /api/business/users/health`
2. Se retornar status 200, o token está funcionando

### 2. Configurar Headers de Autenticação

Os endpoints protegidos já estão configurados com o header:
```
Authorization: Bearer {{jwt_token}}
```

## 📁 Estrutura da Collection

### 1. Users CRUD
- **POST Create User**: Criar novo usuário
- **PUT Update User**: Atualizar usuário existente
- **PUT Change Password**: Alterar senha do usuário
- **PATCH Update User Status**: Alterar status ativo/inativo
- **PATCH Update User Lock**: Alterar status de bloqueio
- **GET User by ID**: Buscar usuário por ID
- **GET All Users with Filters**: Buscar com filtros e paginação
- **GET All Users**: Buscar todos os usuários

### 2. Public Endpoints
- **GET Check Username Availability**: Verificar disponibilidade de username
- **GET Check Email Availability**: Verificar disponibilidade de email
- **GET Health Check**: Verificar saúde do controller

### 3. Test Scenarios
- **Success Cases**: Cenários de sucesso
- **Error Cases**: Cenários de erro

## 🧪 Cenários de Teste

### 1. Fluxo Completo de CRUD

#### Passo 1: Verificar Disponibilidade
```http
GET {{base_url}}/users/check-username/{{test_username}}
GET {{base_url}}/users/check-email/{{test_email}}
```

#### Passo 2: Criar Usuário
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{test_email}}",
  "password": "{{test_password}}",
  "profileImageUrl": "{{test_profile_image}}",
  "roles": ["USER", "MANAGER"]
}
```

#### Passo 3: Buscar Usuário por ID
```http
GET {{base_url}}/users/{{user_id}}
Authorization: Bearer {{jwt_token}}
```

#### Passo 4: Atualizar Usuário
```http
PUT {{base_url}}/users/{{user_id}}
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}} Atualizado",
  "email": "{{test_email}}",
  "profileImageUrl": "{{test_profile_image}}",
  "isActive": true,
  "isNotLocked": true,
  "roles": ["USER", "MANAGER", "ADMIN"]
}
}
```

#### Passo 5: Alterar Senha
```http
PUT {{base_url}}/users/{{user_id}}/change-password
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "currentPassword": "{{test_password}}",
  "newPassword": "{{new_strong_password}}",
  "confirmNewPassword": "{{new_strong_password}}"
}
```

#### Passo 6: Alterar Status
```http
PATCH {{base_url}}/users/{{user_id}}/status
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "isActive": false,
  "comment": "{{deactivation_comment}}"
}
```

#### Passo 7: Alterar Bloqueio
```http
PATCH {{base_url}}/users/{{user_id}}/lock
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "isNotLocked": false,
  "comment": "{{lock_comment_negative}}"
}
```

#### Passo 8: Buscar com Filtros
```http
GET {{base_url}}/users?name={{filter_name}}&isActive={{filter_is_active}}&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```

#### Passo 9: Buscar Todos
```http
GET {{base_url}}/users/all
Authorization: Bearer {{jwt_token}}
```

### 2. Testes de Validação

#### Teste 1: Username Inválido
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{invalid_username}}",
  "email": "{{test_email}}",
  "password": "{{strong_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Erro de validação

#### Teste 2: Email Inválido
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{invalid_email}}",
  "password": "{{strong_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Erro de validação

#### Teste 3: Senha Muito Curta
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{test_email}}",
  "password": "{{short_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Erro de validação

#### Teste 4: Senha Fraca
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{test_email}}",
  "password": "{{weak_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Erro de validação

#### Teste 5: Roles Vazias
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{test_email}}",
  "password": "{{strong_password}}",
  "roles": []
}
```
**Expected**: Status 400, Erro de validação

### 3. Testes de Autenticação

#### Teste 1: Token Ausente
```http
GET {{base_url}}/users
```
**Expected**: Status 401, Não autorizado

#### Teste 2: Token Inválido
```http
GET {{base_url}}/users
Authorization: Bearer {{jwt_token_invalid}}
```
**Expected**: Status 401, Token inválido

#### Teste 3: Token Expirado
```http
GET {{base_url}}/users
Authorization: Bearer {{jwt_token_expired}}
```
**Expected**: Status 401, Token expirado

#### Teste 4: Sem Permissão
```http
GET {{base_url}}/users
Authorization: Bearer {{jwt_token_sem_permissao}}
```
**Expected**: Status 403, Sem permissão

### 4. Testes de Negócio

#### Teste 1: Username Duplicado
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{admin_username}}",
  "email": "{{test_email}}",
  "password": "{{strong_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Username já existe

#### Teste 2: Email Duplicado
```http
POST {{base_url}}/users/create
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "{{test_name}}",
  "username": "{{test_username}}",
  "email": "{{admin_email}}",
  "password": "{{strong_password}}",
  "roles": ["USER"]
}
```
**Expected**: Status 400, Email já existe

#### Teste 3: Usuário Não Encontrado
```http
PUT {{base_url}}/users/{{nonexistent_user_id}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 400, Usuário não encontrado

#### Teste 4: Senha Atual Incorreta
```http
PUT {{base_url}}/users/{{user_id}}/change-password
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "currentPassword": "{{weak_password}}",
  "newPassword": "{{new_strong_password}}",
  "confirmNewPassword": "{{new_strong_password}}"
}
```
**Expected**: Status 400, Senha atual incorreta

#### Teste 5: Confirmação de Senha Não Confere
```http
PUT {{base_url}}/users/{{user_id}}/change-password
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "currentPassword": "{{strong_password}}",
  "newPassword": "{{new_strong_password}}",
  "confirmNewPassword": "{{not_matching_password}}"
}
```
**Expected**: Status 400, Confirmação não confere

### 5. Testes de Endpoints Públicos

#### Teste 1: Username Disponível
```http
GET {{base_url}}/users/check-username/{{available_username}}
```
**Expected**: Status 200, Username disponível

#### Teste 2: Username Não Disponível
```http
GET {{base_url}}/users/check-username/{{unavailable_username}}
```
**Expected**: Status 200, Username não disponível

#### Teste 3: Email Disponível
```http
GET {{base_url}}/users/check-email/{{available_email}}
```
**Expected**: Status 200, Email disponível

#### Teste 4: Email Não Disponível
```http
GET {{base_url}}/users/check-email/{{unavailable_email}}
```
**Expected**: Status 200, Email não disponível

#### Teste 5: Health Check
```http
GET {{base_url}}/users/health
```
**Expected**: Status 200, Status de saúde

### 6. Testes de Busca com Filtros

#### Teste 1: Filtro por Nome
```http
GET {{base_url}}/users?name={{filter_name}}&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista filtrada

#### Teste 2: Filtro por Roles
```http
GET {{base_url}}/users?roles={{filter_roles}}&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista filtrada

#### Teste 3: Filtro por Status Ativo
```http
GET {{base_url}}/users?isActive={{filter_is_active}}&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista filtrada

#### Teste 4: Filtro por Status Bloqueado
```http
GET {{base_url}}/users?isNotLocked={{filter_is_not_locked}}&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista filtrada

#### Teste 5: Filtro Combinado
```http
GET {{base_url}}/users?name={{filter_name}}&isActive={{filter_is_active}}&roles=USER&page={{page_number}}&size={{page_size}}&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista filtrada

### 7. Testes de Paginação

#### Teste 1: Primeira Página
```http
GET {{base_url}}/users?page=0&size=5&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Primeira página

#### Teste 2: Segunda Página
```http
GET {{base_url}}/users?page=1&size=5&sortBy={{sort_by}}&sortDir={{sort_direction}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Segunda página

#### Teste 3: Ordenação por Nome
```http
GET {{base_url}}/users?sortBy=name&sortDir=asc&page={{page_number}}&size={{page_size}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista ordenada por nome

#### Teste 4: Ordenação por Data
```http
GET {{base_url}}/users?sortBy=joinDate&sortDir=desc&page={{page_number}}&size={{page_size}}
Authorization: Bearer {{jwt_token}}
```
**Expected**: Status 200, Lista ordenada por data

## 🔧 Configuração Avançada

### 1. Scripts de Teste Automatizados

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

### 2. Configuração de Timeout

#### Timeout Global
1. Vá em **Settings** (ícone de engrenagem)
2. Selecione **General**
3. Configure **Request timeout in ms** para `30000` (30 segundos)

#### Timeout por Request
1. Abra um request
2. Vá na aba **Settings**
3. Configure **Request timeout** para `30000`

### 3. Configuração de Proxy

#### Se necessário usar proxy:
1. Vá em **Settings** (ícone de engrenagem)
2. Selecione **Proxy**
3. Configure as informações do proxy

### 4. Configuração de SSL

#### Se necessário desabilitar SSL:
1. Vá em **Settings** (ícone de engrenagem)
2. Selecione **General**
3. Desmarque **SSL certificate verification**

## 📊 Monitoramento e Logs

### 1. Logs de Sucesso

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

### 2. Logs de Erro

```
ERROR - Erro ao criar usuário: Username já está em uso
ERROR - Erro ao atualizar usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao alterar senha do usuário 550e8400-e29b-41d4-a716-446655440000: Senha atual incorreta
ERROR - Erro ao alterar status do usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao alterar status de bloqueio do usuário 550e8400-e29b-41d4-a716-446655440000: Usuário não encontrado com ID: 550e8400-e29b-41d4-a716-446655440000
ERROR - Erro ao buscar usuários: Connection timeout
```

### 3. Logs de Debug

```
DEBUG - Verificando disponibilidade do username: novo.usuario
DEBUG - Verificando disponibilidade do email: novo@example.com
DEBUG - Recebida requisição para buscar usuário: 550e8400-e29b-41d4-a716-446655440000
DEBUG - Usuário encontrado: joao.silva (ID: 550e8400-e29b-41d4-a716-446655440000)
DEBUG - Health check do UserController
```

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 401 - Unauthorized
- **Causa**: Token JWT ausente, inválido ou expirado
- **Solução**: Obter novo token usando AuthController

#### Erro 403 - Forbidden
- **Causa**: Usuário sem permissões adequadas
- **Solução**: Verificar se o usuário tem as permissões necessárias

#### Erro 400 - Bad Request
- **Causa**: Dados inválidos ou usuário não encontrado
- **Solução**: Verificar dados enviados e se o usuário existe

#### Erro 500 - Internal Server Error
- **Causa**: Erro interno do servidor
- **Solução**: Verificar logs do servidor

### 2. Validação de Dados

#### Username
- Deve conter apenas letras, números e underscore
- Exemplo válido: `joao.silva`
- Exemplo inválido: `joao@silva`

#### Email
- Deve ser um email válido
- Exemplo válido: `joao@example.com`
- Exemplo inválido: `email-invalido`

#### Senha
- Deve ter pelo menos 6 caracteres
- Deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial
- Exemplo válido: `MinhaSenh@123`
- Exemplo inválido: `123456`

#### Roles
- Deve ser um array não vazio
- Exemplo válido: `["USER", "MANAGER"]`
- Exemplo inválido: `[]`

### 3. Verificação de Ambiente

#### Verificar se a API está rodando:
```bash
curl http://localhost:8089/api/business/users/health
```

#### Verificar se o banco está conectado:
```bash
curl http://localhost:8089/api/business/users/health
```

#### Verificar se o JWT está funcionando:
```bash
curl -H "Authorization: Bearer SEU_TOKEN" http://localhost:8089/api/business/users/health
```

## 📝 Exemplos Práticos

### 1. Criar Usuário com cURL

```bash
curl -X POST "http://localhost:8089/api/business/users/create" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "name": "João Silva",
    "username": "joao.silva",
    "email": "joao@example.com",
    "password": "MinhaSenh@123",
    "profileImageUrl": "https://example.com/avatar.jpg",
    "roles": ["USER", "MANAGER"]
  }'
```

### 2. Atualizar Usuário com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "name": "João Silva Santos",
    "email": "joao.santos@example.com",
    "profileImageUrl": "https://example.com/new-avatar.jpg",
    "isActive": true,
    "isNotLocked": true,
    "roles": ["USER", "MANAGER", "ADMIN"]
  }'
```

### 3. Alterar Senha com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/change-password" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "currentPassword": "MinhaSenh@123",
    "newPassword": "NovaSenh@456",
    "confirmNewPassword": "NovaSenh@456"
  }'
```

### 4. Buscar Usuários com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/users?name=João&isActive=true&page=0&size=10&sortBy=name&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### 5. Verificar Disponibilidade de Username com cURL

```bash
curl -X GET "http://localhost:8089/api/business/users/check-username/novo.usuario" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### 6. Verificar Disponibilidade de Email com cURL

```bash
curl -X GET "http://localhost:8089/api/business/users/check-email/novo@example.com" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### 7. Alterar Status do Usuário com cURL

```bash
curl -X PATCH "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/status" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "isActive": false,
    "comment": "Usuário desativado por violação de política"
  }'
```

### 8. Alterar Status de Bloqueio com cURL

```bash
curl -X PATCH "http://localhost:8089/api/business/users/550e8400-e29b-41d4-a716-446655440000/lock" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "isNotLocked": false,
    "comment": "Usuário bloqueado por tentativas de login suspeitas"
  }'
```

### 9. Health Check com cURL

```bash
curl -X GET "http://localhost:8089/api/business/users/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

## ⚠️ Considerações Importantes

### 1. Limitações da API

- **Autenticação JWT**: Token obrigatório para endpoints protegidos
- **Autorização**: Requer permissões específicas (`user:create`, `user:update`, `user:read`, `user:change-password`)
- **CORS**: Configurado para `http://localhost:3000`
- **Validação**: Bean Validation com regras específicas
- **Relacionamentos**: Dependência com roles existentes

### 2. Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Connection Pool**: HikariCP com configuração otimizada
- **ORM**: Hibernate com dialect PostgreSQL
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### 3. Performance

- **Connection Pool**: Máximo 10 conexões
- **JWT Validation**: Validação de token em cada requisição
- **CORS**: Configurado para frontend React
- **Logging**: INFO para com.sysconard.business
- **Transactions**: @Transactional para operações de escrita
- **Paginação**: Suporte a paginação com filtros
- **Busca**: Filtros otimizados com índices

### 4. Segurança

- **JWT Validation**: Token validado em cada requisição
- **Permission-based Access**: Controle granular por permissões
- **CORS**: Restrito ao frontend
- **PreAuthorize**: Validação de permissões no controller
- **Logging**: Logs estruturados para auditoria
- **Validation**: Bean Validation com regras específicas
- **Password Security**: Critérios complexos para senhas
- **Data Protection**: Senhas não expostas em logs

### 5. Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem permissões adequadas
5. **Response**: Receber dados do usuário ou lista de usuários

### 6. Validações de Negócio

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
