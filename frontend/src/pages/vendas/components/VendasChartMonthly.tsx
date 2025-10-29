import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { getMonthlyChartDataWithMetrics } from '../services/vendasApi';
import { ChartDataWithMetricsResponse } from '@/types/sales';

// Registrar componentes do Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

/**
 * Interface para as propriedades do VendasChartMonthly
 */
interface VendasChartMonthlyProps {
  readonly startYearMonth: string;
  readonly endYearMonth: string;
  readonly selectedStoreCode?: string | null;
  readonly onMetricsChange?: (metrics: any) => void;
  readonly className?: string;
}

/**
 * Interface para o estado de loading e erro
 */
interface ChartState {
  readonly data: ChartDataWithMetricsResponse | null;
  readonly loading: boolean;
  readonly error: string | null;
}

/**
 * Componente de gráfico para exibir dados de vendas mensais.
 * Utiliza Chart.js para renderizar gráfico de linha com dados mensais.
 * Seguindo princípios de Clean Code com responsabilidade única.
 * 
 * @param startYearMonth - Ano/mês de início (formato YYYY-MM)
 * @param endYearMonth - Ano/mês de fim (formato YYYY-MM)
 * @param selectedStoreCode - Código da loja selecionada (opcional)
 * @param onMetricsChange - Callback para receber métricas calculadas
 * @param className - Classes CSS adicionais
 */
const VendasChartMonthly: React.FC<VendasChartMonthlyProps> = ({
  startYearMonth,
  endYearMonth,
  selectedStoreCode,
  onMetricsChange,
  className = ''
}) => {
  
  const [chartState, setChartState] = useState<ChartState>({
    data: null,
    loading: false,
    error: null
  });

  /**
   * Formata ano/mês para exibição no gráfico
   * @param yearMonth - Ano/mês no formato YYYY-MM
   * @returns String formatada (ex: "Jan/2025")
   */
  const formatYearMonthForDisplay = (yearMonth: string): string => {
    const [year, month] = yearMonth.split('-');
    const monthNames = [
      'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
      'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
    ];
    const monthIndex = parseInt(month || '1', 10) - 1;
    return `${monthNames[monthIndex]}/${year}`;
  };

  /**
   * Gera sequência de meses entre startYearMonth e endYearMonth
   * @param startYearMonth - Ano/mês de início (formato YYYY-MM)
   * @param endYearMonth - Ano/mês de fim (formato YYYY-MM)
   * @returns Array de strings com os meses no formato YYYY-MM
   */
  const generateMonthSequence = (startYearMonth: string, endYearMonth: string): string[] => {
    const months: string[] = [];
    const [startYear, startMonth] = startYearMonth.split('-').map(Number);
    const [endYear, endMonth] = endYearMonth.split('-').map(Number);
    
    let currentYear = startYear;
    let currentMonth = startMonth;
    
    while (currentYear < endYear || (currentYear === endYear && currentMonth <= endMonth)) {
      const yearMonth = `${currentYear}-${currentMonth.toString().padStart(2, '0')}`;
      months.push(yearMonth);
      
      currentMonth++;
      if (currentMonth > 12) {
        currentMonth = 1;
        currentYear++;
      }
    }
    
    return months;
  };


  /**
   * Formata valor monetário para exibição
   * @param value - Valor a ser formatado
   * @returns String formatada em reais
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  };

  /**
   * Carrega dados do gráfico
   */
  const loadChartData = async (): Promise<void> => {
    setChartState(prev => ({ ...prev, loading: true, error: null }));

    try {
      const data = await getMonthlyChartDataWithMetrics(
        startYearMonth,
        endYearMonth,
        selectedStoreCode || undefined
      );

      setChartState({
        data,
        loading: false,
        error: null
      });

      // Notificar métricas para o componente pai
      if (onMetricsChange) {
        onMetricsChange(data.metrics);
      }

    } catch (error) {
      setChartState({
        data: null,
        loading: false,
        error: error instanceof Error ? error.message : 'Erro ao carregar dados do gráfico'
      });
    }
  };

  /**
   * Carrega dados quando os parâmetros mudam
   */
  useEffect(() => {
    loadChartData();
  }, [startYearMonth, endYearMonth, selectedStoreCode]);

  /**
   * Renderiza o estado de carregamento
   */
  const renderLoading = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="flex items-center space-x-3">
        <svg className="animate-spin h-8 w-8 text-smart-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span className="text-smart-gray-600 font-medium">Carregando gráfico...</span>
      </div>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-red-500 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Erro ao carregar gráfico</h3>
        <p className="text-smart-gray-600 mb-4">{chartState.error}</p>
        <button
          onClick={loadChartData}
          className="bg-smart-blue-600 hover:bg-smart-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors duration-200"
        >
          Tentar novamente
        </button>
      </div>
    </div>
  );

  /**
   * Renderiza o estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Nenhum dado encontrado</h3>
        <p className="text-smart-gray-600">Não há dados de vendas para o período selecionado.</p>
      </div>
    </div>
  );

  /**
   * Renderiza o gráfico
   */
  const renderChart = (): React.ReactNode => {
    if (!chartState.data || !chartState.data.chartData || chartState.data.chartData.length === 0) {
      return renderEmpty();
    }

    const { chartData } = chartState.data;

    // Debug: Log dos dados recebidos
    console.log('🔍 Dados do gráfico recebidos:', chartData);
    console.log('📅 Período solicitado:', startYearMonth, 'a', endYearMonth);
    
    // Gerar sequência de meses baseada no período solicitado
    const monthSequence = generateMonthSequence(startYearMonth, endYearMonth);
    console.log('📆 Sequência de meses gerada:', monthSequence);
    
    // Criar mapa de dados por mês para facilitar a busca
    const dataMap = new Map<string, number>();
    chartData.forEach((item, index) => {
      let yearMonth: string;
      
      if (typeof item.date === 'string') {
        // Se for "YYYY-MM-DD", extrair apenas "YYYY-MM"
        if (item.date.length === 10 && item.date.includes('-')) {
          yearMonth = item.date.substring(0, 7);
        } else if (item.date.includes(',')) {
          // Se for formato "2025,1,1", converter para "2025-01"
          const parts = item.date.split(',');
          const year = parts[0];
          const month = parts[1].padStart(2, '0');
          yearMonth = `${year}-${month}`;
        } else {
          yearMonth = item.date;
        }
      } else {
        // Fallback: tentar converter para string e extrair ano/mês
        const dateStr = String(item.date);
        if (dateStr.includes(',')) {
          const parts = dateStr.split(',');
          const year = parts[0];
          const month = parts[1].padStart(2, '0');
          yearMonth = `${year}-${month}`;
        } else {
          yearMonth = dateStr.length >= 7 ? dateStr.substring(0, 7) : dateStr;
        }
      }
      
      console.log(`📊 Item ${index}: date=${item.date}, yearMonth=${yearMonth}, total=${item.total}`);
      dataMap.set(yearMonth, item.total);
    });
    
    console.log('🗺️ Mapa de dados criado:', Object.fromEntries(dataMap));

    // Preparar dados para o Chart.js usando a sequência de meses
    const finalLabels = monthSequence.map(month => formatYearMonthForDisplay(month));
    const finalData = monthSequence.map(month => dataMap.get(month) || 0);
    
    console.log('🏷️ Labels finais:', finalLabels);
    console.log('📈 Dados finais:', finalData);
    
    const chartConfig = {
      labels: finalLabels,
      datasets: [
        {
          label: 'Vendas Mensais',
          data: finalData,
          borderColor: 'rgb(59, 130, 246)',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: 'rgb(59, 130, 246)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          pointRadius: 6,
          pointHoverRadius: 8,
        }
      ]
    };

    const options = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        title: {
          display: true,
          text: selectedStoreCode 
            ? `Vendas Mensais - Loja ${selectedStoreCode}` 
            : 'Vendas Mensais - Todas as Lojas',
          font: {
            size: 16,
            weight: 'bold' as const
          }
        },
        tooltip: {
          callbacks: {
            label: (context: any) => {
              const value = context.parsed.y;
              return `Vendas: ${formatCurrency(value)}`;
            }
          }
        }
      },
      scales: {
        x: {
          display: true,
          title: {
            display: true,
            text: 'Mês'
          },
          grid: {
            display: false
          }
        },
        y: {
          display: true,
          title: {
            display: true,
            text: 'Valor (R$)'
          },
          ticks: {
            callback: (value: any) => formatCurrency(value)
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.1)'
          }
        }
      },
      interaction: {
        intersect: false,
        mode: 'index' as const
      }
    };

    return (
      <div className="h-96 w-full">
        <Line data={chartConfig} options={options} />
      </div>
    );
  };

  /**
   * Renderiza o conteúdo principal
   */
  const renderContent = (): React.ReactNode => {
    if (chartState.loading) {
      return renderLoading();
    }

    if (chartState.error) {
      return renderError();
    }

    return renderChart();
  };

  return (
    <div className={`vendas-chart-monthly bg-white rounded-lg shadow-md p-6 ${className}`}>
      <div className="mb-4">
        <h3 className="text-lg font-medium text-smart-gray-900">
          Gráfico de Vendas Mensais
        </h3>
        <p className="text-sm text-smart-gray-600">
          Período: {formatYearMonthForDisplay(startYearMonth)} a {formatYearMonthForDisplay(endYearMonth)}
        </p>
      </div>
      
      {renderContent()}
    </div>
  );
};

export default VendasChartMonthly;
