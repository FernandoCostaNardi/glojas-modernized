import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { PurchaseAnalysisPageResponse, PAGE_SIZES, PurchaseAnalysisTabType } from '@/types/purchaseAnalysis';

/**
 * Interface para as propriedades do componente
 */
interface PurchaseAnalysisTableProps {
  readonly data: PurchaseAnalysisPageResponse | null;
  readonly loading: boolean;
  readonly error: string | null;
  readonly onPageChange: (page: number) => void;
  readonly onSizeChange: (size: number) => void;
  readonly onSortChange: (sortBy: string, sortDir: 'asc' | 'desc') => void;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly activeTab: PurchaseAnalysisTabType;
}

/**
 * Componente de tabela para análise de compras
 * Exibe dados com paginação e ordenação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const PurchaseAnalysisTable: React.FC<PurchaseAnalysisTableProps> = ({
  data,
  loading,
  error,
  onPageChange,
  onSizeChange,
  onSortChange,
  currentPage,
  pageSize,
  sortBy,
  sortDir,
  activeTab
}) => {
  const { isMobile } = useLayout();

  /**
   * Formata valores monetários
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  /**
   * Formata valores numéricos
   */
  const formatNumber = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  };

  /**
   * Determina se uma linha deve ser destacada como crítica
   * Apenas na aba de estoque crítico, destaca produtos com maior diferença
   */
  const getRowClassName = (diferenca?: number): string => {
    if (activeTab !== 'estoque-critico' || diferenca === undefined) {
      return '';
    }
    
    // Destaque mais forte para produtos muito críticos (diferença > 50)
    if (diferenca > 50) {
      return 'bg-smart-red-50 hover:bg-smart-red-100';
    }
    
    // Destaque médio para produtos críticos (diferença > 20)
    if (diferenca > 20) {
      return 'bg-smart-yellow-50 hover:bg-smart-yellow-100';
    }
    
    return 'hover:bg-smart-gray-50';
  };

  /**
   * Renderiza indicador de ordenação
   */
  const renderSortIndicator = (field: string): React.ReactNode => {
    if (sortBy !== field) return null;
    
    return (
      <span className="ml-1">
        {sortDir === 'asc' ? '↑' : '↓'}
      </span>
    );
  };

  /**
   * Manipula clique no cabeçalho da coluna para ordenação
   */
  const handleHeaderClick = (field: string): void => {
    if (sortBy === field) {
      // Inverte direção se já estiver ordenando por esse campo
      onSortChange(field, sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      // Ordenação crescente por padrão
      onSortChange(field, 'asc');
    }
  };

  /**
   * Renderiza estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="flex flex-col items-center">
        <svg className="animate-spin h-10 w-10 text-smart-blue-600 mb-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p className="text-smart-gray-600">Carregando análise de compras...</p>
      </div>
    </div>
  );

  /**
   * Renderiza estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="flex flex-col items-center">
        <span className="text-4xl mb-3">⚠️</span>
        <p className="text-smart-red-600 font-medium mb-2">Erro ao carregar dados</p>
        <p className="text-smart-gray-600 text-sm">{error}</p>
      </div>
    </div>
  );

  /**
   * Renderiza estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className="flex justify-center items-center py-12">
      <div className="flex flex-col items-center">
        <span className="text-4xl mb-3">📦</span>
        <p className="text-smart-gray-600 font-medium mb-2">Nenhum resultado encontrado</p>
        <p className="text-smart-gray-500 text-sm">Tente ajustar os filtros de busca</p>
      </div>
    </div>
  );

  /**
   * Renderiza controles de paginação
   */
  const renderPagination = (): React.ReactNode => {
    if (!data || data.content.length === 0) return null;

    const { pagination } = data;
    const totalPages = pagination.totalPages;

    return (
      <div className={`flex flex-col sm:flex-row justify-between items-center gap-4 mt-4 ${
        isMobile ? 'text-sm' : 'text-base'
      }`}>
        {/* Info de registros */}
        <div className="text-smart-gray-600">
          Mostrando {(pagination.currentPage * pagination.pageSize) + 1} - {Math.min((pagination.currentPage + 1) * pagination.pageSize, pagination.totalElements)} de {pagination.totalElements} registros
        </div>

        {/* Controles de navegação */}
        <div className="flex items-center gap-2">
          {/* Tamanho da página */}
          <div className="flex items-center gap-2">
            <label className="text-smart-gray-600">Itens por página:</label>
            <select
              value={pageSize}
              onChange={(e) => onSizeChange(Number(e.target.value))}
              className="px-2 py-1 border border-smart-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-smart-blue-500"
            >
              {PAGE_SIZES.map((size) => (
                <option key={size} value={size}>{size}</option>
              ))}
            </select>
          </div>

          {/* Botões de navegação */}
          <div className="flex gap-1">
            <button
              onClick={() => onPageChange(0)}
              disabled={pagination.first}
              className="px-3 py-1 rounded-md border border-smart-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-smart-gray-100"
              title="Primeira página"
            >
              ««
            </button>
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={pagination.first}
              className="px-3 py-1 rounded-md border border-smart-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-smart-gray-100"
              title="Página anterior"
            >
              «
            </button>
            <span className="px-4 py-1 border border-smart-gray-300 rounded-md bg-smart-gray-50">
              {pagination.currentPage + 1} / {totalPages}
            </span>
            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={pagination.last}
              className="px-3 py-1 rounded-md border border-smart-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-smart-gray-100"
              title="Próxima página"
            >
              »
            </button>
            <button
              onClick={() => onPageChange(totalPages - 1)}
              disabled={pagination.last}
              className="px-3 py-1 rounded-md border border-smart-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-smart-gray-100"
              title="Última página"
            >
              »»
            </button>
          </div>
        </div>
      </div>
    );
  };

  /**
   * Renderiza a tabela com dados
   */
  const renderTable = (): React.ReactNode => {
    if (!data || data.content.length === 0) return renderEmpty();

    return (
      <>
        <div className="overflow-x-auto">
          <table className="w-full border-collapse">
            <thead>
              <tr className="bg-smart-gray-100 border-b border-smart-gray-300">
                <th 
                  className="px-3 py-2 text-left text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('refplu')}
                >
                  REFPLU {renderSortIndicator('refplu')}
                </th>
                <th 
                  className="px-3 py-2 text-left text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('descricaoProduto')}
                >
                  Descrição {renderSortIndicator('descricaoProduto')}
                </th>
                <th 
                  className="px-3 py-2 text-left text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('descricaoGrupo')}
                >
                  Grupo {renderSortIndicator('descricaoGrupo')}
                </th>
                <th 
                  className="px-3 py-2 text-left text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('descricaoMarca')}
                >
                  Marca {renderSortIndicator('descricaoMarca')}
                </th>
                <th 
                  className="px-3 py-2 text-left text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('codigoPartNumber')}
                >
                  Part Number {renderSortIndicator('codigoPartNumber')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('custoReposicao')}
                >
                  Custo Rep. {renderSortIndicator('custoReposicao')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-gray-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('precoVenda')}
                >
                  Preço Venda {renderSortIndicator('precoVenda')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-blue-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('vendas90Dias')}
                >
                  Vendas 90d {renderSortIndicator('vendas90Dias')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-blue-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('vendas60Dias')}
                >
                  Vendas 60d {renderSortIndicator('vendas60Dias')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-blue-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('vendas30Dias')}
                >
                  Vendas 30d {renderSortIndicator('vendas30Dias')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-green-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('vendasMesAtual')}
                >
                  Vendas Mês {renderSortIndicator('vendasMesAtual')}
                </th>
                <th 
                  className="px-3 py-2 text-right text-xs font-semibold text-smart-purple-700 cursor-pointer hover:bg-smart-gray-200"
                  onClick={() => handleHeaderClick('estoque')}
                >
                  Estoque {renderSortIndicator('estoque')}
                </th>
                {activeTab === 'estoque-critico' && (
                  <>
                    <th 
                      className="px-3 py-2 text-right text-xs font-semibold text-smart-indigo-700 cursor-pointer hover:bg-smart-gray-200"
                      onClick={() => handleHeaderClick('mediaMensal')}
                    >
                      Média Mensal {renderSortIndicator('mediaMensal')}
                    </th>
                    <th 
                      className="px-3 py-2 text-right text-xs font-semibold text-smart-red-700 cursor-pointer hover:bg-smart-gray-200"
                      onClick={() => handleHeaderClick('diferenca')}
                    >
                      Diferença {renderSortIndicator('diferenca')}
                    </th>
                  </>
                )}
              </tr>
            </thead>
            <tbody>
              {data.content.map((item, index) => (
                <tr 
                  key={`${item.refplu}-${index}`}
                  className={`border-b border-smart-gray-200 ${getRowClassName(item.diferenca)}`}
                >
                  <td className="px-3 py-2 text-xs text-smart-gray-900 font-medium">{item.refplu}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-700">{item.descricaoProduto}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-700">{item.descricaoGrupo}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-700">{item.descricaoMarca}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-700">{item.codigoPartNumber || '-'}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-900 text-right">{formatCurrency(item.custoReposicao)}</td>
                  <td className="px-3 py-2 text-xs text-smart-gray-900 text-right">{formatCurrency(item.precoVenda)}</td>
                  <td className="px-3 py-2 text-xs text-smart-blue-700 text-right font-medium">{formatNumber(item.vendas90Dias)}</td>
                  <td className="px-3 py-2 text-xs text-smart-blue-700 text-right font-medium">{formatNumber(item.vendas60Dias)}</td>
                  <td className="px-3 py-2 text-xs text-smart-blue-700 text-right font-medium">{formatNumber(item.vendas30Dias)}</td>
                  <td className="px-3 py-2 text-xs text-smart-green-700 text-right font-medium">{formatNumber(item.vendasMesAtual)}</td>
                  <td className="px-3 py-2 text-xs text-smart-purple-700 text-right font-medium">{formatNumber(item.estoque)}</td>
                  {activeTab === 'estoque-critico' && (
                    <>
                      <td className="px-3 py-2 text-xs text-smart-indigo-700 text-right font-semibold">
                        {item.mediaMensal ? formatNumber(item.mediaMensal) : '-'}
                      </td>
                      <td className="px-3 py-2 text-xs text-smart-red-700 text-right font-bold">
                        {item.diferenca ? formatNumber(item.diferenca) : '-'}
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {renderPagination()}
      </>
    );
  };

  // Renderização principal
  if (loading) return renderLoading();
  if (error) return renderError();
  return renderTable();
};

export default PurchaseAnalysisTable;

