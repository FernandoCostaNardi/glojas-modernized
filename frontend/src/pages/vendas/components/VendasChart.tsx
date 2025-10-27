import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { getChartDataWithMetrics } from '../services/vendasApi';
import { ChartState, ChartMetrics } from '@/types/sales';

/**
 * Interface para as propriedades do VendasChart
 */
interface VendasChartProps {
  readonly startDate: string;
  readonly endDate: string;
  readonly selectedStoreCode?: string | null;
  readonly className?: string;
  readonly onMetricsChange?: (metrics: ChartMetrics) => void;
}

/**
 * Componente de gráfico de vendas diárias usando Recharts
 * Exibe dados de vendas em formato de linha com tooltip interativo
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasChart: React.FC<VendasChartProps> = ({
  startDate,
  endDate,
  selectedStoreCode,
  className = '',
  onMetricsChange
}) => {
  const { isMobile } = useLayout();
  const [chartState, setChartState] = useState<ChartState>({
    data: [],
    loading: true,
    error: null
  });

  /**
   * Carrega dados do gráfico quando as props mudam
   */
  useEffect(() => {
    console.log('🔄 VendasChart useEffect disparado:', { 
      startDate, 
      endDate, 
      selectedStoreCode 
    });

    const loadChartData = async (): Promise<void> => {
      if (!startDate || !endDate) {
        return;
      }

      try {
        setChartState(prev => ({ ...prev, loading: true, error: null }));
        
        console.log(`Carregando dados do gráfico: ${startDate} até ${endDate}${selectedStoreCode ? ` para loja ${selectedStoreCode}` : ' (todas as lojas)'}`);
        
        const response = await getChartDataWithMetrics(startDate, endDate, selectedStoreCode || undefined);
        
        // Formatar dados para o Recharts
        const formattedData = response.chartData.map(item => ({
          date: item.date,
          total: Number(item.total),
          // Formatar data para exibição
          displayDate: new Date(item.date).toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit'
          })
        }));
        
        setChartState({
          data: formattedData,
          loading: false,
          error: null
        });
        
        // Notificar métricas para o componente pai
        if (onMetricsChange) {
          onMetricsChange(response.metrics);
        }
        
        console.log(`Dados do gráfico com métricas carregados: ${formattedData.length} pontos`);
        
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Erro ao carregar dados do gráfico';
        setChartState({
          data: [],
          loading: false,
          error: errorMessage
        });
        console.error('❌ Erro ao carregar dados do gráfico:', error);
      }
    };

    loadChartData();
  }, [startDate, endDate, selectedStoreCode]);

  /**
   * Formata um valor monetário para exibição compacta nos eixos
   * @param value - Valor numérico
   * @returns String formatada de forma compacta (ex: R$ 1M, R$ 500K)
   */
  const formatCompactCurrency = (value: number): string => {
    if (value >= 1000000) {
      return `R$ ${(value / 1000000).toFixed(1)}M`;
    } else if (value >= 1000) {
      return `R$ ${(value / 1000).toFixed(0)}K`;
    } else {
      return `R$ ${value.toFixed(0)}`;
    }
  };

  /**
   * Formata um valor monetário para exibição completa no tooltip
   * @param value - Valor numérico
   * @returns String formatada em reais com formatação completa
   */
  const formatFullCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2
    }).format(value);
  };

  /**
   * Renderiza o estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className={`flex items-center justify-center ${isMobile ? 'h-64' : 'h-80'}`}>
      <div className="text-center">
        <svg className="animate-spin h-8 w-8 text-smart-red-500 mx-auto mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p className={`text-smart-gray-600 ${isMobile ? 'text-xs' : 'text-sm'}`}>
          Carregando dados do gráfico...
        </p>
      </div>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className={`flex items-center justify-center ${isMobile ? 'h-64' : 'h-80'}`}>
      <div className="text-center">
        <svg className="h-12 w-12 text-red-500 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"></path>
        </svg>
        <p className={`text-red-600 ${isMobile ? 'text-xs' : 'text-sm'}`}>
          Erro ao carregar dados
        </p>
        <p className={`text-smart-gray-500 ${isMobile ? 'text-xs' : 'text-xs'} mt-1`}>
          {chartState.error}
        </p>
      </div>
    </div>
  );

  /**
   * Renderiza o gráfico vazio quando não há dados
   */
  const renderEmpty = (): React.ReactNode => (
    <div className={`flex items-center justify-center ${isMobile ? 'h-64' : 'h-80'}`}>
      <div className="text-center">
        <svg className="h-12 w-12 text-smart-gray-400 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zm0 4a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1V8zm8 0a1 1 0 011-1h4a1 1 0 011 1v2a1 1 0 01-1 1h-4a1 1 0 01-1-1V8zm0 4a1 1 0 011-1h4a1 1 0 011 1v2a1 1 0 01-1 1h-4a1 1 0 01-1-1v-2z" clipRule="evenodd"></path>
        </svg>
        <p className={`text-smart-gray-500 ${isMobile ? 'text-xs' : 'text-sm'}`}>
          Nenhum dado disponível para o período selecionado
        </p>
      </div>
    </div>
  );

  /**
   * Tooltip customizado para o gráfico
   */
  const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white px-4 py-3 rounded-lg shadow-xl border border-smart-gray-200">
          <p className="text-sm font-semibold text-smart-gray-900">{label}</p>
          <p className="text-lg font-bold text-smart-blue-600">
            {formatFullCurrency(payload[0].value)}
          </p>
        </div>
      );
    }
    return null;
  };

  /**
   * Renderiza o gráfico com dados
   */
  const renderChart = (): React.ReactNode => (
    <div className={`${isMobile ? 'h-64' : 'h-80'}`}>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={chartState.data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <defs>
            <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="#3b82f6" stopOpacity={0.1}/>
            </linearGradient>
          </defs>
          <CartesianGrid 
            strokeDasharray="2 4" 
            stroke="#f3f4f6" 
            strokeOpacity={0.6}
          />
          <XAxis 
            dataKey="displayDate" 
            stroke="#6B7280"
            fontSize={isMobile ? 10 : 12}
            tickLine={false}
            axisLine={false}
            tick={{ fill: '#6B7280', fontSize: isMobile ? 10 : 12 }}
          />
          <YAxis 
            stroke="#6B7280"
            fontSize={isMobile ? 10 : 12}
            tickLine={false}
            axisLine={false}
            tickFormatter={formatCompactCurrency}
            tick={{ fill: '#6B7280', fontSize: isMobile ? 10 : 12 }}
          />
          <Tooltip content={<CustomTooltip />} />
          <Area
            type="monotone"
            dataKey="total"
            stroke="#3b82f6"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorTotal)"
            name="Vendas Diárias"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );

  return (
    <div className={`vendas-chart bg-white rounded-lg shadow-lg border border-smart-gray-100 p-6 ${className}`}>
      <div className="flex justify-between items-center mb-4">
        <div>
          <h3 className={`font-semibold text-smart-gray-800 ${
            isMobile ? 'text-sm' : 'text-lg'
          }`}>
            📊 Vendas Diárias
          </h3>
          <p className={`text-smart-gray-600 ${
            isMobile ? 'text-xs' : 'text-sm'
          }`}>
            {selectedStoreCode 
              ? `Loja: ${selectedStoreCode}` 
              : 'Todas as lojas'
            } • {startDate} até {endDate}
          </p>
        </div>
        <span className="text-xs text-smart-gray-500 bg-smart-gray-100 px-3 py-1 rounded-full">
          meta diária de vendas
        </span>
      </div>
      
      {chartState.loading ? renderLoading() : 
       chartState.error ? renderError() : 
       chartState.data.length === 0 ? renderEmpty() : 
       renderChart()}
    </div>
  );
};

export default VendasChart;
