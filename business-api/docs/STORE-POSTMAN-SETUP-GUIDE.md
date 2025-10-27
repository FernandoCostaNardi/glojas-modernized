# Guia de Configuração do Postman - StoreController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Lojas (`StoreController`) da Business API usando o Postman. O controller oferece 7 endpoints para CRUD completo de lojas, busca avançada com filtros e paginação, e gerenciamento com autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `StoreController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `StoreController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"StoreController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `store_id`: ID de uma loja existente para testes
- `store_code`: LOJA01
- `store_name`: Loja Centro
- `store_city`: São Paulo

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
1. Execute o endpoint `GET /api/business/stores/health`
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

#### Passo 2: Criar Loja
```http
POST {{base_url}}/stores
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "LOJA01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```
Expected: Status 201, Loja criada

#### Passo 3: Buscar Loja por ID
```http
GET {{base_url}}/stores/{{store_id}}
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Dados da loja

#### Passo 4: Atualizar Loja
```http
PUT {{base_url}}/stores/{{store_id}}
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "name": "Loja Centro - Atualizada",
  "city": "São Paulo - SP",
  "status": true
}
```
Expected: Status 200, Loja atualizada

#### Passo 5: Buscar com Filtros
```http
GET {{base_url}}/stores/search?city=São Paulo&status=true
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Lista filtrada

#### Passo 6: Remover Loja
```http
DELETE {{base_url}}/stores/{{store_id}}
Authorization: Bearer {{jwt_token}}
```
Expected: Status 204, Loja removida

### 2. Testes de Validação

#### Teste 1: Código Inválido (Muito Curto)
```http
POST {{base_url}}/stores
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "LJ01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```
Expected: Status 400, Erro de validação

#### Teste 2: Código com Minúsculas
```http
POST {{base_url}}/stores
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "loja01",
  "name": "Loja Centro",
  "city": "São Paulo",
  "status": true
}
```
Expected: Status 400, Erro de validação

#### Teste 3: Nome Ausente
```http
POST {{base_url}}/stores
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "code": "LOJA01",
  "city": "São Paulo",
  "status": true
}
```
Expected: Status 400, Erro de validação

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Loja atualizada, encontrada, listada |
| `201` | Created | Loja criada com sucesso |
| `204` | No Content | Loja removida com sucesso |
| `400` | Bad Request | Dados inválidos, loja não encontrada |
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
- **Causa**: Dados inválidos ou loja não encontrada
- **Solução**: Verificar dados enviados e se a loja existe

### 2. Validação de Dados

#### Código da Loja
- Deve ter exatamente 6 caracteres
- Deve conter apenas letras maiúsculas e números
- Exemplo válido: `LOJA01`
- Exemplo inválido: `loja01` ou `LJ01`

#### Nome
- Deve ter entre 2 e 255 caracteres
- Não pode ser vazio
- Exemplo válido: `Loja Centro`
- Exemplo inválido: `L`

#### Cidade
- Deve ter entre 2 e 100 caracteres
- Não pode ser vazia
- Exemplo válido: `São Paulo`
- Exemplo inválido: `S`

## 📝 Exemplos Práticos

### Exemplo 1: Criar Loja com cURL

```bash
curl -X POST "http://localhost:8089/api/business/stores" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "code": "LOJA01",
    "name": "Loja Centro",
    "city": "São Paulo",
    "status": true
  }'
```

### Exemplo 2: Buscar Lojas com Filtros com cURL

```bash
curl -X GET "http://localhost:8089/api/business/stores/search?city=São Paulo&status=true&page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 3: Remover Loja com cURL

```bash
curl -X DELETE "http://localhost:8089/api/business/stores/550e8400-e29b-41d4-a716-446655440000" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para endpoints protegidos
2. **Autorização**: Requer permissões específicas (`store:create`, `store:update`, `store:read`, `store:delete`)
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação**: Bean Validation com regras específicas
5. **Código único**: Não permite lojas com mesmo código
6. **Código imutável**: Não pode ser alterado após criação

### Dependências

- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **CRUD completo**: POST, PUT, GET, DELETE
2. **Busca avançada**: Endpoint separado `/search` com múltiplos filtros
3. **Código com padrão rígido**: 6 caracteres alfanuméricos maiúsculos
4. **Código imutável**: Não pode ser alterado no PUT
5. **Status boolean**: Ativa/inativa
6. **Timestamps**: createdAt e updatedAt
7. **Paginação Spring**: Usa Page do Spring Data

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
