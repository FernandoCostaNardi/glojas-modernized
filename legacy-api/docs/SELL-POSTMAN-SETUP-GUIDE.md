# Guia de Configuração - Postman para SellController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `SellController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `SellController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **SellController Environment**

## 📋 Estrutura da Collection

```
📁 SellController API - Legacy
├── 📁 Sales Reports
│   ├── 📄 POST Store Sales Report
│   └── 📄 POST Store Sales Report By Day
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Store Report - Success
    │   └── 📄 POST Store Report By Day - Success
    └── 📁 Error Cases
        ├── 📄 POST Store Report - Missing Fields
        ├── 📄 POST Store Report - Invalid Date Format
        ├── 📄 POST Store Report - Empty Lists
        └── 📄 POST Store Report - Server Error
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8087/api/legacy` | URL base da API |
| `start_date` | `2024-01-01` | Data de início para testes |
| `end_date` | `2024-01-31` | Data de fim para testes |
| `start_date_short` | `2024-01-01` | Data de início para testes curtos |
| `end_date_short` | `2024-01-03` | Data de fim para testes curtos |
| `store_codes` | `["001", "002", "003"]` | Códigos de loja para testes |
| `store_codes_short` | `["001", "002"]` | Códigos de loja para testes curtos |
| `danfe_origin` | `["DANFE1", "DANFE2"]` | Códigos de origem DANFE |
| `pdv_origin` | `["PDV1", "PDV2"]` | Códigos de origem PDV |
| `exchange_origin` | `["TROCA1", "TROCA2"]` | Códigos de origem de troca |
| `sell_operation` | `["VENDA1", "VENDA2"]` | Códigos de operação de venda |
| `exchange_operation` | `["TROCA1", "TROCA2"]` | Códigos de operação de troca |
| `invalid_date_format` | `01/01/2024` | Formato de data inválido para testes |
| `empty_list` | `[]` | Lista vazia para testes de validação |

## 🧪 Como Executar os Testes

### Teste 1: Relatório de Vendas por Loja

1. Selecione **Sales Reports > POST Store Sales Report**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de lojas com dados agregados
   - Cada loja tem `storeName`, `storeCode`, `danfe`, `pdv`, `troca3`

### Teste 2: Relatório de Vendas por Loja e por Dia

1. Selecione **Sales Reports > POST Store Sales Report By Day**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de registros por loja e por dia
   - Cada registro tem `storeName`, `storeCode`, `reportDate`, `danfe`, `pdv`, `troca3`

### Teste 3: Cenários de Erro

#### Campos Obrigatórios Ausentes
1. Selecione **Test Scenarios > Error Cases > POST Store Report - Missing Fields**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### Formato de Data Inválido
1. Selecione **Test Scenarios > Error Cases > POST Store Report - Invalid Date Format**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### Listas Vazias
1. Selecione **Test Scenarios > Error Cases > POST Store Report - Empty Lists**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### Erro do Servidor
1. Selecione **Test Scenarios > Error Cases > POST Store Report - Server Error**
2. Clique em **Send**
3. Verifique: Status Code `500`

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para POST Store Sales Report:
- Status code é 200
- Response é um array JSON
- Cada loja tem os campos obrigatórios (`storeName`, `storeCode`, `danfe`, `pdv`, `troca3`)
- Valores monetários são BigDecimal

### Para POST Store Sales Report By Day:
- Status code é 200
- Response é um array JSON
- Cada registro tem os campos obrigatórios (`storeName`, `storeCode`, `reportDate`, `danfe`, `pdv`, `troca3`)
- Data está no formato correto (YYYY-MM-DD)

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **SellController API - Legacy**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 1000ms (recomendado para APIs de relatório)
   - **Data**: None
4. Clique em **Run SellController API - Legacy**

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se a Legacy API está rodando na porta 8087
- Confirme se o context path está correto: `/api/legacy`

### Erro 400 (Bad Request)
- Verifique se todos os campos obrigatórios estão presentes
- Confirme se o formato das datas está correto (YYYY-MM-DD)
- Verifique se todas as listas têm pelo menos um elemento

### Erro 500 (Internal Server Error)
- Verifique os logs da aplicação
- Confirme se o banco de dados está acessível
- Verifique se as datas estão no período válido

### Erro de Validação
- **Data de início ausente**: Adicione o campo `startDate`
- **Data de fim ausente**: Adicione o campo `endDate`
- **Formato de data inválido**: Use formato YYYY-MM-DD
- **Lista vazia**: Adicione pelo menos um elemento nas listas

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Body da requisição
- Status code da resposta
- Tempo de resposta
- Tamanho da resposta

Verifique o console do Postman para ver os logs detalhados.

## 🔄 Diferenças dos Controllers Anteriores

### Método HTTP
- **SellController**: POST (não GET)
- **Request Body**: JSON complexo com múltiplas validações
- **Response**: Múltiplos campos BigDecimal (monetários)

### Validações
- **Bean Validation**: @Valid com múltiplas validações
- **Campos Obrigatórios**: Todos os campos são obrigatórios
- **Formato de Data**: Apenas YYYY-MM-DD aceito
- **Listas**: Todas as listas devem ter pelo menos um elemento

### Endpoints
- **Dois endpoints**: Relatório agregado e por dia
- **Lógica Complexa**: Processamento de dados agregados
- **Performance**: Query otimizada com filtro de data no banco

### Cenários de Teste
- **Validação Rigorosa**: Múltiplos cenários de erro
- **Dados Monetários**: Validação de valores BigDecimal
- **Datas**: Validação de formato e período
- **Listas**: Validação de conteúdo não vazio

## 💡 Dicas de Uso

### Para Testes de Sucesso
- Use datas válidas no formato YYYY-MM-DD
- Certifique-se de que todas as listas têm elementos
- Use códigos de loja válidos

### Para Testes de Erro
- Remova campos obrigatórios para testar validação
- Use formato de data inválido (DD/MM/YYYY)
- Use listas vazias para testar validação

### Para Testes de Performance
- Use períodos curtos (1-3 dias) para testes rápidos
- Use períodos longos (1 mês) para testes de performance
- Monitore o tempo de resposta nos logs

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
