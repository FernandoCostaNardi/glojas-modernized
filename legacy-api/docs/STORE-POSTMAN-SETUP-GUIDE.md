# Guia de Configuração - Postman para StoreController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `StoreController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `StoreController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **StoreController Environment**

## 📋 Estrutura da Collection

```
📁 StoreController API - Legacy
├── 📁 Stores
│   ├── 📄 GET All Stores
│   └── 📄 GET Store by ID
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET All Stores - Success
    │   └── 📄 GET Store by ID - Success
    └── 📁 Error Cases
        ├── 📄 GET Store by ID - Invalid ID (Negative)
        ├── 📄 GET Store by ID - Invalid ID (Zero)
        ├── 📄 GET Store by ID - Not Found
        ├── 📄 GET Store by ID - Non-numeric ID
        └── 📄 GET Store by ID - Empty ID
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8087/api/legacy` | URL base da API |
| `store_id` | `1` | ID válido para testes |
| `test_store_id` | `2` | ID alternativo para testes |
| `invalid_store_id` | `-1` | ID inválido (negativo) |
| `not_found_store_id` | `999999` | ID inexistente |
| `zero_store_id` | `0` | ID inválido (zero) |
| `non_numeric_store_id` | `abc` | ID não numérico |
| `empty_store_id` | `` | ID vazio |

## 🧪 Como Executar os Testes

### Teste 1: Listar Todas as Lojas

1. Selecione **Stores > GET All Stores**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de lojas
   - Cada loja tem `id`, `name` e `city`

### Teste 2: Buscar Loja por ID

1. Selecione **Stores > GET Store by ID**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Objeto com `id`, `name` e `city`
   - ID formatado com 6 dígitos

### Teste 3: Cenários de Erro

#### ID Inválido (Negativo)
1. Selecione **Test Scenarios > Error Cases > GET Store by ID - Invalid ID (Negative)**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### ID Inválido (Zero)
1. Selecione **Test Scenarios > Error Cases > GET Store by ID - Invalid ID (Zero)**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### ID Não Numérico
1. Selecione **Test Scenarios > Error Cases > GET Store by ID - Non-numeric ID**
2. Clique em **Send**
3. Verifique: Status Code `400`

#### Loja Não Encontrada
1. Selecione **Test Scenarios > Error Cases > GET Store by ID - Not Found**
2. Clique em **Send**
3. Verifique: Status Code `404`

#### ID Vazio
1. Selecione **Test Scenarios > Error Cases > GET Store by ID - Empty ID**
2. Clique em **Send**
3. Verifique: Status Code `404` (roteamento)

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para GET All Stores:
- Status code é 200
- Response é um array JSON
- Cada loja tem os campos obrigatórios (`id`, `name`, `city`)
- ID está no formato correto (6 dígitos)

### Para GET Store by ID:
- Status code é 200
- Response é um objeto JSON
- Loja tem os campos obrigatórios (`id`, `name`, `city`)
- ID está no formato correto (6 dígitos)

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **StoreController API - Legacy**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 0ms
   - **Data**: None
4. Clique em **Run StoreController API - Legacy**

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

### Erro 400 (ID inválido)
- Verifique se o ID é numérico
- Confirme se o ID é maior que 0
- Para IDs não numéricos, use variáveis do environment

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Status code da resposta
- Tempo de resposta

Verifique o console do Postman para ver os logs detalhados.

## 🔄 Diferenças do OperationController

### Validações Adicionais
- **ID como String**: Recebido como String e convertido para Long
- **Validação de conversão**: Verifica se o ID é numérico
- **Validação de valor**: Verifica se o ID é maior que 0
- **Tratamento de erro**: Diferentes tipos de erro para diferentes validações

### Campos do DTO
- **3 campos**: `id`, `name`, `city` (vs 2 campos do OperationController)
- **Formato do ID**: 6 dígitos com zeros à esquerda
- **Validação de campos**: Todos os campos são obrigatórios

### Cenários de Teste
- **ID vazio**: Cenário adicional para validação de roteamento
- **ID não numérico**: Validação específica para conversão String → Long
- **Validação de campos**: Verificação de 3 campos obrigatórios

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
