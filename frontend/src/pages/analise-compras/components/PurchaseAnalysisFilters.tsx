import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { PurchaseAnalysisFilters } from '@/types/purchaseAnalysis';

/**
 * Interface para as propriedades do componente
 */
interface PurchaseAnalysisFiltersProps {
  readonly filters: PurchaseAnalysisFilters;
  readonly onFiltersChange: (filters: PurchaseAnalysisFilters) => void;
  readonly onSearch: () => void;
  readonly onClear: () => void;
  readonly loading: boolean;
}

/**
 * Componente de filtros para análise de compras
 * Permite filtrar por REFPLU
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const PurchaseAnalysisFiltersComponent: React.FC<PurchaseAnalysisFiltersProps> = ({
  filters,
  onFiltersChange,
  onSearch,
  onClear,
  loading
}) => {
  const { isMobile } = useLayout();

  /**
   * Manipula mudança no campo REFPLU
   */
  const handleRefpluChange = (value: string): void => {
    onFiltersChange({ ...filters, refplu: value });
  };

  /**
   * Manipula mudança no switch de ocultar produtos sem vendas
   */
  const handleHideNoSalesChange = (checked: boolean): void => {
    onFiltersChange({ ...filters, hideNoSales: checked });
  };

  /**
   * Manipula o evento de tecla Enter
   */
  const handleKeyPress = (e: React.KeyboardEvent): void => {
    if (e.key === 'Enter') {
      onSearch();
    }
  };

  return (
    <div className={`bg-smart-gray-50 rounded-lg border border-smart-gray-200 ${
      isMobile ? 'p-2' : 'p-4'
    }`}>
      <div className={`grid gap-3 ${isMobile ? 'grid-cols-1' : 'grid-cols-12'}`}>
        {/* Campo REFPLU */}
        <div className={isMobile ? '' : 'col-span-3'}>
          <label className="block text-sm font-medium text-smart-gray-700 mb-1">
            REFPLU
          </label>
          <input
            type="text"
            value={filters.refplu || ''}
            onChange={(e) => handleRefpluChange(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Digite o REFPLU"
            disabled={loading}
            className={`w-full px-3 rounded-lg border border-smart-gray-300 
              focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-transparent
              disabled:bg-smart-gray-100 disabled:cursor-not-allowed
              ${isMobile ? 'py-2 text-sm' : 'py-2 text-base'}`}
          />
        </div>

        {/* Switch - Ocultar produtos sem vendas */}
        <div className={`flex items-end ${isMobile ? '' : 'col-span-3'}`}>
          <label className={`flex items-center cursor-pointer group ${
            loading ? 'opacity-50 cursor-not-allowed' : ''
          }`}>
            <div className="relative">
              <input
                type="checkbox"
                checked={filters.hideNoSales ?? true}
                onChange={(e) => handleHideNoSalesChange(e.target.checked)}
                disabled={loading}
                className="sr-only"
              />
              <div className={`block w-12 h-6 rounded-full transition-colors duration-200 ${
                filters.hideNoSales ?? true
                  ? 'bg-smart-blue-600'
                  : 'bg-smart-gray-300'
              }`}></div>
              <div className={`absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ${
                filters.hideNoSales ?? true
                  ? 'transform translate-x-6'
                  : ''
              }`}></div>
            </div>
            <span className={`ml-3 font-medium ${
              isMobile ? 'text-xs' : 'text-sm'
            } text-smart-gray-700`}>
              Ocultar sem vendas (90d)
            </span>
          </label>
        </div>

        {/* Botões de ação */}
        <div className={`flex gap-2 items-end ${
          isMobile ? '' : 'col-span-6 justify-end'
        }`}>
          <button
            onClick={onSearch}
            disabled={loading}
            className={`flex-1 sm:flex-none px-6 rounded-lg font-medium
              bg-smart-blue-600 text-white hover:bg-smart-blue-700
              focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:ring-offset-2
              disabled:bg-smart-gray-300 disabled:cursor-not-allowed
              transition-colors duration-200
              ${isMobile ? 'py-2 text-sm' : 'py-2 text-base'}`}
          >
            {loading ? (
              <span className="flex items-center justify-center">
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Buscando...
              </span>
            ) : (
              '🔍 Buscar'
            )}
          </button>

          <button
            onClick={onClear}
            disabled={loading}
            className={`flex-1 sm:flex-none px-6 rounded-lg font-medium
              bg-smart-gray-200 text-smart-gray-700 hover:bg-smart-gray-300
              focus:outline-none focus:ring-2 focus:ring-smart-gray-500 focus:ring-offset-2
              disabled:bg-smart-gray-100 disabled:cursor-not-allowed
              transition-colors duration-200
              ${isMobile ? 'py-2 text-sm' : 'py-2 text-base'}`}
          >
            🗑️ Limpar
          </button>
        </div>
      </div>
    </div>
  );
};

export default PurchaseAnalysisFiltersComponent;

