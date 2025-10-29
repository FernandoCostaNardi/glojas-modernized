import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import VendasChart from '@/pages/vendas/components/VendasChart';

/**
 * Props do componente DashboardSalesChart
 */
interface DashboardSalesChartProps {
  readonly className?: string;
}

/**
 * Componente de gráfico de vendas para o dashboard.
 * Reutiliza o componente VendasChart existente com configurações específicas.
 * Mostra vendas diárias do mês atual sem seleção de loja.
 * 
 * @param className Classes CSS adicionais
 */
const DashboardSalesChart: React.FC<DashboardSalesChartProps> = ({ 
  className = '' 
}) => {
  const { isMobile } = useLayout();

  /**
   * Obtém o primeiro dia do mês atual
   */
  const getFirstDayOfMonth = (): string => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    return firstDay.toISOString().split('T')[0] || '';
  };

  /**
   * Obtém a data atual
   */
  const getToday = (): string => {
    return new Date().toISOString().split('T')[0] || '';
  };

  // Configurações do gráfico para o dashboard
  const startDate = getFirstDayOfMonth();
  const endDate = getToday();
  const selectedStoreCode = null; // Sempre mostrar todas as lojas

  /**
   * Callback para métricas do gráfico (não utilizado no dashboard)
   */
  const handleChartMetricsChange = (metrics: any): void => {
    // No dashboard, não precisamos das métricas do gráfico
    // pois já temos os cards de métricas principais
  };

  return (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${className}`}>
      {/* Cabeçalho do gráfico */}
      <div className={`p-6 ${isMobile ? 'pb-4' : 'pb-6'}`}>
        <h2 className={`font-semibold text-smart-gray-800 mb-2 ${
          isMobile ? 'text-base' : 'text-lg'
        }`}>
          Vendas Diárias do Mês Atual 📊
        </h2>
        <p className={`text-smart-gray-600 ${
          isMobile ? 'text-xs' : 'text-sm'
        }`}>
          Acompanhe o desempenho de vendas do mês atual em tempo real.
        </p>
      </div>

      {/* Gráfico de vendas */}
      <div className={`px-6 ${isMobile ? 'pb-4' : 'pb-6'}`}>
        <VendasChart
          startDate={startDate}
          endDate={endDate}
          selectedStoreCode={selectedStoreCode}
          onMetricsChange={handleChartMetricsChange}
          className="dashboard-sales-chart"
        />
      </div>
    </div>
  );
};

export default DashboardSalesChart;
