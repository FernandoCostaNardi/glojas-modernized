# Guia de Configuração de Email - Sistema Glojas

## Visão Geral

Este guia explica como configurar o envio de emails automáticos para notificações de vendas diárias no Sistema Glojas.

## Configuração do Gmail App Password

### Passo 1: Acessar Configurações do Google

1. Acesse [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
2. Faça login com a conta `fcostanardi@gmail.com`
3. Se solicitado, confirme sua identidade com 2FA

### Passo 2: Criar App Password

1. Clique em "Selecionar app" e escolha "Outro (nome personalizado)"
2. Digite: `Glojas Business API`
3. Clique em "Gerar"
4. **IMPORTANTE**: Copie a senha gerada (16 caracteres com espaços)
   - Exemplo: `abcd efgh ijkl mnop`

### Passo 3: Configurar Variáveis de Ambiente

Configure as seguintes variáveis de ambiente no sistema:

```bash
# Windows (PowerShell)
$env:EMAIL_USERNAME="fcostanardi@gmail.com"
$env:EMAIL_PASSWORD="abcd efgh ijkl mnop"  # Use a senha gerada
$env:EMAIL_FROM="fcostanardi@gmail.com"

# Windows (CMD)
set EMAIL_USERNAME=fcostanardi@gmail.com
set EMAIL_PASSWORD=abcd efgh ijkl mnop
set EMAIL_FROM=fcostanardi@gmail.com

# Linux/Mac
export EMAIL_USERNAME="fcostanardi@gmail.com"
export EMAIL_PASSWORD="abcd efgh ijkl mnop"
export EMAIL_FROM="fcostanardi@gmail.com"
```

### Passo 4: Reiniciar Aplicação

Após configurar as variáveis de ambiente, reinicie a aplicação Business API.

## Configuração no application.yml

O arquivo `application.yml` já está configurado com os valores padrão:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME:fcostanardi@gmail.com}
    password: ${EMAIL_PASSWORD:}
    default-from: ${EMAIL_FROM:fcostanardi@gmail.com}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Testando a Configuração

### Teste Manual via API

1. Inicie a aplicação Business API
2. Faça login como usuário com permissão `SYSTEM_ADMIN`
3. Execute o endpoint de teste:

```bash
POST /api/business/scheduler/daily-sales/execute
Authorization: Bearer <seu-token-jwt>
Content-Type: application/json

# Opcional: especificar uma data
POST /api/business/scheduler/daily-sales/execute?date=2024-01-15
```

### Verificar Logs

Monitore os logs da aplicação para verificar se o processo está funcionando:

```
INFO  - === Iniciando processo agendado de sincronização e envio de emails ===
INFO  - Sincronizando vendas do dia: 2024-01-15
INFO  - Sincronização concluída: 5 criados, 2 atualizados, 3 lojas processadas
INFO  - Encontrados 2 destinatários para notificação
INFO  - Enviando email para: destinatario1@exemplo.com
INFO  - Email enviado com sucesso para: destinatario1@exemplo.com
INFO  - === Processo agendado concluído com sucesso ===
```

## Configuração de Destinatários

### Cadastrar Emails para Notificação

1. Acesse a tela "Configurações do Sistema" no frontend
2. Clique em "Cadastro de Notificações por Email"
3. Clique em "Adicionar Email"
4. Preencha o email e marque "Email com diário de vendas do dia anterior"
5. Salve o cadastro

### Verificar Destinatários via API

```bash
GET /api/business/email-notifiers
Authorization: Bearer <seu-token-jwt>
```

## Agendamento Automático

O processo é executado automaticamente todos os dias às **01:00 AM** (1h da madrugada).

### Configuração do Cron

A configuração está no `application.yml`:

```yaml
sync:
  daily-sales:
    schedule:
      enabled: true
      cron: "0 0 1 * * *"  # Todo dia às 01:00 AM
```

### Desabilitar Agendamento

Para desabilitar temporariamente:

```yaml
sync:
  daily-sales:
    schedule:
      enabled: false
```

## Estrutura do Email

O email enviado contém:

- **Cabeçalho**: Data do relatório
- **Resumo**: Estatísticas da sincronização
- **Tabela**: Vendas por loja (PDV, DANFE, Troca, Total)
- **Rodapé**: Totais consolidados e timestamp de execução

### Exemplo de Email

```
Relatório de Vendas Diárias - 15/01/2024

Resumo da Sincronização:
• Registros criados: 5
• Registros atualizados: 2
• Lojas processadas: 3
• Total de lojas com vendas: 3

| Loja        | PDV        | DANFE      | Troca      | Total      |
|-------------|------------|------------|------------|------------|
| Loja Centro | R$ 1.500,00| R$ 2.300,00| R$ 100,00  | R$ 3.700,00|
| Loja Norte  | R$ 800,00  | R$ 1.200,00| R$ 50,00   | R$ 1.950,00|
| TOTAL GERAL | R$ 2.300,00| R$ 3.500,00| R$ 150,00  | R$ 5.650,00|

Sincronização executada em: 16/01/2024 01:05:23
Sistema Glojas - Relatório Automático de Vendas Diárias
```

## Solução de Problemas

### Erro: "Authentication failed"

- Verifique se o App Password está correto
- Confirme se a conta tem 2FA habilitado
- Verifique se as variáveis de ambiente estão configuradas

### Erro: "No recipients found"

- Verifique se há emails cadastrados com `dailySellNotifier = true`
- Confirme se o banco de dados está acessível

### Erro: "No sales data found"

- Verifique se há dados de vendas para o dia anterior
- Confirme se a sincronização foi executada com sucesso

### Email não chega

- Verifique a pasta de spam/lixo eletrônico
- Confirme se o endereço de email está correto
- Verifique os logs da aplicação para erros específicos

## Monitoramento

### Logs Importantes

- `=== Iniciando processo agendado ===`
- `Sincronização concluída: X criados, Y atualizados`
- `Encontrados N destinatários para notificação`
- `Email enviado com sucesso para: email@exemplo.com`
- `=== Processo agendado concluído com sucesso ===`

### Endpoints de Monitoramento

```bash
# Status do scheduler
GET /api/business/scheduler/status

# Execução manual
POST /api/business/scheduler/daily-sales/execute
```

## Segurança

- O App Password é específico para esta aplicação
- Pode ser revogado a qualquer momento no Google
- As variáveis de ambiente não devem ser commitadas no Git
- Use apenas contas corporativas para envio de emails

---

**Última Atualização**: Janeiro 2024  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Glojas
