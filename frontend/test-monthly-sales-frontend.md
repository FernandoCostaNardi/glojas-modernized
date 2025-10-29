# Teste da Aba de Vendas Mensais - Frontend

## ✅ Dependências Instaladas

As seguintes dependências foram instaladas com sucesso:

- `chart.js`: ^4.5.1
- `react-chartjs-2`: ^5.3.1

## 🚀 Como Testar

1. **Iniciar o servidor de desenvolvimento:**
   ```bash
   cd frontend
   npm run dev
   ```

2. **Acessar a aplicação:**
   - URL: http://localhost:5173 (ou a porta mostrada no terminal)
   - Navegar para a página de Vendas
   - Clicar na aba "Vendas Mensais"

## 🎯 Funcionalidades para Testar

### Filtros de Mês
- [ ] Selecionar período de início (input tipo month)
- [ ] Selecionar período de fim (input tipo month)
- [ ] Validar que fim >= início
- [ ] Testar formato YYYY-MM

### Gráfico
- [ ] Gráfico carrega automaticamente com dados do ano atual
- [ ] Eixo X mostra meses (Jan/2025, Fev/2025, etc.)
- [ ] Eixo Y mostra valores em reais
- [ ] Filtro de loja funciona
- [ ] Atualização do gráfico baseada no período selecionado

### Tabela
- [ ] Colunas: Loja | Total | % do Total
- [ ] Formatação de moeda (R$)
- [ ] Formatação de percentual (%)
- [ ] Ordenação por total (decrescente)

### Cards de Métricas
- [ ] "Melhor Mês" e "Pior Mês" (ao invés de "Melhor Dia"/"Pior Dia")
- [ ] Total de lojas ativas
- [ ] Valores formatados corretamente

### Lógica de Atualização do Gráfico
- [ ] Ao abrir: mostra todos os meses do ano atual
- [ ] Filtro ≤ 12 meses: atualiza gráfico com range filtrado
- [ ] Filtro mês atual: mantém gráfico com ano atual
- [ ] Filtro > 12 meses: mantém gráfico com ano atual

## 🔧 APIs Necessárias

Certifique-se de que o backend está rodando e as seguintes APIs estão funcionando:

1. **Relatório de Vendas Mensais:**
   ```
   GET /api/business/sales/monthly-sales?startYearMonth=2025-01&endYearMonth=2025-12
   ```

2. **Dados do Gráfico com Métricas:**
   ```
   GET /api/business/sales/monthly-chart-data-with-metrics?startYearMonth=2025-01&endYearMonth=2025-12
   ```

3. **Dados do Gráfico por Loja:**
   ```
   GET /api/business/sales/monthly-chart-data-with-metrics?startYearMonth=2025-01&endYearMonth=2025-12&storeCode=001
   ```

## 🐛 Possíveis Problemas

### Erro de Importação
Se ainda houver erro de importação do `react-chartjs-2`:
1. Pare o servidor (Ctrl+C)
2. Execute: `npm install`
3. Reinicie: `npm run dev`

### Dados Não Carregam
1. Verifique se o backend está rodando
2. Verifique se há dados na tabela `monthly_sells`
3. Execute o script de sincronização: `.\test-monthly-sales-sync.ps1`

### Gráfico Não Renderiza
1. Verifique o console do navegador para erros
2. Verifique se os dados estão chegando da API
3. Verifique se o Chart.js está registrado corretamente

## 📊 Dados de Teste

Para testar, você pode usar os seguintes períodos:

- **Ano completo:** 2025-01 a 2025-12
- **Trimestre:** 2025-01 a 2025-03
- **Mês único:** 2025-01 a 2025-01
- **Semestre:** 2025-01 a 2025-06

## ✅ Checklist de Funcionamento

- [ ] Página carrega sem erros
- [ ] Aba "Vendas Mensais" está visível
- [ ] Filtros funcionam corretamente
- [ ] Gráfico renderiza com dados
- [ ] Tabela mostra dados formatados
- [ ] Cards de métricas mostram valores
- [ ] Filtro de loja funciona
- [ ] Responsividade em mobile
- [ ] Performance adequada

## 🎉 Sucesso

Se todos os itens acima estão funcionando, a implementação da aba de Vendas Mensais está completa e pronta para uso!
