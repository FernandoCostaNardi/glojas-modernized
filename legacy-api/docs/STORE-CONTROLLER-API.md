# Documentação StoreController - Legacy API

## 📋 Visão Geral

O `StoreController` é responsável por gerenciar as lojas do sistema na Legacy API. Este controller segue os princípios de Clean Code e oferece endpoints para consulta de lojas cadastradas.

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
  "name": "Loja Centro",
  "city": "São Paulo"
}
```

**Campos:**
- `id`: Código único formatado com 6 dígitos (ex: 000001)
- `name`: Nome fantasia da loja
- `city`: Cidade da loja

---

## 🚀 Endpoints Disponíveis

### 1. Listar Todas as Lojas

**Endpoint:** `GET /api/legacy/stores`

**Descrição:** Retorna todas as lojas cadastradas no sistema.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Lista todas as lojas

**Request:**
```http
GET http://localhost:8087/api/legacy/stores
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": "000001",
    "name": "Loja Centro",
    "city": "São Paulo"
  },
  {
    "id": "000002", 
    "name": "Loja Norte",
    "city": "Rio de Janeiro"
  },
  {
    "id": "000003",
    "name": "Loja Sul",
    "city": "Belo Horizonte"
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Array não vazio (se houver lojas cadastradas)
- Formato do ID: 6 dígitos com zeros à esquerda
- Cada loja tem `id`, `name` e `city`

##### ⚠️ Cenário 2: Erro interno do servidor

**Request:**
```http
GET http://localhost:8087/api/legacy/stores
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

### 2. Buscar Loja por ID

**Endpoint:** `GET /api/legacy/stores/{id}`

**Descrição:** Busca uma loja específica pelo seu ID.

#### Parâmetros

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `id` | String | Sim | ID da loja (path parameter) |

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/stores/{{store_id}}`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Loja encontrada

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/1
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
{
  "id": "000001",
  "name": "Loja Centro",
  "city": "São Paulo"
}
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- ID formatado com 6 dígitos
- Name e city não nulos

##### ❌ Cenário 2: ID vazio

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/
```

**Response (404 Not Found):**
```http
HTTP/1.1 404 Not Found
```

**Validações:**
- Status Code: `404`
- Erro de roteamento do Spring

##### ❌ Cenário 3: ID nulo (string vazia)

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/
```

**Response (404 Not Found):**
```http
HTTP/1.1 404 Not Found
```

**Validações:**
- Status Code: `404`
- Erro de roteamento do Spring

##### ❌ Cenário 4: ID não numérico

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/abc
Content-Type: application/json
Accept: application/json
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Log de warning no console

##### ❌ Cenário 5: ID inválido (negativo)

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/-1
Content-Type: application/json
Accept: application/json
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Log de warning no console

##### ❌ Cenário 6: ID inválido (zero)

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/0
Content-Type: application/json
Accept: application/json
```

**Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
```

**Validações:**
- Status Code: `400`
- Log de warning no console

##### ❌ Cenário 7: Loja não encontrada

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/999999
Content-Type: application/json
Accept: application/json
```

**Response (404 Not Found):**
```http
HTTP/1.1 404 Not Found
```

**Validações:**
- Status Code: `404`
- Log de warning no console

##### ⚠️ Cenário 8: Erro interno do servidor

**Request:**
```http
GET http://localhost:8087/api/legacy/stores/1
Content-Type: application/json
Accept: application/json
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
| `store_id` | `1` | ID de exemplo para testes |
| `test_store_id` | `2` | ID alternativo para testes |
| `invalid_store_id` | `-1` | ID inválido (negativo) |
| `not_found_store_id` | `999999` | ID inexistente |
| `zero_store_id` | `0` | ID inválido (zero) |
| `non_numeric_store_id` | `abc` | ID não numérico |

### Collection Structure

```
📁 StoreController API
├── 📁 Stores
│   ├── 📄 GET All Stores
│   └── 📄 GET Store by ID
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET All Stores - Success
    │   └── 📄 GET Store by ID - Success
    └── 📁 Error Cases
        ├── 📄 GET Store by ID - Invalid ID (Negative)
        ├── 📄 GET Store by ID - Invalid ID (Zero)
        ├── 📄 GET Store by ID - Not Found
        ├── 📄 GET Store by ID - Non-numeric ID
        └── 📄 GET Store by ID - Empty ID
```

### Request Templates

#### 1. GET All Stores

```json
{
  "name": "GET All Stores",
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
      "raw": "{{base_url}}/stores",
      "host": ["{{base_url}}"],
      "path": ["stores"]
    }
  }
}
```

#### 2. GET Store by ID

```json
{
  "name": "GET Store by ID",
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
      "raw": "{{base_url}}/stores/{{store_id}}",
      "host": ["{{base_url}}"],
      "path": ["stores", "{{store_id}}"]
    }
  }
}
```

### Test Scripts (Opcional)

#### Para GET All Stores:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each store has required fields", function () {
    const stores = pm.response.json();
    stores.forEach(store => {
        pm.expect(store).to.have.property('id');
        pm.expect(store).to.have.property('name');
        pm.expect(store).to.have.property('city');
        pm.expect(store.id).to.match(/^\d{6}$/);
    });
});
```

#### Para GET Store by ID:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON object", function () {
    pm.expect(pm.response.json()).to.be.an('object');
});

pm.test("Store has required fields", function () {
    const store = pm.response.json();
    pm.expect(store).to.have.property('id');
    pm.expect(store).to.have.property('name');
    pm.expect(store).to.have.property('city');
    pm.expect(store.id).to.match(/^\d{6}$/);
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Loja encontrada com sucesso |
| `400` | Bad Request | ID inválido (≤ 0, não numérico) |
| `404` | Not Found | Loja não encontrada |
| `500` | Internal Server Error | Erro interno do servidor |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Sucesso

1. **Listar todas as lojas**
   - Request: `GET /api/legacy/stores`
   - Expected: Status 200, Array de lojas

2. **Buscar loja específica**
   - Request: `GET /api/legacy/stores/1`
   - Expected: Status 200, Loja com ID formatado

### Teste 2: Validação de IDs Inválidos

1. **ID negativo**
   - Request: `GET /api/legacy/stores/-1`
   - Expected: Status 400

2. **ID zero**
   - Request: `GET /api/legacy/stores/0`
   - Expected: Status 400

3. **ID não numérico**
   - Request: `GET /api/legacy/stores/abc`
   - Expected: Status 400

4. **ID vazio**
   - Request: `GET /api/legacy/stores/`
   - Expected: Status 404 (roteamento)

### Teste 3: Loja Não Encontrada

1. **ID inexistente**
   - Request: `GET /api/legacy/stores/999999`
   - Expected: Status 404

### Teste 4: Validação de Formato

1. **Verificar formato do ID**
   - Expected: 6 dígitos com zeros à esquerda (ex: 000001)

2. **Verificar campos obrigatórios**
   - Expected: `id`, `name` e `city` presentes

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
DEBUG - Recebida requisição para buscar todas as lojas
INFO  - Requisição de busca de lojas processada com sucesso. Total: 3
```

```
DEBUG - Recebida requisição para buscar loja com ID: 1
INFO  - Requisição de busca de loja por ID processada com sucesso: ID=1
```

### Logs de Erro

```
WARN  - ID inválido fornecido na requisição: -1
WARN  - Loja não encontrada com ID: 999999
ERROR - Erro ao processar requisição de busca de lojas: Connection timeout
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Consulta de Lojas Disponíveis

```bash
# Listar todas as lojas
curl -X GET "http://localhost:8087/api/legacy/stores" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 2: Consulta de Loja Específica

```bash
# Buscar loja por ID
curl -X GET "http://localhost:8087/api/legacy/stores/1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 3: Teste de Validação

```bash
# Testar ID inválido
curl -X GET "http://localhost:8087/api/legacy/stores/-1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 4: Teste de ID Não Numérico

```bash
# Testar ID não numérico
curl -X GET "http://localhost:8087/api/legacy/stores/abc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Read-Only**: A API é somente leitura, não permite operações de escrita
2. **Sem Autenticação**: Endpoints públicos, sem controle de acesso
3. **Formato de ID**: IDs são sempre formatados com 6 dígitos
4. **Validação de String**: ID é recebido como String e convertido para Long
5. **Legacy**: Compatível com Java 8 e Spring Boot 2.7.x

### Dependências

- **Database**: SQL Server (45.174.189.210:1433)
- **Connection Pool**: HikariCP com configuração read-only
- **ORM**: Hibernate com dialect SQL Server 2012

### Performance

- **Connection Pool**: Máximo 1 conexão (read-only)
- **Lazy Loading**: Habilitado para otimização
- **Batch Size**: 1 (configuração conservadora)

### Diferenças do OperationController

1. **Tipo do ID**: String (não Long)
2. **Campos do DTO**: 3 campos (`id`, `name`, `city`)
3. **Validações**: Conversão String → Long com validações adicionais
4. **Formato**: ID formatado com 6 dígitos usando `String.format()`

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Legacy API
