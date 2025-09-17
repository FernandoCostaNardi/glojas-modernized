# API de Vendas - Store Report

## Visão Geral

Esta API fornece um endpoint para obter relatórios de vendas por loja, consumindo dados da Legacy API.

## Endpoint

### POST `/sales/store-report`

Obtém relatório de vendas agregados por loja para um período específico.

#### Autenticação
Requer autenticação JWT com role `ADMIN` ou `USER`.

#### Headers
```
Content-Type: application/json
Authorization: Bearer {jwt-token}
```

#### Request Body

```json
{
  "startDate": "2025-09-13",
  "endDate": "2025-09-13", 
  "storeCodes": [
    "000002",
    "000003",
    "000004",
    "000005",
    "000006",
    "000007",
    "000008"
  ],
  "danfeOrigin": [
    "051",
    "065"
  ],
  "pdvOrigin": [
    "009"
  ],
  "exchangeOrigin": [
    "015",
    "002"
  ],
  "sellOperation": [
    "000999",
    "000007",
    "000001",
    "000045",
    "000054",
    "000062",
    "000063",
    "000064",
    "000065",
    "000067",
    "000068",
    "000069",
    "000071"
  ],
  "exchangeOperation": [
    "000015",
    "000048"
  ]
}
```

#### Request Parameters

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `startDate` | `string (date)` | Sim | Data de início do período (formato: YYYY-MM-DD) |
| `endDate` | `string (date)` | Sim | Data de fim do período (formato: YYYY-MM-DD) |
| `storeCodes` | `array[string]` | Sim | Lista de códigos das lojas (máximo 50) |
| `danfeOrigin` | `array[string]` | Não | Lista de códigos origem DANFE |
| `pdvOrigin` | `array[string]` | Não | Lista de códigos origem PDV |
| `exchangeOrigin` | `array[string]` | Não | Lista de códigos origem troca |
| `sellOperation` | `array[string]` | Não | Lista de operações de venda |
| `exchangeOperation` | `array[string]` | Não | Lista de operações de troca |

#### Validações
- `startDate` não pode ser posterior a `endDate`
- `startDate` e `endDate` não podem ser futuras
- `storeCodes` não pode estar vazio
- Máximo de 50 lojas por requisição

#### Response Success (200)

```json
[
  {
    "storeName": "CD JANGURUSSU",
    "storeCode": "000002",
    "danfe": 0,
    "pdv": 0,
    "troca3": 0
  },
  {
    "storeName": "SMART ANT. SALES",
    "storeCode": "000003",
    "danfe": 1788.0000,
    "pdv": 184.3200,
    "troca3": 0
  },
  {
    "storeName": "SMART IANDE",
    "storeCode": "000007",
    "danfe": 16555.0000,
    "pdv": 955.7000,
    "troca3": 0
  }
]
```

#### Response Fields

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `storeName` | `string` | Nome da loja |
| `storeCode` | `string` | Código da loja |
| `danfe` | `number` | Valor total das vendas DANFE |
| `pdv` | `number` | Valor total das vendas PDV |
| `troca3` | `number` | Valor total das trocas (tipo 3) |

#### Response Error (400 - Bad Request)

```json
{
  "status": "ERROR",
  "message": "Data de início não pode ser posterior à data de fim",
  "details": "uri=/api/v1/sales/store-report"
}
```

#### Response Error (502 - Bad Gateway)

```json
{
  "status": "ERROR", 
  "message": "Erro HTTP 500 ao chamar Legacy API: Internal Server Error",
  "details": "uri=/api/v1/sales/store-report"
}
```

#### Response Error (401 - Unauthorized)

```json
{
  "status": "ERROR",
  "message": "Credenciais inválidas",
  "details": "uri=/api/v1/sales/store-report"
}
```

#### Response Error (403 - Forbidden)

```json
{
  "status": "ERROR",
  "message": "Acesso negado. Você não tem permissão para executar esta operação.",
  "details": "uri=/api/v1/sales/store-report"
}
```

## Exemplo com cURL

```bash
curl -X POST http://localhost:8082/api/business/sales/store-report \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "startDate": "2025-09-13",
    "endDate": "2025-09-13",
    "storeCodes": ["000002", "000003", "000004"],
    "danfeOrigin": ["051", "065"],
    "pdvOrigin": ["009"]
  }'
```

## Integração com Legacy API

Esta API atua como um proxy para a Legacy API, realizando as seguintes operações:

1. **Validação**: Valida os parâmetros de entrada
2. **Transformação**: Converte as datas para o formato esperado pela Legacy API
3. **Comunicação**: Faz chamada HTTP POST para `http://localhost:8081/api/legacy/api/sales/store-report`
4. **Mapeamento**: Converte a resposta da Legacy API para nosso formato
5. **Tratamento de Erro**: Gerencia timeouts e erros HTTP

## Configuração

As seguintes propriedades podem ser configuradas no `application.yml`:

```yaml
legacy-api:
  base-url: http://localhost:8081
  context-path: /api/legacy
  timeout: 30
```

## Logs

A API gera logs estruturados para auditoria:

```
2025-09-15 10:30:00.123 [http-nio-8082-exec-1] INFO  c.s.b.c.s.SellController - Recebida solicitação de relatório de vendas por loja: startDate=2025-09-13, endDate=2025-09-13, storeCodes=[000002, 000003]

2025-09-15 10:30:00.456 [http-nio-8082-exec-1] INFO  c.s.b.s.s.SellService - Solicitando relatório de vendas por loja: startDate=2025-09-13, endDate=2025-09-13, storeCodes=[000002, 000003]

2025-09-15 10:30:01.789 [http-nio-8082-exec-1] INFO  c.s.b.s.s.SellService - Relatório de vendas obtido com sucesso: 2 lojas processadas
```

## Arquitetura

```
Business API                     Legacy API
┌─────────────────┐             ┌──────────────────┐
│  SellController │────────────▶│  SellController  │
│                 │             │                  │
│   SellService   │────HTTP────▶│ StoreSalesService│
│                 │             │                  │
│   WebClient     │             │   Database       │
└─────────────────┘             └──────────────────┘
```

