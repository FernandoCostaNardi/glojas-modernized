# Implementação da Aba de Vendas Anuais - Frontend

## Visão Geral

Este documento descreve a implementação completa da aba "Vendas Anuais" no frontend, seguindo o mesmo padrão das abas de vendas diárias e mensais, com dados provenientes da tabela `year_sells`.

## Arquivos Implementados

### Backend (Business API)

1. **`YearlySalesReportResponse.java`** - DTO para resposta de relatório de vendas anuais
2. **`YearSellRepository.java`** - Queries customizadas para busca de dados anuais
3. **`YearlySalesReportService.java`** - Service para geração de relatórios anuais
4. **`YearlySalesChartService.java`** - Service para dados de gráfico anual
5. **`SellController.java`** - Endpoints REST para vendas anuais

### Frontend (React/TypeScript)

1. **`vendasApi.ts`** - Funções de API para vendas anuais
2. **`VendasTableYearly.tsx`** - Tabela de vendas anuais
3. **`VendasChartYearly.tsx`** - Gráfico de vendas anuais
4. **`VendasAnuais.tsx`** - Componente principal da aba
5. **`VendasTabs.tsx`** - Integração da nova aba

## Funcionalidades Implementadas

### 1. Filtros de Ano

- **Ano de Início**: Input numérico com validação (2000 a ano atual + 1)
- **Ano de Fim**: Input numérico com validação (2000 a ano atual + 1)
- **Validação**: Ano de início deve ser menor ou igual ao ano de fim
- **Valor Padrão**: Ano atual selecionado em ambos os campos

### 2. Cards de Métricas

- **Melhor Ano**: Ano com maior valor de vendas
- **Pior Ano**: Ano com menor valor de vendas
- **Total de Lojas**: Número de lojas ativas no período
- **Valores**: Formatados em moeda brasileira (R$)

### 3. Gráfico de Vendas Anuais

- **Tipo**: Gráfico de barras (Chart.js)
- **Eixo X**: Anos
- **Eixo Y**: Valores em R$ (formato brasileiro)
- **Filtro de Loja**: Suporte a filtro por loja específica
- **Responsivo**: Adapta-se a diferentes tamanhos de tela

### 4. Tabela de Vendas Anuais

- **Colunas**:
  - **Loja**: Nome da loja
  - **Total**: Valor total das vendas (formatado em R$)
  - **% do Total**: Percentual de participação com progress bar
- **Footer**: Total geral de todas as lojas
- **Estados**: Loading, erro e vazio

### 5. Lógica de Sincronização do Gráfico

O gráfico possui uma lógica inteligente de sincronização:

- **Ao abrir**: Exibe os últimos 5 anos
- **Filtro ≤ 5 anos**: Atualiza o gráfico com o range selecionado
- **Filtro > 5 anos**: Mantém o gráfico com os últimos 5 anos
- **Ano atual selecionado**: Volta para os últimos 5 anos

## APIs Utilizadas

### 1. Relatório de Vendas Anuais

```http
GET /api/business/sales/yearly-sales?startYear=2020&endYear=2024
```

**Resposta:**
```json
[
  {
    "storeName": "Loja Matriz",
    "total": 1500000.00,
    "percentageOfTotal": 25.5
  }
]
```

### 2. Dados do Gráfico com Métricas

```http
GET /api/business/sales/yearly-chart-data-with-metrics?startYear=2020&endYear=2024&storeCode=000001
```

**Resposta:**
```json
{
  "chartData": [
    {
      "date": "2020-01-01T00:00:00",
      "total": 1200000.00
    }
  ],
  "metrics": {
    "bestDay": "2023-01-01",
    "bestDayValue": 1800000.00,
    "worstDay": "2020-01-01",
    "worstDayValue": 1200000.00,
    "totalActiveStores": 15
  }
}
```

## Validações Implementadas

### Frontend

- **Anos válidos**: 2000 até ano atual + 1
- **Range válido**: Ano de início ≤ ano de fim
- **Inputs obrigatórios**: Ambos os anos são obrigatórios
- **Desabilitação**: Inputs desabilitados durante loading

### Backend

- **Validação de range**: startYear ≤ endYear
- **Validação de anos**: 2000 ≤ ano ≤ ano atual + 1
- **StoreCode opcional**: Se não fornecido, agrega todas as lojas

## Componentes Reutilizados

### 1. StoreSelector
- Seletor de loja para filtro do gráfico
- Reutilizado das abas diárias e mensais

### 2. SalesMetricsCards
- Cards de métricas (melhor/pior ano)
- Adaptado para usar labels "Melhor Ano" e "Pior Ano"

## Estrutura de Dados

### YearlySalesReportResponse
```typescript
interface YearlySalesReportResponse {
  readonly storeName: string;
  readonly total: number;
  readonly percentageOfTotal: number;
}
```

### ChartMetrics
```typescript
interface ChartMetrics {
  readonly bestDay: string;        // Mapeado para bestYear
  readonly bestDayValue: number;   // Mapeado para bestYearValue
  readonly worstDay: string;       // Mapeado para worstYear
  readonly worstDayValue: number;  // Mapeado para worstYearValue
  readonly totalActiveStores: number;
}
```

## Testes

### Script de Teste
- **`test-yearly-sales-api.ps1`**: Script PowerShell para testar as APIs
- Testa todos os endpoints implementados
- Valida respostas e tratamento de erros

### Como Executar os Testes
```powershell
# No diretório business-api
.\test-yearly-sales-api.ps1 -Token "seu_token_jwt"
```

## Padrões Seguidos

### Clean Code
- **Responsabilidade única**: Cada componente tem uma função específica
- **Nomes descritivos**: Variáveis e funções com nomes claros
- **Funções pequenas**: Métodos com no máximo 20 linhas
- **Comentários**: Documentação JavaDoc e JSDoc

### React/TypeScript
- **Interfaces tipadas**: Todas as props e estados tipados
- **Hooks**: useState e useEffect para gerenciamento de estado
- **Componentes funcionais**: Uso de React.FC
- **Props readonly**: Imutabilidade das props

### Styling
- **Tailwind CSS**: Classes utilitárias para styling
- **Responsividade**: Adaptação para mobile e desktop
- **Consistência**: Mesmo padrão visual das outras abas
- **Acessibilidade**: Contraste e navegação por teclado

## Próximos Passos

1. **Testes de Integração**: Testar com dados reais da tabela `year_sells`
2. **Otimizações**: Implementar cache para dados frequentes
3. **Exportação**: Adicionar funcionalidade de exportar relatórios
4. **Filtros Avançados**: Adicionar filtros por região ou categoria

## Troubleshooting

### Problemas Comuns

1. **Erro 401 Unauthorized**
   - Verificar se o token JWT está válido
   - Verificar permissões do usuário

2. **Erro 400 Bad Request**
   - Verificar se os anos estão no range válido (2000-2030)
   - Verificar se startYear ≤ endYear

3. **Dados não carregam**
   - Verificar se a tabela `year_sells` tem dados
   - Verificar logs do backend para erros

4. **Gráfico não atualiza**
   - Verificar se a lógica de sincronização está funcionando
   - Verificar se os dados da API estão corretos

### Logs Úteis

- **Frontend**: Console do navegador
- **Backend**: Logs do Spring Boot
- **API**: Logs de requisições HTTP

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento
