import { businessApi } from './api';
import { DashboardSummary } from '@/types/dashboard';

/**
 * Serviço de API para operações do dashboard.
 * Fornece métodos para buscar métricas e resumos da página inicial.
 * Seguindo princípios de Clean Code com responsabilidade única.
 */

/**
 * Busca resumo consolidado do dashboard.
 * Retorna métricas de vendas e contagem de lojas ativas em uma única chamada.
 * 
 * @returns Promise com resumo consolidado do dashboard
 * @throws Error em caso de falha na requisição
 */
export const getDashboardSummary = async (): Promise<DashboardSummary> => {
  try {
    console.log('🌐 Dashboard API: Buscando resumo do dashboard');
    
    const response = await businessApi.get('/dashboard/summary');
    
    console.log('🌐 Dashboard API: Resumo recebido:', response.data);
    
    return response.data;
  } catch (error) {
    console.error('❌ Erro ao buscar resumo do dashboard:', error);
    throw error;
  }
};
