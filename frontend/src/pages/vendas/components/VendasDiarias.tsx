import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import VendasTable from './VendasTable';
import { getDailySalesReport, getCurrentDailySales } from '../services/vendasApi';

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
 * Componente da aba de vendas diárias
 * Gerencia filtros de data e exibe relatórios de vendas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasDiarias: React.FC<VendasDiariasProps> = ({ className = '' }) => {
  const { isMobile } = useLayout();
  const today = new Date().toISOString().split('T')[0];
  
  const [startDate, setStartDate] = useState<string>(today);
  const [endDate, setEndDate] = useState<string>(today);
  const [vendasState, setVendasState] = useState<VendasState>({
    data: [],
    loading: false,
    error: null,
  });

  /**
   * Auto-carrega dados ao montar o componente
   */
  useEffect(() => {
    handleSearch();
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
    } catch (error) {
      setVendasState({
        data: [],
        loading: false,
        error: error instanceof Error ? error.message : 'Erro ao buscar dados de vendas',
      });
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
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 mb-4 ${
      isMobile ? 'p-3 mx-2' : 'p-4 mx-4'
    }`}>
      <h2 className={`font-semibold text-smart-gray-800 mb-3 ${
        isMobile ? 'text-xs' : 'text-sm'
      }`}>
        Filtros de Período
      </h2>
      
      <form onSubmit={handleSubmit} className="space-y-3">
        <div className={`grid gap-3 ${
          isMobile ? 'grid-cols-1' : 'grid-cols-1 md:grid-cols-3'
        }`}>
          <div>
            <label htmlFor="startDate" className="block text-xs font-medium text-smart-gray-700 mb-1">
              Data Início
            </label>
            <input
              id="startDate"
              type="date"
              value={startDate}
              onChange={handleStartDateChange}
              className="w-full px-2 py-1 text-xs border border-smart-gray-300 rounded focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 transition-colors duration-200"
              required
            />
          </div>
          
          <div>
            <label htmlFor="endDate" className="block text-xs font-medium text-smart-gray-700 mb-1">
              Data Fim
            </label>
            <input
              id="endDate"
              type="date"
              value={endDate}
              onChange={handleEndDateChange}
              className="w-full px-2 py-1 text-xs border border-smart-gray-300 rounded focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 transition-colors duration-200"
              required
            />
          </div>
          
          <div className={`flex ${isMobile ? 'flex-col items-center mt-3 space-y-2' : 'items-end space-x-2'}`}>
            <button
              type="submit"
              disabled={vendasState.loading}
              className={`bg-smart-red-600 text-white font-medium rounded hover:bg-smart-red-700 focus:ring-1 focus:ring-smart-red-500 focus:ring-offset-1 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 ${
                isMobile ? 'px-6 py-2 text-sm w-full' : 'px-4 py-1 text-xs'
              }`}
            >
              {vendasState.loading ? (
                <span className="flex items-center justify-center">
                  <svg className="animate-spin -ml-1 mr-2 h-3 w-3 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Carregando...
                </span>
              ) : (
                'Filtrar'
              )}
            </button>
            
            {/* Indicador de tipo de dados */}
            {shouldUseCurrentDailySales(startDate, endDate) && (
              <div className={`flex items-center ${
                isMobile ? 'justify-center text-xs' : 'text-xs'
              }`}>
                <div className="flex items-center px-2 py-1 bg-green-100 text-green-800 rounded-md border border-green-200">
                  <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"></path>
                  </svg>
                  <span className="font-medium">Tempo Real</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </form>
    </div>
  );

  return (
    <div className={`vendas-diarias ${className}`}>
      {renderFilters()}
      <VendasTable 
        data={vendasState.data} 
        loading={vendasState.loading} 
        error={vendasState.error} 
      />
    </div>
  );
};

export default VendasDiarias;
