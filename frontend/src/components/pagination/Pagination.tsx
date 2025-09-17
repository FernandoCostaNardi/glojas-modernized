import React from 'react';

/**
 * Props do componente de paginação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface PaginationProps {
  readonly currentPage: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly pageSize: number;
  readonly onPageChange: (page: number) => void;
  readonly isLoading?: boolean;
}

/**
 * Componente de paginação reutilizável
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  isLoading = false
}) => {
  /**
   * Calcula o range de elementos exibidos
   */
  const getElementRange = (): string => {
    const end = Math.min((currentPage + 1) * pageSize, totalElements);
    return `${end}`;
  };

  /**
   * Gera array de números de página para exibição
   */
  const getPageNumbers = (): number[] => {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      // Se total de páginas é menor que o máximo visível, mostra todas
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Lógica para mostrar páginas relevantes
      const start = Math.max(0, currentPage - 2);
      const end = Math.min(totalPages - 1, start + maxVisiblePages - 1);
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
    }
    
    return pages;
  };

  /**
   * Verifica se pode ir para página anterior
   */
  const canGoPrevious = (): boolean => {
    return currentPage > 0 && !isLoading;
  };

  /**
   * Verifica se pode ir para próxima página
   */
  const canGoNext = (): boolean => {
    return currentPage < totalPages - 1 && !isLoading;
  };

  /**
   * Manipula mudança de página
   */
  const handlePageChange = (page: number): void => {
    console.log('🔄 Pagination: handlePageChange chamado com página:', page, {
      isLoading,
      totalPages,
      currentPage
    });
    
    if (!isLoading && page >= 0 && page < totalPages) {
      console.log('🔄 Pagination: Chamando onPageChange com página:', page);
      onPageChange(page);
    } else {
      console.warn('🔄 Pagination: Mudança de página bloqueada:', {
        isLoading,
        page,
        totalPages,
        condition: !isLoading && page >= 0 && page < totalPages
      });
    }
  };

  // Não renderiza se não há páginas
  if (totalPages <= 1) {
    return null;
  }

  return (
    <div className="flex items-center justify-between px-6 py-4 bg-white border-t border-smart-gray-200">
      {/* Informações da página */}
      <div className="flex items-center text-sm text-smart-gray-700">
        <span>
          Mostrando {getElementRange()} de {totalElements} resultados
        </span>
      </div>

      {/* Controles de paginação */}
      <div className="flex items-center space-x-2">
        {/* Botão Anterior */}
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={!canGoPrevious()}
          className="px-3 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          title="Página anterior"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        {/* Números das páginas */}
        <div className="flex items-center space-x-1">
          {getPageNumbers().map((page) => (
            <button
              key={page}
              onClick={() => handlePageChange(page)}
              disabled={isLoading}
              className={`px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200 ${
                currentPage === page
                  ? 'text-white bg-smart-blue-600 border border-smart-blue-600'
                  : 'text-smart-gray-700 bg-white border border-smart-gray-300 hover:bg-smart-gray-50'
              } disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500`}
            >
              {page + 1}
            </button>
          ))}
        </div>

        {/* Botão Próximo */}
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={!canGoNext()}
          className="px-3 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          title="Próxima página"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>
  );
};