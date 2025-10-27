# Documentação AuthController - Business API

## 📋 Visão Geral

O `AuthController` é responsável por gerenciar a autenticação de usuários na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para autenticação JWT com validação de credenciais.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token
- **Método**: POST (com request body)

### Estrutura do Request DTO

```json
{
  "email": "admin@glojas.com",
  "password": "senha123"
}
```

**Campos:**
- `email`: Email do usuário (obrigatório, formato válido)
- `password`: Senha do usuário (obrigatório)

### Estrutura do Response DTO

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "name": "Administrador",
  "roles": ["ADMIN", "USER"],
  "permissions": ["READ", "WRITE", "DELETE"]
}
```

**Campos:**
- `token`: Token JWT para autenticação
- `username`: Nome de usuário
- `name`: Nome completo do usuário
- `roles`: Lista de roles do usuário
- `permissions`: Lista de permissões do usuário

---

## 🚀 Endpoints Disponíveis

### 1. Autenticação de Usuário

**Endpoint:** `POST /api/business/auth/login`

**Descrição:** Autentica um usuário e retorna um token JWT com informações de roles e permissões.

#### Configuração no Postman

**Request:**
- **Method:** `POST`
- **URL:** `{{base_url}}/auth/login`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```
- **Body:** JSON com email e password

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Login válido

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "admin@glojas.com",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBnbG9qYXMuY29tIiwiaWF0IjoxNzM1MzQ1NjAwLCJleHAiOjE3MzU0MzIwMDB9.example",
  "username": "admin",
  "name": "Administrador",
  "roles": ["ADMIN", "USER"],
  "permissions": ["READ", "WRITE", "DELETE", "MANAGE_USERS"]
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Token JWT válido e não vazio
- Username e name presentes
- Roles e permissions como arrays não vazios

##### ❌ Cenário 2: Email ausente

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "password": "senha123"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro de validação",
  "details": {
    "email": "Email é obrigatório"
  }
}
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por campo obrigatório ausente

##### ❌ Cenário 3: Email inválido

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "email-invalido",
  "password": "senha123"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro de validação",
  "details": {
    "email": "Formato de email inválido"
  }
}
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por formato de email inválido

##### ❌ Cenário 4: Senha ausente

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "admin@glojas.com"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro de validação",
  "details": {
    "password": "Senha é obrigatória"
  }
}
```

**Validações:**
- Status Code: `400`
- Bean Validation falha por campo obrigatório ausente

##### ❌ Cenário 5: Credenciais inválidas

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "admin@glojas.com",
  "password": "senha_incorreta"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro na autenticação",
  "message": "Bad credentials"
}
```

**Validações:**
- Status Code: `400`
- Erro de autenticação por credenciais incorretas

##### ❌ Cenário 6: Usuário não encontrado

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "usuario@inexistente.com",
  "password": "senha123"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro na autenticação",
  "message": "Bad credentials"
}
```

**Validações:**
- Status Code: `400`
- Erro de autenticação por usuário não encontrado

##### ⚠️ Cenário 7: Erro interno do servidor

**Request:**
```http
POST http://localhost:8089/api/business/auth/login
Content-Type: application/json
Accept: application/json

{
  "email": "admin@glojas.com",
  "password": "senha123"
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
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `valid_email` | `admin@glojas.com` | Email válido para testes |
| `valid_password` | `senha123` | Senha válida para testes |
| `invalid_email` | `email-invalido` | Email inválido para testes |
| `invalid_password` | `senha_incorreta` | Senha incorreta para testes |
| `nonexistent_email` | `usuario@inexistente.com` | Email de usuário inexistente |

### Collection Structure

```
📁 AuthController API - Business
├── 📁 Authentication
│   └── 📄 POST Login
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   └── 📄 POST Login - Success
    └── 📁 Error Cases
        ├── 📄 POST Login - Missing Email
        ├── 📄 POST Login - Invalid Email
        ├── 📄 POST Login - Missing Password
        ├── 📄 POST Login - Invalid Credentials
        └── 📄 POST Login - User Not Found
```

### Request Template

#### POST Login

```json
{
  "name": "POST Login",
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
      "raw": "{{base_url}}/auth/login",
      "host": ["{{base_url}}"],
      "path": ["auth", "login"]
    },
    "body": {
      "mode": "raw",
      "raw": "{\n  \"email\": \"{{valid_email}}\",\n  \"password\": \"{{valid_password}}\"\n}",
      "options": {
        "raw": {
          "language": "json"
        }
      }
    }
  }
}
```

### Test Scripts (Opcional)

#### Para POST Login:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Response has required fields", function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('token');
    pm.expect(response).to.have.property('username');
    pm.expect(response).to.have.property('name');
    pm.expect(response).to.have.property('roles');
    pm.expect(response).to.have.property('permissions');
});

pm.test("Token is not empty", function () {
    const response = pm.response.json();
    pm.expect(response.token).to.not.be.empty;
});

pm.test("Roles is an array", function () {
    const response = pm.response.json();
    pm.expect(response.roles).to.be.an('array');
});

pm.test("Permissions is an array", function () {
    const response = pm.response.json();
    pm.expect(response.permissions).to.be.an('array');
});

// Salvar token para uso em outras requisições
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwt_token", response.token);
    pm.environment.set("user_roles", JSON.stringify(response.roles));
    pm.environment.set("user_permissions", JSON.stringify(response.permissions));
}
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Login realizado com sucesso |
| `400` | Bad Request | Erro de validação ou credenciais inválidas |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sucesso

1. **Login com credenciais válidas**
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido, Roles e permissions

### Teste 2: Validação de Campos Obrigatórios

1. **Email ausente**
   - Request: Sem campo `email`
   - Expected: Status 400, Erro de validação

2. **Senha ausente**
   - Request: Sem campo `password`
   - Expected: Status 400, Erro de validação

### Teste 3: Validação de Formato

1. **Email inválido**
   - Request: `email: "email-invalido"`
   - Expected: Status 400, Erro de formato

2. **Email válido**
   - Request: `email: "admin@glojas.com"`
   - Expected: Status 200 (se senha correta)

### Teste 4: Validação de Credenciais

1. **Credenciais incorretas**
   - Request: Email válido + senha incorreta
   - Expected: Status 400, Erro de autenticação

2. **Usuário inexistente**
   - Request: Email inexistente + senha qualquer
   - Expected: Status 400, Erro de autenticação

### Teste 5: Validação de Token JWT

1. **Token presente**
   - Expected: Token não vazio e válido

2. **Informações do usuário**
   - Expected: Username, name, roles e permissions presentes

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - === INICIANDO LOGIN ===
INFO  - Email recebido: admin@glojas.com
INFO  - Password recebido: ***
INFO  - Criando token de autenticação...
INFO  - Tentando autenticar com AuthenticationManager...
INFO  - Autenticação bem-sucedida!
INFO  - Gerando token JWT...
INFO  - Token JWT gerado com sucesso!
INFO  - Usuário encontrado: admin (ID: 1)
INFO  - Email do usuário: admin@glojas.com
INFO  - Extraindo roles e permissions...
INFO  - Roles encontradas: [ADMIN, USER]
INFO  - Permissions encontradas: [READ, WRITE, DELETE, MANAGE_USERS]
INFO  - === LOGIN CONCLUÍDO COM SUCESSO ===
```

### Logs de Erro

```
ERROR - === ERRO NO LOGIN ===
ERROR - Exceção capturada: BadCredentialsException
ERROR - Mensagem de erro: Bad credentials
ERROR - Stack trace: [stack trace completo]
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Login com Credenciais Válidas

```bash
curl -X POST "http://localhost:8089/api/business/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "email": "admin@glojas.com",
    "password": "senha123"
  }'
```

### Exemplo 2: Teste de Validação

```bash
curl -X POST "http://localhost:8089/api/business/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "email": "email-invalido",
    "password": "senha123"
  }'
```

### Exemplo 3: Teste de Credenciais Incorretas

```bash
curl -X POST "http://localhost:8089/api/business/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "email": "admin@glojas.com",
    "password": "senha_incorreta"
  }'
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Requer token válido para endpoints protegidos
2. **Validação Rigorosa**: Bean Validation com Jakarta (Java 17)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Timeout**: Token expira em 24 horas
5. **Segurança**: Senhas são criptografadas com BCrypt

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Connection Pool**: HikariCP com configuração otimizada
- **ORM**: Hibernate com dialect PostgreSQL
- **Security**: Spring Security com JWT
- **Validation**: Bean Validation 3.x (Jakarta)

### Performance

- **Connection Pool**: Máximo 10 conexões
- **JWT Expiration**: 24 horas (86400000ms)
- **CORS**: Configurado para frontend React
- **Logging**: DEBUG para com.sysconard.business

### Diferenças da Legacy API

1. **Porta**: 8089 (vs 8087 da Legacy)
2. **Context Path**: `/api/business` (vs `/api/legacy`)
3. **Autenticação**: JWT (vs read-only)
4. **Validação**: Jakarta (vs javax)
5. **DTOs**: Records (vs Lombok classes)
6. **Database**: PostgreSQL (vs SQL Server)
7. **Java**: 17 (vs 8)

### Segurança

1. **JWT Secret**: Configurado no application.yml
2. **Password Encoding**: BCrypt automático
3. **CORS**: Restrito ao frontend
4. **Token Expiration**: 24 horas
5. **Role-based Access**: Roles e permissions

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
