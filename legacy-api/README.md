# Legacy API - Sistema Glojas

## 📋 Descrição
API responsável por expor dados do SQL Server existente como endpoints REST.
Esta API opera em **modo read-only** e serve como ponte entre o banco legado e a nova arquitetura.

## ⚙️ Configuração

### Pré-requisitos
- Java 17+
- SQL Server com dados existentes
- Maven 3.6+

### Configuração do Banco
1. Edite `src/main/resources/application.yml`
2. Configure as credenciais do SQL Server:
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=SEU_BANCO
    username: SEU_USUARIO
    password: SUA_SENHA
```

## 🚀 Como Executar

### Via Maven
```bash
cd legacy-api
mvn clean compile
mvn spring-boot:run
```

### Via JAR
```bash
mvn clean package
java -jar target/legacy-api-1.0.0.jar
```

## 🌐 Endpoints

### Health Check
- **GET** `/` - Informações básicas da API
- **GET** `/status` - Status detalhado + conexão com banco
- **GET** `/info` - Informações dos endpoints disponíveis
- **GET** `/actuator/health` - Spring Actuator health check

### Futuros Endpoints (próximas tasks)
- **GET** `/produtos` - Lista produtos
- **GET** `/produtos/{id}` - Busca produto específico
- **GET** `/produtos/search` - Filtrar produtos

## 📊 Monitoramento
- **Health Check**: http://localhost:8082/api/legacy/actuator/health
- **Info**: http://localhost:8082/api/legacy/actuator/info
- **Metrics**: http://localhost:8082/api/legacy/actuator/metrics

## 🔒 Características de Segurança
- **Read-only**: Todas as transações são somente leitura
- **Pool limitado**: Máximo 5 conexões simultâneas
- **Timeout configurado**: 30s para conexões
- **DDL desabilitado**: Não pode alterar estrutura do banco

## 📁 Estrutura do Projeto
```
legacy-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── sysconard/
│       │           └── legacy/
│       │               ├── LegacyApiApplication.java
│       │               ├── controller/
│       │               │   └── HealthController.java
│       │               ├── entity/
│       │               └── repository/
│       └── resources/
│           └── application.yml
├── pom.xml
└── README.md
```

## 📝 Próximos Passos (Tasks)
- [ ] LEGACY-002: Configurar conexão SQL Server específica
- [ ] LEGACY-003: Criar Entity Produto
- [ ] LEGACY-004: Criar Repository Produto
- [ ] LEGACY-005: Implementar Controllers de Produtos

## 🎯 Critério de Aceite - LEGACY-001
✅ Projeto Spring Boot criado  
✅ pom.xml configurado com dependências  
✅ application.yml configurado para porta 8082  
✅ Classe principal criada  
✅ Health controller básico  
✅ Estrutura de packages organizada  

**Status**: CONCLUÍDO ✅

## 🛠️ Tecnologias Utilizadas
- **Spring Boot 3.2.0**
- **Java 17**
- **Maven**
- **SQL Server JDBC Driver**
- **Spring Data JPA**
- **Spring Boot Actuator**
- **Lombok**

## 👥 Equipe
Desenvolvido pela equipe de modernização do Sistema Glojas
