import React from 'react';
import { StockFilters as StockFiltersType } from '@/types/stock';

/**
 * Interface para as propriedades do StockFilters
 */
interface StockFiltersProps {
  readonly filters: StockFiltersType;
  readonly onFiltersChange: (filters: StockFiltersType) => void;
  readonly onSearch: () => void;
  readonly onClear: () => void;
  readonly loading?: boolean;
}

/**
 * Componente para filtros de busca de estoque
 * Permite filtrar por RefPLU, Marca e Descrição
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StockFilters: React.FC<StockFiltersProps> = ({
  filters,
  onFiltersChange,
  onSearch,
  onClear,
  loading = false
}) => {
  /**
   * Manipula mudança nos campos de filtro
   * @param field Campo que foi alterado
   * @param value Novo valor
   */
  const handleFilterChange = (field: keyof Pick<StockFiltersType, 'refplu' | 'marca' | 'descricao'>, value: string): void => {
    // Para descrição, permite múltiplas palavras (preserva espaços internos)
    // Para outros campos, remove espaços extras
    const processedValue = field === 'descricao' 
      ? (value || undefined)
      : (value.trim() || undefined);
    
    onFiltersChange({
      ...filters,
      [field]: processedValue
    });
  };

  /**
   * Manipula submissão do formulário
   * @param event Evento de submissão
   */
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault();
    onSearch();
  };

  /**
   * Manipula clique no botão de limpar
   */
  const handleClear = (): void => {
    const { refplu, marca, descricao, ...rest } = filters;
    onFiltersChange(rest);
    onClear();
  };

  return (
    <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 mb-6">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Campo RefPLU */}
          <div>
            <label htmlFor="refplu" className="block text-sm font-medium text-gray-700 mb-1">
              RefPLU
            </label>
            <input
              type="text"
              id="refplu"
              value={filters.refplu || ''}
              onChange={(e) => handleFilterChange('refplu', e.target.value)}
              placeholder="Digite o RefPLU..."
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              disabled={loading}
            />
          </div>

          {/* Campo Marca */}
          <div>
            <label htmlFor="marca" className="block text-sm font-medium text-gray-700 mb-1">
              Marca
            </label>
            <input
              type="text"
              id="marca"
              value={filters.marca || ''}
              onChange={(e) => handleFilterChange('marca', e.target.value)}
              placeholder="Digite a marca..."
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              disabled={loading}
            />
          </div>

          {/* Campo Descrição */}
          <div>
            <label htmlFor="descricao" className="block text-sm font-medium text-gray-700 mb-1">
              Descrição
            </label>
            <input
              type="text"
              id="descricao"
              value={filters.descricao || ''}
              onChange={(e) => handleFilterChange('descricao', e.target.value)}
              placeholder="Digite a descrição..."
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              disabled={loading}
            />
          </div>
        </div>

        {/* Botões de ação */}
        <div className="flex flex-col sm:flex-row gap-2 sm:justify-end">
          <button
            type="button"
            onClick={handleClear}
            disabled={loading}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Limpar Filtros
          </button>
          
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? (
              <div className="flex items-center">
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Buscando...
              </div>
            ) : (
              'Buscar Estoque'
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default StockFilters;
