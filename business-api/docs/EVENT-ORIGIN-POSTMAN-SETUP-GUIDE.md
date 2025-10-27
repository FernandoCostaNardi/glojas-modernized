# Guia de Configuração do Postman - EventOriginController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Origens de Eventos (`EventOriginController`) da Business API usando o Postman. O controller oferece 4 endpoints para CRUD completo de origens de eventos, busca com filtros e paginação, e gerenciamento com autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Selecione o arquivo `EventOriginController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `EventOriginController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"EventOriginController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `event_origin_id`: ID de uma origem de evento existente para testes
- `event_source_pdv`: PDV
- `event_source_exchange`: EXCHANGE
- `event_source_danfe`: DANFE

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
1. Execute o endpoint `GET /api/business/event-origins`
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

#### Passo 2: Criar Origem de Evento
```http
POST {{base_url}}/event-origins
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001"
}
```
Expected: Status 201, Origem criada

#### Passo 3: Buscar Origem por ID
```http
GET {{base_url}}/event-origins/{{event_origin_id}}
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Dados da origem

#### Passo 4: Atualizar Origem
```http
PUT {{base_url}}/event-origins/{{event_origin_id}}
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001-UPDATED"
}
```
Expected: Status 200, Origem atualizada

#### Passo 5: Buscar com Filtros
```http
GET {{base_url}}/event-origins?eventSource=PDV&page=0&size=10
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Lista filtrada

### 2. Testes de Validação

#### Teste 1: EventSource Ausente
```http
POST {{base_url}}/event-origins
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "sourceCode": "PDV-001"
}
```
Expected: Status 400, Erro de validação

#### Teste 2: SourceCode Ausente
```http
POST {{base_url}}/event-origins
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "eventSource": "PDV"
}
```
Expected: Status 400, Erro de validação

#### Teste 3: SourceCode Muito Longo
```http
POST {{base_url}}/event-origins
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "eventSource": "PDV",
  "sourceCode": "PDV-001-CODIGO-MUITO-LONGO-QUE-EXCEDE-O-LIMITE-DE-CINQUENTA-CARACTERES"
}
```
Expected: Status 400, Erro de validação

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Origem atualizada, encontrada, listada |
| `201` | Created | Origem criada com sucesso |
| `400` | Bad Request | Dados inválidos, origem não encontrada |
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
- **Causa**: Dados inválidos ou origem não encontrada
- **Solução**: Verificar dados enviados e se a origem existe

### 2. Validação de Dados

#### EventSource
- Deve ser PDV, EXCHANGE ou DANFE
- Exemplo válido: `PDV`
- Exemplo inválido: `VENDA`

#### SourceCode
- Deve ter no máximo 50 caracteres
- Não pode ser vazio
- Exemplo válido: `PDV-001`
- Exemplo inválido: `PDV-001-CODIGO-MUITO-LONGO-QUE-EXCEDE-O-LIMITE-DE-CINQUENTA-CARACTERES`

## 📝 Exemplos Práticos

### Exemplo 1: Criar Origem com cURL

```bash
curl -X POST "http://localhost:8089/api/business/event-origins" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "eventSource": "PDV",
    "sourceCode": "PDV-001"
  }'
```

### Exemplo 2: Atualizar Origem com cURL

```bash
curl -X PUT "http://localhost:8089/api/business/event-origins/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "eventSource": "PDV",
    "sourceCode": "PDV-001-UPDATED"
  }'
```

### Exemplo 3: Buscar Origens com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/event-origins?eventSource=PDV&page=0&size=10&sortBy=sourceCode&sortDir=asc" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissões específicas (`origin:create`, `origin:update`, `origin:read`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **SourceCode**: Máximo 50 caracteres

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **Controller simples**: 4 endpoints (vs 5 do OperationController)
2. **DTOs com Records**: Uso de Records para DTOs simples (≤5 campos)
3. **Enum EventSource**: PDV, EXCHANGE, DANFE
4. **Totalizadores**: Por tipo de fonte de evento
5. **Busca simples**: Filtro apenas por eventSource
6. **Sem atualização parcial**: Todos os campos são obrigatórios

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
