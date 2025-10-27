# Guia de Configuração do Postman - OperationKindController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Tipos de Operação (`OperationKindController`) da Business API usando o Postman. O controller oferece 1 endpoint para busca de tipos de operação disponíveis para cadastro, integrando com a Legacy API e aplicando regras de negócio para filtrar apenas os tipos que ainda não foram cadastrados no sistema.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Selecione o arquivo `OperationKindController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `OperationKindController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"OperationKindController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `jwt_token_invalid`: Token JWT inválido para testes
- `jwt_token_expired`: Token JWT expirado para testes
- `jwt_token_sem_permissao`: Token JWT válido mas sem permissões adequadas

#### Variáveis de Teste
- `operation_kind_sample_id`: ID de exemplo de tipo de operação
- `operation_kind_sample_description`: Descrição de exemplo de tipo de operação
- `required_permission`: Permissão necessária (`operation:read`)
- `test_timeout`: Timeout para testes (5000ms)

#### Variáveis de Validação
- `operation_kind_id_pattern`: Padrão regex para validação de ID (6 dígitos)
- `expected_success_status`: Status HTTP esperado para sucesso (200)
- `expected_unauthorized_status`: Status HTTP esperado para não autorizado (401)
- `expected_forbidden_status`: Status HTTP esperado para proibido (403)
- `expected_internal_server_error_status`: Status HTTP esperado para erro interno (500)

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
1. Execute o endpoint `GET /api/business/operation-kinds`
2. Se retornar status 200, o token está funcionando

### 2. Configurar Headers de Autenticação

O endpoint protegido já está configurado com o header:
```
Authorization: Bearer {{jwt_token}}
```

## 📁 Estrutura da Collection

### 1. Operation Kinds
- **GET All Operation Kinds**: Buscar tipos de operação disponíveis

### 2. Test Scenarios
- **Success Cases**: Cenários de sucesso
- **Error Cases**: Cenários de erro

## 🧪 Cenários de Teste

### 1. Fluxo de Sucesso

#### Passo 1: Fazer Login
```http
POST {{base_url}}/auth/login
```
Body:
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
```
Expected: Status 200, Token JWT válido

#### Passo 2: Buscar Tipos de Operação Disponíveis
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Lista de tipos disponíveis

#### Passo 3: Verificar Estrutura da Resposta
Expected: Array com objetos contendo `id` e `description`
Expected: IDs formatados com 6 dígitos
Expected: Descrições não vazias

### 2. Testes de Validação

#### Teste 1: Token Ausente
```http
GET {{base_url}}/operation-kinds
```
Expected: Status 401, Token JWT ausente

#### Teste 2: Token Inválido
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token_invalid}}
```
Expected: Status 401, Token JWT inválido

#### Teste 3: Token Expirado
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token_expired}}
```
Expected: Status 401, Token JWT expirado

#### Teste 4: Sem Permissão
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token_sem_permissao}}
```
Expected: Status 403, Usuário sem permissão operation:read

### 3. Testes de Negócio

#### Teste 1: Lista com Tipos Disponíveis
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Array com tipos não cadastrados

#### Teste 2: Lista Vazia (Todos Cadastrados)
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Array vazio

#### Teste 3: Erro de Integração com Legacy API
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: Status 500, Erro interno

### 4. Testes de Performance

#### Teste 1: Tempo de Resposta
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: Response time < 5000ms

#### Teste 2: Múltiplas Requisições
```http
GET {{base_url}}/operation-kinds
Authorization: Bearer {{jwt_token}}
```
Expected: 10 requisições simultâneas com status 200

## 🔧 Configuração Avançada

### 1. Scripts de Teste Automatizados

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

### 2. Configuração de Timeout

#### Timeout Global
1. Vá em **Settings** (ícone de engrenagem)
2. Selecione **General**
3. Configure **Request timeout in ms** para `5000` (5 segundos)

#### Timeout por Request
1. Abra um request
2. Vá na aba **Settings**
3. Configure **Request timeout** para `5000`

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
INFO  - Recebida requisição para buscar todos os tipos de operação
INFO  - Iniciando busca de tipos de operação disponíveis (não cadastrados)
INFO  - Total de tipos de operação na Legacy API: 10
INFO  - Total de operações já cadastradas: 5
INFO  - Total de tipos de operação disponíveis: 5
INFO  - Retornando 5 tipos de operação
```

### 2. Logs de Erro

```
ERROR - Erro ao buscar tipos de operação: Connection timeout
ERROR - Erro ao buscar tipos de operação disponíveis
ERROR - Erro interno do servidor ao processar requisição
```

### 3. Logs de Debug

```
DEBUG - Buscando tipos de operação da Legacy API
DEBUG - Filtrando tipos já cadastrados
DEBUG - Retornando tipos disponíveis
```

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 401 - Unauthorized
- **Causa**: Token JWT ausente, inválido ou expirado
- **Solução**: Obter novo token usando AuthController

#### Erro 403 - Forbidden
- **Causa**: Usuário sem permissão `operation:read`
- **Solução**: Verificar se o usuário tem a permissão necessária

#### Erro 500 - Internal Server Error
- **Causa**: Erro de integração com Legacy API
- **Solução**: Verificar logs do servidor e conectividade

### 2. Validação de Dados

#### ID do Tipo de Operação
- Deve ser formatado com 6 dígitos
- Exemplo válido: `000001`
- Exemplo inválido: `1`

#### Descrição
- Deve ser uma string não vazia
- Exemplo válido: `Venda de Produtos`
- Exemplo inválido: `""`

### 3. Verificação de Ambiente

#### Verificar se a API está rodando:
```bash
curl http://localhost:8089/api/business/operation-kinds
```

#### Verificar se o JWT está funcionando:
```bash
curl -H "Authorization: Bearer SEU_TOKEN" http://localhost:8089/api/business/operation-kinds
```

#### Verificar se a Legacy API está acessível:
```bash
curl http://localhost:8087/api/legacy/operations
```

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
