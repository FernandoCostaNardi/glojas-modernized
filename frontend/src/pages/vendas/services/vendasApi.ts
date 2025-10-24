import { businessApi } from '@/services/api';

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

export default undefined as unknown as never;
