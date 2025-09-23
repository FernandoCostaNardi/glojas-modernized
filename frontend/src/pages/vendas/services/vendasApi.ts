import axios from 'axios';
import { getToken, STORAGE_KEYS } from '@/utils/token';

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
 * Configuração base da API
 */
const API_BASE_URL = 'http://localhost:8082/api/business/sales';

/**
 * Instância do axios configurada para a API de vendas
 */
const vendasApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 segundos
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Interceptor para adicionar token de autenticação em todas as requisições
 */
vendasApi.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Token JWT adicionado à requisição de vendas');
    } else {
      console.warn('Token JWT não encontrado para requisição de vendas');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Interceptor para tratamento de erros de resposta
 */
vendasApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Erro com resposta do servidor
      const status = error.response.status;
      const message = error.response.data?.message || 'Erro desconhecido do servidor';
      
      // Tratamento especial para erro 401 - Token expirado ou inválido
      if (status === 401) {
        localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.USER_DATA);
        window.location.href = '/login';
        throw new Error('Sessão expirada. Faça login novamente.');
      }
      
      switch (status) {
        case 400:
          throw new Error(`Dados inválidos: ${message}`);
        case 403:
          throw new Error('Acesso negado. Você não tem permissão para acessar este recurso.');
        case 404:
          throw new Error('Endpoint não encontrado.');
        case 500:
          throw new Error('Erro interno do servidor. Tente novamente mais tarde.');
        default:
          throw new Error(`Erro ${status}: ${message}`);
      }
    } else if (error.request) {
      // Erro de rede
      throw new Error('Erro de conexão. Verifique sua internet e tente novamente.');
    } else {
      // Outros erros
      throw new Error('Erro inesperado. Tente novamente.');
    }
  }
);

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
    
    const response = await vendasApi.get('/daily-sales', {
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
    
    const response = await vendasApi.get('/current-daily-sales');
    
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
    await vendasApi.get('/health', { timeout: 5000 });
    return true;
  } catch (error) {
    console.warn('Teste de conectividade falhou:', error);
    return false;
  }
};

export default vendasApi;
