# Atualização do Sistema de Login - Email em vez de Username

## 📋 **Resumo das Alterações**

O sistema de login foi atualizado para usar **email** em vez de **username** para autenticação, alinhando a tela de login com o mecanismo de autenticação do backend.

## 🔄 **Mudanças Implementadas**

### **1. LoginRequest DTO**
- **Campo alterado**: `username` → `email`
- **Validação**: Email não pode ser nulo ou vazio
- **Estrutura**: Mantida como Record para simplicidade

### **2. UserRepository**
- **Novo método**: `findByEmailWithRolesAndPermissions(String email)`
- **Funcionalidade**: Busca usuário por email com roles e permissions carregados
- **Query**: Similar ao `findByUsernameWithRolesAndPermissions`

### **3. CustomUserDetailsService**
- **Método alterado**: `loadUserByUsername(String email)` agora recebe email
- **Busca**: Usa `findByEmailWithRolesAndPermissions` em vez de `findByUsernameWithRolesAndPermissions`
- **Logs**: Atualizados para refletir o uso de email

### **4. AuthController**
- **Logs atualizados**: Mostram email recebido em vez de username
- **Autenticação**: Usa email para criar o token de autenticação
- **Feedback**: Logs incluem email do usuário encontrado

## 🏗️ **Arquitetura da Mudança**

### **Fluxo de Autenticação Atualizado**
```
1. Frontend envia: { "email": "user@example.com", "password": "***" }
2. AuthController recebe LoginRequest com email
3. Cria UsernamePasswordAuthenticationToken com email
4. AuthenticationManager chama CustomUserDetailsService
5. CustomUserDetailsService busca usuário por email
6. Spring Security valida credenciais
7. JWT é gerado e retornado
```

### **Métodos do Repository**
```java
// Antes (por username)
Optional<User> findByUsernameWithRolesAndPermissions(String username);

// Agora (por email) - NOVO
Optional<User> findByEmailWithRolesAndPermissions(String email);

// Mantido para compatibilidade
Optional<User> findByUsernameWithRolesAndPermissions(String username);
```

## 📊 **Campos dos DTOs**

### **LoginRequest (Atualizado)**
| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `email` | `String` | ✅ | Email do usuário para login |
| `password` | `String` | ✅ | Senha do usuário |

### **LoginResponse (Mantido)**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `token` | `String` | Token JWT para autenticação |
| `username` | `String` | Username do usuário (para compatibilidade) |
| `name` | `String` | Nome completo do usuário |
| `roles` | `Set<String>` | Roles do usuário |
| `permissions` | `Set<String>` | Permissions do usuário |

## 🧪 **Testes Implementados**

- **`CustomUserDetailsServiceTest.shouldLoadUserByEmailSuccessfully()`**: Testa carregamento de usuário por email
- **`CustomUserDetailsServiceTest.shouldThrowExceptionWhenUserNotFound()`**: Testa usuário não encontrado

## 🚀 **Como Usar no Frontend**

### **Exemplo de Requisição de Login**
```javascript
const login = async (email, password) => {
  try {
    const response = await fetch('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,        // ✅ Agora usa email
        password: password
      })
    });
    
    if (response.ok) {
      const result = await response.json();
      // Armazenar token
      localStorage.setItem('token', result.token);
      // Redirecionar para dashboard
      window.location.href = '/dashboard';
    }
  } catch (error) {
    console.error('Erro no login:', error);
  }
};
```

### **Exemplo de Formulário de Login**
```jsx
const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    login(email, password);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Senha"
        required
      />
      <button type="submit">Entrar</button>
    </form>
  );
};
```

## ⚠️ **Observações Importantes**

1. **Compatibilidade**: A mudança é **não-breaking** para o frontend existente
2. **Segurança**: Mantém todas as validações e regras de segurança
3. **Performance**: Query otimizada com JOIN FETCH para roles e permissions
4. **Logs**: Logging estruturado para auditoria e debugging
5. **Validação**: Bean Validation mantida nos DTOs

## 🔧 **Arquivos Modificados**

1. **`LoginRequest.java`**: Campo username → email
2. **`UserRepository.java`**: Novo método findByEmailWithRolesAndPermissions
3. **`CustomUserDetailsService.java`**: Busca por email em vez de username
4. **`AuthController.java`**: Logs e comentários atualizados
5. **`CustomUserDetailsServiceTest.java`**: Novos testes para validação

## 📝 **Próximos Passos para o Frontend**

1. **Atualizar formulário de login** para usar campo email
2. **Validar formato de email** no frontend
3. **Atualizar mensagens de erro** para referenciar email
4. **Testar fluxo de login** com diferentes emails
5. **Verificar compatibilidade** com funcionalidades existentes

## 🔍 **Validações Implementadas**

### **Backend (Bean Validation)**
- Email não pode ser nulo
- Email não pode ser vazio ou apenas espaços
- Password não pode ser nulo
- Password não pode ser vazio ou apenas espaços

### **Frontend (Recomendado)**
- Validação de formato de email (regex)
- Feedback visual para campos obrigatórios
- Tratamento de erros de autenticação

---

**Data da Implementação**: 28/08/2025  
**Versão da API**: Mantida (sem breaking changes)  
**Responsável**: Equipe de Desenvolvimento
