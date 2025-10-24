# Atualização do Frontend para Nova Rota Legacy API

## Mudanças Realizadas

### 1. Atualização do Serviço API
**Arquivo:** `frontend/src/services/api.ts`

**Mudança:** Atualizada rota de `/stores-legacy` para `/legacy/stores`
```typescript
// Antes
const response = await api.get('/stores-legacy');

// Depois  
const response = await api.get('/legacy/stores');
```

### 2. Atualização de Comentários
**Arquivo:** `frontend/src/components/modals/StoreModal.tsx`

**Mudança:** Atualizado comentário para refletir nova rota
```typescript
// Antes
* Mapeia os campos retornados pela API /stores-legacy

// Depois
* Mapeia os campos retornados pela API /legacy/stores
```

## Arquivos Modificados

- ✅ `frontend/src/services/api.ts` - Rota atualizada
- ✅ `frontend/src/components/modals/StoreModal.tsx` - Comentário atualizado

## Validação das Mudanças

### 1. Verificar se não há referências à rota antiga:
```bash
grep -r "stores-legacy" frontend/src/
# Deve retornar vazio
```

### 2. Verificar se a nova rota está sendo usada:
```bash
grep -r "legacy/stores" frontend/src/
# Deve mostrar as referências à nova rota
```

### 3. Testar build do frontend:
```bash
cd frontend
npm run build
```

### 4. Testar servidor de desenvolvimento:
```bash
cd frontend
npm run dev
```

## URLs de Teste

### Antes (não funcionava):
- `https://gestaosmarteletron.com.br/api/stores-legacy` → Retornava HTML do frontend

### Depois (funcionando):
- `https://gestaosmarteletron.com.br/api/legacy/stores` → Retorna dados JSON da Legacy API

## Script de Teste

Execute o script `test-frontend-legacy-api.ps1` para validar todas as mudanças:

```powershell
./test-frontend-legacy-api.ps1
```

## Impacto das Mudanças

### ✅ Benefícios:
- **Consistência**: Todas as rotas seguem o padrão `/api/legacy/*`
- **Funcionamento**: Frontend agora consegue acessar dados da Legacy API
- **Manutenibilidade**: Código mais organizado e fácil de entender

### ⚠️ Considerações:
- **Deploy**: Frontend precisa ser rebuild e redeploy
- **Cache**: Pode ser necessário limpar cache do navegador
- **Monitoramento**: Verificar logs para garantir que não há erros

## Próximos Passos

1. **Rebuild do Frontend:**
   ```bash
   cd frontend
   npm run build
   ```

2. **Deploy para Produção:**
   - Copiar arquivos da pasta `dist/` para `/opt/glojas-modernized/frontend/dist/`
   - Reiniciar Nginx se necessário

3. **Validação em Produção:**
   - Testar acessando a aplicação web
   - Verificar se o modal de lojas carrega dados da Legacy API
   - Monitorar logs para erros

## Rollback (se necessário)

Se algo der errado, reverta as mudanças:

```typescript
// Em frontend/src/services/api.ts
const response = await api.get('/stores-legacy'); // Voltar para rota antiga
```

---

**Data da Atualização:** $(Get-Date)  
**Responsável:** Equipe de Desenvolvimento  
**Status:** ✅ Implementado
