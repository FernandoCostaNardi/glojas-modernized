/**
 * Interfaces TypeScript para o dashboard.
 * Define tipos de dados para métricas e resumos da página inicial.
 */

/**
 * Resumo consolidado do dashboard com métricas principais.
 * Contém dados de vendas e contagem de lojas ativas.
 */
export interface DashboardSummary {
  /** Total de vendas do dia atual */
  totalSalesToday: number;
  /** Total de vendas do mês atual */
  totalSalesMonth: number;
  /** Total de vendas do ano atual */
  totalSalesYear: number;
  /** Quantidade de lojas ativas no sistema */
  activeStoresCount: number;
}

/**
 * Estado de carregamento para componentes do dashboard.
 * Utilizado para controlar loading states e tratamento de erros.
 */
export interface DashboardState {
  /** Dados do resumo do dashboard */
  data: DashboardSummary | null;
  /** Indica se está carregando dados */
  loading: boolean;
  /** Mensagem de erro, se houver */
  error: string | null;
}
