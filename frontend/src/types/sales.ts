/**
 * Tipos TypeScript para dados de vendas e gráficos
 * Seguindo princípios de Clean Code com interfaces bem definidas
 */

/**
 * Interface para dados do gráfico de vendas
 * Representa um ponto de dados no gráfico com data e valor total
 */
export interface ChartDataResponse {
  readonly date: string;
  readonly total: number;
}

/**
 * Interface para opção de loja no seletor
 * Usado no dropdown de seleção de lojas para o gráfico
 */
export interface StoreOption {
  readonly code: string;
  readonly name: string;
}

/**
 * Interface para dados de vendas diárias (existente)
 * Mantida para compatibilidade com componentes existentes
 */
export interface DailySalesData {
  readonly storeName: string;
  readonly pdv: number;
  readonly danfe: number;
  readonly exchange: number;
  readonly total: number;
}

/**
 * Interface para resposta da API de vendas diárias
 * Mapeia a resposta do endpoint /sales/daily-sales
 */
export interface DailySalesReportResponse {
  readonly storeName: string;
  readonly pdv: number;
  readonly danfe: number;
  readonly exchange: number;
  readonly total: number;
}

/**
 * Interface para parâmetros de busca de dados do gráfico
 * Usado na chamada para o endpoint /sales/monthly-chart-data
 */
export interface ChartDataParams {
  readonly startDate: string;
  readonly endDate: string;
  readonly storeCode?: string;
}

/**
 * Interface para métricas calculadas do gráfico
 * Contém informações sobre melhor dia, pior dia e total de lojas ativas
 */
export interface ChartMetrics {
  readonly bestDay: string;
  readonly bestDayValue: number;
  readonly worstDay: string;
  readonly worstDayValue: number;
  readonly totalActiveStores: number;
}

/**
 * Interface para resposta consolidada de dados do gráfico com métricas
 * Combina dados do gráfico com métricas calculadas em uma única resposta
 */
export interface ChartDataWithMetricsResponse {
  readonly chartData: ChartDataResponse[];
  readonly metrics: ChartMetrics;
}

/**
 * Interface para estado de loading e erro do gráfico
 * Usado nos componentes de gráfico para gerenciar estados
 */
export interface ChartState {
  readonly data: ChartDataResponse[];
  readonly loading: boolean;
  readonly error: string | null;
}
