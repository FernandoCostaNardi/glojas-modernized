import React from 'react';
import { UserSearchFilters, AvailableRole } from '@/types';

/**
 * Props dos filtros de usuários
 */
interface UserFiltersProps {
  readonly filters: UserSearchFilters;
  readonly availableRoles: readonly AvailableRole[];
  readonly totalElements: number;
  readonly onFiltersChange: (filters: UserSearchFilters) => void;
  readonly onClearFilters: () => void;
}

/**
 * Componente de filtros para busca de usuários
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const UserFilters: React.FC<UserFiltersProps> = ({
  filters,
  availableRoles,
  totalElements,
  onFiltersChange,
  onClearFilters
}) => {
  const handleFilterChange = (field: keyof UserSearchFilters, value: any) => {
    onFiltersChange({ ...filters, [field]: value });
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4 mb-4">
      <h3 className="text-base font-medium text-smart-gray-800 mb-3">Filtros de Busca</h3>
      
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-3">
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

        {/* Filtro por roles */}
        <div>
          <label htmlFor="filter-roles" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Roles
          </label>
          <select
            id="filter-roles"
            value={filters.roles?.[0] || ''}
            onChange={(e) => handleFilterChange('roles', e.target.value ? [e.target.value] : [])}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
          >
            <option value="">Todas as roles</option>
            {availableRoles.map((role) => (
              <option key={role.id} value={role.name}>
                {role.name}
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

        {/* Filtro por bloqueio */}
        <div>
          <label htmlFor="filter-locked" className="block text-xs font-medium text-smart-gray-700 mb-1">
            Bloqueio
          </label>
          <select
            id="filter-locked"
            value={filters.isNotLocked?.toString() || ''}
            onChange={(e) => handleFilterChange('isNotLocked', e.target.value === '' ? undefined : e.target.value === 'true')}
            className="w-full px-2 py-1.5 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 text-sm"
          >
            <option value="">Todos os bloqueios</option>
            <option value="true">Desbloqueado</option>
            <option value="false">Bloqueado</option>
          </select>
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
          Total de usuários: {totalElements}
        </div>
      </div>
    </div>
  );
};
