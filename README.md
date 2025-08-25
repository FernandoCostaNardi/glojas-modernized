# 🏪 Projeto Glojas - Sistema Modernizado

## 📋 Visão Geral

Este é um **monorepo** que contém todos os componentes do sistema Glojas modernizado, incluindo APIs de backend e frontend.

## 🏗️ Arquitetura do Projeto

```
glojas-modernized/
├── 🔧 scripts/           # Scripts de automação e setup
├── 🏛️ legacy-api/        # API Java 8 - Integração com SQL Server legado
├── 🚀 business-api/      # API principal - Lógica de negócio (PostgreSQL)
└── 🎨 frontend/          # Interface do usuário (React/Vue/Angular)
```

## 🚀 Serviços

### 🏛️ Legacy API
- **Tecnologia**: Java 8 + Spring Boot 2.7.18
- **Banco**: SQL Server (Read-Only)
- **Porta**: 8082
- **Propósito**: Integração com sistema legado

### 🚀 Business API  
- **Tecnologia**: Java 17 + Spring Boot 3.x
- **Banco**: PostgreSQL
- **Porta**: 8080
- **Propósito**: Lógica de negócio principal

### 🎨 Frontend
- **Tecnologia**: [A definir - React/Vue/Angular]
- **Porta**: 3000
- **Propósito**: Interface do usuário

## 🛠️ Setup Rápido

### Pré-requisitos
- Java 8 (para Legacy API)
- Java 17 (para Business API)
- PostgreSQL 15
- Redis
- Node.js 18+ (para Frontend)

### 🚀 Inicialização Completa
```bash
# 1. Executar setup completo
scripts/setup-completo.bat

# 2. Iniciar todos os serviços
scripts/start-all.bat

# 3. Verificar status
scripts/status-sucesso.bat
```

### 🔧 Inicialização Individual

#### Legacy API (Java 8)
```bash
cd legacy-api
scripts/start-legacy-java8.bat
```

#### Business API (Java 17)
```bash
cd business-api
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm start
```

## 📊 Status dos Serviços

| Serviço | Status | URL | Documentação |
|---------|--------|-----|--------------|
| Legacy API | ✅ Funcionando | http://localhost:8082/api/legacy | [README](legacy-api/README.md) |
| Business API | 🚧 Em desenvolvimento | http://localhost:8080/api | [README](business-api/README.md) |
| Frontend | 🚧 Em desenvolvimento | http://localhost:3000 | [README](frontend/README.md) |

## 🔄 Endpoints Principais

### Legacy API
- **Health Check**: `GET /api/legacy/actuator/health`
- **Teste Conexão**: `GET /api/legacy/products/test-connection`
- **Produtos**: `GET /api/legacy/products/registered`

### Business API
- **Health Check**: `GET /api/health`
- **Swagger**: `GET /api/swagger-ui.html`

## 🗄️ Bancos de Dados

### SQL Server (Legacy)
- **Host**: 45.174.189.210:1433
- **Database**: SysacME
- **Modo**: Read-Only
- **Uso**: Consulta dados legados

### PostgreSQL (Principal)
- **Host**: localhost:5432
- **Database**: glojas_business
- **User**: glojas_user
- **Uso**: Operações principais

## 🛠️ Scripts Utilitários

| Script | Descrição |
|--------|-----------|
| `setup-completo.bat` | Setup completo do ambiente |
| `start-all.bat` | Inicia todos os serviços |
| `stop-all.bat` | Para todos os serviços |
| `teste-final.bat` | Testa todos os endpoints |
| `diagnostico-pg.bat` | Diagnóstico PostgreSQL |

## 🔧 Desenvolvimento

### Estrutura de Commits
```
tipo(escopo): descrição

Tipos:
- feat: nova funcionalidade
- fix: correção de bug
- docs: documentação
- style: formatação
- refactor: refatoração
- test: testes
- chore: manutenção

Escopos:
- legacy-api: mudanças na API legada
- business-api: mudanças na API principal
- frontend: mudanças no frontend
- scripts: mudanças nos scripts
- docs: documentação geral
```

### Exemplo de Commits
```bash
feat(legacy-api): adicionar endpoint de produtos
fix(legacy-api): corrigir erro ClassCastException
docs(readme): atualizar documentação de setup
chore(scripts): melhorar script de inicialização
```

## 🐛 Resolução de Problemas

### Legacy API - Erro TLS
```bash
# Verificar Java 8
java -version

# Se necessário, configurar
scripts/set-java8.bat
```

### PostgreSQL não conecta
```bash
# Diagnosticar
scripts/diagnostico-pg.bat

# Reconfigurar
scripts/setup-postgres.bat
```

## 🤝 Contribuição

1. Faça um branch: `git checkout -b feature/nova-funcionalidade`
2. Commit suas mudanças: `git commit -m 'feat(escopo): descrição'`
3. Push para o branch: `git push origin feature/nova-funcionalidade`
4. Abra um Pull Request

## 📝 Licença

[Definir licença do projeto]

## 📞 Contato

- **Equipe**: Sysconard
- **Projeto**: Sistema Glojas Modernizado
- **Versão**: 1.0.0
