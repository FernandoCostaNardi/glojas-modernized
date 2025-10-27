# Guia de Configuração do Postman - SellController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Relatórios de Vendas (`SellController`) da Business API usando o Postman. O controller oferece 4 endpoints para relatórios de vendas com diferentes níveis de agregação, consumindo dados da Legacy API. Todos os endpoints requerem autenticação JWT obrigatória.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import** (botão no canto superior esquerdo)
3. Selecione o arquivo `SellController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `SellController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"SellController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `jwt_token`: Token JWT válido (preenchido pelo AuthController)
- `start_date`: Data de início para relatórios (formato: YYYY-MM-DD)
- `end_date`: Data de fim para relatórios (formato: YYYY-MM-DD)
- `store_code_001`: Código da loja 1
- `store_code_002`: Código da loja 2
- `store_code_003`: Código da loja 3

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
1. Execute o endpoint `GET /api/business/sales/current-daily-sales`
2. Se retornar status 200, o token está funcionando

## 🧪 Cenários de Teste

### 1. Fluxo Completo de Relatórios

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

#### Passo 2: Relatório por Loja
```http
POST {{base_url}}/sales/store-report
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": ["001", "002"]
}
```
Expected: Status 200, Dados agregados por loja

#### Passo 3: Relatório por Loja e Dia
```http
POST {{base_url}}/sales/store-report-by-day
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-03",
  "storeCodes": ["001"]
}
```
Expected: Status 200, Dados por loja e dia

#### Passo 4: Relatório de Vendas Diárias
```http
GET {{base_url}}/sales/daily-sales?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Dados consolidados

#### Passo 5: Vendas do Dia Atual
```http
GET {{base_url}}/sales/current-daily-sales
Authorization: Bearer {{jwt_token}}
```
Expected: Status 200, Dados em tempo real

### 2. Testes de Validação

#### Teste 1: Data de Início Ausente
```http
POST {{base_url}}/sales/store-report
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "endDate": "2024-01-31",
  "storeCodes": ["001"]
}
```
Expected: Status 400, Erro de validação

#### Teste 2: Lista de Lojas Vazia
```http
POST {{base_url}}/sales/store-report
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "storeCodes": []
}
```
Expected: Status 400, Erro de validação

#### Teste 3: Data de Início Posterior à Data de Fim
```http
POST {{base_url}}/sales/store-report
Authorization: Bearer {{jwt_token}}
```
Body:
```json
{
  "startDate": "2024-01-31",
  "endDate": "2024-01-01",
  "storeCodes": ["001"]
}
```
Expected: Status 400, Erro de validação

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Relatório gerado com sucesso |
| `400` | Bad Request | Dados inválidos, datas inválidas |
| `401` | Unauthorized | Token ausente, inválido ou expirado |
| `403` | Forbidden | Usuário sem permissão adequada |
| `500` | Internal Server Error | Erro interno ou Legacy API indisponível |

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 401 - Unauthorized
- **Causa**: Token JWT ausente, inválido ou expirado
- **Solução**: Obter novo token usando AuthController

#### Erro 403 - Forbidden
- **Causa**: Usuário sem permissão `sell:read`
- **Solução**: Verificar se o usuário tem a permissão adequada

#### Erro 400 - Bad Request
- **Causa**: Dados inválidos ou datas inválidas
- **Solução**: Verificar formato das datas (YYYY-MM-DD) e lista de lojas

#### Erro 500 - Internal Server Error
- **Causa**: Legacy API indisponível ou erro interno
- **Solução**: Verificar se a Legacy API está rodando (porta 8087)

### 2. Validação de Dados

#### Datas
- Formato: YYYY-MM-DD (ISO 8601)
- Data de início não pode ser posterior à data de fim
- Ambas as datas são obrigatórias
- Exemplo válido: `2024-01-01`
- Exemplo inválido: `01/01/2024`

#### Códigos de Lojas
- Lista não pode estar vazia
- Códigos não podem ser nulos ou vazios
- Exemplo válido: `["001", "002"]`
- Exemplo inválido: `[]` ou `[null]` ou `[""]`

## 📝 Exemplos Práticos

### Exemplo 1: Relatório por Loja com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sales/store-report" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "storeCodes": ["001", "002"]
  }'
```

### Exemplo 2: Relatório por Loja e Dia com cURL

```bash
curl -X POST "http://localhost:8089/api/business/sales/store-report-by-day" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-01-03",
    "storeCodes": ["001"]
  }'
```

### Exemplo 3: Vendas Diárias com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sales/daily-sales?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Exemplo 4: Vendas do Dia Atual com cURL

```bash
curl -X GET "http://localhost:8089/api/business/sales/current-daily-sales" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer SEU_TOKEN"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Autenticação JWT**: Token obrigatório para todos os endpoints
2. **Autorização**: Requer permissão `sell:read`
3. **CORS**: Configurado para `http://localhost:3000`
4. **Integração**: Depende da disponibilidade da Legacy API (porta 8087)
5. **Datas**: Formato ISO (YYYY-MM-DD)
6. **Cache**: Endpoint `/current-daily-sales` não usa cache

### Dependências

- **Legacy API**: Fonte de dados de vendas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432)
- **Security**: Spring Security com JWT
- **Authorization**: PreAuthorize com permissões
- **Validation**: Bean Validation com anotações

### Diferenças dos Controllers Anteriores

1. **Integração externa**: Consome dados da Legacy API
2. **Múltiplos tipos de relatório**: 4 endpoints diferentes
3. **Mix de métodos**: POST e GET
4. **Dados financeiros**: BigDecimal para valores monetários
5. **Tempo real**: Endpoint sem cache para dados atualizados
6. **Headers especiais**: Anti-cache headers no endpoint de tempo real
7. **Validação de datas**: Lógica customizada de validação
8. **Agregação de dados**: Calcula totais automaticamente

### Tipos de Vendas

- **DANFE**: Vendas via Nota Fiscal Eletrônica
- **PDV**: Vendas via Ponto de Venda
- **TROCA/EXCHANGE**: Trocas de produtos (reduzem o faturamento)
- **TOTAL**: DANFE + PDV - TROCA

### Cálculo de Totais

O total é calculado automaticamente pela fórmula:
```
TOTAL = DANFE + PDV - TROCA
```

A troca é subtraída pois representa devoluções/trocas que reduzem o faturamento líquido.

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API
