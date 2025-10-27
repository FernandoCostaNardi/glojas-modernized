# Guia de Configuração - Postman para OperationController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `OperationController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `OperationController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **OperationController Environment**

## 📋 Estrutura da Collection

```
📁 OperationController API - Legacy
├── 📁 Operations
│   ├── 📄 GET All Operations
│   └── 📄 GET Operation by ID
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET All Operations - Success
    │   └── 📄 GET Operation by ID - Success
    └── 📁 Error Cases
        ├── 📄 GET Operation by ID - Invalid ID (Negative)
        ├── 📄 GET Operation by ID - Invalid ID (Zero)
        ├── 📄 GET Operation by ID - Not Found
        └── 📄 GET Operation by ID - Non-numeric ID
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8087/api/legacy` | URL base da API |
| `operation_id` | `1` | ID válido para testes |
| `test_operation_id` | `2` | ID alternativo para testes |
| `invalid_operation_id` | `-1` | ID inválido (negativo) |
| `not_found_operation_id` | `999999` | ID inexistente |
| `zero_operation_id` | `0` | ID inválido (zero) |
| `non_numeric_operation_id` | `abc` | ID não numérico |

## 🧪 Como Executar os Testes

### Teste 1: Listar Todas as Operações

1. Selecione **Operations > GET All Operations**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de operações
   - Cada operação tem `id` e `description`

### Teste 2: Buscar Operação por ID

1. Selecione **Operations > GET Operation by ID**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Objeto com `id` e `description`
   - ID formatado com 6 dígitos

### Teste 3: Cenários de Erro

#### ID Inválido (Negativo)
1. Selecione **Test Scenarios > Error Cases > GET Operation by ID - Invalid ID (Negative)**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### ID Inválido (Zero)
1. Selecione **Test Scenarios > Error Cases > GET Operation by ID - Invalid ID (Zero)**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### Operação Não Encontrada
1. Selecione **Test Scenarios > Error Cases > GET Operation by ID - Not Found**
2. Clique em **Send**
3. Verifique: Status Code `404`

#### ID Não Numérico
1. Selecione **Test Scenarios > Error Cases > GET Operation by ID - Non-numeric ID**
2. Clique em **Send**
3. Verifique: Status Code `400`

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para GET All Operations:
- Status code é 200
- Response é um array JSON
- Cada operação tem os campos obrigatórios
- ID está no formato correto (6 dígitos)

### Para GET Operation by ID:
- Status code é 200
- Response é um objeto JSON
- Operação tem os campos obrigatórios
- ID está no formato correto (6 dígitos)

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **OperationController API - Legacy**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 0ms
   - **Data**: None
4. Clique em **Run OperationController API - Legacy**

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se a Legacy API está rodando na porta 8087
- Confirme se o context path está correto: `/api/legacy`

### Erro 404
- Verifique se a URL está correta
- Confirme se o endpoint existe

### Erro 500
- Verifique os logs da aplicação
- Confirme se o banco de dados está acessível

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Status code da resposta
- Tempo de resposta

Verifique o console do Postman para ver os logs detalhados.

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
