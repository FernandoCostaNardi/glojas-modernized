# 🤝 Guia de Contribuição - Projeto Glojas

## 🎯 Estratégia de Branching

### Branches Principais
- **`main`**: Código em produção
- **`develop`**: Código em desenvolvimento
- **`staging`**: Código para testes

### Branches de Feature
```bash
feature/[serviço]-[funcionalidade]
```

**Exemplos:**
- `feature/legacy-api-produtos`
- `feature/business-api-auth`
- `feature/frontend-dashboard`
- `feature/scripts-deploy`

### Branches de Correção
```bash
fix/[serviço]-[problema]
hotfix/[problema-crítico]
```

## 📝 Convenção de Commits

### Formato
```
tipo(escopo): descrição curta

[corpo opcional]

[rodapé opcional]
```

### Tipos de Commit
| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| `feat` | Nova funcionalidade | `feat(legacy-api): adicionar endpoint de produtos` |
| `fix` | Correção de bug | `fix(legacy-api): corrigir erro ClassCastException` |
| `docs` | Documentação | `docs(readme): atualizar guia de instalação` |
| `style` | Formatação | `style(legacy-api): formatar código Java` |
| `refactor` | Refatoração | `refactor(business-api): otimizar queries SQL` |
| `test` | Testes | `test(legacy-api): adicionar testes unitários` |
| `chore` | Manutenção | `chore(scripts): atualizar script de deploy` |
| `perf` | Performance | `perf(frontend): otimizar carregamento` |
| `ci` | CI/CD | `ci: configurar GitHub Actions` |

### Escopos
- `legacy-api`: API Java 8 + SQL Server
- `business-api`: API Java 17 + PostgreSQL  
- `frontend`: Interface do usuário
- `scripts`: Scripts de automação
- `docs`: Documentação geral
- `config`: Configurações gerais

## 🔄 Workflow de Desenvolvimento

### 1. Criar Nova Feature
```bash
# 1. Atualizar develop
git checkout develop
git pull origin develop

# 2. Criar branch de feature
git checkout -b feature/legacy-api-nova-funcionalidade

# 3. Desenvolver
# ... fazer suas alterações ...

# 4. Commits organizados
git add .
git commit -m "feat(legacy-api): implementar nova funcionalidade"

# 5. Push e Pull Request
git push origin feature/legacy-api-nova-funcionalidade
```

### 2. Correção de Bug
```bash
# 1. Branch de fix
git checkout -b fix/legacy-api-erro-especifico

# 2. Corrigir e testar
# ... fazer correções ...

# 3. Commit descritivo
git commit -m "fix(legacy-api): corrigir erro específico

- Problema: erro XYZ acontecia quando...
- Solução: implementada validação...
- Teste: verificado com cenário..."
```

### 3. Hotfix Crítico
```bash
# 1. Branch direto da main
git checkout main
git checkout -b hotfix/erro-critico-producao

# 2. Correção urgente
# ... fazer correção ...

# 3. Commit e merge urgente
git commit -m "hotfix: corrigir erro crítico em produção"
git checkout main
git merge hotfix/erro-critico-producao
git tag v1.0.1
```

## 🧪 Testes Antes de Commit

### Legacy API
```bash
cd legacy-api
mvn clean test
mvn spring-boot:run # Testar endpoints
```

### Business API
```bash
cd business-api
mvn clean test
mvn spring-boot:run
```

### Scripts
```bash
scripts/teste-final.bat
scripts/status-sucesso.bat
```

## 📋 Checklist de Pull Request

### ✅ Antes de Abrir PR
- [ ] Código testado localmente
- [ ] Commits seguem convenção
- [ ] Documentação atualizada
- [ ] Testes passando
- [ ] Scripts funcionando
- [ ] Sem arquivos temporários

### ✅ Descrição do PR
```markdown
## 🎯 Objetivo
Breve descrição do que foi implementado

## 🔧 Mudanças
- Lista das principais alterações
- Arquivos modificados
- Novas funcionalidades

## 🧪 Testes
- Como testar as mudanças
- Cenários testados
- URLs de endpoints (se aplicável)

## 📸 Screenshots
(Se aplicável)

## ⚠️ Breaking Changes
(Se houver mudanças que quebram compatibilidade)
```

## 🚀 Deploy e Versionamento

### Versionamento Semântico
```
MAJOR.MINOR.PATCH

MAJOR: Mudanças incompatíveis
MINOR: Novas funcionalidades compatíveis  
PATCH: Correções de bugs
```

### Tags de Release
```bash
# Release menor
git tag v1.1.0
git push origin v1.1.0

# Release maior
git tag v2.0.0
git push origin v2.0.0
```

## 🛠️ Configuração do Ambiente

### 1. Clone do Projeto
```bash
git clone [url-do-repositorio]
cd glojas-modernized
```

### 2. Setup Inicial
```bash
# Windows
scripts/setup-completo.bat

# Ou manual
scripts/setup-postgres.bat
scripts/setup-database.sql
scripts/start-redis.bat
```

### 3. Verificação
```bash
scripts/teste-final.bat
scripts/status-sucesso.bat
```

## 🚨 Resolução de Conflitos

### Merge Conflicts
```bash
# 1. Atualizar branch principal
git checkout develop
git pull origin develop

# 2. Voltar para sua branch
git checkout feature/sua-branch

# 3. Rebase interativo
git rebase develop

# 4. Resolver conflitos manualmente
# Editar arquivos conflitantes

# 5. Continuar rebase
git add .
git rebase --continue
```

## 📞 Contato e Suporte

- **Issues**: Use GitHub Issues para bugs e features
- **Discussões**: Use GitHub Discussions para perguntas
- **Urgente**: [definir canal de comunicação]

## 📚 Recursos Úteis

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Flow](https://github.com/nvie/gitflow)
- [Semantic Versioning](https://semver.org/)
