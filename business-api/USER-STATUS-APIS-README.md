# APIs de Alteração de Status de Usuário

## 📋 **Resumo das Novas Funcionalidades**

Foram implementadas **duas novas APIs PATCH** para alterar o status dos usuários:

1. **`PATCH /users/{userId}/status`** - Altera status ativo/inativo
2. **`PATCH /users/{userId}/lock`** - Altera status de bloqueio

## 🔄 **Endpoints Implementados**

### **1. Alterar Status Ativo/Inativo**
```http
PATCH /users/{userId}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "isActive": false,
  "comment": "Usuário inativo temporariamente"
}
```

**Resposta:**
```json
{
  "userId": "uuid-123",
  "username": "john.doe",
  "name": "John Doe",
  "previousStatus": true,
  "newStatus": false,
  "comment": "Usuário inativo temporariamente",
  "updatedAt": "2025-08-28T10:30:00",
  "message": "Status alterado com sucesso de ativo para inativo"
}
```

### **2. Alterar Status de Bloqueio**
```http
PATCH /users/{userId}/lock
Authorization: Bearer {token}
Content-Type: application/json

{
  "isNotLocked": false,
  "comment": "Usuário bloqueado por violação"
}
```

**Resposta:**
```json
{
  "userId": "uuid-123",
  "username": "john.doe",
  "name": "John Doe",
  "previousLockStatus": true,
  "newLockStatus": false,
  "comment": "Usuário bloqueado por violação",
  "updatedAt": "2025-08-28T10:30:00",
  "message": "Status de bloqueio alterado com sucesso de não bloqueado para bloqueado"
}
```

## 🏗️ **Arquitetura Implementada**

### **1. DTOs de Requisição**
- **`UpdateUserStatusRequest`**: Para alterar status ativo/inativo
- **`UpdateUserLockRequest`**: Para alterar status de bloqueio

### **2. DTOs de Resposta**
- **`UpdateUserStatusResponse`**: Resposta da alteração de status
- **`UpdateUserLockResponse`**: Resposta da alteração de bloqueio

### **3. Métodos no Service**
- **`updateUserStatus()`**: Lógica de alteração de status
- **`updateUserLockStatus()`**: Lógica de alteração de bloqueio

### **4. Endpoints no Controller**
- **`PATCH /{userId}/status`**: Endpoint para status
- **`PATCH /{userId}/lock`**: Endpoint para bloqueio

## 🔒 **Segurança e Autorização**

- **Acesso**: Apenas usuários com role `ADMIN`
- **Validação**: Bean Validation nos DTOs
- **Auditoria**: Logs estruturados de todas as operações
- **Tratamento de Erros**: Exceções customizadas e respostas HTTP apropriadas

## 📊 **Campos dos DTOs**

### **UpdateUserStatusRequest**
| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `isActive` | `Boolean` | ✅ | Novo status (true = ativo, false = inativo) |
| `comment` | `String` | ❌ | Comentário sobre a alteração |

### **UpdateUserLockRequest**
| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `isNotLocked` | `Boolean` | ✅ | Novo status (true = não bloqueado, false = bloqueado) |
| `comment` | `String` | ❌ | Comentário sobre a alteração |

### **Respostas**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `userId` | `UUID` | ID do usuário |
| `username` | `String` | Username do usuário |
| `name` | `String` | Nome do usuário |
| `previousStatus/previousLockStatus` | `Boolean` | Status anterior |
| `newStatus/newLockStatus` | `Boolean` | Novo status |
| `comment` | `String` | Comentário da alteração |
| `updatedAt` | `LocalDateTime` | Data/hora da alteração |
| `message` | `String` | Mensagem de confirmação |

## 🧪 **Testes Implementados**

- **`UserServiceStatusTest.shouldUpdateUserStatusSuccessfully()`**: Testa alteração de status
- **`UserServiceStatusTest.shouldUpdateUserLockStatusSuccessfully()`**: Testa alteração de bloqueio
- **`UserServiceStatusTest.shouldThrowExceptionWhenUserNotFound()`**: Testa usuário não encontrado

## 🚀 **Como Usar no Frontend**

### **Exemplo de Alteração de Status**
```javascript
// Alterar usuário para inativo
const updateUserStatus = async (userId, isActive, comment) => {
  try {
    const response = await fetch(`/users/${userId}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        isActive: isActive,
        comment: comment
      })
    });
    
    if (response.ok) {
      const result = await response.json();
      console.log('Status alterado:', result.message);
      // Atualizar interface
      refreshUserList();
    }
  } catch (error) {
    console.error('Erro ao alterar status:', error);
  }
};
```

### **Exemplo de Alteração de Bloqueio**
```javascript
// Bloquear usuário
const updateUserLock = async (userId, isNotLocked, comment) => {
  try {
    const response = await fetch(`/users/${userId}/lock`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        isNotLocked: isNotLocked,
        comment: comment
      })
    });
    
    if (response.ok) {
      const result = await response.json();
      console.log('Bloqueio alterado:', result.message);
      // Atualizar interface
      refreshUserList();
    }
  } catch (error) {
    console.error('Erro ao alterar bloqueio:', error);
  }
};
```

## ⚠️ **Observações Importantes**

1. **Validação**: Se tentar alterar para o mesmo status, retorna mensagem informativa
2. **Auditoria**: Todas as alterações são logadas com detalhes
3. **Transacional**: Operações são executadas em transação
4. **Compatibilidade**: Não afeta funcionalidades existentes
5. **Performance**: Operações otimizadas com validações em memória

## 🔧 **Arquivos Criados/Modificados**

1. **Novos DTOs**:
   - `UpdateUserStatusRequest.java`
   - `UpdateUserStatusResponse.java`
   - `UpdateUserLockRequest.java`
   - `UpdateUserLockResponse.java`

2. **Service Atualizado**:
   - `UserService.java` - Novos métodos adicionados

3. **Controller Atualizado**:
   - `UserController.java` - Novos endpoints PATCH

4. **Testes**:
   - `UserServiceStatusTest.java` - Testes das novas funcionalidades

## 📝 **Próximos Passos para o Frontend**

1. **Implementar botões/ícones** para alterar status e bloqueio
2. **Adicionar modais** para comentários das alterações
3. **Atualizar interface** após alterações bem-sucedidas
4. **Implementar feedback visual** para o usuário
5. **Adicionar confirmações** antes de alterações críticas

---

**Data da Implementação**: 28/08/2025  
**Versão da API**: Mantida (sem breaking changes)  
**Responsável**: Equipe de Desenvolvimento
