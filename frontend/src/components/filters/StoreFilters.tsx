import React from 'react';
import { StoreSearchFilters, BRAZILIAN_STATES } from '@/types';

/**
 * Props dos filtros de lojas
 */
interface StoreFiltersProps {
  readonly filters: StoreSearchFilters;
  readonly totalElements: number;
  readonly onFiltersChange: (filters: StoreSearchFilters) => void;
  readonly onClearFilters: () => void;
}

/**
 * Componente de filtros para busca de lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const StoreFilters: React.FC<StoreFiltersProps> = ({
  filters,
  totalElements,
  onFiltersChange,
  onClearFilters
}) => {
  const handleFilterChange = (field: keyof StoreSearchFilters, value: any) => {
    onFiltersChange({ ...filters, [field]: value });
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4 mb-4">
      <h3 className="text-base font-medium text-smart-gray-800 mb-3">Filtros de Busca</h3>
      
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3 mb-3">
        {/* Filtro por código */}
        <div>
          <label htmlFor="filter-code" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Código
          </label>
          <input
            type="text"
            id="filter-code"
            value={filters.code || ''}
            onChange={(e) => handleFilterChange('code', e.target.value || '')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
            placeholder="Digite o código"
          />
        </div>

        {/* Filtro por nome */}
        <div>
          <label htmlFor="filter-name" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Nome
          </label>
          <input
            type="text"
            id="filter-name"
            value={filters.name || ''}
            onChange={(e) => handleFilterChange('name', e.target.value || '')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
            placeholder="Digite o nome"
          />
        </div>

        {/* Filtro por cidade */}
        <div>
          <label htmlFor="filter-city" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Cidade
          </label>
          <input
            type="text"
            id="filter-city"
            value={filters.city || ''}
            onChange={(e) => handleFilterChange('city', e.target.value || '')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
            placeholder="Digite a cidade"
          />
        </div>

        {/* Filtro por estado */}
        <div>
          <label htmlFor="filter-state" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Estado
          </label>
          <select
            id="filter-state"
            value={filters.state || ''}
            onChange={(e) => handleFilterChange('state', e.target.value || '')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
          >
            <option value="">Todos os estados</option>
            {BRAZILIAN_STATES.map((state) => (
              <option key={state.value} value={state.value}>
                {state.label}
              </option>
            ))}
          </select>
        </div>

        {/* Filtro por status ativo */}
        <div>
          <label htmlFor="filter-active" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Status
          </label>
          <select
            id="filter-active"
            value={filters.isActive?.toString() || ''}
            onChange={(e) => handleFilterChange('isActive', e.target.value === '' ? undefined : e.target.value === 'true')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
          >
            <option value="">Todos os status</option>
            <option value="true">Ativo</option>
            <option value="false">Inativo</option>
          </select>
        </div>

        {/* Filtro por gerente */}
        <div>
          <label htmlFor="filter-manager" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Gerente
          </label>
          <input
            type="text"
            id="filter-manager"
            value={filters.managerName || ''}
            onChange={(e) => handleFilterChange('managerName', e.target.value || '')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
            placeholder="Nome do gerente"
          />
        </div>
      </div>

      {/* Botões de ação dos filtros */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <button
            onClick={onClearFilters}
            className="bg-smart-gray-600 hover:bg-smart-gray-700 text-white px-3 py-1.5 rounded-md font-medium transition-colors duration-200 text-sm"
            aria-label="Limpar todos os filtros aplicados"
          >
            Limpar Filtros
          </button>
        </div>
        
        {/* Informações de paginação */}
        <div className="text-xs text-smart-gray-600">
          Total de lojas: {totalElements}
        </div>
      </div>
    </div>
  );
};
