# Guia de Configuração - Postman para PermissionController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `PermissionController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `PermissionController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **PermissionController Environment**

## 📋 Estrutura da Collection

```
📁 PermissionController API - Business
├── 📁 Permissions
│   ├── 📄 GET All Permissions
│   └── 📄 GET Health Check
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 GET All Permissions - Success
    │   └── 📄 GET Health Check - Success
    └── 📁 Error Cases
        ├── 📄 GET All Permissions - No Token
        ├── 📄 GET All Permissions - Invalid Token
        ├── 📄 GET All Permissions - Expired Token
        ├── 📄 GET All Permissions - No Permission
        └── 📄 GET All Permissions - Server Error
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `` | Token JWT válido (preenchido pelo AuthController) |
| `jwt_token_invalid` | `token_invalido` | Token JWT inválido para testes |
| `jwt_token_expired` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado` | Token JWT expirado para testes |
| `jwt_token_sem_permissao` | `` | Token JWT sem permissão para testes |

## 🧪 Como Executar os Testes

### Pré-requisito: Obter Token JWT

**IMPORTANTE**: Antes de testar o PermissionController, você deve fazer login usando o AuthController:

1. **Fazer Login**:
   - Use a collection do `AuthController`
   - Execute `POST /api/business/auth/login`
   - O token será salvo automaticamente no environment

2. **Verificar Token**:
   - Confirme se `{{jwt_token}}` está preenchido no environment
   - O token deve ter a permissão `permission:read`

### Teste 1: Listar Permissões com Token Válido

1. Selecione **Permissions > GET All Permissions**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de permissões com dados completos
   - Cada permissão tem `id`, `name`, `resource`, `action`, `description`, `createdAt`

### Teste 2: Health Check (Público)

1. Selecione **Permissions > GET Health Check**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: "PermissionController está funcionando"

### Teste 3: Cenários de Erro

#### Sem Token
1. Selecione **Test Scenarios > Error Cases > GET All Permissions - No Token**
2. Clique em **Send**
3. Verifique: Status Code `401`

#### Token Inválido
1. Selecione **Test Scenarios > Error Cases > GET All Permissions - Invalid Token**
2. Clique em **Send**
3. Verifique: Status Code `401`

#### Token Expirado
1. Selecione **Test Scenarios > Error Cases > GET All Permissions - Expired Token**
2. Clique em **Send**
3. Verifique: Status Code `401`

#### Sem Permissão
1. Selecione **Test Scenarios > Error Cases > GET All Permissions - No Permission**
2. Clique em **Send**
3. Verifique: Status Code `403`

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para GET All Permissions:
- Status code é 200
- Response é um array JSON
- Cada permissão tem os campos obrigatórios (`id`, `name`, `resource`, `action`, `description`, `createdAt`)
- ID é um UUID válido

### Para GET Health Check:
- Status code é 200
- Response é uma string
- Response contém a mensagem esperada

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **PermissionController API - Business**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 500ms (recomendado para autenticação)
   - **Data**: None
4. Clique em **Run PermissionController API - Business**

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se a Business API está rodando na porta 8089
- Confirme se o context path está correto: `/api/business`

### Erro 401 (Unauthorized)
- **Token ausente**: Adicione header `Authorization: Bearer {{jwt_token}}`
- **Token inválido**: Faça login novamente com AuthController
- **Token expirado**: Faça login novamente (token expira em 24h)

### Erro 403 (Forbidden)
- **Sem permissão**: Verifique se o usuário tem `permission:read`
- **Token incorreto**: Use token de usuário com permissões adequadas

### Erro 500 (Internal Server Error)
- Verifique os logs da aplicação
- Confirme se o banco de dados PostgreSQL está acessível
- Verifique se as permissões estão cadastradas no banco

### Token não está sendo salvo
- Execute primeiro o login com AuthController
- Verifique se o script de teste do AuthController está funcionando
- Confirme se o environment está selecionado

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Headers da requisição
- Status code da resposta
- Tempo de resposta
- Tamanho da resposta

Verifique o console do Postman para ver os logs detalhados.

## 🔄 Diferenças do AuthController

### Autenticação
- **PermissionController**: Requer token JWT válido
- **AuthController**: Gera token JWT

### Método HTTP
- **PermissionController**: GET (consulta)
- **AuthController**: POST (autenticação)

### Autorização
- **PermissionController**: PreAuthorize com `permission:read`
- **AuthController**: Sem autorização (público)

### Response
- **PermissionController**: Lista de permissões
- **AuthController**: Token JWT + dados do usuário

### Health Check
- **PermissionController**: Endpoint público `/health`
- **AuthController**: Sem health check

## 💡 Dicas de Uso

### Para Testes de Sucesso
- **Primeiro**: Faça login com AuthController para obter token
- **Segundo**: Use o token salvo para testar PermissionController
- **Verifique**: Se o usuário tem permissão `permission:read`

### Para Testes de Erro
- **Sem token**: Remova o header Authorization
- **Token inválido**: Use `{{jwt_token_invalid}}`
- **Token expirado**: Use `{{jwt_token_expired}}`
- **Sem permissão**: Use token de usuário sem `permission:read`

### Para Usar Token em Outras Requisições
- O token é salvo automaticamente pelo AuthController
- Use `{{jwt_token}}` em headers de outras requisições
- Exemplo: `Authorization: Bearer {{jwt_token}}`

### Para Verificar Permissões
- Execute `GET All Permissions` para ver todas as permissões
- Verifique se o usuário tem as permissões necessárias
- Use as permissões retornadas para configurar outros testes

## 🔐 Segurança

### Autenticação JWT
- **Obrigatória**: Todos os endpoints protegidos requerem token
- **Validação**: Token é validado em cada requisição
- **Expiração**: 24 horas (configurável)

### Autorização
- **Permission-based**: Controle granular por permissões
- **PreAuthorize**: Validação no controller
- **Resource-based**: Permissões por recurso e ação

### CORS
- **Origem permitida**: `http://localhost:3000`
- **Métodos**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: Todos permitidos

## 🔄 Fluxo Completo de Teste

### 1. Preparação
1. **Importar collections**: AuthController + PermissionController
2. **Configurar environment**: Selecionar environment correto
3. **Verificar API**: Confirmar que Business API está rodando

### 2. Autenticação
1. **Fazer login**: `POST /api/business/auth/login`
2. **Verificar token**: Confirmar que `{{jwt_token}}` foi salvo
3. **Verificar permissões**: Confirmar que usuário tem `permission:read`

### 3. Teste de Permissões
1. **Listar permissões**: `GET /api/business/permissions`
2. **Health check**: `GET /api/business/permissions/health`
3. **Testar cenários de erro**: Sem token, token inválido, etc.

### 4. Validação
1. **Verificar responses**: Status codes e dados corretos
2. **Verificar logs**: Console do Postman e logs da aplicação
3. **Testar autorização**: Diferentes níveis de permissão

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
