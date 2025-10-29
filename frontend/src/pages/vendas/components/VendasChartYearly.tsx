import React, { useEffect, useState, useCallback } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import { getYearlyChartDataWithMetrics } from '../services/vendasApi';
import { ChartMetrics } from '@/types/sales';

// Registrar componentes do Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

/**
 * Interface para as propriedades do VendasChartYearly
 */
interface VendasChartYearlyProps {
  readonly startYear: number;
  readonly endYear: number;
  readonly selectedStoreCode: string | null;
  readonly onMetricsChange: (metrics: ChartMetrics) => void;
}

/**
 * Componente de gráfico para exibir dados de vendas anuais
 * Gráfico de barras com anos no eixo X e valores no eixo Y
 * Suporte a filtro de loja e cálculo automático de métricas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasChartYearly: React.FC<VendasChartYearlyProps> = ({
  startYear,
  endYear,
  selectedStoreCode,
  onMetricsChange
}) => {
  const [chartData, setChartData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Busca dados do gráfico quando os parâmetros mudam
   */
  useEffect(() => {
    const fetchChartData = async (): Promise<void> => {
      try {
        setLoading(true);
        setError(null);

        console.log(`Buscando dados do gráfico anual: ${startYear} até ${endYear}${selectedStoreCode ? ` para loja ${selectedStoreCode}` : ' (todas as lojas)'}`);

        const response = await getYearlyChartDataWithMetrics(startYear, endYear, selectedStoreCode || undefined);

        console.log('Dados do gráfico anual recebidos:', response);

        // Processar dados para o Chart.js
        const processedData = processChartData(response.chartData);
        setChartData(processedData);

        // Notificar métricas para o componente pai
        onMetricsChange(response.metrics);

      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Erro ao buscar dados do gráfico';
        setError(errorMessage);
        console.error('Erro ao buscar dados do gráfico anual:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchChartData();
  }, [startYear, endYear, selectedStoreCode, onMetricsChange]);

  /**
   * Processa dados da API para o formato do Chart.js
   */
  const processChartData = useCallback((apiData: any[]): any => {
    if (!apiData || apiData.length === 0) {
      return {
        labels: [],
        datasets: []
      };
    }

    // Ordenar dados por data
    const sortedData = [...apiData].sort((a, b) => {
      const dateA = new Date(a.date);
      const dateB = new Date(b.date);
      return dateA.getTime() - dateB.getTime();
    });

    const labels = sortedData.map(item => {
      // Extrair ano da data
      const year = new Date(item.date).getFullYear();
      return year.toString();
    });

    const values = sortedData.map(item => item.total);

    return {
      labels,
      datasets: [
        {
          label: 'Vendas Anuais',
          data: values,
          backgroundColor: 'rgba(59, 130, 246, 0.6)',
          borderColor: 'rgba(59, 130, 246, 1)',
          borderWidth: 1,
          borderRadius: 4,
          borderSkipped: false,
        },
      ],
    };
  }, []); // Função pura, sem dependências

  /**
   * Opções de configuração do gráfico
   */
  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: `Vendas Anuais ${startYear} - ${endYear}`,
        font: {
          size: 16,
          weight: 'bold' as const,
        },
      },
      tooltip: {
        callbacks: {
          label: function(context: any) {
            const value = context.parsed.y;
            return `Ano: ${context.label}\nValor: R$ ${value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
          },
        },
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: 'Ano',
        },
        grid: {
          display: false,
        },
      },
      y: {
        title: {
          display: true,
          text: 'Valor (R$)',
        },
        beginAtZero: true,
        ticks: {
          callback: function(value: any) {
            return 'R$ ' + value.toLocaleString('pt-BR');
          },
        },
      },
    },
  };

  /**
   * Renderiza estado de loading
   */
  const renderLoadingState = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="flex items-center space-x-2">
        <svg className="animate-spin h-8 w-8 text-smart-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span className="text-lg text-smart-gray-600">Carregando dados do gráfico...</span>
      </div>
    </div>
  );

  /**
   * Renderiza estado de erro
   */
  const renderErrorState = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="text-center">
        <svg className="w-16 h-16 text-red-500 mx-auto mb-4" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
        </svg>
        <h3 className="text-lg font-medium text-gray-900 mb-2">Erro ao carregar dados</h3>
        <p className="text-sm text-gray-500">{error}</p>
      </div>
    </div>
  );

  /**
   * Renderiza estado vazio
   */
  const renderEmptyState = (): React.ReactNode => (
    <div className="flex items-center justify-center h-96">
      <div className="text-center">
        <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-9a1 1 0 00-2 0v4a1 1 0 102 0V9zm3-1a1 1 0 10-2 0v5a1 1 0 102 0V8z" clipRule="evenodd" />
        </svg>
        <h3 className="text-lg font-medium text-gray-900 mb-2">Nenhum dado encontrado</h3>
        <p className="text-sm text-gray-500">Não há dados de vendas anuais para o período selecionado.</p>
      </div>
    </div>
  );

  if (loading) {
    return renderLoadingState();
  }

  if (error) {
    return renderErrorState();
  }

  if (!chartData || chartData.labels.length === 0) {
    return renderEmptyState();
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="h-96">
        <Bar data={chartData} options={chartOptions} />
      </div>
    </div>
  );
};

export default VendasChartYearly;
