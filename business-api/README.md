# 🚀 Business API - Spring Boot Java 17

API principal com lógica de negócio do Sistema Glojas.

## 🛠️ Pré-requisitos

- Java 17+
- Maven 3.6+

## 🚀 Como Executar

### 1. Compilar e Executar
```bash
cd business-api
mvn clean spring-boot:run
```

### 2. Verificar se está funcionando

**Hello World Endpoint:**
```
GET http://localhost:8081/api/business/hello
```
Resposta: `Hello World from Business API! 🚀`

**Health Check:**
```
GET http://localhost:8081/api/business/health  
```
Resposta: `Business API is running! ✅`

**Actuator Health:**
```
GET http://localhost:8081/api/business/actuator/health
```

## 📊 Status

- ✅ **Estrutura criada**
- ✅ **Endpoints básicos funcionando**
- 🚧 **Em desenvolvimento**

## 🔧 Configuração

- **Porta**: 8081
- **Context Path**: `/api/business`
- **Java**: 17
- **Spring Boot**: 3.3.11