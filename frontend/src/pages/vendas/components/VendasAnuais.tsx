import React, { useState, useEffect, useCallback } from 'react';
import VendasTableYearly from './VendasTableYearly';
import VendasChartYearly from './VendasChartYearly';
import StoreSelector from './StoreSelector';
import SalesMetricsCards from './SalesMetricsCards';
import { getYearlySalesReport } from '../services/vendasApi';
import { ChartMetrics } from '@/types/sales';

/**
 * Interface para as propriedades do VendasAnuais
 */
interface VendasAnuaisProps {
  readonly className?: string;
}

/**
 * Interface para os dados de vendas anuais
 */
export interface YearlySalesData {
  readonly storeName: string;
  readonly total: number;
  readonly percentageOfTotal: number;
}

/**
 * Interface para o estado de loading e erro
 */
interface VendasState {
  readonly data: YearlySalesData[];
  readonly loading: boolean;
  readonly error: string | null;
}

/**
 * Interface para métricas do gráfico
 */
interface ChartMetricsState {
  readonly bestYear: string;
  readonly bestYearValue: number;
  readonly worstYear: string;
  readonly worstYearValue: number;
  readonly totalActiveStores: number;
}

/**
 * Componente da aba de vendas anuais
 * Gerencia filtros de ano e exibe relatórios de vendas anuais
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasAnuais: React.FC<VendasAnuaisProps> = ({ className = '' }) => {
  const currentYear = new Date().getFullYear();
  const lastFiveYearsStart = currentYear - 4;
  const lastFiveYearsEnd = currentYear;

  // Filtros iniciam com o ano atual selecionado
  const [startYear, setStartYear] = useState<number>(currentYear);
  const [endYear, setEndYear] = useState<number>(currentYear);
  const [vendasState, setVendasState] = useState<VendasState>({
    data: [],
    loading: false,
    error: null,
  });

  // Estados para o gráfico
  const [selectedStoreForChart, setSelectedStoreForChart] = useState<string | null>(null);
  // Gráfico sempre inicia com os últimos 5 anos
  const [chartStartYear, setChartStartYear] = useState<number>(lastFiveYearsStart);
  const [chartEndYear, setChartEndYear] = useState<number>(lastFiveYearsEnd);
  const [isFirstLoad, setIsFirstLoad] = useState(true);

  // Estado para métricas do gráfico vindas da API
  const [chartMetrics, setChartMetrics] = useState<ChartMetricsState>({
    bestYear: '',
    bestYearValue: 0,
    worstYear: '',
    worstYearValue: 0,
    totalActiveStores: 0
  });

  /**
   * Callback para receber métricas do gráfico vindas da API
   * @param metrics - Métricas calculadas no backend
   */
  const handleChartMetricsChange = useCallback((metrics: ChartMetrics): void => {
    setChartMetrics({
      bestYear: metrics.bestDay, // Mapeando bestDay para bestYear
      bestYearValue: metrics.bestDayValue,
      worstYear: metrics.worstDay, // Mapeando worstDay para worstYear
      worstYearValue: metrics.worstDayValue,
      totalActiveStores: metrics.totalActiveStores
    });
  }, []); // Array de dependências vazio para evitar recriação desnecessária

  /**
   * Retorna métricas formatadas para os cards de vendas
   * @returns Dados das métricas vindas da API
   */
  const getMetricsForCards = useCallback(() => {
    return {
      totalActiveStores: chartMetrics.totalActiveStores,
      bestDay: { // Renomeado para bestDay para compatibilidade com SalesMetricsCards
        date: chartMetrics.bestYear,
        value: chartMetrics.bestYearValue
      },
      worstDay: { // Renomeado para worstDay para compatibilidade com SalesMetricsCards
        date: chartMetrics.worstYear,
        value: chartMetrics.worstYearValue
      }
    };
  }, [chartMetrics]); // Dependência apenas do chartMetrics

  /**
   * Auto-carrega dados ao montar o componente
   */
  useEffect(() => {
    handleSearch();
    setIsFirstLoad(false);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  /**
   * Verifica se o ano é o ano atual
   * @param year - Ano a verificar
   * @returns true se for o ano atual
   */
  const isCurrentYear = (year: number): boolean => {
    return year === currentYear;
  };

  /**
   * Calcula a diferença em anos entre duas datas
   * @param start - Ano de início
   * @param end - Ano de fim
   * @returns Número de anos de diferença
   */
  const calculateYearsDifference = (start: number, end: number): number => {
    return end - start + 1;
  };

  /**
   * Valida se os anos são válidos
   * @param start - Ano de início
   * @param end - Ano de fim
   * @returns true se as datas são válidas
   */
  const validateYears = (start: number, end: number): boolean => {
    if (!start || !end) {
      setVendasState(prev => ({ ...prev, error: 'Ambos os anos são obrigatórios' }));
      return false;
    }

    if (start > end) {
      setVendasState(prev => ({ ...prev, error: 'Ano de início deve ser menor ou igual ao ano de fim' }));
      return false;
    }

    if (start < 2000 || start > currentYear + 1) {
      setVendasState(prev => ({ ...prev, error: 'Ano de início deve estar entre 2000 e ' + (currentYear + 1) }));
      return false;
    }

    if (end < 2000 || end > currentYear + 1) {
      setVendasState(prev => ({ ...prev, error: 'Ano de fim deve estar entre 2000 e ' + (currentYear + 1) }));
      return false;
    }

    return true;
  };

  /**
   * Executa a busca de dados de vendas anuais
   * Atualiza o gráfico conforme a lógica de sincronização
   */
  const handleSearch = async (): Promise<void> => {
    if (!validateYears(startYear, endYear)) {
      return;
    }

    setVendasState(prev => ({ ...prev, loading: true, error: null }));

    try {
      const data = await getYearlySalesReport(startYear, endYear);

      setVendasState({
        data,
        loading: false,
        error: null,
      });

      // Lógica de sincronização do gráfico (apenas após primeira carga)
      if (!isFirstLoad) {
        updateChartBasedOnYearRange(startYear, endYear);
      }

    } catch (error) {
      setVendasState({
        data: [],
        loading: false,
        error: error instanceof Error ? error.message : 'Erro ao buscar dados de vendas anuais',
      });
    }
  };

  /**
   * Atualiza o gráfico baseado no range de anos selecionado
   * Se for o ano atual: mantém gráfico com últimos 5 anos
   * Se <= 5 anos (e não for ano atual): atualiza gráfico com range selecionado
   * Se > 5 anos: mantém gráfico com últimos 5 anos
   *
   * @param start - Ano de início
   * @param end - Ano de fim
   */
  const updateChartBasedOnYearRange = (start: number, end: number): void => {
    const yearsDifference = calculateYearsDifference(start, end);

    console.log(`Diferença de anos: ${yearsDifference}`);

    // Se for o ano atual (mesmo ano selecionado), sempre manter os últimos 5 anos no gráfico
    if (start === end && isCurrentYear(start)) {
      console.log('Pesquisa no ano atual - mantendo gráfico com últimos 5 anos');
      setChartStartYear(lastFiveYearsStart);
      setChartEndYear(lastFiveYearsEnd);
      return;
    }

    if (yearsDifference <= 5) {
      // Atualizar gráfico com range selecionado (apenas se não for ano atual)
      console.log('Atualizando gráfico com range selecionado (≤ 5 anos)');
      setChartStartYear(start);
      setChartEndYear(end);
    } else {
      // Manter gráfico com últimos 5 anos
      console.log('Mantendo gráfico com últimos 5 anos (> 5 anos)');
      setChartStartYear(lastFiveYearsStart);
      setChartEndYear(lastFiveYearsEnd);
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
   * Manipula mudanças no ano de início
   * @param event - Evento de mudança do input
   */
  const handleStartYearChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setStartYear(parseInt(event.target.value));
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Manipula mudanças no ano de fim
   * @param event - Evento de mudança do input
   */
  const handleEndYearChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setEndYear(parseInt(event.target.value));
    setVendasState(prev => ({ ...prev, error: null }));
  };

  /**
   * Renderiza o formulário de filtros
   */
  const renderFilters = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-md px-3 py-3 md:px-6 md:py-4 mb-6 w-full max-w-full box-border">
      <form onSubmit={handleSubmit} className="w-full max-w-full box-border">
        <div className="flex flex-col md:flex-row md:flex-wrap md:items-end gap-3 md:gap-4 w-full max-w-full box-border">
          {/* Year Range */}
          <div className="flex-1 w-full md:min-w-[200px] max-w-full box-border">
            <label className="block text-xs font-medium text-smart-gray-700 mb-1">
              Período (Ano)
            </label>
            <div className="flex items-center space-x-2 w-full min-w-0">
              <input
                type="number"
                value={startYear}
                onChange={handleStartYearChange}
                min="2000"
                max={currentYear + 1}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
              <span className="text-smart-gray-500 flex-shrink-0">-</span>
              <input
                type="number"
                value={endYear}
                onChange={handleEndYearChange}
                min="2000"
                max={currentYear + 1}
                className="flex-1 min-w-0 px-3 py-2 text-sm border border-smart-gray-300 rounded-lg focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 transition-colors duration-200"
                required
              />
            </div>
            <p className="text-xs text-smart-gray-500 mt-1">
              Selecione o mesmo ano para dados de um ano específico
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
          <VendasChartYearly
            startYear={chartStartYear}
            endYear={chartEndYear}
            selectedStoreCode={selectedStoreForChart}
            onMetricsChange={handleChartMetricsChange}
          />
        </div>

        {/* Tabela */}
        <VendasTableYearly
          data={vendasState.data}
          loading={vendasState.loading}
          error={vendasState.error}
        />
      </div>
    );
  };

  return (
    <div className={`vendas-anuais bg-smart-gray-50 p-3 md:p-6 overflow-x-hidden w-full max-w-full box-border ${className}`}>
      <div className="w-full max-w-full box-border space-y-4 md:space-y-6">
        {/* Cards de Métricas */}
        <SalesMetricsCards
          data={getMetricsForCards()}
          bestLabel="Melhor Ano"
          worstLabel="Pior Ano"
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

export default VendasAnuais;
