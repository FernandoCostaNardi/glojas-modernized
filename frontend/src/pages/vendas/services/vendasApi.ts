import { businessApi } from '@/services/api';
import { ChartDataResponse, ChartDataWithMetricsResponse } from '@/types/sales';

/**
 * Interface para os dados de vendas diárias retornados pela API
 */
export interface DailySalesReportResponse {
  readonly storeName: string;
  readonly pdv: number;
  readonly danfe: number;
  readonly exchange: number;
  readonly total: number;
}

/**
 * Interface para os dados de vendas mensais retornados pela API
 */
export interface MonthlySalesReportResponse {
  readonly storeName: string;
  readonly total: number;
  readonly percentageOfTotal: number;
}

/**
 * Interface para os dados de vendas anuais retornados pela API
 */
export interface YearlySalesReportResponse {
  readonly storeName: string;
  readonly total: number;
  readonly percentageOfTotal: number;
}

// Usamos o cliente central `businessApi` cujo baseURL já aponta para /api/business

/**
 * Busca relatório de vendas diárias
 * @param startDate - Data de início no formato YYYY-MM-DD
 * @param endDate - Data de fim no formato YYYY-MM-DD
 * @returns Promise com array de dados de vendas
 * @throws Error em caso de falha na requisição
 */
export const getDailySalesReport = async (
  startDate: string, 
  endDate: string
): Promise<DailySalesReportResponse[]> => {
  try {
    console.log(`Buscando relatório de vendas: ${startDate} até ${endDate}`);
    
    const response = await businessApi.get('/sales/daily-sales', {
      params: { 
        startDate, 
        endDate 
      }
    });
    
    console.log(`Relatório recebido: ${response.data.length} registros`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar relatório de vendas:', error);
    throw error;
  }
};

/**
 * Busca vendas do dia atual em tempo real
 * Chama a API especializada que busca dados diretamente da Legacy API
 * sem usar cache, garantindo informações sempre atualizadas
 * @returns Promise com array de dados de vendas do dia atual
 * @throws Error em caso de falha na requisição
 */
export const getCurrentDailySales = async (): Promise<DailySalesReportResponse[]> => {
  try {
    const today = new Date().toISOString().split('T')[0];
    console.log(`Buscando vendas do dia atual em tempo real: ${today}`);
    
    const response = await businessApi.get('/sales/current-daily-sales');
    
    console.log(`Vendas do dia atual recebidas: ${response.data.length} registros`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar vendas do dia atual:', error);
    throw error;
  }
};

/**
 * Testa a conectividade com a API de vendas
 * @returns Promise com status da conexão
 */
export const testVendasApiConnection = async (): Promise<boolean> => {
  try {
    await businessApi.get('/sales/health', { timeout: 5000 });
    return true;
  } catch (error) {
    console.warn('Teste de conectividade falhou:', error);
    return false;
  }
};

/**
 * Busca dados para gráfico de vendas diárias
 * @param startDate - Data de início no formato YYYY-MM-DD
 * @param endDate - Data de fim no formato YYYY-MM-DD
 * @param storeCode - Código da loja específica (opcional)
 * @returns Promise com array de dados do gráfico
 * @throws Error em caso de falha na requisição
 */
export const getChartData = async (
  startDate: string,
  endDate: string,
  storeCode?: string
): Promise<ChartDataResponse[]> => {
  try {
    console.log(`Buscando dados do gráfico: ${startDate} até ${endDate}${storeCode ? ` para loja ${storeCode}` : ' (todas as lojas)'}`);
    
    const params: Record<string, string> = {
      startDate,
      endDate
    };
    
    if (storeCode) {
      params.storeCode = storeCode;
    }
    
    const response = await businessApi.get('/sales/monthly-chart-data', {
      params
    });
    
    console.log(`Dados do gráfico recebidos: ${response.data.length} pontos de dados`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do gráfico:', error);
    throw error;
  }
};

/**
 * Busca dados para gráfico de vendas diárias com métricas calculadas
 * @param startDate - Data de início no formato YYYY-MM-DD
 * @param endDate - Data de fim no formato YYYY-MM-DD
 * @param storeCode - Código da loja específica (opcional)
 * @returns Promise com dados do gráfico e métricas calculadas
 * @throws Error em caso de falha na requisição
 */
export const getChartDataWithMetrics = async (
  startDate: string,
  endDate: string,
  storeCode?: string
): Promise<ChartDataWithMetricsResponse> => {
  try {
    console.log(`Buscando dados do gráfico com métricas: ${startDate} até ${endDate}${storeCode ? ` para loja ${storeCode}` : ' (todas as lojas)'}`);
    
    const params: Record<string, string> = {
      startDate,
      endDate
    };
    
    if (storeCode) {
      params.storeCode = storeCode;
    }
    
    const response = await businessApi.get('/sales/chart-data-with-metrics', {
      params
    });
    
    console.log(`Dados do gráfico com métricas recebidos: ${response.data.chartData.length} pontos de dados`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do gráfico com métricas:', error);
    throw error;
  }
};

/**
 * Busca relatório de vendas mensais agregadas por loja
 * @param startYearMonth - Ano/mês de início no formato YYYY-MM
 * @param endYearMonth - Ano/mês de fim no formato YYYY-MM
 * @returns Promise com array de dados de vendas mensais
 * @throws Error em caso de falha na requisição
 */
export const getMonthlySalesReport = async (
  startYearMonth: string, 
  endYearMonth: string
): Promise<MonthlySalesReportResponse[]> => {
  try {
    console.log(`Buscando relatório de vendas mensais: ${startYearMonth} até ${endYearMonth}`);
    
    const response = await businessApi.get('/sales/monthly-sales', {
      params: { 
        startYearMonth, 
        endYearMonth 
      }
    });
    
    console.log(`Relatório mensal recebido: ${response.data.length} registros`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar relatório de vendas mensais:', error);
    throw error;
  }
};

/**
 * Busca dados para gráfico de vendas mensais com métricas calculadas
 * @param startYearMonth - Ano/mês de início no formato YYYY-MM
 * @param endYearMonth - Ano/mês de fim no formato YYYY-MM
 * @param storeCode - Código da loja específica (opcional)
 * @returns Promise com dados do gráfico mensal e métricas calculadas
 * @throws Error em caso de falha na requisição
 */
export const getMonthlyChartDataWithMetrics = async (
  startYearMonth: string,
  endYearMonth: string,
  storeCode?: string
): Promise<ChartDataWithMetricsResponse> => {
  try {
    console.log(`Buscando dados do gráfico mensal com métricas: ${startYearMonth} até ${endYearMonth}${storeCode ? ` para loja ${storeCode}` : ' (todas as lojas)'}`);
    
    const params: Record<string, string> = {
      startYearMonth,
      endYearMonth
    };
    
    if (storeCode) {
      params.storeCode = storeCode;
    }
    
    const response = await businessApi.get('/sales/monthly-chart-data-with-metrics', {
      params
    });
    
    console.log(`Dados do gráfico mensal com métricas recebidos: ${response.data.chartData.length} pontos de dados`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do gráfico mensal com métricas:', error);
    throw error;
  }
};

/**
 * Busca relatório de vendas anuais
 * @param startYear - Ano de início
 * @param endYear - Ano de fim
 * @returns Promise com array de dados de vendas anuais
 * @throws Error em caso de falha na requisição
 */
export const getYearlySalesReport = async (
  startYear: number,
  endYear: number
): Promise<YearlySalesReportResponse[]> => {
  try {
    console.log(`Buscando relatório de vendas anuais: ${startYear} até ${endYear}`);
    
    const response = await businessApi.get('/sales/yearly-sales', {
      params: { 
        startYear, 
        endYear 
      }
    });
    
    console.log(`Relatório anual recebido: ${response.data.length} registros`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar relatório de vendas anuais:', error);
    throw error;
  }
};

/**
 * Busca dados para gráfico de vendas anuais com métricas calculadas
 * @param startYear - Ano de início
 * @param endYear - Ano de fim
 * @param storeCode - Código da loja específica (opcional)
 * @returns Promise com dados do gráfico anual e métricas calculadas
 * @throws Error em caso de falha na requisição
 */
export const getYearlyChartDataWithMetrics = async (
  startYear: number,
  endYear: number,
  storeCode?: string
): Promise<ChartDataWithMetricsResponse> => {
  try {
    console.log(`Buscando dados do gráfico anual com métricas: ${startYear} até ${endYear}${storeCode ? ` para loja ${storeCode}` : ' (todas as lojas)'}`);
    
    const params: Record<string, string> = {
      startYear: startYear.toString(),
      endYear: endYear.toString()
    };
    
    if (storeCode) {
      params.storeCode = storeCode;
    }
    
    const response = await businessApi.get('/sales/yearly-chart-data-with-metrics', {
      params
    });
    
    console.log(`Dados do gráfico anual com métricas recebidos: ${response.data.chartData.length} pontos de dados`);
    
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do gráfico anual com métricas:', error);
    throw error;
  }
};

export default undefined as unknown as never;
