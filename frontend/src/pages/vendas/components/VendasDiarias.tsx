import React, { useState, useEffect } from 'react';
import VendasTable from './VendasTable';
import VendasChart from './VendasChart';
import StoreSelector from './StoreSelector';
import SalesMetricsCards from './SalesMetricsCards';
import { getDailySalesReport, getCurrentDailySales } from '../services/vendasApi';
import { ChartMetrics } from '@/types/sales';

/**
 * Interface para as propriedades do VendasDiarias
 */
interface VendasDiariasProps {
  readonly className?: string;
}

/**
 * Interface para os dados de vendas diárias
 */
export interface DailySalesData {
  readonly storeName: string;
  readonly pdv: number;
  readonly danfe: number;
  readonly exchange: number;
  readonly total: number;
}

/**
 * Interface para o estado de loading e erro
 */
interface VendasState {
  readonly data: DailySalesData[];
  readonly loading: boolean;
  readonly error: string | null;
}

/**
 * Interface para métricas do gráfico
 */
interface ChartMetricsState {
  readonly bestDay: string;
  readonly bestDayValue: number;
  readonly worstDay: string;
  readonly worstDayValue: number;
  readonly totalActiveStores: number;
}

/**
 * Componente da aba de vendas diárias
 * Gerencia filtros de data e exibe relatórios de vendas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasDiarias: React.FC<VendasDiariasProps> = ({ className = '' }) => {
  /**
   * Obtém o primeiro dia do mês atual
   * @returns Data no formato YYYY-MM-DD
   */
  const getFirstDayOfMonth = (): string => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    return firstDay.toISOString().split('T')[0] || '';
  };

  const today = new Date().toISOString().split('T')[0] || '';
  
  const [startDate, setStartDate] = useState<string>(today);
  const [endDate, setEndDate] = useState<string>(today);
  const [vendasState, setVendasState] = useState<VendasState>({
    data: [],
    loading: false,
    error: null,
  });
  
  // Estados para o gráfico
  const [selectedStoreForChart, setSelectedStoreForChart] = useState<string | null>(null);
  const [chartStartDate, setChartStartDate] = useState<string>(getFirstDayOfMonth());
  const [chartEndDate, setChartEndDate] = useState<string>(today);
  const [isFirstLoad, setIsFirstLoad] = useState(true);
  
  // Estado para métricas do gráfico vindas da API
  const [chartMetrics, setChartMetrics] = useState<ChartMetricsState>({
    bestDay: '',
    bestDayValue: 0,
    worstDay: '',
    worstDayValue: 0,
    totalActiveStores: 0
  });

  /**
   * Callback para receber métricas do gráfico vindas da API
   * @param metrics - Métricas calculadas no backend
   */
  const handleChartMetricsChange = (metrics: ChartMetrics): void => {
    setChartMetrics({
      bestDay: metrics.bestDay,
      bestDayValue: metrics.bestDayValue,
      worstDay: metrics.worstDay,
      worstDayValue: metrics.worstDayValue,
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
        date: chartMetrics.bestDay,
        value: chartMetrics.bestDayValue
      },
      worstDay: {
        date: chartMetrics.worstDay,
        value: chartMetrics.worstDayValue
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
   * Verifica se uma data é hoje
   * @param date - Data no formato YYYY-MM-DD
   * @returns true se a data é hoje
   */
  const isToday = (date: string): boolean => {
    return date === today;
  };

  /**
   * Determina se deve usar a API de vendas do dia atual em tempo real
   * @param start - Data de início
   * @param end - Data de fim
   * @returns true se deve usar getCurrentDailySales()
   */
  const shouldUseCurrentDailySales = (start: string, end: string): boolean => {
    return isToday(start) && isToday(end);
  };

  /**
   * Valida se as datas são válidas e se não há chamadas cruzadas
   * @param start - Data de início
   * @param end - Data de fim
   * @returns true se as datas são válidas
   */
  const validateDates = (start: string, end: string): boolean => {
    if (!start || !end) {
      setVendasState(prev => ({ ...prev, error: 'Ambas as datas são obrigatórias' }));
      return false;
    }

    if (new Date(start) > new Date(end)) {
      setVendasState(prev => ({ ...prev, error: 'Data de início deve ser menor ou igual à data de fim' }));
      return false;
    }

    // Validação para evitar chamadas cruzadas
    // Não permitir que data fim seja hoje se data início não for hoje
    if (isToday(end) && !isToday(start)) {
      setVendasState(prev => ({ 
        ...prev, 
        error: 'Para consultar dados de hoje, a data de início também deve ser hoje. Isso garante dados sempre atualizados.' 
      }));
      return false;
    }

    // Não permitir que data início seja hoje se data fim não for hoje
    if (isToday(start) && !isToday(end)) {
      setVendasState(prev => ({ 
        ...prev, 
        error: 'Para consultar dados de hoje, a data de fim também deve ser hoje. Isso garante dados sempre atualizados.' 
      }));
      return false;
    }

    return true;
  };

  /**
   * Executa a busca de dados de vendas
   * Decide automaticamente qual API usar baseado nas datas selecionadas
   * Atualiza o gráfico conforme a lógica de sincronização
   */
  const handleSearch = async (): Promise<void> => {
    if (!validateDates(startDate, endDate)) {
      return;
    }

    setVendasState(prev => ({ ...prev, loading: true, error: null }));

    try {
      let data: DailySalesData[];

      // Decide qual API usar baseado nas datas
      if (shouldUseCurrentDailySales(startDate, endDate)) {
        // Usa API de tempo real para vendas do dia atual
        console.log('Usando API de vendas em tempo real para o dia atual');
        data = await getCurrentDailySales();
      } else {
        // Usa API histórica para outros períodos
        console.log('Usando API histórica para período:', startDate, 'até', endDate);
        data = await getDailySalesReport(startDate, endDate);
      }

      setVendasState({
        data,
        loading: false,
        error: null,
      });

      // Lógica de sincronização do gráfico (apenas após primeira carga)
      if (!isFirstLoad) {
        updateChartBasedOnDateRange(startDate, endDate);
      }

    } catch (error) {
      setVendasState({
        data: [],
        loading: false,
        error: error instanceof Error ? error.message : 'Erro ao buscar dados de vendas',
      });
    }
  };

  /**
   * Atualiza o gráfico baseado no range de datas selecionado
   * Se for o dia atual: mantém gráfico com vendas do mês atual
   * Se <= 31 dias (e não for dia atual): atualiza gráfico com range selecionado
   * Se > 31 dias: mantém gráfico com vendas do mês atual
   * 
   * @param start - Data de início
   * @param end - Data de fim
   */
  const updateChartBasedOnDateRange = (start: string, end: string): void => {
    const startDateObj = new Date(start);
    const endDateObj = new Date(end);
    const daysDifference = Math.ceil((endDateObj.getTime() - startDateObj.getTime()) / (1000 * 60 * 60 * 24)) + 1;

    console.log(`Diferença de dias: ${daysDifference}`);

    // Se for o dia atual, sempre manter o mês inteiro no gráfico
    if (isToday(start) && isToday(end)) {
      console.log('Pesquisa no dia atual - mantendo gráfico com vendas do mês atual');
      // Não alterar chartStartDate e chartEndDate - manter mês inteiro
      return;
    }

    if (daysDifference <= 31) {
      // Atualizar gráfico com range selecionado (apenas se não for dia atual)
      console.log('Atualizando gráfico com range selecionado (≤ 31 dias)');
      setChartStartDate(start);
      setChartEndDate(end);
    } else {
      // Manter gráfico com vendas do mês atual
      console.log('Mantendo gráfico com vendas do mês atual (> 31 dias)');
      // Não alterar chartStartDate e chartEndDate
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
   * Manipula mudanças na data de início
   * @param event - Evento de mudança do input
   */
  const handleStartDateChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setStartDate(event.target.value);
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Manipula mudanças na data de fim
   * @param event - Evento de mudança do input
   */
  const handleEndDateChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setEndDate(event.target.value);
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Renderiza o formulário de filtros
   */
  const renderFilters = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-md px-3 py-3 md:px-6 md:py-4 mb-6 w-full max-w-full box-border">
      <form onSubmit={handleSubmit} className="w-full max-w-full box-border">
        <div className="flex flex-col md:flex-row md:flex-wrap md:items-end gap-3 md:gap-4 w-full max-w-full box-border">
          {/* Date Range */}
          <div className="flex-1 w-full md:min-w-[200px] max-w-full box-border">
            <label className="block text-xs font-medium text-smart-gray-700 mb-1">
              Período
            </label>
            <div className="flex items-center space-x-2 w-full min-w-0">
              <input
                type="date"
                value={startDate}
                onChange={handleStartDateChange}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
              <span className="text-smart-gray-500 flex-shrink-0">-</span>
              <input
                type="date"
                value={endDate}
                onChange={handleEndDateChange}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
            </div>
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
          
          {/* Indicador de tipo de dados */}
          {shouldUseCurrentDailySales(startDate, endDate) && (
            <div className="flex items-center justify-center md:justify-start text-xs text-smart-blue-600 bg-smart-blue-50 px-3 py-2 rounded-lg">
              <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              Dados em tempo real
            </div>
          )}
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
          <VendasChart
            startDate={chartStartDate}
            endDate={chartEndDate}
            selectedStoreCode={selectedStoreForChart}
            onMetricsChange={handleChartMetricsChange}
          />
        </div>
        
        {/* Tabela */}
        <VendasTable 
          data={vendasState.data} 
          loading={vendasState.loading} 
          error={vendasState.error} 
        />
      </div>
    );
  };

  return (
    <div className={`vendas-diarias bg-smart-gray-50 p-3 md:p-6 overflow-x-hidden w-full max-w-full box-border ${className}`}>
      <div className="w-full max-w-full box-border space-y-4 md:space-y-6">
        {/* Cards de Métricas */}
        <SalesMetricsCards data={getMetricsForCards()} />
        
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

export default VendasDiarias;
