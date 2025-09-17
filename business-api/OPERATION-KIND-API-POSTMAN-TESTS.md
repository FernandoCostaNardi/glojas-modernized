# Testes da API OperationKind - Postman

## 📋 **Visão Geral**
Este documento contém os testes para a API de tipos de operação (OperationKind) que consome dados da Legacy API.

## 🔧 **Configuração do Postman**

### **Variáveis de Ambiente**
Configure as seguintes variáveis no Postman:

```
baseUrl: http://localhost:8082
legacyUrl: http://localhost:8081
token: [seu_jwt_token_aqui]
```

### **Headers Padrão**
Para todas as requisições autenticadas, adicione:
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

## 🧪 **Testes da API**

### **1. Health Check - OperationKind Controller**

**Método:** `GET`  
**URL:** `{{baseUrl}}/api/business/operation-kinds/health`  
**Headers:** Nenhum necessário  
**Body:** Nenhum  

**Resposta Esperada:**
```json
"OperationKindController está funcionando"
```

**Status Code:** `200 OK`

---

### **2. Teste de Conexão com Legacy API**

**Método:** `GET`  
**URL:** `{{baseUrl}}/api/business/operation-kinds/test-connection`  
**Headers:** Nenhum necessário  
**Body:** Nenhum  

**Resposta Esperada (Sucesso):**
```json
{
  "status": "SUCCESS",
  "message": "Conexão com Legacy API estabelecida com sucesso",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Resposta Esperada (Erro):**
```json
{
  "status": "ERROR",
  "message": "Erro ao testar conectividade: [detalhes do erro]",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Status Code:** `200 OK` (sucesso) ou `503 Service Unavailable` (erro)

---

### **3. Buscar Todos os Tipos de Operação**

**Método:** `GET`  
**URL:** `{{baseUrl}}/api/business/operation-kinds`  
**Headers:** 
```
Authorization: Bearer {{token}}
```

**Body:** Nenhum  

**Resposta Esperada:**
```json
[
  {
    "id": 1,
    "description": "Venda"
  },
  {
    "id": 2,
    "description": "Devolução"
  },
  {
    "id": 3,
    "description": "Troca"
  },
  {
    "id": 4,
    "description": "Cancelamento"
  }
]
```

**Status Code:** `200 OK`

**Cenários de Teste:**
- ✅ **Sucesso:** Retorna lista de tipos de operação
- ❌ **Sem Token:** Retorna `401 Unauthorized`
- ❌ **Token Inválido:** Retorna `401 Unauthorized`
- ❌ **Sem Permissão:** Retorna `403 Forbidden`
- ❌ **Legacy API Indisponível:** Retorna `500 Internal Server Error`

---

## 🔍 **Teste Direto da Legacy API**

### **4. Teste Direto - Legacy API Operations**

**Método:** `GET`  
**URL:** `{{legacyUrl}}/api/legacy/operations`  
**Headers:** Nenhum necessário  
**Body:** Nenhum  

**Resposta Esperada:**
```json
[
  {
    "id": 1,
    "description": "Venda"
  },
  {
    "id": 2,
    "description": "Devolução"
  },
  {
    "id": 3,
    "description": "Troca"
  },
  {
    "id": 4,
    "description": "Cancelamento"
  }
]
```

**Status Code:** `200 OK`

---

## 📊 **Coleção Completa do Postman**

### **Importar Coleção**
```json
{
  "info": {
    "name": "OperationKind API Tests",
    "description": "Testes para a API de tipos de operação",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8082"
    },
    {
      "key": "legacyUrl", 
      "value": "http://localhost:8081"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/business/operation-kinds/health",
          "host": ["{{baseUrl}}"],
          "path": ["api", "business", "operation-kinds", "health"]
        }
      }
    },
    {
      "name": "Test Connection",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/operation-kinds/test-connection",
          "host": ["{{baseUrl}}"],
          "path": ["operation-kinds", "test-connection"]
        }
      }
    },
    {
      "name": "Get All Operation Kinds",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/operation-kinds",
          "host": ["{{baseUrl}}"],
          "path": ["operation-kinds"]
        }
      }
    },
    {
      "name": "Legacy API Direct Test",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{legacyUrl}}/api/legacy/operations",
          "host": ["{{legacyUrl}}"],
          "path": ["api", "legacy", "operations"]
        }
      }
    }
  ]
}
```

---

## 🚀 **Como Executar os Testes**

### **1. Preparação**
1. Certifique-se de que a Business API está rodando em `http://localhost:8082`
2. Certifique-se de que a Legacy API está rodando em `http://localhost:8081`
3. Faça login na Business API para obter o token JWT

### **2. Obter Token de Autenticação**
```bash
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### **3. Executar Testes em Sequência**
1. **Health Check** - Verificar se o controller está funcionando
2. **Test Connection** - Verificar conectividade com Legacy API
3. **Legacy API Direct Test** - Testar diretamente a Legacy API
4. **Get All Operation Kinds** - Testar o endpoint principal

### **4. Validações**
- ✅ Verificar se todas as respostas têm o status code correto
- ✅ Verificar se o JSON está bem formado
- ✅ Verificar se os dados retornados são consistentes
- ✅ Verificar logs da aplicação para erros

---

## 🐛 **Troubleshooting**

### **Erro 401 Unauthorized**
- Verificar se o token JWT está válido
- Verificar se o header Authorization está correto
- Fazer novo login se necessário

### **Erro 403 Forbidden**
- Verificar se o usuário tem a permissão `operation:read`
- Verificar se o usuário está ativo

### **Erro 500 Internal Server Error**
- Verificar se a Legacy API está rodando
- Verificar logs da Business API
- Testar conectividade com a Legacy API

### **Erro 503 Service Unavailable**
- Verificar se a Legacy API está acessível
- Verificar configuração de rede
- Verificar se o endpoint `/api/legacy/operations` existe na Legacy API

---

## 📝 **Notas Importantes**

1. **Autenticação:** O endpoint principal requer autenticação JWT
2. **Permissões:** É necessário ter a permissão `operation:read`
3. **CORS:** A API está configurada para aceitar requisições do frontend em `http://localhost:3000`
4. **Timeout:** A API tem timeout de 30 segundos para chamadas à Legacy API
5. **Logs:** Todos os requests são logados com informações detalhadas

---

**Última Atualização:** 15/01/2024  
**Versão:** 1.0  
**Responsável:** Equipe de Desenvolvimento
