# Guia de Configuração do Postman - OperationController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Operações (`OperationController`) da Business API usando o Postman. O controller oferece 5 endpoints para CRUD completo de operações, busca com filtros e paginação, e gerenciamento com autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Selecione o arquivo `OperationController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `OperationController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"OperationController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `operation_id`: ID de uma operação existente para testes
- `operation_code`: Código de uma operação para testes
- `operation_source_sell`: SELL
- `operation_source_exchange`: EXCHANGE

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
1. Execute o endpoint `GET /api/business/operations/health`
2. Se retornar status 200, o token está funcionando

## 🧪 Cenários de Teste

### 1. Fluxo Completo de CRUD

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
Expected: Status 200, Token JWT válido

#### Passo 2: Criar Operação
```http
POST {{base_url}}/operations
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL"
}
```
Expected: Status 201, Operação criada

#### Passo 3: Buscar Operação por ID
```http
GET {{base_url}}/operations/{{operation_id}}
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Dados da operação

#### Passo 4: Atualizar Operação
```http
PUT {{base_url}}/operations/{{operation_id}}
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "description": "Nova Descrição da Operação"
}
```
Expected: Status 200, Operação atualizada

#### Passo 5: Buscar com Filtros
```http
GET {{base_url}}/operations?code=VENDA&operationSource=SELL&page=0&size=10
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Lista filtrada

### 2. Testes de Validação

#### Teste 1: Código Inválido
```http
POST {{base_url}}/operations
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "venda@001",
  "description": "Operação de Venda de Produtos",
  "operationSource": "SELL"
}
```
Expected: Status 400, Erro de validação

#### Teste 2: Descrição Muito Curta
```http
POST {{base_url}}/operations
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "VENDA-001",
  "description": "Op",
  "operationSource": "SELL"
}
```
Expected: Status 400, Erro de validação

#### Teste 3: Fonte Ausente
```http
POST {{base_url}}/operations
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "VENDA-001",
  "description": "Operação de Venda de Produtos"
}
```
Expected: Status 400, Erro de validação

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Operação atualizada, encontrada, listada |
| `201` | Created | Operação criada com sucesso |
| `400` | Bad Request | Dados inválidos, operação não encontrada |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 401 - Unauthorized
- **Causa**: Token JWT ausente, inválido ou expirado
- **Solução**: Obter novo token usando AuthController

#### Erro 403 - Forbidden
- **Causa**: Usuário sem permissões adequadas
- **Solução**: Verificar se o usuário tem as permissões necessárias

#### Erro 400 - Bad Request
- **Causa**: Dados inválidos ou operação não encontrada
- **Solução**: Verificar dados enviados e se a operação existe

### 2. Validação de Dados

#### Código da Operação
- Deve conter apenas letras maiúsculas, números, underscore e hífen
- Exemplo válido: `VENDA-001`
- Exemplo inválido: `venda@001`

#### Descrição
- Deve ter entre 3 e 255 caracteres
- Exemplo válido: `Operação de Venda de Produtos`
- Exemplo inválido: `Op`

#### Fonte da Operação
- Deve ser SELL ou EXCHANGE
- Exemplo válido: `SELL`
- Exemplo inválido: `VENDA`

## 📝 Exemplos Práticos

### Exemplo 1: Criar Operação com cURL

```bash
curl -X POST "http://localhost:8089/api/business/operations" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "code": "VENDA-001",
    "description": "Operação de Venda de Produtos",
    "operationSource": "SELL"
  }'
```

### Exemplo 2: Atualizar Operação com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/operations/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "description": "Nova Descrição da Operação"
  }'
```

### Exemplo 3: Buscar Operações com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/operations?code=VENDA&operationSource=SELL&page=0&size=10&sortBy=code&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`operation:create`, `operation:update`, `operation:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Código único**: Não permite operações com mesmo código

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **Controller intermediário**: 5 endpoints (vs 1 do OperationKindController e 11 do UserController)
2. **CRUD completo**: POST, PUT, GET
3. **Busca avançada**: Filtros, paginação e totalizadores
4. **Validações com Pattern**: Código com letras maiúsculas, números, underscore e hífen
5. **Enum**: OperationSource (SELL, EXCHANGE)
6. **Response com timestamps**: createdAt e updatedAt
7. **Totalizadores**: Por tipo de operação (SELL, EXCHANGE)
8. **Atualização parcial**: Permite atualizar apenas campos específicos

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
