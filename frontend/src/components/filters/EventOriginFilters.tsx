import React, { useState, useEffect } from 'react';
import { EventOriginFilters as EventOriginFiltersType, EventSource, EVENT_SOURCE_OPTIONS } from '@/types';

/**
 * Props do componente de filtros
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface EventOriginFiltersProps {
  readonly filters: EventOriginFiltersType;
  readonly onFiltersChange: (filters: EventOriginFiltersType) => void;
  readonly onClearFilters: () => void;
  readonly isLoading?: boolean;
  readonly totalElements?: number;
}

/**
 * Componente de filtros para códigos de origem
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const EventOriginFilters: React.FC<EventOriginFiltersProps> = ({
  filters,
  onFiltersChange,
  onClearFilters,
  isLoading = false,
  totalElements = 0
}) => {
  // Estados locais para os campos de filtro
  const [localEventSource, setLocalEventSource] = useState<EventSource | ''>('');
  const [localSourceCode, setLocalSourceCode] = useState<string>('');

  /**
   * Sincroniza estados locais com props quando filtros mudam
   */
  useEffect(() => {
    setLocalEventSource(filters.eventSource || '');
    setLocalSourceCode(filters.sourceCode || '');
  }, [filters]);

  /**
   * Aplica os filtros atuais
   */
  const applyFilters = (): void => {
    const newFilters: EventOriginFiltersType = {
      ...(localEventSource && { eventSource: localEventSource }),
      ...(localSourceCode.trim() && { sourceCode: localSourceCode.trim() })
    };
    
    onFiltersChange(newFilters);
  };

  /**
   * Limpa todos os filtros
   */
  const handleClearFilters = (): void => {
    setLocalEventSource('');
    setLocalSourceCode('');
    onClearFilters();
  };

  /**
   * Manipula mudança no campo EventSource
   */
  const handleEventSourceChange = (value: string): void => {
    setLocalEventSource(value as EventSource | '');
  };

  /**
   * Manipula mudança no campo SourceCode
   */
  const handleSourceCodeChange = (value: string): void => {
    setLocalSourceCode(value);
  };

  /**
   * Manipula submissão do formulário
   */
  const handleSubmit = (e: React.FormEvent): void => {
    e.preventDefault();
    applyFilters();
  };

  /**
   * Verifica se há filtros ativos
   */
  const hasActiveFilters = (): boolean => {
    return !!(filters.eventSource || filters.sourceCode);
  };

  return (
    <div className="bg-white p-4 rounded-lg shadow-sm border border-smart-gray-200">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-base font-medium text-smart-gray-900">
          Filtros de Busca
        </h3>
        {hasActiveFilters() && (
          <button
            type="button"
            onClick={handleClearFilters}
            disabled={isLoading}
            className="text-sm text-smart-blue-600 hover:text-smart-blue-800 transition-colors duration-200 disabled:opacity-50"
          >
            Limpar filtros
          </button>
        )}
      </div>

      <form onSubmit={handleSubmit} className="space-y-3">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Filtro por EventSource */}
          <div>
            <label htmlFor="eventSource" className="block text-sm font-medium text-smart-gray-700 mb-1">
              Fonte do Evento
            </label>
            <select
              id="eventSource"
              value={localEventSource}
              onChange={(e) => handleEventSourceChange(e.target.value)}
              disabled={isLoading}
              className="w-full px-3 py-2 border border-smart-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-50 disabled:text-smart-gray-500"
            >
              <option value="">Todas as fontes</option>
              {EVENT_SOURCE_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          {/* Filtro por SourceCode */}
          <div>
            <label htmlFor="sourceCode" className="block text-sm font-medium text-smart-gray-700 mb-1">
              Código da Fonte
            </label>
            <input
              type="text"
              id="sourceCode"
              value={localSourceCode}
              onChange={(e) => handleSourceCodeChange(e.target.value)}
              disabled={isLoading}
              placeholder="Digite parte do código..."
              className="w-full px-3 py-2 border border-smart-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-50 disabled:text-smart-gray-500"
            />
          </div>
        </div>

        {/* Botões de ação e contagem */}
        <div className="flex items-center justify-between pt-3 border-t border-smart-gray-200">
          {/* Contagem de resultados */}
          <div className="text-sm text-smart-gray-600">
            {totalElements} {totalElements === 1 ? 'código encontrado' : 'códigos encontrados'}
          </div>
          
          {/* Botões de ação */}
          <div className="flex items-center space-x-3">
            <button
              type="button"
              onClick={handleClearFilters}
              disabled={isLoading || !hasActiveFilters()}
              className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md shadow-sm hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              Limpar
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-4 py-2 text-sm font-medium text-white bg-smart-blue-600 border border-transparent rounded-md shadow-sm hover:bg-smart-blue-700 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              {isLoading ? (
                <div className="flex items-center space-x-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  <span>Buscando...</span>
                </div>
              ) : (
                'Aplicar Filtros'
              )}
            </button>
          </div>
        </div>
      </form>

      {/* Indicador de filtros ativos */}
      {hasActiveFilters() && (
        <div className="mt-3 p-2 bg-smart-blue-50 border border-smart-blue-200 rounded-md">
          <div className="flex items-center">
            <svg className="h-5 w-5 text-smart-blue-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.207A1 1 0 013 6.5V4z" />
            </svg>
            <div className="text-sm text-smart-blue-800">
              <span className="font-medium">Filtros ativos:</span>
              <div className="mt-1 space-y-1">
                {filters.eventSource && (
                  <div>
                    <span className="font-medium">Fonte:</span> {EVENT_SOURCE_OPTIONS.find(opt => opt.value === filters.eventSource)?.label}
                  </div>
                )}
                {filters.sourceCode && (
                  <div>
                    <span className="font-medium">Código:</span> {filters.sourceCode}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
