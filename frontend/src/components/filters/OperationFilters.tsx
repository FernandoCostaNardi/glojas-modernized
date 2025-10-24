import React from 'react';
import { OperationSource } from '@/types';
import type { OperationFilters } from '@/types';

/**
 * Props do componente de filtros de operações
 */
interface OperationFiltersProps {
  readonly filters: OperationFilters;
  readonly pendingFilters: OperationFilters;
  readonly onPendingFiltersChange: (filters: OperationFilters) => void;
  readonly onApplyFilters: () => void;
  readonly onClearFilters: () => void;
  readonly isLoading: boolean;
  readonly totalElements: number;
}

/**
 * Componente de filtros para operações do sistema
 * Permite filtrar por tipo de evento e código
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const OperationFilters: React.FC<OperationFiltersProps> = ({
  filters,
  pendingFilters,
  onPendingFiltersChange,
  onApplyFilters,
  onClearFilters,
  isLoading,
  totalElements
}) => {
  /**
   * Manipula mudança no filtro de fonte da operação
   */
  const handleOperationSourceChange = (event: React.ChangeEvent<HTMLSelectElement>): void => {
    const value = event.target.value;
    const operationSource = value === '' ? undefined : value as OperationSource;
    
    onPendingFiltersChange({
      ...pendingFilters,
      operationSource
    });
  };

  /**
   * Manipula mudança no filtro de código
   */
  const handleCodeChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    const value = event.target.value;
    
    onPendingFiltersChange({
      ...pendingFilters,
      code: value.trim() === '' ? undefined : value.trim()
    });
  };

  /**
   * Manipula aplicação dos filtros
   */
  const handleApplyFilters = (): void => {
    console.log('🔍 Aplicando filtros pendentes:', pendingFilters);
    onApplyFilters();
  };

  return (
    <div className="bg-white rounded-lg shadow-smart-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-medium text-smart-gray-900">
          Filtros de Busca
        </h3>
        <div className="text-sm text-smart-gray-500">
          {totalElements} operações encontradas
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Filtro por Fonte do Evento */}
        <div>
          <label htmlFor="operationSource" className="block text-sm font-medium text-smart-gray-700 mb-2">
            Fonte do Evento
          </label>
          <select
            id="operationSource"
            value={pendingFilters.operationSource || ''}
            onChange={handleOperationSourceChange}
            disabled={isLoading}
            className="w-full px-3 py-2 border border-smart-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">Todas as fontes</option>
            <option value="SELL">Venda</option>
            <option value="EXCHANGE">Troca</option>
          </select>
        </div>

        {/* Filtro por Código */}
        <div>
          <label htmlFor="code" className="block text-sm font-medium text-smart-gray-700 mb-2">
            Código da Operação
          </label>
          <input
            type="text"
            id="code"
            value={pendingFilters.code || ''}
            onChange={handleCodeChange}
            disabled={isLoading}
            placeholder="Digite parte do código..."
            className="w-full px-3 py-2 border border-smart-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-100 disabled:cursor-not-allowed"
          />
        </div>

        {/* Botões de Ação */}
        <div className="flex items-end space-x-3">
          <button
            type="button"
            onClick={onClearFilters}
            disabled={isLoading}
            className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          >
            Limpar
          </button>
          <button
            type="button"
            onClick={handleApplyFilters}
            disabled={isLoading}
            className="px-4 py-2 text-sm font-medium text-white bg-smart-blue-600 border border-transparent rounded-md hover:bg-smart-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          >
            Aplicar Filtros
          </button>
        </div>
      </div>
    </div>
  );
};

export default OperationFilters;
