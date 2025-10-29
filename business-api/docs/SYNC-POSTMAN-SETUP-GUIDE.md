# Guia de Configuração do Postman - SyncController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Sincronização (`SyncController`) da Business API usando o Postman. O controller oferece 2 endpoints para sincronização manual de vendas diárias e verificação de status do serviço. Todos os endpoints requerem autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `SyncController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `SyncController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"SyncController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `sync_start_date`: Data de início para sincronização (YYYY-MM-DD)
- `sync_end_date`: Data de fim para sincronização (YYYY-MM-DD)

## 🔐 Autenticação JWT

### 1. Obter Token JWT

**IMPORTANTE**: Antes de testar os endpoints, você deve obter um token JWT válido usando o `AuthController`.

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

## 🧪 Cenários de Teste

### 1. Fluxo Completo de Sincronização

#### Passo 1: Fazer Login
```http
POST {{base_url}}/auth/login
```
Expected: Status 200, Token JWT válido

#### Passo 2: Verificar Status do Serviço
```http
GET {{base_url}}/sync/status
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Status HEALTHY

#### Passo 3: Executar Sincronização
```http
POST {{base_url}}/sync/daily-sales
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31"
}
```
Expected: Status 200, Estatísticas de sincronização

### 2. Testes de Validação

#### Teste 1: Data de Início Ausente
```http
POST {{base_url}}/sync/daily-sales
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "endDate": "2024-01-31"
}
```
Expected: Status 400, Erro de validação

#### Teste 2: Data de Início Posterior à Data de Fim
```http
POST {{base_url}}/sync/daily-sales
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-31",
  "endDate": "2024-01-01"
}
```
Expected: Status 400, Erro de validação

#### Teste 3: Data Futura
```http
POST {{base_url}}/sync/daily-sales
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2025-01-01",
  "endDate": "2025-01-31"
}
```
Expected: Status 400, Erro de validação

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Sincronização executada, status saudável |
| `400` | Bad Request | Dados inválidos, datas inválidas |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno do servidor |
| `503` | Service Unavailable | Serviço com problemas |

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 401 - Unauthorized
- **Causa**: Token JWT ausente, inválido ou expirado
- **Solução**: Obter novo token usando AuthController

#### Erro 403 - Forbidden
- **Causa**: Usuário sem permissões `sync:execute` ou `sync:read`
- **Solução**: Verificar permissões do usuário

#### Erro 400 - Bad Request
- **Causa**: Datas inválidas
- **Solução**: Verificar formato (YYYY-MM-DD) e lógica das datas

### 2. Validação de Dados

#### Datas
- Formato: YYYY-MM-DD (ISO 8601)
- Data de início não pode ser posterior à data de fim
- Datas não podem ser futuras
- Ambas as datas são obrigatórias
- Exemplo válido: `2024-01-01`
- Exemplo inválido: `01/01/2024` ou `2025-01-01`

## 📝 Exemplos Práticos

### Exemplo 1: Sincronização com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sync/daily-sales" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  }'
```

### Exemplo 2: Status do Serviço com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sync/status" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissões `sync:execute` e `sync:read`
3. **CORS**: Configurado para `http://localhost:3000`
4. **Validação de datas**: Não permite datas futuras
5. **Período**: startDate não pode ser posterior a endDate
6. **Integração**: Depende da disponibilidade da Legacy API

### Dependências

- **Legacy API**: Fonte de dados de vendas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões

### Diferenças dos Controllers Anteriores

1. **Operação de sincronização**: Processa dados em lote
2. **Estatísticas de processamento**: Retorna contadores
3. **Validação de datas**: Não permite datas futuras
4. **Endpoint de status**: Verifica saúde do serviço
5. **Status 503**: Pode retornar Service Unavailable
6. **Processamento assíncrono**: Pode demorar mais tempo

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API


