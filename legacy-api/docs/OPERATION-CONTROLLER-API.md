# Documentação OperationController - Legacy API

## 📋 Visão Geral

O `OperationController` é responsável por gerenciar as operações do sistema na Legacy API. Este controller segue os princípios de Clean Code e oferece endpoints para consulta de operações cadastradas.

### Informações Técnicas

- **Base URL**: `http://localhost:8087/api/legacy`
- **Context Path**: `/api/legacy`
- **Porta**: `8087`
- **Tecnologia**: Spring Boot 2.7.x (Java 8)
- **Padrão**: REST API (Read-Only)
- **Autenticação**: Não requerida (API pública)

### Estrutura do DTO

```json
{
  "id": "000001",
  "description": "Descrição da operação"
}
```

**Campos:**
- `id`: Código único formatado com 6 dígitos (ex: 000001)
- `description`: Descrição da operação

---

## 🚀 Endpoints Disponíveis

### 1. Listar Todas as Operações

**Endpoint:** `GET /api/legacy/operations`

**Descrição:** Retorna todas as operações cadastradas no sistema.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operations`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista todas as operações

**Request:**
```http
GET http://localhost:8087/api/legacy/operations
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": "000001",
    "description": "Venda à vista"
  },
  {
    "id": "000002", 
    "description": "Venda a prazo"
  },
  {
    "id": "000003",
    "description": "Devolução"
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array não vazio (se houver operações cadastradas)
- Formato do ID: 6 dígitos com zeros à esquerda

##### ⚠️ Cenário 2: Erro interno do servidor

**Request:**
```http
GET http://localhost:8087/api/legacy/operations
```

**Response (500 Internal Server Error):**
```http
HTTP/1.1 500 Internal Server Error
Content-Type: application/json
```

**Validações:**
- Status Code: `500`
- Log de erro no console da aplicação

---

### 2. Buscar Operação por ID

**Endpoint:** `GET /api/legacy/operations/{id}`

**Descrição:** Busca uma operação específica pelo seu ID.

#### Parâmetros

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `id` | Long | Sim | ID da operação (path parameter) |

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/operations/{{operation_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Operação encontrada

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/1
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "id": "000001",
  "description": "Venda à vista"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- ID formatado com 6 dígitos
- Description não nula

##### ❌ Cenário 2: ID inválido (negativo)

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/-1
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Log de warning no console

##### ❌ Cenário 3: ID inválido (zero)

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/0
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Log de warning no console

##### ❌ Cenário 4: Operação não encontrada

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/999999
```

**Response (404 Not Found):**
```http
HTTP/1.1 404 Not Found
```

**Validações:**
- Status Code: `404`
- Log de warning no console

##### ❌ Cenário 5: ID nulo (parâmetro ausente)

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/
```

**Response (404 Not Found):**
```http
HTTP/1.1 404 Not Found
```

**Validações:**
- Status Code: `404`
- Erro de roteamento do Spring

##### ⚠️ Cenário 6: Erro interno do servidor

**Request:**
```http
GET http://localhost:8087/api/legacy/operations/1
```

**Response (500 Internal Server Error):**
```http
HTTP/1.1 500 Internal Server Error
```

**Validações:**
- Status Code: `500`
- Log de erro no console

---

## 🔧 Configuração no Postman

### Environment Variables

Crie um environment no Postman com as seguintes variáveis:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8087/api/legacy` | URL base da API |
| `operation_id` | `1` | ID de exemplo para testes |

### Collection Structure

```
📁 OperationController API
├── 📁 Operations
│   ├── 📄 GET All Operations
│   └── 📄 GET Operation by ID
└── 📁 Test Scenarios
    ├── 📄 Success Cases
    └── 📄 Error Cases
```

### Request Templates

#### 1. GET All Operations

```json
{
  "name": "GET All Operations",
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
      "raw": "{{base_url}}/operations",
      "host": ["{{base_url}}"],
      "path": ["operations"]
    }
  }
}
```

#### 2. GET Operation by ID

```json
{
  "name": "GET Operation by ID",
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
      "raw": "{{base_url}}/operations/{{operation_id}}",
      "host": ["{{base_url}}"],
      "path": ["operations", "{{operation_id}}"]
    }
  }
}
```

### Test Scripts (Opcional)

#### Para GET All Operations:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each operation has required fields", function () {
    const operations = pm.response.json();
    operations.forEach(operation => {
        pm.expect(operation).to.have.property('id');
        pm.expect(operation).to.have.property('description');
        pm.expect(operation.id).to.match(/^\d{6}$/);
    });
});
```

#### Para GET Operation by ID:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Operation has required fields", function () {
    const operation = pm.response.json();
    pm.expect(operation).to.have.property('id');
    pm.expect(operation).to.have.property('description');
    pm.expect(operation.id).to.match(/^\d{6}$/);
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Operação encontrada com sucesso |
| `400` | Bad Request | ID inválido (≤ 0) |
| `404` | Not Found | Operação não encontrada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sucesso

1. **Listar todas as operações**
   - Request: `GET /api/legacy/operations`
   - Expected: Status 200, Array de operações

2. **Buscar operação específica**
   - Request: `GET /api/legacy/operations/1`
   - Expected: Status 200, Operação com ID formatado

### Teste 2: Validação de IDs Inválidos

1. **ID negativo**
   - Request: `GET /api/legacy/operations/-1`
   - Expected: Status 400

2. **ID zero**
   - Request: `GET /api/legacy/operations/0`
   - Expected: Status 400

3. **ID não numérico**
   - Request: `GET /api/legacy/operations/abc`
   - Expected: Status 400

### Teste 3: Operação Não Encontrada

1. **ID inexistente**
   - Request: `GET /api/legacy/operations/999999`
   - Expected: Status 404

### Teste 4: Validação de Formato

1. **Verificar formato do ID**
   - Expected: 6 dígitos com zeros à esquerda (ex: 000001)

2. **Verificar campos obrigatórios**
   - Expected: `id` e `description` presentes

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
DEBUG - Recebida requisição para buscar todas as operações
INFO  - Requisição de busca de operações processada com sucesso. Total: 3
```

```
DEBUG - Recebida requisição para buscar operação com ID: 1
INFO  - Requisição de busca de operação por ID processada com sucesso: ID=1
```

### Logs de Erro

```
WARN  - ID inválido fornecido na requisição: -1
WARN  - Operação não encontrada com ID: 999999
ERROR - Erro ao processar requisição de busca de operações: Connection timeout
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Consulta de Operações Disponíveis

```bash
# Listar todas as operações
curl -X GET "http://localhost:8087/api/legacy/operations" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 2: Consulta de Operação Específica

```bash
# Buscar operação por ID
curl -X GET "http://localhost:8087/api/legacy/operations/1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 3: Teste de Validação

```bash
# Testar ID inválido
curl -X GET "http://localhost:8087/api/legacy/operations/-1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Read-Only**: A API é somente leitura, não permite operações de escrita
2. **Sem Autenticação**: Endpoints públicos, sem controle de acesso
3. **Formato de ID**: IDs são sempre formatados com 6 dígitos
4. **Legacy**: Compatível com Java 8 e Spring Boot 2.7.x

### Dependências

- **Database**: SQL Server (45.174.189.210:1433)
- **Connection Pool**: HikariCP com configuração read-only
- **ORM**: Hibernate com dialect SQL Server 2012

### Performance

- **Connection Pool**: Máximo 1 conexão (read-only)
- **Lazy Loading**: Habilitado para otimização
- **Batch Size**: 1 (configuração conservadora)

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Legacy API
