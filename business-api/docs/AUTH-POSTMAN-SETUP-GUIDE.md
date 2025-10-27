# Guia de Configuração - Postman para AuthController

## 🚀 Configuração Rápida

### 1. Importar Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo: `AuthController-Postman-Collection.json`
4. Clique em **Import**

### 2. Importar Environment

1. No Postman, clique em **Environments**
2. Clique em **Import**
3. Selecione o arquivo: `AuthController-Postman-Environment.json`
4. Clique em **Import**

### 3. Selecionar Environment

1. No canto superior direito, clique no dropdown de environments
2. Selecione **AuthController Environment**

## 📋 Estrutura da Collection

```
📁 AuthController API - Business
├── 📁 Authentication
│   └── 📄 POST Login
└── 📁 Test Scenarios
    ├── 📁 Success Cases
    │   └── 📄 POST Login - Success
    └── 📁 Error Cases
        ├── 📄 POST Login - Missing Email
        ├── 📄 POST Login - Invalid Email
        ├── 📄 POST Login - Missing Password
        ├── 📄 POST Login - Invalid Credentials
        └── 📄 POST Login - User Not Found
```

## 🔧 Variáveis de Environment

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `http://localhost:8089/api/business` | URL base da API |
| `valid_email` | `admin@glojas.com` | Email válido para testes |
| `valid_password` | `senha123` | Senha válida para testes |
| `invalid_email` | `email-invalido` | Email inválido para testes |
| `invalid_password` | `senha_incorreta` | Senha incorreta para testes |
| `nonexistent_email` | `usuario@inexistente.com` | Email de usuário inexistente |
| `jwt_token` | `` | Token JWT (preenchido automaticamente) |
| `user_roles` | `` | Roles do usuário (preenchido automaticamente) |
| `user_permissions` | `` | Permissões do usuário (preenchido automaticamente) |

## 🧪 Como Executar os Testes

### Teste 1: Login com Credenciais Válidas

1. Selecione **Authentication > POST Login**
2. Clique em **Send**
3. Verifique:
   - Status Code: `200`
   - Response: Token JWT, username, name, roles, permissions
   - Token salvo automaticamente no environment

### Teste 2: Cenários de Erro

#### Email Ausente
1. Selecione **Test Scenarios > Error Cases > POST Login - Missing Email**
2. Clique em **Send**
3. Verifique: Status Code `400`, Erro de validação

#### Email Inválido
1. Selecione **Test Scenarios > Error Cases > POST Login - Invalid Email**
2. Clique em **Send**
3. Verifique: Status Code `400`, Erro de formato

#### Senha Ausente
1. Selecione **Test Scenarios > Error Cases > POST Login - Missing Password**
2. Clique em **Send**
3. Verifique: Status Code `400`, Erro de validação

#### Credenciais Inválidas
1. Selecione **Test Scenarios > Error Cases > POST Login - Invalid Credentials**
2. Clique em **Send**
3. Verifique: Status Code `400`, Erro de autenticação

#### Usuário Não Encontrado
1. Selecione **Test Scenarios > Error Cases > POST Login - User Not Found**
2. Clique em **Send**
3. Verifique: Status Code `400`, Erro de autenticação

## 🔍 Validações Automáticas

A collection inclui scripts de teste que validam automaticamente:

### Para POST Login:
- Status code é 200
- Response é um objeto JSON
- Response tem os campos obrigatórios (`token`, `username`, `name`, `roles`, `permissions`)
- Token não está vazio
- Roles é um array
- Permissions é um array
- **Token salvo automaticamente** no environment para uso em outras requisições

## 📊 Executar Collection Completa

1. Clique com botão direito na collection **AuthController API - Business**
2. Selecione **Run collection**
3. Configure:
   - **Iterations**: 1
   - **Delay**: 500ms (recomendado para autenticação)
   - **Data**: None
4. Clique em **Run AuthController API - Business**

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se a Business API está rodando na porta 8089
- Confirme se o context path está correto: `/api/business`

### Erro 400 (Bad Request)
- Verifique se todos os campos obrigatórios estão presentes
- Confirme se o formato do email está correto
- Verifique se as credenciais estão corretas

### Erro 500 (Internal Server Error)
- Verifique os logs da aplicação
- Confirme se o banco de dados PostgreSQL está acessível
- Verifique se o JWT secret está configurado

### Erro de Validação
- **Email ausente**: Adicione o campo `email`
- **Email inválido**: Use formato válido (ex: `admin@glojas.com`)
- **Senha ausente**: Adicione o campo `password`
- **Credenciais incorretas**: Verifique email e senha

## 📝 Logs de Teste

Os scripts de teste incluem logs automáticos:
- URL da requisição
- Body da requisição
- Status code da resposta
- Tempo de resposta
- Tamanho da resposta

Verifique o console do Postman para ver os logs detalhados.

## 🔄 Diferenças da Legacy API

### Porta e Context Path
- **Business API**: Porta 8089, Context `/api/business`
- **Legacy API**: Porta 8087, Context `/api/legacy`

### Autenticação
- **Business API**: JWT Token com roles e permissions
- **Legacy API**: Read-only, sem autenticação

### Validação
- **Business API**: Jakarta Bean Validation (Java 17)
- **Legacy API**: javax Bean Validation (Java 8)

### DTOs
- **Business API**: Records (Java 17)
- **Legacy API**: Lombok classes (Java 8)

### Database
- **Business API**: PostgreSQL
- **Legacy API**: SQL Server

## 💡 Dicas de Uso

### Para Testes de Sucesso
- Use credenciais válidas configuradas no environment
- Verifique se o usuário existe no banco de dados
- Confirme se as roles e permissions estão configuradas

### Para Testes de Erro
- Remova campos obrigatórios para testar validação
- Use formato de email inválido
- Use credenciais incorretas

### Para Usar Token em Outras Requisições
- O token é salvo automaticamente no environment
- Use `{{jwt_token}}` em headers de outras requisições
- Exemplo: `Authorization: Bearer {{jwt_token}}`

### Para Verificar Roles e Permissions
- As roles são salvas em `{{user_roles}}`
- As permissions são salvas em `{{user_permissions}}`
- Use em testes condicionais baseados em permissões

## 🔐 Segurança

### Token JWT
- **Expiração**: 24 horas (configurável)
- **Algoritmo**: HS256
- **Secret**: Configurado no application.yml

### CORS
- **Origem permitida**: `http://localhost:3000`
- **Métodos**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: Todos permitidos

### Validação
- **Email**: Formato RFC 5322
- **Password**: Não vazio (validação básica)
- **BCrypt**: Senhas são criptografadas automaticamente

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0
