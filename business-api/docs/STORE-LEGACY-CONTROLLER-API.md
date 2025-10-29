# Documentação StoreLegacyController - Business API

## 📋 Visão Geral

O `StoreLegacyController` é responsável por gerenciar operações de lojas que consomem dados da Legacy API. Este controller expõe endpoints que buscam dados de lojas do serviço legacy e compara com as lojas já cadastradas no sistema, retornando apenas as lojas não cadastradas. Segue princípios de Clean Code com responsabilidades bem definidas.

### Informações Técnicas

- **Base URL**: `http://localhost:8089/api/business`
- **Context Path**: `/api/business`
- **Porta**: `8089`
- **Tecnologia**: Spring Boot 3.x (Java 17)
- **Padrão**: REST API com integração Legacy API
- **Autenticação**: Não requer JWT (endpoints públicos)
- **Métodos**: GET
- **Integração**: Consome dados da Legacy API (porta 8087)

### Estrutura do Response DTO

```json
{
  "id": "000001",
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```

**Campos:**
- `id`: Identificador único da loja (String com 6 dígitos, ex: 000001)
- `code`: Código da loja
- `name`: Nome da loja
- `city`: Cidade onde a loja está localizada
- `status`: Status da loja (true = ativa, false = inativa)

**Observação Importante**: O campo `id` é mantido como String para preservar a formatação de 6 dígitos com zeros à esquerda (ex: 000001, 000002).

---

## 🚀 Endpoints Disponíveis

### 1. Buscar Lojas Não Cadastradas

**Endpoint:** `GET /api/business/legacy/stores`

**Descrição:** Busca lojas da Legacy API que não estão cadastradas no sistema. Compara os códigos das lojas cadastradas com as lojas da Legacy API e retorna apenas as que não existem no cadastro. O ID é mantido com a formatação original de 6 dígitos. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/legacy/stores`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Buscar lojas não cadastradas

**Request:**
```http
GET http://localhost:8089/api/business/legacy/stores
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": "000001",
    "code": "LOJA01",
    "name": "Loja Centro",
    "city": "São Paulo",
    "status": true
  },
  {
    "id": "000002",
    "code": "LOJA02",
    "name": "Loja Norte",
    "city": "Rio de Janeiro",
    "status": true
  },
  {
    "id": "000003",
    "code": "LOJA03",
    "name": "Loja Sul",
    "city": "Porto Alegre",
    "status": true
  }
]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista de lojas não cadastradas
- ID com formatação de 6 dígitos preservada
- Apenas lojas que não existem no cadastro

##### ✅ Cenário 2: Sucesso - Nenhuma loja não cadastrada

**Request:**
```http
GET http://localhost:8089/api/business/legacy/stores
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```json
[]
```

**Validações:**
- Status Code: `200`
- Content-Type: `application/json`
- Lista vazia (todas as lojas já estão cadastradas)

##### ❌ Cenário 3: Legacy API indisponível

**Request:**
```http
GET http://localhost:8089/api/business/legacy/stores
Content-Type: application/json
Accept: application/json
```

**Response (500 Internal Server Error):**
```json
{
  "error": "Internal Server Error",
  "message": "Erro ao buscar lojas não cadastradas"
}
```

**Validações:**
- Status Code: `500`
- Erro de integração com Legacy API

---

### 2. Health Check

**Endpoint:** `GET /api/business/legacy/stores/health`

**Descrição:** Endpoint de health check para verificar se o controller está funcionando. Acesso público, não requer autenticação.

#### Configuração no Postman

**Request:**
- **Method:** `GET`
- **URL:** `{{base_url}}/legacy/stores/health`
- **Headers:** 
  ```
  Content-Type: application/json
  Accept: application/json
  ```

#### Cenários de Teste

##### ✅ Cenário 1: Sucesso - Health check

**Request:**
```http
GET http://localhost:8089/api/business/legacy/stores/health
Content-Type: application/json
Accept: application/json
```

**Response (200 OK):**
```
StoreLegacyController está funcionando
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
| `legacy_api_url` | `http://localhost:8087/api/legacy` | URL da Legacy API |

### Collection Structure

```
📁 StoreLegacyController API - Business
├── 📁 Legacy Stores
│   ├── 📄 GET Unregistered Stores
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET Unregistered Stores - With Results
    │   ├── 📄 GET Unregistered Stores - Empty List
    │   └── 📄 GET Health Check - Success
    └── 📁 Error Cases
        └── 📄 GET Unregistered Stores - Legacy API Down
```

### Test Scripts

#### Para GET Unregistered Stores:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is JSON array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each store has required fields", function () {
    const stores = pm.response.json();
    if (stores.length > 0) {
        stores.forEach(store => {
            pm.expect(store).to.have.property('id');
            pm.expect(store).to.have.property('code');
            pm.expect(store).to.have.property('name');
            pm.expect(store).to.have.property('city');
            pm.expect(store).to.have.property('status');
        });
    }
});

pm.test("ID has 6 digits format", function () {
    const stores = pm.response.json();
    if (stores.length > 0) {
        stores.forEach(store => {
            pm.expect(store.id).to.match(/^\d{6}$/);
        });
    }
});

pm.test("Store code is valid", function () {
    const stores = pm.response.json();
    if (stores.length > 0) {
        stores.forEach(store => {
            pm.expect(store.code).to.be.a('string');
            pm.expect(store.code).to.have.length.greaterThan(0);
        });
    }
});
```

#### Para GET Health Check:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is text", function () {
    pm.expect(pm.response.text()).to.be.a('string');
});

pm.test("Response contains health message", function () {
    pm.expect(pm.response.text()).to.include('StoreLegacyController');
});
```

---

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Lojas não cadastradas encontradas, health check |
| `500` | Internal Server Error | Erro interno ou Legacy API indisponível |

---

## 🧪 Cenários de Teste Completos

### Teste 1: Fluxo Completo de Integração

1. **Health check**
   - Request: `GET /api/business/legacy/stores/health`
   - Expected: Status 200, Mensagem de saúde

2. **Buscar lojas não cadastradas**
   - Request: `GET /api/business/legacy/stores`
   - Expected: Status 200, Lista de lojas

3. **Verificar formatação de IDs**
   - Validar que IDs têm 6 dígitos
   - Validar que zeros à esquerda são preservados

### Teste 2: Validação de Integração

1. **Legacy API disponível**
   - Request: `GET /api/business/legacy/stores`
   - Expected: Status 200, Lista de lojas

2. **Legacy API indisponível**
   - Request: `GET /api/business/legacy/stores`
   - Expected: Status 500, Erro de integração

### Teste 3: Validação de Dados

1. **Lojas não cadastradas existem**
   - Request: `GET /api/business/legacy/stores`
   - Expected: Status 200, Lista não vazia

2. **Todas as lojas já cadastradas**
   - Request: `GET /api/business/legacy/stores`
   - Expected: Status 200, Lista vazia

---

## 🔍 Logs e Monitoramento

### Logs de Sucesso

```
INFO  - Recebida requisição para buscar lojas não cadastradas da Legacy API
INFO  - Lojas não cadastradas encontradas: 3 registros
```

### Logs de Erro

```
ERROR - Erro ao buscar lojas não cadastradas: Connection timeout
ERROR - Erro ao buscar lojas não cadastradas: Legacy API retornou erro 500
```

### Logs de Debug

```
DEBUG - Health check do StoreLegacyController
```

---

## 📝 Exemplos Práticos

### Exemplo 1: Buscar Lojas Não Cadastradas com cURL

```bash
curl -X GET "http://localhost:8089/api/business/legacy/stores" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 2: Health Check com cURL

```bash
curl -X GET "http://localhost:8089/api/business/legacy/stores/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

---

## ⚠️ Considerações Importantes

### Limitações da API

1. **Sem autenticação**: Endpoints públicos (não requerem JWT)
2. **Sem autorização**: Não requer permissões específicas
3. **CORS**: Configurado para `http://localhost:3000`
4. **Integração**: Depende da disponibilidade da Legacy API (porta 8087)
5. **Apenas leitura**: Não permite criar, atualizar ou remover lojas
6. **Formatação de ID**: Preserva zeros à esquerda (000001, 000002)

### Dependências

- **Legacy API**: Fonte de dados de lojas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432) - para comparação de lojas cadastradas
- **Integração**: WebClient para consumir Legacy API

### Lógica de Negócio

1. **Busca lojas da Legacy API**: Consome endpoint `/stores` da Legacy API
2. **Busca lojas cadastradas**: Consulta banco de dados local
3. **Comparação**: Compara códigos das lojas
4. **Filtro**: Retorna apenas lojas que não existem no cadastro local
5. **Formatação**: Preserva formatação original do ID (6 dígitos com zeros à esquerda)

### Diferenças dos Controllers Anteriores

1. **Sem autenticação**: Endpoints públicos (vs JWT obrigatório)
2. **Sem autorização**: Não requer permissões (vs PreAuthorize)
3. **Integração externa**: Consome Legacy API (vs dados internos)
4. **Apenas leitura**: Somente GET (vs CRUD completo)
5. **Comparação de dados**: Filtra lojas não cadastradas
6. **ID como String**: Preserva formatação de 6 dígitos
7. **Controller simples**: Apenas 2 endpoints
8. **Sem paginação**: Retorna todas as lojas não cadastradas

### Casos de Uso

1. **Sincronização de lojas**: Identificar lojas da Legacy API que precisam ser cadastradas
2. **Auditoria**: Verificar quais lojas estão faltando no cadastro
3. **Integração**: Facilitar importação de lojas do sistema legado
4. **Monitoramento**: Verificar disponibilidade da Legacy API

---

## 🔄 Fluxo de Integração

### Diagrama de Fluxo

```
1. Cliente → GET /api/business/legacy/stores
2. StoreLegacyController → StoreService.getUnregisteredStores()
3. StoreService → Legacy API (GET /api/legacy/stores)
4. StoreService → Database (SELECT códigos cadastrados)
5. StoreService → Comparação (filtrar não cadastradas)
6. StoreService → StoreLegacyController
7. StoreLegacyController → Cliente (Lista de lojas não cadastradas)
```

### Exemplo de Comparação

**Lojas na Legacy API:**
- 000001 - LOJA01 - Loja Centro
- 000002 - LOJA02 - Loja Norte
- 000003 - LOJA03 - Loja Sul

**Lojas Cadastradas no Sistema:**
- LOJA01 - Loja Centro
- LOJA03 - Loja Sul

**Resultado (Lojas Não Cadastradas):**
- 000002 - LOJA02 - Loja Norte

---

## 📈 Métricas e Performance

### Tempo de Resposta Esperado

- **Health Check**: < 50ms
- **Buscar Lojas Não Cadastradas**: < 2000ms (depende da Legacy API)

### Otimizações

- **Cache**: Considerar cache para reduzir chamadas à Legacy API
- **Timeout**: Configurar timeout adequado para Legacy API
- **Retry**: Implementar retry em caso de falha temporária

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API


