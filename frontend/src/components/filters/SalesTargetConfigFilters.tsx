import React from 'react';
import { SalesTargetConfigFilters as SalesTargetConfigFiltersType, ApiStore } from '@/types';

/**
 * Props do componente de filtros de configurações de metas e comissões
 */
interface SalesTargetConfigFiltersProps {
  readonly filters: SalesTargetConfigFiltersType;
  readonly availableStores: readonly ApiStore[];
  readonly totalElements: number;
  readonly onFiltersChange: (filters: SalesTargetConfigFiltersType) => void;
  readonly onClearFilters: () => void;
}

/**
 * Componente de filtros para busca de configurações de metas e comissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const SalesTargetConfigFilters: React.FC<SalesTargetConfigFiltersProps> = ({
  filters,
  availableStores,
  totalElements,
  onFiltersChange,
  onClearFilters
}) => {
  /**
   * Manipula mudança nos filtros
   */
  const handleFilterChange = (field: keyof SalesTargetConfigFiltersType, value: string): void => {
    onFiltersChange({ ...filters, [field]: value || undefined });
  };

  /**
   * Valida formato de competência (MM/YYYY)
   */
  const validateCompetenceDate = (value: string): boolean => {
    if (!value || value.trim() === '') return true; // Vazio é válido (sem filtro)
    return /^(0[1-9]|1[0-2])\/\d{4}$/.test(value);
  };

  /**
   * Manipula mudança no campo de competência com validação
   */
  const handleCompetenceDateChange = (value: string): void => {
    // Permitir digitação livre, mas validar antes de aplicar
    if (validateCompetenceDate(value) || value === '') {
      handleFilterChange('competenceDate', value);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4 mb-4">
      <h3 className="text-base font-medium text-smart-gray-800 mb-3">Filtros de Busca</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3 mb-3">
        {/* Filtro por Loja */}
        <div>
          <label htmlFor="filter-store" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Loja
          </label>
          <select
            id="filter-store"
            value={filters.storeCode || ''}
            onChange={(e) => handleFilterChange('storeCode', e.target.value)}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
          >
            <option value="">Todas as lojas</option>
            {availableStores.map((store) => (
              <option key={store.id} value={store.code}>
                {store.name} ({store.code})
              </option>
            ))}
          </select>
        </div>

        {/* Filtro por Competência */}
        <div>
          <label htmlFor="filter-competence" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Competência (MM/YYYY)
          </label>
          <input
            type="text"
            id="filter-competence"
            value={filters.competenceDate || ''}
            onChange={(e) => handleCompetenceDateChange(e.target.value)}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
            placeholder="Ex: 11/2025"
            maxLength={7}
          />
          {filters.competenceDate && !validateCompetenceDate(filters.competenceDate) && (
            <p className="mt-1 text-xs text-red-500">Formato inválido. Use MM/YYYY</p>
          )}
        </div>

        {/* Espaço vazio para alinhamento */}
        <div></div>
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
          Total de configurações: {totalElements}
        </div>
      </div>
    </div>
  );
};

