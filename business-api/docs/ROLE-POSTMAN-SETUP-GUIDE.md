# Guia de Configuração - Postman para RoleController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `RoleController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `RoleController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **RoleController Environment**

## 📋 Estrutura da Collection

```
📁 RoleController API - Business
├── 📁 Roles CRUD
│   ├── 📄 POST Create Role
│   ├── 📄 PUT Update Role
│   ├── 📄 PATCH Update Role Status
│   ├── 📄 GET All Roles
│   └── 📄 GET Active Roles
├── 📁 Public Endpoints
│   ├── 📄 GET Health Check
│   └── 📄 GET Debug Info
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   ├── 📄 POST Create Role - Success
    │   ├── 📄 PUT Update Role - Success
    │   ├── 📄 PATCH Update Status - Success
    │   ├── 📄 GET All Roles - Success
    │   └── 📄 GET Active Roles - Success
    └── 📁 Error Cases
        ├── 📄 POST Create Role - Invalid Name
        ├── 📄 POST Create Role - Duplicate Name
        ├── 📄 POST Create Role - Invalid Permissions
        ├── 📄 PUT Update Role - Not Found
        ├── 📄 PATCH Update Status - Not Found
        ├── 📄 GET All Roles - No Token
        ├── 📄 GET All Roles - Invalid Token
        └── 📄 GET All Roles - No Permission
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `jwt_token` | `` | Token JWT válido (preenchido pelo AuthController) |
| `role_id` | `550e8400-e29b-41d4-a716-446655440002` | ID de uma role existente para testes |
| `jwt_token_invalid` | `token_invalido` | Token JWT inválido para testes |
| `jwt_token_expired` | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_expirado` | Token JWT expirado para testes |
| `jwt_token_sem_permissao` | `` | Token JWT sem permissão para testes |

## 🧪 Como Executar os Testes

### Pré-requisito: Obter Token JWT

**IMPORTANTE**: Antes de testar o RoleController, você deve fazer login usando o AuthController:

1. **Fazer Login**:
   - Use a collection do `AuthController`
   - Execute `POST /api/business/auth/login`
   - O token será salvo automaticamente no environment

2. **Verificar Token**:
   - Confirme se `{{jwt_token}}` está preenchido no environment
   - O token deve ter as permissões: `role:create`, `role:update`, `role:read`

### Teste 1: Criar Role com Token Válido

1. Selecione **Roles CRUD > POST Create Role**
2. Clique em **Send**
3. Verifique:
   - Status Code: `201`
   - Response: Role criada com dados completos
   - ID da role salvo automaticamente em `{{role_id}}`

### Teste 2: Atualizar Role

1. Selecione **Roles CRUD > PUT Update Role**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Role atualizada com novos dados
   - `updatedAt` atualizado

### Teste 3: Alterar Status da Role

1. Selecione **Roles CRUD > PATCH Update Role Status**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Status alterado
   - Outros campos inalterados

### Teste 4: Listar Todas as Roles

1. Selecione **Roles CRUD > GET All Roles**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array de roles com dados completos
   - Inclui roles ativas e inativas

### Teste 5: Listar Roles Ativas

1. Selecione **Roles CRUD > GET Active Roles**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Array contendo apenas roles ativas
   - Exclui roles inativas

### Teste 6: Health Check (Público)

1. Selecione **Public Endpoints > GET Health Check**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: "RoleController está funcionando"

### Teste 7: Debug Info (Público)

1. Selecione **Public Endpoints > GET Debug Info**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: "RoleController - Debug: Roteamento funcionando corretamente"

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para POST Create Role:
- Status code é 201
- Response é um objeto JSON
- Role tem os campos obrigatórios (`id`, `name`, `description`, `active`, `createdAt`, `updatedAt`, `permissionNames`)
- ID é um UUID válido
- Role está ativa por padrão
- ID da role é salvo automaticamente para testes subsequentes

### Para PUT Update Role:
- Status code é 200
- Response é um objeto JSON
- Role tem dados atualizados
- `updatedAt` é mais recente que `createdAt`

### Para PATCH Update Role Status:
- Status code é 200
- Response é um objeto JSON
- Status da role foi atualizado
- Outros campos permanecem inalterados

### Para GET All Roles:
- Status code é 200
- Response é um array JSON
- Cada role tem os campos obrigatórios
- ID é um UUID válido para cada role

### Para GET Active Roles:
- Status code é 200
- Response é um array JSON
- Todas as roles retornadas estão ativas
- Contagem de roles ativas é menor ou igual ao total

### Para GET Health Check:
- Status code é 200
- Response é uma string
- Response contém a mensagem esperada

### Para GET Debug Info:
- Status code é 200
- Response é uma string
- Response contém a mensagem de debug esperada

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **RoleController API - Business**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 500ms (recomendado para autenticação)
   - **Data**: None
4. Clique em **Run RoleController API - Business**

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se a Business API está rodando na porta 8089
- Confirme se o context path está correto: `/api/business`

### Erro 401 (Unauthorized)
- **Token ausente**: Adicione header `Authorization: Bearer {{jwt_token}}`
- **Token inválido**: Faça login novamente com AuthController
- **Token expirado**: Faça login novamente (token expira em 24h)

### Erro 403 (Forbidden)
- **Sem permissão role:create**: Verifique se o usuário tem `role:create`
- **Sem permissão role:update**: Verifique se o usuário tem `role:update`
- **Sem permissão role:read**: Verifique se o usuário tem `role:read`
- **Token incorreto**: Use token de usuário com permissões adequadas

### Erro 400 (Bad Request)
- **Nome inválido**: Use apenas letras maiúsculas e underscore (ex: `MANAGER`)
- **Nome muito curto**: Mínimo 2 caracteres
- **Nome muito longo**: Máximo 50 caracteres
- **Descrição muito curta**: Mínimo 5 caracteres
- **Permissões vazias**: Pelo menos uma permissão deve ser especificada
- **Nome duplicado**: Role com mesmo nome já existe
- **Permissões inválidas**: IDs de permissões não encontrados
- **Role não encontrada**: ID da role não existe

### Erro 500 (Internal Server Error)
- Verifique os logs da aplicação
- Confirme se o banco de dados PostgreSQL está acessível
- Verifique se as permissões estão cadastradas no banco

### Token não está sendo salvo
- Execute primeiro o login com AuthController
- Verifique se o script de teste do AuthController está funcionando
- Confirme se o environment está selecionado

### Role ID não está sendo salvo
- Execute primeiro o POST Create Role
- Verifique se o script de teste está funcionando
- Confirme se o environment está selecionado

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Headers da requisição
- Status code da resposta
- Tempo de resposta
- Tamanho da resposta

Verifique o console do Postman para ver os logs detalhados.

## 🔄 Diferenças dos Controllers Anteriores

### Autenticação
- **RoleController**: Requer token JWT válido
- **AuthController**: Gera token JWT
- **PermissionController**: Requer token JWT válido

### Métodos HTTP
- **RoleController**: POST, PUT, PATCH, GET (CRUD completo)
- **AuthController**: POST (autenticação)
- **PermissionController**: GET (consulta)

### Autorização
- **RoleController**: PreAuthorize com `role:create`, `role:update`, `role:read`
- **AuthController**: Sem autorização (público)
- **PermissionController**: PreAuthorize com `permission:read`

### Response
- **RoleController**: Objeto de role ou lista de roles
- **AuthController**: Token JWT + dados do usuário
- **PermissionController**: Lista de permissões

### Validação
- **RoleController**: Bean Validation complexa (Pattern, Size, NotEmpty)
- **AuthController**: Bean Validation simples (NotBlank, Email)
- **PermissionController**: Sem validação de entrada

### Relacionamentos
- **RoleController**: Set<UUID> de permissões
- **AuthController**: Sem relacionamentos
- **PermissionController**: Sem relacionamentos

### Operações Específicas
- **RoleController**: PATCH para alterar apenas status
- **AuthController**: Sem operações específicas
- **PermissionController**: Sem operações específicas

## 💡 Dicas de Uso

### Para Testes de Sucesso
- **Primeiro**: Faça login com AuthController para obter token
- **Segundo**: Use o token salvo para testar RoleController
- **Verifique**: Se o usuário tem as permissões necessárias (`role:create`, `role:update`, `role:read`)

### Para Testes de Erro
- **Sem token**: Remova o header Authorization
- **Token inválido**: Use `{{jwt_token_invalid}}`
- **Token expirado**: Use `{{jwt_token_expired}}`
- **Sem permissão**: Use token de usuário sem as permissões necessárias

### Para Usar Token em Outras Requisições
- O token é salvo automaticamente pelo AuthController
- Use `{{jwt_token}}` em headers de outras requisições
- Exemplo: `Authorization: Bearer {{jwt_token}}`

### Para Usar Role ID em Outras Requisições
- O ID da role é salvo automaticamente pelo POST Create Role
- Use `{{role_id}}` em URLs de outras requisições
- Exemplo: `{{base_url}}/roles/{{role_id}}`

### Para Verificar Permissões
- Execute `GET All Roles` para ver todas as roles
- Execute `GET Active Roles` para ver apenas roles ativas
- Verifique se o usuário tem as permissões necessárias

### Para Testar Validações
- **Nome inválido**: Use `"manager"` (minúsculas)
- **Nome muito curto**: Use `"A"`
- **Nome muito longo**: Use `"VERY_LONG_ROLE_NAME_THAT_EXCEEDS_LIMIT"`
- **Descrição muito curta**: Use `"Desc"`
- **Permissões vazias**: Use `[]`
- **Nome duplicado**: Use `"ADMIN"` (já existe)
- **Permissões inválidas**: Use `["00000000-0000-0000-0000-000000000000"]`

### Para Testar Relacionamentos
- **Permissões válidas**: Use IDs de permissões existentes
- **Permissões inválidas**: Use IDs de permissões inexistentes
- **Múltiplas permissões**: Use array com vários IDs
- **Permissões vazias**: Use array vazio

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

### Validação
- **Bean Validation**: Anotações com regras específicas
- **Pattern**: Nome deve conter apenas letras maiúsculas e underscore
- **Size**: Limites de tamanho para campos
- **NotEmpty**: Pelo menos uma permissão deve ser especificada

## 🔄 Fluxo Completo de Teste

### 1. Preparação
1. **Importar collections**: AuthController + RoleController
2. **Configurar environment**: Selecionar environment correto
3. **Verificar API**: Confirmar que Business API está rodando

### 2. Autenticação
1. **Fazer login**: `POST /api/business/auth/login`
2. **Verificar token**: Confirmar que `{{jwt_token}}` foi salvo
3. **Verificar permissões**: Confirmar que usuário tem `role:create`, `role:update`, `role:read`

### 3. Teste de Roles
1. **Criar role**: `POST /api/business/roles/create`
2. **Atualizar role**: `PUT /api/business/roles/{id}`
3. **Alterar status**: `PATCH /api/business/roles/{id}/status`
4. **Listar todas**: `GET /api/business/roles`
5. **Listar ativas**: `GET /api/business/roles/active`

### 4. Validação
1. **Verificar responses**: Status codes e dados corretos
2. **Verificar logs**: Console do Postman e logs da aplicação
3. **Testar autorização**: Diferentes níveis de permissão
4. **Testar validações**: Diferentes tipos de dados inválidos

### 5. Teste de Endpoints Públicos
1. **Health check**: `GET /api/business/roles/health`
2. **Debug info**: `GET /api/business/roles/debug`

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
