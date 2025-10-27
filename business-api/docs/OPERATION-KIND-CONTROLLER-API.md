# Documentação OperationKindController - Business API

## 📋 Visão Geral

O `OperationKindController` é responsável por gerenciar os tipos de operação (OperationKind) na Business API. Este controller segue os princípios de Clean Code e oferece endpoints para buscar tipos de operação disponíveis para cadastro, integrando com a Legacy API e aplicando regras de negócio para filtrar apenas os tipos que ainda não foram cadastrados no sistema.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com JWT
- **Autenticação**: JWT Token obrigatório
- **Métodos**: GET (somente leitura)
- **Integração**: Legacy API para busca de dados

### Estrutura do Response DTO

```json
{
  "id": "000001",
  "description": "Venda de Produtos"
}
```

**Campos:**
- `id`: Identificador único do tipo de operação formatado com 6 dígitos (ex: 000001)
- `description`: Descrição do tipo de operação

---

## 🚀 Endpoints Disponíveis

### 1. Buscar Tipos de Operação Disponíveis

**Endpoint:** `GET /api/business/operation-kinds`

**Descrição:** Busca todos os tipos de operação disponíveis para cadastro. Retorna apenas os tipos de operação da Legacy API que ainda não foram cadastrados no sistema. A comparação é feita entre o campo 'id' do OperationKindDto e o campo 'code' da entidade Operation. Requer autenticação JWT e permissão `operation:read`.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operation-kinds`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  Authorization: Bearer {{jwt_token}}
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar tipos de operação disponíveis

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "id": "000001",
    "description": "Venda de Produtos"
  },
  {
    "id": "000002",
    "description": "Compra de Produtos"
  },
  {
    "id": "000003",
    "description": "Transferência de Estoque"
  },
  {
    "id": "000004",
    "description": "Devolução de Produtos"
  },
  {
    "id": "000005",
    "description": "Ajuste de Inventário"
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array de tipos de operação disponíveis
- Cada item tem `id` e `description`
- IDs formatados com 6 dígitos

##### ✅ Cenário 2: Sucesso - Lista vazia (todos os tipos já cadastrados)

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (200 OK):**
```json
[]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array vazio quando todos os tipos já foram cadastrados

##### ❌ Cenário 3: Token ausente

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
```

**Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Token JWT ausente"
}
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token ausente

##### ❌ Cenário 4: Token inválido

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer token_invalido
```

**Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Token JWT inválido"
}
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token inválido

##### ❌ Cenário 5: Token expirado

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado
```

**Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Token JWT expirado"
}
```

**Validações:**
- Status Code: `401`
- Erro de autenticação por token expirado

##### ❌ Cenário 6: Sem permissão

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token_sem_permissao}}
```

**Response (403 Forbidden):**
```json
{
  "error": "Forbidden",
  "message": "Usuário sem permissão operation:read"
}
```

**Validações:**
- Status Code: `403`
- Erro de autorização por falta de permissão

##### ❌ Cenário 7: Erro interno do servidor

**Request:**
```http
GET http://localhost:8089/api/business/operation-kinds
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{jwt_token}}
```

**Response (500 Internal Server Error):**
```json
{
  "error": "Internal Server Error",
  "message": "Erro interno do servidor"
}
```

**Validações:**
- Status Code: `500`
- Erro interno do servidor

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
| `jwt_token_sem_permissao` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_sem_permissao` | Token JWT válido mas sem permissões adequadas |

### Collection Structure

```
📁 OperationKindController API - Business
├── 📁 Operation Kinds
│   └── 📄 GET All Operation Kinds
├── 📁 Test Scenarios
│   ├── 📁 Success Cases
│   │   ├── 📄 GET All Operation Kinds - Success
│   │   └── 📄 GET All Operation Kinds - Empty List
│   └── 📁 Error Cases
│       ├── 📄 GET All Operation Kinds - No Token
│       ├── 📄 GET All Operation Kinds - Invalid Token
│       ├── 📄 GET All Operation Kinds - Expired Token
│       ├── 📄 GET All Operation Kinds - No Permission
│       └── 📄 GET All Operation Kinds - Internal Server Error
```

### Request Templates

#### 1. GET All Operation Kinds

```json
{
  "name": "GET All Operation Kinds",
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
      "raw": "{{base_url}}/operation-kinds",
      "host": ["{{base_url}}"],
      "path": ["operation-kinds"]
    }
  }
}
```

### Test Scripts

#### Para GET All Operation Kinds:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each operation kind has required fields", function () {
    const operationKinds = pm.response.json();
    operationKinds.forEach(kind => {
        pm.expect(kind).to.have.property('id');
        pm.expect(kind).to.have.property('description');
    });
});

pm.test("ID is formatted with 6 digits", function () {
    const operationKinds = pm.response.json();
    operationKinds.forEach(kind => {
        pm.expect(kind.id).to.match(/^\d{6}$/);
    });
});

pm.test("Description is not empty", function () {
    const operationKinds = pm.response.json();
    operationKinds.forEach(kind => {
        pm.expect(kind.description).to.not.be.empty;
    });
});

pm.test("Response time is less than 5000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(5000);
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Tipos de operação listados com sucesso |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo de Sucesso

1. **Fazer login** (usar AuthController)
   - Request: `POST /api/business/auth/login`
   - Expected: Status 200, Token JWT válido

2. **Buscar tipos de operação disponíveis**
   - Request: `GET /api/business/operation-kinds`
   - Expected: Status 200, Lista de tipos disponíveis

3. **Verificar estrutura da resposta**
   - Expected: Array com objetos contendo `id` e `description`
   - Expected: IDs formatados com 6 dígitos
   - Expected: Descrições não vazias

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

1. **Sem permissão operation:read**
   - Request: Token válido mas sem `operation:read`
   - Expected: Status 403

### Teste 4: Cenários de Negócio

1. **Lista com tipos disponíveis**
   - Request: GET com token válido
   - Expected: Status 200, Array com tipos não cadastrados

2. **Lista vazia (todos cadastrados)**
   - Request: GET com token válido
   - Expected: Status 200, Array vazio

3. **Erro de integração com Legacy API**
   - Request: GET com token válido
   - Expected: Status 500, Erro interno

### Teste 5: Performance

1. **Tempo de resposta**
   - Request: GET com token válido
   - Expected: Response time < 5000ms

2. **Múltiplas requisições**
   - Request: 10 requisições simultâneas
   - Expected: Todas com status 200

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para buscar todos os tipos de operação
INFO  - Iniciando busca de tipos de operação disponíveis (não cadastrados)
INFO  - Total de tipos de operação na Legacy API: 10
INFO  - Total de operações já cadastradas: 5
INFO  - Total de tipos de operação disponíveis: 5
INFO  - Retornando 5 tipos de operação
```

### Logs de Erro

```
ERROR - Erro ao buscar tipos de operação: Connection timeout
ERROR - Erro ao buscar tipos de operação disponíveis
ERROR - Erro interno do servidor ao processar requisição
```

### Logs de Debug

```
DEBUG - Buscando tipos de operação da Legacy API
DEBUG - Filtrando tipos já cadastrados
DEBUG - Retornando tipos disponíveis
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Buscar Tipos de Operação com cURL

```bash
curl -X GET "http://localhost:8089/api/business/operation-kinds" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 2: Resposta de Sucesso

```json
[
  {
    "id": "000001",
    "description": "Venda de Produtos"
  },
  {
    "id": "000002",
    "description": "Compra de Produtos"
  },
  {
    "id": "000003",
    "description": "Transferência de Estoque"
  },
  {
    "id": "000004",
    "description": "Devolução de Produtos"
  },
  {
    "id": "000005",
    "description": "Ajuste de Inventário"
  }
]
```

### Exemplo 3: Resposta de Lista Vazia

```json
[]
```

### Exemplo 4: Resposta de Erro 401

```json
{
  "error": "Unauthorized",
  "message": "Token JWT ausente"
}
```

### Exemplo 5: Resposta de Erro 403

```json
{
  "error": "Forbidden",
  "message": "Usuário sem permissão operation:read"
}
```

### Exemplo 6: Resposta de Erro 500

```json
{
  "error": "Internal Server Error",
  "message": "Erro interno do servidor"
}
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissão `operation:read`
3. **CORS**: Configurado para `http://localhost:3000`
4. **Integração**: Dependência com Legacy API
5. **Somente Leitura**: Apenas operações de consulta

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Legacy API**: Integração para busca de dados
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Client**: LegacyApiClient para comunicação

### Performance

- **Connection Pool**: Máximo 10 conexões
- **JWT Validation**: Validação de token em cada requisição
- **CORS**: Configurado para frontend React
- **Logging**: INFO para com.sysconard.business
- **Integration**: Timeout configurado para Legacy API
- **Filtering**: Filtros aplicados em memória

### Segurança

- **JWT Validation**: Token validado em cada requisição
- **Permission-based Access**: Controle granular por permissões
- **CORS**: Restrito ao frontend
- **PreAuthorize**: Validação de permissões no controller
- **Logging**: Logs estruturados para auditoria
- **Data Protection**: Dados não sensíveis expostos

### Fluxo de Autenticação

1. **Login**: Usar `AuthController` para obter token JWT
2. **Token**: Salvar token no environment do Postman
3. **Authorization**: Usar token em header `Authorization: Bearer {token}`
4. **Permission**: Verificar se usuário tem permissão `operation:read`
5. **Response**: Receber lista de tipos de operação disponíveis

### Validações de Negócio

1. **Integração Legacy**: Busca dados da Legacy API
2. **Filtros**: Remove tipos já cadastrados no sistema
3. **Formatação**: IDs formatados com 6 dígitos
4. **Validação**: Descrições não podem ser vazias
5. **Performance**: Filtros aplicados em memória
6. **Error Handling**: Tratamento de erros de integração
7. **Logging**: Logs estruturados para monitoramento
8. **Data Consistency**: Validação de dados antes de retornar

### Diferenças dos Controllers Anteriores

1. **Controller simples**: 1 endpoint (vs 11 do UserController)
2. **Somente leitura**: Apenas GET (vs POST, PUT, PATCH, GET)
3. **Integração externa**: Dependência com Legacy API
4. **Filtros de negócio**: Remove itens já cadastrados
5. **DTO simples**: Apenas 2 campos (id, description)
6. **Sem validações complexas**: Dados vêm da Legacy API
7. **Sem operações CRUD**: Apenas consulta
8. **Sem relacionamentos**: DTO simples
9. **Sem query parameters**: Endpoint simples
10. **Operação específica**: Busca de tipos disponíveis

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
