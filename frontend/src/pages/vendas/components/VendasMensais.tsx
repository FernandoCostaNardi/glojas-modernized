import React, { useState, useEffect } from 'react';
import VendasTableMonthly from './VendasTableMonthly';
import VendasChartMonthly from './VendasChartMonthly';
import StoreSelector from './StoreSelector';
import SalesMetricsCards from './SalesMetricsCards';
import { getMonthlySalesReport } from '../services/vendasApi';
import { ChartMetrics } from '@/types/sales';

/**
 * Interface para as propriedades do VendasMensais
 */
interface VendasMensaisProps {
  readonly className?: string;
}

/**
 * Interface para os dados de vendas mensais
 */
export interface MonthlySalesData {
  readonly storeName: string;
  readonly total: number;
  readonly percentageOfTotal: number;
}

/**
 * Interface para o estado de loading e erro
 */
interface VendasState {
  readonly data: MonthlySalesData[];
  readonly loading: boolean;
  readonly error: string | null;
}

/**
 * Interface para métricas do gráfico
 */
interface ChartMetricsState {
  readonly bestMonth: string;
  readonly bestMonthValue: number;
  readonly worstMonth: string;
  readonly worstMonthValue: number;
  readonly totalActiveStores: number;
}

/**
 * Componente da aba de vendas mensais
 * Gerencia filtros de mês e exibe relatórios de vendas mensais
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasMensais: React.FC<VendasMensaisProps> = ({ className = '' }) => {
  const currentYear = new Date().getFullYear();
  const currentMonth = new Date().getMonth() + 1;
  const currentYearMonth = `${currentYear}-${currentMonth.toString().padStart(2, '0')}`;
  
  // Filtros iniciam com o mês atual selecionado
  const [startYearMonth, setStartYearMonth] = useState<string>(currentYearMonth);
  const [endYearMonth, setEndYearMonth] = useState<string>(currentYearMonth);
  const [vendasState, setVendasState] = useState<VendasState>({
    data: [],
    loading: false,
    error: null,
  });
  
  // Estados para o gráfico
  const [selectedStoreForChart, setSelectedStoreForChart] = useState<string | null>(null);
  const [chartStartYearMonth, setChartStartYearMonth] = useState<string>(`${currentYear}-01`);
  const [chartEndYearMonth, setChartEndYearMonth] = useState<string>(`${currentYear}-12`);
  const [isFirstLoad, setIsFirstLoad] = useState(true);
  
  // Estado para métricas do gráfico vindas da API
  const [chartMetrics, setChartMetrics] = useState<ChartMetricsState>({
    bestMonth: '',
    bestMonthValue: 0,
    worstMonth: '',
    worstMonthValue: 0,
    totalActiveStores: 0
  });

  /**
   * Callback para receber métricas do gráfico vindas da API
   * @param metrics - Métricas calculadas no backend
   */
  const handleChartMetricsChange = (metrics: ChartMetrics): void => {
    setChartMetrics({
      bestMonth: metrics.bestDay,
      bestMonthValue: metrics.bestDayValue,
      worstMonth: metrics.worstDay,
      worstMonthValue: metrics.worstDayValue,
      totalActiveStores: metrics.totalActiveStores
    });
  };

  /**
   * Retorna métricas formatadas para os cards de vendas
   * @returns Dados das métricas vindas da API
   */
  const getMetricsForCards = () => {
    return {
      totalActiveStores: chartMetrics.totalActiveStores,
      bestDay: {
        date: chartMetrics.bestMonth,
        value: chartMetrics.bestMonthValue
      },
      worstDay: {
        date: chartMetrics.worstMonth,
        value: chartMetrics.worstMonthValue
      }
    };
  };

  /**
   * Auto-carrega dados ao montar o componente
   */
  useEffect(() => {
    handleSearch();
    setIsFirstLoad(false);
  }, []);

  /**
   * Verifica se um ano/mês é o mês atual
   * @param yearMonth - Ano/mês no formato YYYY-MM
   * @returns true se o ano/mês é o atual
   */
  const isCurrentMonth = (yearMonth: string): boolean => {
    return yearMonth === currentYearMonth;
  };

  /**
   * Calcula a diferença em meses entre dois anos/meses
   * @param start - Ano/mês de início
   * @param end - Ano/mês de fim
   * @returns Diferença em meses
   */
  const calculateMonthsDifference = (start: string, end: string): number => {
    const [startYear, startMonth] = start.split('-').map(Number);
    const [endYear, endMonth] = end.split('-').map(Number);
    
    const startYearNum = startYear || 0;
    const startMonthNum = startMonth || 0;
    const endYearNum = endYear || 0;
    const endMonthNum = endMonth || 0;
    
    return (endYearNum - startYearNum) * 12 + (endMonthNum - startMonthNum) + 1;
  };

  /**
   * Valida se os anos/meses são válidos
   * @param start - Ano/mês de início
   * @param end - Ano/mês de fim
   * @returns true se os anos/meses são válidos
   */
  const validateYearMonths = (start: string, end: string): boolean => {
    if (!start || !end) {
      setVendasState(prev => ({ ...prev, error: 'Ambos os anos/meses são obrigatórios' }));
      return false;
    }

    if (end < start) {
      setVendasState(prev => ({ ...prev, error: 'Ano/mês de fim deve ser maior ou igual ao ano/mês de início' }));
      return false;
    }

    // Validar formato YYYY-MM
    const yearMonthRegex = /^\d{4}-\d{2}$/;
    if (!yearMonthRegex.test(start) || !yearMonthRegex.test(end)) {
      setVendasState(prev => ({ ...prev, error: 'Formato deve ser YYYY-MM' }));
      return false;
    }

    return true;
  };

  /**
   * Executa a busca de dados de vendas mensais
   * Atualiza o gráfico conforme a lógica de sincronização
   */
  const handleSearch = async (): Promise<void> => {
    if (!validateYearMonths(startYearMonth, endYearMonth)) {
      return;
    }

    setVendasState(prev => ({ ...prev, loading: true, error: null }));

    try {
      const data = await getMonthlySalesReport(startYearMonth, endYearMonth);

      setVendasState({
        data,
        loading: false,
        error: null,
      });

      // Lógica de sincronização do gráfico (apenas após primeira carga)
      if (!isFirstLoad) {
        updateChartBasedOnYearMonthRange(startYearMonth, endYearMonth);
      }

    } catch (error) {
      setVendasState({
        data: [],
        loading: false,
        error: error instanceof Error ? error.message : 'Erro ao buscar dados de vendas mensais',
      });
    }
  };

  /**
   * Atualiza o gráfico baseado no range de anos/meses selecionado
   * Se for o mês atual: mantém gráfico com vendas do ano atual
   * Se <= 12 meses (e não for mês atual): atualiza gráfico com range selecionado
   * Se > 12 meses: mantém gráfico com vendas do ano atual
   * 
   * @param start - Ano/mês de início
   * @param end - Ano/mês de fim
   */
  const updateChartBasedOnYearMonthRange = (start: string, end: string): void => {
    const monthsDifference = calculateMonthsDifference(start, end);

    console.log(`Diferença de meses: ${monthsDifference}`);

    // Se for o mês atual (mesmo mês selecionado), sempre manter o ano inteiro no gráfico
    if (start === end && isCurrentMonth(start)) {
      console.log('Pesquisa no mês atual - mantendo gráfico com vendas do ano atual');
      // Não alterar chartStartYearMonth e chartEndYearMonth - manter ano inteiro
      return;
    }

    if (monthsDifference <= 12) {
      // Atualizar gráfico com range selecionado (apenas se não for mês atual)
      console.log('Atualizando gráfico com range selecionado (≤ 12 meses)');
      setChartStartYearMonth(start);
      setChartEndYearMonth(end);
    } else {
      // Manter gráfico com vendas do ano atual
      console.log('Mantendo gráfico com vendas do ano atual (> 12 meses)');
      // Não alterar chartStartYearMonth e chartEndYearMonth
    }
  };

  /**
   * Manipula o envio do formulário
   * @param event - Evento de submit do formulário
   */
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault();
    handleSearch();
  };

  /**
   * Manipula mudanças no ano/mês de início
   * @param event - Evento de mudança do input
   */
  const handleStartYearMonthChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setStartYearMonth(event.target.value);
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Manipula mudanças no ano/mês de fim
   * @param event - Evento de mudança do input
   */
  const handleEndYearMonthChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setEndYearMonth(event.target.value);
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Renderiza o formulário de filtros
   */
  const renderFilters = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-md px-3 py-3 md:px-6 md:py-4 mb-6 w-full max-w-full box-border">
      <form onSubmit={handleSubmit} className="w-full max-w-full box-border">
        <div className="flex flex-col md:flex-row md:flex-wrap md:items-end gap-3 md:gap-4 w-full max-w-full box-border">
          {/* Year/Month Range */}
          <div className="flex-1 w-full md:min-w-[200px] max-w-full box-border">
            <label className="block text-xs font-medium text-smart-gray-700 mb-1">
              Período (Mês)
            </label>
            <div className="flex items-center space-x-2 w-full min-w-0">
              <input
                type="month"
                value={startYearMonth}
                onChange={handleStartYearMonthChange}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
              <span className="text-smart-gray-500 flex-shrink-0">-</span>
              <input
                type="month"
                value={endYearMonth}
                onChange={handleEndYearMonthChange}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
            </div>
            <p className="text-xs text-smart-gray-500 mt-1">
              Selecione o mesmo mês para dados de um mês específico
            </p>
          </div>
          
          {/* Botão Filtrar */}
          <button
            type="submit"
            disabled={vendasState.loading}
            className="w-full md:w-auto bg-smart-blue-600 hover:bg-smart-blue-700 text-white px-6 py-2 rounded-lg text-sm font-medium transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
          >
            {vendasState.loading ? (
              <>
                <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>Carregando...</span>
              </>
            ) : (
              <>
                <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.207A1 1 0 013 6.5V4z" />
                </svg>
                <span>Filtrar</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );

  /**
   * Renderiza o layout principal do componente
   * Layout único: Cards de métricas, filtros, gráfico, tabela
   */
  const renderMainLayout = (): React.ReactNode => {
    return (
      <div className="space-y-4 md:space-y-6">
        {/* Gráfico */}
        <div className="space-y-3 md:space-y-4">
          <StoreSelector
            selectedStoreCode={selectedStoreForChart}
            onStoreChange={setSelectedStoreForChart}
            disabled={vendasState.loading}
          />
          <VendasChartMonthly
            startYearMonth={chartStartYearMonth}
            endYearMonth={chartEndYearMonth}
            selectedStoreCode={selectedStoreForChart}
            onMetricsChange={handleChartMetricsChange}
          />
        </div>
        
        {/* Tabela */}
        <VendasTableMonthly 
          data={vendasState.data} 
          loading={vendasState.loading} 
          error={vendasState.error} 
        />
      </div>
    );
  };

  return (
    <div className={`vendas-mensais bg-smart-gray-50 p-3 md:p-6 overflow-x-hidden w-full max-w-full box-border ${className}`}>
      <div className="w-full max-w-full box-border space-y-4 md:space-y-6">
        {/* Cards de Métricas */}
        <SalesMetricsCards 
          data={getMetricsForCards()} 
          bestLabel="Melhor Mês"
          worstLabel="Pior Mês"
        />
        
        {/* Filtros Modernizados */}
        {renderFilters()}
        
        {/* Layout Principal */}
        {renderMainLayout()}
      </div>
      
      {/* Footer */}
      <footer className="bg-smart-gray-800 text-white py-6 md:py-8 mt-8 md:mt-12">
        <div className="max-w-7xl mx-auto px-3 md:px-6">
          <div className="text-center">
            <p className="text-smart-gray-300 mb-2 text-sm md:text-base">
              © 2025 Smart Eletron. Todos os direitos reservados.
            </p>
            <p className="text-xs md:text-sm text-smart-gray-400">
              Sistema de Gestão de Vendas - Desenvolvido com ❤️
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default VendasMensais;
