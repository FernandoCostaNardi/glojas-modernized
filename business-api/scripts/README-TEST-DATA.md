# 📋 Dados de Teste - Business API

Este documento descreve os dados de teste criados para o desenvolvimento e testes da Business API.

## 🎯 Objetivo

Fornecer um conjunto completo de dados de teste para:
- Desenvolvimento da aplicação
- Testes unitários e de integração
- Demonstração das funcionalidades
- Validação de permissões e roles

## 📁 Arquivos Criados

### 1. Scripts de Dados
- **`populate-test-data.sql`**: Script SQL para execução manual

### 2. Scripts de Execução
- **`run-populate-test-data.bat`**: Script batch para Windows
- **`run-populate-test-data.ps1`**: Script PowerShell para Windows

## 🗄️ Estrutura dos Dados

### Roles Criadas
| Role | Descrição | Permissões |
|------|-----------|------------|
| `ADMIN` | Administrador do sistema | Todas as permissões |
| `USER` | Usuário padrão | `user:read`, `user:change-password` |
| `MANAGER` | Gerente | `user:create`, `user:read`, `user:update`, `user:change-password`, `role:read` |
| `TESTER` | Usuário para testes | `test:execute`, `test:create`, `test:read`, `user:read` |
| `DEVELOPER` | Desenvolvedor | `dev:code`, `dev:deploy`, `dev:review`, `user:read`, `user:update`, `test:execute` |
| `ANALYST` | Analista de negócio | `analysis:create`, `analysis:read`, `analysis:update`, `user:read`, `role:read` |

### Permissões Adicionais
| Permissão | Recurso | Ação | Descrição |
|-----------|---------|------|-----------|
| `test:execute` | test | execute | Executar testes |
| `test:create` | test | create | Criar testes |
| `test:read` | test | read | Ler testes |
| `dev:code` | development | code | Escrever código |
| `dev:deploy` | development | deploy | Fazer deploy |
| `dev:review` | development | review | Revisar código |
| `analysis:create` | analysis | create | Criar análises |
| `analysis:read` | analysis | read | Ler análises |
| `analysis:update` | analysis | update | Atualizar análises |

## 👥 Usuários de Teste

| Username | Nome | Email | Role(s) | Senha |
|----------|------|-------|---------|-------|
| `admin` | Administrador | admin@exemplo.com | ADMIN | admin123 |
| `joao.silva` | João Silva | joao.silva@exemplo.com | TESTER | admin123 |
| `maria.santos` | Maria Santos | maria.santos@exemplo.com | DEVELOPER | admin123 |
| `pedro.oliveira` | Pedro Oliveira | pedro.oliveira@exemplo.com | ANALYST | admin123 |
| `ana.costa` | Ana Costa | ana.costa@exemplo.com | MANAGER | admin123 |
| `carlos.ferreira` | Carlos Ferreira | carlos.ferreira@exemplo.com | USER | admin123 |
| `lucia.rodriguez` | Lucía Rodriguez | lucia.rodriguez@exemplo.com | DEVELOPER, TESTER | admin123 |

## 🔑 Tokens de Reset de Senha

| Token | Usuário | Expiração | Status |
|-------|---------|-----------|--------|
| `test-token-joao-silva-12345` | joao.silva | 1 hora | Ativo |
| `test-token-maria-santos-67890` | maria.santos | 30 minutos | Ativo |

## 🚀 Como Executar

### Opção 1: Script Manual (Recomendado)
1. Navegue para o diretório `scripts/`
2. Execute um dos scripts:
   ```bash
   # Windows Batch
   run-populate-test-data.bat
   
   # Windows PowerShell
   .\run-populate-test-data.ps1
   
   # Linux/Mac
   psql -U glojas_user -d glojas_business -f populate-test-data.sql
   ```

### Opção 2: Execução Manual
```bash
psql -U glojas_user -d glojas_business -f populate-test-data.sql
```

## ✅ Verificação

Após a execução, você pode verificar os dados com:

```sql
-- Contar usuários
SELECT COUNT(*) as total_usuarios FROM users;

-- Contar roles
SELECT COUNT(*) as total_roles FROM roles;

-- Contar permissões
SELECT COUNT(*) as total_permissoes FROM permissions;

-- Listar usuários com roles
SELECT 
    u.username,
    u.name,
    u.email,
    STRING_AGG(r.name, ', ') as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.name, u.email
ORDER BY u.username;
```

## 🧪 Casos de Teste

### Teste de Login
```bash
# Login como admin
curl -X POST http://localhost:8082/api/business/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Login como desenvolvedor
curl -X POST http://localhost:8082/api/business/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"maria.santos","password":"admin123"}'
```

### Teste de Permissões
```bash
# Criar usuário (requer ADMIN)
curl -X POST http://localhost:8082/api/business/users/create \
  -H "Authorization: Bearer {TOKEN_ADMIN}" \
  -H "Content-Type: application/json" \
  -d '{"name":"Novo Usuário","username":"novo.user","email":"novo@exemplo.com","password":"senha123","roles":["USER"]}'

# Buscar usuário (requer ADMIN ou próprio usuário)
curl -X GET http://localhost:8082/api/business/users/{USER_ID} \
  -H "Authorization: Bearer {TOKEN}"
```

## 🔧 Configurações

### Banco de Dados
- **Database**: `glojas_business`
- **Usuário**: `glojas_user`
- **Senha**: `F1e0r8n0#1`
- **Host**: `localhost`
- **Porta**: `5432`

### Aplicação
- **URL Base**: `http://localhost:8082/api/business`
- **Context Path**: `/api/business`

## ⚠️ Observações Importantes

1. **Senha Padrão**: Todos os usuários usam a senha `admin123`
2. **UUID**: Todos os IDs são UUIDs gerados automaticamente
3. **Conflitos**: Os scripts usam `ON CONFLICT DO NOTHING` para evitar duplicatas
4. **Dependências**: Execute a aplicação para criar as tabelas antes dos dados de teste
5. **Ambiente**: Use apenas em ambiente de desenvolvimento/teste

## 🆘 Solução de Problemas

### Erro: "Database não existe"
```sql
CREATE DATABASE glojas_business;
```

### Erro: "Usuário não existe"
```sql
CREATE USER glojas_user WITH PASSWORD 'F1e0r8n0#1';
GRANT ALL PRIVILEGES ON DATABASE glojas_business TO glojas_user;
```

### Erro: "Tabelas não existem"
1. Execute a aplicação para criar as tabelas via Hibernate
2. Execute o script de dados de teste

### Erro: "PostgreSQL não está rodando"
```bash
# Windows
net start postgresql-x64-15

# Linux
sudo systemctl start postgresql

# Mac
brew services start postgresql
```

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs da aplicação
2. Consulte a documentação do Spring Boot
3. Verifique a conectividade com o PostgreSQL
4. Confirme se as tabelas foram criadas corretamente pelo Hibernate
