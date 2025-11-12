import React from 'react';
import { StockItem, StockPageResponse, PAGE_SIZES } from '@/types/stock';
import StockCardList from './StockCardList';

/**
 * Interface para as propriedades do StockTable
 */
interface StockTableProps {
  readonly data: StockPageResponse | null;
  readonly loading: boolean;
  readonly error: string | null;
  readonly storeNames: Map<number, string>;
  readonly onPageChange: (page: number) => void;
  readonly onSizeChange: (size: number) => void;
  readonly onSortChange: (sortBy: string, sortDir: 'asc' | 'desc') => void;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly isMobile?: boolean;
}

/**
 * Componente para exibição da tabela de estoque
 * Mostra produtos com quantidades por loja em formato tabular
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StockTable: React.FC<StockTableProps> = ({
  data,
  loading,
  error,
  storeNames,
  onPageChange,
  onSizeChange,
  onSortChange,
  currentPage: _currentPage,
  pageSize,
  sortBy,
  sortDir,
  isMobile = false
}) => {
  // Log para debug - verificar o pageSize recebido
  console.log(`📏 StockTable recebeu pageSize: ${pageSize}`);
  /**
   * Manipula clique em header para ordenação
   * @param column Nome da coluna
   */
  const handleSortClick = (column: string): void => {
    if (sortBy === column) {
      // Alternar direção
      const newDir = sortDir === 'asc' ? 'desc' : 'asc';
      onSortChange(column, newDir);
    } else {
      // Nova coluna, começar com desc (maiores valores primeiro)
      onSortChange(column, 'desc');
    }
  };

  /**
   * Renderiza ícone de ordenação
   * @param column Nome da coluna
   */
  const renderSortIcon = (column: string): React.ReactNode => {
    if (sortBy !== column) {
      return (
        <svg className="w-4 h-4 ml-1 text-smart-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
        </svg>
      );
    }
    
    if (sortDir === 'asc') {
      return (
        <svg className="w-4 h-4 ml-1 text-smart-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
        </svg>
      );
    }
    
    return (
      <svg className="w-4 h-4 ml-1 text-smart-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
      </svg>
    );
  };

  /**
   * Renderiza header ordenável
   * @param column Nome da coluna
   * @param label Label a ser exibido
   * @param className Classes CSS adicionais
   */
  const renderSortableHeader = (column: string, label: string, className: string = ''): React.ReactNode => (
    <th 
      onClick={() => handleSortClick(column)}
      className={`cursor-pointer hover:bg-smart-gray-100 transition-colors ${className}`}
      title={`Ordenar por ${label}`}
    >
      <div className="flex items-center justify-center">
        <span>{label}</span>
        {renderSortIcon(column)}
      </div>
    </th>
  );

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
   * Renderiza uma célula de quantidade com formatação
   * @param quantity Quantidade a ser exibida
   */
  const renderQuantityCell = (quantity: number | null | undefined): React.ReactNode => {
    if (quantity == null || quantity === 0) {
      return <span className="text-smart-gray-400">-</span>;
    }
    return <span className="font-medium">{quantity.toLocaleString('pt-BR')}</span>;
  };

  /**
   * Obtém o nome da loja baseado no número
   * @param storeNumber Número da loja
   * @returns Nome da loja ou nome genérico se não encontrado
   */
  const getStoreName = (storeNumber: number): string => {
    return storeNames.get(storeNumber) || `Loja ${storeNumber}`;
  };

  /**
   * Obtém lista de números de lojas ordenados
   * @returns Array de números de lojas ordenados
   */
  const getSortedStoreNumbers = (): number[] => {
    return Array.from(storeNames.keys()).sort((a, b) => a - b);
  };

  /**
   * Renderiza o cabeçalho da tabela com controles de ordenação
   * Renderiza colunas dinamicamente baseado nas lojas carregadas
   */
  const renderTableHeader = (): React.ReactNode => {
    const sortedStoreNumbers = getSortedStoreNumbers();
    
    return (
      <thead className="bg-smart-gray-50">
        <tr>
          <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider border-r border-smart-gray-300">
            RefPLU
          </th>
          <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider border-r border-smart-gray-300">
            Marca
          </th>
          <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider border-r border-smart-gray-300">
            Descrição
          </th>
          {sortedStoreNumbers.map((storeNumber) => {
            const storeName = getStoreName(storeNumber);
            const columnName = `loj${storeNumber}`;
            
            return (
              <React.Fragment key={storeNumber}>
                {renderSortableHeader(
                  columnName,
                  storeName,
                  "px-2 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider border-r border-smart-gray-300"
                )}
              </React.Fragment>
            );
          })}
          {renderSortableHeader(
            'total',
            'Total',
            'px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider bg-smart-gray-100'
          )}
        </tr>
      </thead>
    );
  };

  /**
   * Obtém o valor de estoque de uma loja específica do item
   * @param item Item de estoque
   * @param storeNumber Número da loja
   * @returns Valor do estoque ou null
   */
  const getStoreQuantity = (item: StockItem, storeNumber: number): number | null => {
    // Mapear número da loja para propriedade do item (loj1, loj2, etc.)
    const propertyMap: { [key: number]: keyof StockItem } = {
      1: 'loj1', 2: 'loj2', 3: 'loj3', 4: 'loj4', 5: 'loj5',
      6: 'loj6', 7: 'loj7', 8: 'loj8', 9: 'loj9', 10: 'loj10',
      11: 'loj11', 12: 'loj12', 13: 'loj13', 14: 'loj14'
    };
    
    const property = propertyMap[storeNumber];
    if (property) {
      const value = item[property];
      return typeof value === 'number' ? value : null;
    }
    return null;
  };

  /**
   * Renderiza uma linha da tabela
   * Renderiza colunas de lojas dinamicamente baseado nas lojas carregadas
   * @param item Item de estoque
   * @param index Índice da linha
   */
  const renderTableRow = (item: StockItem, index: number): React.ReactNode => {
    const sortedStoreNumbers = getSortedStoreNumbers();
    
    return (
      <tr key={item.refplu} className={index % 2 === 0 ? 'bg-white' : 'bg-smart-gray-50'}>
        <td className="px-4 py-3 text-sm font-medium text-smart-gray-900 border-r border-smart-gray-200">
          {item.refplu}
        </td>
        <td className="px-4 py-3 text-sm text-smart-gray-900 border-r border-smart-gray-200">
          {item.marca}
        </td>
        <td className="px-4 py-3 text-sm text-smart-gray-900 max-w-xs truncate border-r border-smart-gray-200" title={item.descricao}>
          {item.descricao}
        </td>
        {sortedStoreNumbers.map((storeNumber) => {
          const quantity = getStoreQuantity(item, storeNumber);
          return (
            <td 
              key={storeNumber}
              className="px-2 py-3 text-center text-sm text-smart-gray-900 border-r border-smart-gray-200"
            >
              {renderQuantityCell(quantity)}
            </td>
          );
        })}
        <td className="px-4 py-3 text-center text-sm font-bold text-smart-gray-900 bg-smart-gray-100">
          {renderQuantityCell(item.total)}
        </td>
      </tr>
    );
  };

  /**
   * Renderiza controles de paginação
   */
  const renderPagination = (): React.ReactNode => {
    if (!data?.pagination) return null;

    const { totalPages, currentPage, hasNext, hasPrevious, totalElements } = data.pagination;

    return (
      <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-smart-gray-200 sm:px-6">
        <div className="flex-1 flex justify-between sm:hidden">
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={!hasPrevious || loading}
            className="relative inline-flex items-center px-4 py-2 border border-smart-gray-300 text-sm font-medium rounded-md text-smart-gray-700 bg-white hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Anterior
          </button>
          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={!hasNext || loading}
            className="ml-3 relative inline-flex items-center px-4 py-2 border border-smart-gray-300 text-sm font-medium rounded-md text-smart-gray-700 bg-white hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Próximo
          </button>
        </div>
        
        <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p className="text-sm text-smart-gray-700">
              Mostrando <span className="font-medium">{currentPage * pageSize + 1}</span> até{' '}
              <span className="font-medium">
                {Math.min((currentPage + 1) * pageSize, totalElements)}
              </span>{' '}
              de <span className="font-medium">{totalElements}</span> resultados
            </p>
          </div>
          
          <div className="flex items-center space-x-2">
            <select
              value={pageSize}
              onChange={(e) => onSizeChange(Number(e.target.value))}
              disabled={loading}
              className="text-sm border-smart-gray-300 rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500"
            >
              {PAGE_SIZES.map((size) => (
                <option key={size.value} value={size.value}>
                  {size.label}
                </option>
              ))}
            </select>
            
            <div className="flex">
              <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={!hasPrevious || loading}
                className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-smart-gray-300 bg-white text-sm font-medium text-smart-gray-500 hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span className="sr-only">Anterior</span>
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
              </button>
              
              <span className="relative inline-flex items-center px-4 py-2 border border-smart-gray-300 bg-white text-sm font-medium text-smart-gray-700">
                Página {currentPage + 1} de {totalPages}
              </span>
              
              <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={!hasNext || loading}
                className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-smart-gray-300 bg-white text-sm font-medium text-smart-gray-500 hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span className="sr-only">Próximo</span>
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  // Renderizar versão mobile (cards) se isMobile for true
  if (isMobile) {
    return (
      <StockCardList
        data={data}
        loading={loading}
        error={error}
        storeNames={storeNames}
        onPageChange={onPageChange}
        onSizeChange={onSizeChange}
        pageSize={pageSize}
      />
    );
  }

  // Renderizar estados especiais (desktop)
  if (loading) return renderLoading();
  if (error) return renderError();
  if (!data || !data.content || data.content.length === 0) return renderEmpty();

  // Log para debug - verificar quantos itens estão sendo renderizados
  console.log(`📊 Renderizando tabela: ${data.content.length} itens (pageSize recebido: ${pageSize}, esperado: ${data.pagination?.pageSize || 'N/A'})`);

  // Renderizar versão desktop (tabela)
  return (
    <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-smart-gray-300">
          {renderTableHeader()}
          <tbody className="bg-white divide-y divide-smart-gray-200">
            {data.content.map((item, index) => renderTableRow(item, index))}
          </tbody>
        </table>
      </div>
      {renderPagination()}
    </div>
  );
};

export default StockTable;
