import React from 'react';
import { StockPageResponse, PAGE_SIZES } from '@/types/stock';
import StockCard from './StockCard';

/**
 * Interface para as propriedades do StockCardList
 */
interface StockCardListProps {
  readonly data: StockPageResponse | null;
  readonly loading: boolean;
  readonly error: string | null;
  readonly storeNames: Map<number, string>;
  readonly onPageChange: (page: number) => void;
  readonly onSizeChange: (size: number) => void;
  readonly pageSize: number;
}

/**
 * Componente para exibição da lista de cards de estoque mobile
 * Renderiza produtos em formato de cards otimizado para dispositivos móveis
 * Inclui estados de loading, error, empty e paginação mobile-friendly
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StockCardList: React.FC<StockCardListProps> = ({
  data,
  loading,
  error,
  storeNames,
  onPageChange,
  onSizeChange,
  pageSize
}) => {
  /**
   * Renderiza o estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="flex flex-col items-center">
        <svg className="animate-spin h-8 w-8 text-smart-blue-600 mb-4" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p className="text-smart-gray-600 font-medium">Carregando estoque...</p>
      </div>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-red-500 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Erro ao carregar estoque</h3>
        <p className="text-smart-gray-600">{error}</p>
      </div>
    </div>
  );

  /**
   * Renderiza o estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Nenhum item encontrado</h3>
        <p className="text-smart-gray-600">Tente ajustar os filtros de busca</p>
      </div>
    </div>
  );

  /**
   * Renderiza controles de paginação mobile-friendly
   */
  const renderPagination = (): React.ReactNode => {
    if (!data?.pagination) return null;

    const { totalPages, currentPage: paginationCurrentPage, hasNext, hasPrevious, totalElements } = data.pagination;

    return (
      <div className="bg-white px-4 py-4 border-t border-smart-gray-200 mt-4">
        {/* Informações de paginação */}
        <div className="mb-4 text-center">
          <p className="text-sm text-smart-gray-700">
            Mostrando <span className="font-medium">{paginationCurrentPage * pageSize + 1}</span> até{' '}
            <span className="font-medium">
              {Math.min((paginationCurrentPage + 1) * pageSize, totalElements)}
            </span>{' '}
            de <span className="font-medium">{totalElements}</span> resultados
          </p>
        </div>

        {/* Controles de navegação */}
        <div className="flex flex-col space-y-3">
          {/* Botões Anterior/Próximo */}
          <div className="flex justify-between gap-2">
            <button
              onClick={() => onPageChange(paginationCurrentPage - 1)}
              disabled={!hasPrevious || loading}
              className="flex-1 px-4 py-2.5 border border-smart-gray-300 text-sm font-medium rounded-md text-smart-gray-700 bg-white hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Anterior
            </button>
            <button
              onClick={() => onPageChange(paginationCurrentPage + 1)}
              disabled={!hasNext || loading}
              className="flex-1 px-4 py-2.5 border border-smart-gray-300 text-sm font-medium rounded-md text-smart-gray-700 bg-white hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Próximo
            </button>
          </div>

          {/* Seletor de tamanho de página */}
          <div className="flex items-center justify-center">
            <label htmlFor="page-size-select" className="text-sm text-smart-gray-700 mr-2">
              Itens por página:
            </label>
            <select
              id="page-size-select"
              value={pageSize}
              onChange={(e) => onSizeChange(Number(e.target.value))}
              disabled={loading}
              className="text-sm border-smart-gray-300 rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 px-2 py-1"
            >
              {PAGE_SIZES.map((size) => (
                <option key={size.value} value={size.value}>
                  {size.label}
                </option>
              ))}
            </select>
          </div>

          {/* Indicador de página */}
          <div className="text-center">
            <span className="text-sm text-smart-gray-600">
              Página {paginationCurrentPage + 1} de {totalPages}
            </span>
          </div>
        </div>
      </div>
    );
  };

  // Renderizar estados especiais
  if (loading) return renderLoading();
  if (error) return renderError();
  if (!data || !data.content || data.content.length === 0) return renderEmpty();

  return (
    <div>
      {/* Lista de Cards */}
      <div className="space-y-0">
        {data.content.map((item) => (
          <StockCard key={item.refplu} item={item} storeNames={storeNames} />
        ))}
      </div>

      {/* Paginação */}
      {renderPagination()}
    </div>
  );
};

export default StockCardList;

