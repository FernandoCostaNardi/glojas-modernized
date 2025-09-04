import React from 'react';

/**
 * Props do componente de paginação
 */
interface PaginationProps {
  readonly currentPage: number;
  readonly totalPages: number;
  readonly totalElements: number;
  readonly itemsPerPage: number;
  readonly onPageChange: (page: number) => void;
}

/**
 * Componente de paginação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  itemsPerPage,
  onPageChange
}) => {
  /**
   * Gera array de números de páginas para exibição
   */
  const generatePageNumbers = (currentPage: number, totalPages: number): number[] => {
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      return Array.from({ length: totalPages }, (_, i) => i);
    }
    
    if (currentPage < 3) {
      return Array.from({ length: maxVisiblePages }, (_, i) => i);
    }
    
    if (currentPage > totalPages - 3) {
      return Array.from({ length: maxVisiblePages }, (_, i) => totalPages - maxVisiblePages + i);
    }
    
    return Array.from({ length: maxVisiblePages }, (_, i) => currentPage - 2 + i);
  };

  const pageNumbers = generatePageNumbers(currentPage, totalPages);
  const startItem = currentPage * itemsPerPage + 1;
  const endItem = Math.min((currentPage + 1) * itemsPerPage, totalElements);

  return (
    <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 p-4 mt-4">
      <div className="flex items-center justify-between">
        {/* Informações de itens */}
        <div className="text-sm text-smart-gray-600">
          Mostrando <span className="font-medium">{startItem}</span> a <span className="font-medium">{endItem}</span> de <span className="font-medium">{totalElements}</span> itens
        </div>
        
        {/* Controles de navegação */}
        <div className="flex items-center justify-center space-x-2">
          {/* Botão primeira página */}
          <button
            onClick={() => onPageChange(0)}
            disabled={currentPage === 0}
            className="px-3 py-2 text-sm font-medium text-smart-gray-500 bg-white border border-smart-gray-300 rounded-lg hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Ir para primeira página"
          >
            Primeira
          </button>

          {/* Botão página anterior */}
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="px-3 py-2 text-sm font-medium text-smart-gray-500 bg-white border border-smart-gray-300 rounded-lg hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Ir para página anterior"
          >
            Anterior
          </button>

          {/* Números das páginas */}
          <div className="flex items-center space-x-1">
            {pageNumbers.map((pageNum) => (
              <button
                key={pageNum}
                onClick={() => onPageChange(pageNum)}
                className={`px-3 py-2 text-sm font-medium rounded-lg ${
                  currentPage === pageNum
                    ? 'bg-smart-red-600 text-white'
                    : 'text-smart-gray-500 bg-white border border-smart-gray-300 hover:bg-smart-gray-50'
                }`}
                aria-label={`Ir para página ${pageNum + 1}`}
              >
                {pageNum + 1}
              </button>
            ))}
          </div>

          {/* Botão próxima página */}
          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-2 text-sm font-medium text-smart-gray-500 bg-white border border-smart-gray-300 rounded-lg hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Ir para próxima página"
          >
            Próxima
          </button>

          {/* Botão última página */}
          <button
            onClick={() => onPageChange(totalPages - 1)}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-2 text-sm font-medium text-smart-gray-500 bg-white border border-smart-gray-300 rounded-lg hover:bg-smart-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Ir para última página"
          >
            Última
          </button>
        </div>
        
        {/* Informações de página */}
        <div className="text-sm text-smart-gray-600">
          Página <span className="font-medium">{currentPage + 1}</span> de <span className="font-medium">{totalPages}</span>
        </div>
      </div>
    </div>
  );
};
