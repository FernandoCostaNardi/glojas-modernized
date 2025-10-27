import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { DailySalesData } from './VendasDiarias';

/**
 * Interface para as propriedades do VendasTable
 */
interface VendasTableProps {
  readonly data: DailySalesData[];
  readonly loading: boolean;
  readonly error: string | null;
  readonly className?: string;
}

/**
 * Componente de tabela para exibir dados de vendas
 * Responsiva e com formatação monetária brasileira
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasTable: React.FC<VendasTableProps> = ({ 
  data, 
  loading, 
  error, 
  className = '' 
}) => {
  const { isMobile } = useLayout();
  
  /**
   * Formata valor monetário para o padrão brasileiro
   * @param value - Valor numérico a ser formatado
   * @returns String formatada em reais brasileiros
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  /**
   * Calcula o total geral de todas as lojas
   * @returns Valor total somado
   */
  const calculateTotal = (): number => {
    return data.reduce((total, item) => total + item.total, 0);
  };

  /**
   * Calcula a porcentagem de vendas de uma loja em relação ao total
   * @param value - Valor da loja
   * @param total - Total geral
   * @returns Porcentagem formatada
   */
  const calculatePercentage = (value: number, total: number): number => {
    return total > 0 ? (value / total) * 100 : 0;
  };

  /**
   * Renderiza o estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
      isMobile ? 'p-6 mx-2' : 'p-8 mx-4'
    }`}>
      <div className="flex items-center justify-center">
        <div className={`flex items-center ${isMobile ? 'space-x-2' : 'space-x-3'}`}>
          <svg className={`animate-spin text-smart-red-600 ${
            isMobile ? 'h-6 w-6' : 'h-8 w-8'
          }`} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span className={`font-medium text-smart-gray-600 ${
            isMobile ? 'text-sm' : 'text-lg'
          }`}>
            {isMobile ? 'Carregando...' : 'Carregando dados de vendas...'}
          </span>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
      isMobile ? 'p-6 mx-2' : 'p-8 mx-4'
    }`}>
      <div className="text-center">
        <div className={`text-red-500 mb-4 ${isMobile ? 'text-4xl' : 'text-6xl'}`}>⚠️</div>
        <h3 className={`font-semibold text-smart-gray-800 mb-2 ${
          isMobile ? 'text-base' : 'text-lg'
        }`}>
          Erro ao carregar dados
        </h3>
        <p className={`text-smart-gray-600 ${isMobile ? 'text-sm' : 'text-base'}`}>
          {error}
        </p>
      </div>
    </div>
  );

  /**
   * Renderiza o estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
      isMobile ? 'p-6 mx-2' : 'p-8 mx-4'
    }`}>
      <div className="text-center">
        <div className={`text-smart-gray-400 mb-4 ${isMobile ? 'text-4xl' : 'text-6xl'}`}>📊</div>
        <h3 className={`font-semibold text-smart-gray-800 mb-2 ${
          isMobile ? 'text-base' : 'text-lg'
        }`}>
          Nenhum dado encontrado
        </h3>
        <p className={`text-smart-gray-600 ${isMobile ? 'text-sm' : 'text-base'}`}>
          {isMobile 
            ? 'Sem dados para o período.' 
            : 'Não há dados de vendas para o período selecionado.'
          }
        </p>
      </div>
    </div>
  );

  /**
   * Renderiza o cabeçalho da tabela
   */
  const renderTableHeader = (): React.ReactNode => (
    <thead className="bg-smart-gray-50">
      <tr>
        <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          Loja
        </th>
        <th className="px-6 py-3 text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          PDV
        </th>
        <th className="px-6 py-3 text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          DANFE
        </th>
        <th className="px-6 py-3 text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          Exchange
        </th>
        <th className="px-6 py-3 text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          Total
        </th>
        <th className="px-6 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
          % do total
        </th>
      </tr>
    </thead>
  );

  /**
   * Renderiza uma linha da tabela
   * @param item - Dados da venda
   * @param index - Índice da linha
   */
  const renderTableRow = (item: DailySalesData, index: number): React.ReactNode => {
    const grandTotal = calculateTotal();
    const percentage = calculatePercentage(item.total, grandTotal);
    
    return (
      <tr 
        key={index} 
        className={`hover:bg-smart-gray-50 transition-colors duration-200 ${
          index % 2 === 0 ? 'bg-white' : 'bg-smart-gray-50'
        }`}
      >
        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-smart-gray-900">
          {item.storeName}
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-right">
          {formatCurrency(item.pdv)}
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-right">
          {formatCurrency(item.danfe)}
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-right">
          {formatCurrency(item.exchange)}
        </td>
        <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-smart-gray-900 text-right">
          {formatCurrency(item.total)}
        </td>
        <td className="px-6 py-4">
          <div className="flex items-center space-x-3">
            {/* Barra de Progresso */}
            <div className="flex-1 bg-smart-gray-200 rounded-full h-2 overflow-hidden">
              <div 
                className="bg-smart-blue-500 h-full rounded-full transition-all duration-300"
                style={{ width: `${percentage}%` }}
              />
            </div>
            
            {/* Porcentagem */}
            <span className="text-sm font-medium text-smart-gray-700 min-w-[45px]">
              {percentage.toFixed(1)}%
            </span>
          </div>
        </td>
      </tr>
    );
  };

  /**
   * Renderiza o rodapé da tabela com totais
   */
  const renderTableFooter = (): React.ReactNode => {
    const totals = data.reduce(
      (acc, item) => ({
        pdv: acc.pdv + item.pdv,
        danfe: acc.danfe + item.danfe,
        exchange: acc.exchange + item.exchange,
        total: acc.total + item.total,
      }),
      { pdv: 0, danfe: 0, exchange: 0, total: 0 }
    );

    return (
      <tfoot className="bg-smart-red-50 border-t-2 border-smart-red-200">
        <tr>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-gray-900">
            TOTAL GERAL
          </td>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-gray-900 text-right">
            {formatCurrency(totals.pdv)}
          </td>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-gray-900 text-right">
            {formatCurrency(totals.danfe)}
          </td>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-gray-900 text-right">
            {formatCurrency(totals.exchange)}
          </td>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-red-700 text-right">
            {formatCurrency(totals.total)}
          </td>
          <td className="px-6 py-3 whitespace-nowrap text-sm font-bold text-smart-red-700 text-center">
            100.0%
          </td>
        </tr>
      </tfoot>
    );
  };

  /**
   * Renderiza um card individual para mobile
   */
  const renderMobileCard = (item: DailySalesData, index: number): React.ReactNode => (
    <div key={index} className="bg-white rounded-lg shadow-sm border border-smart-gray-200 p-4 mb-3">
      {/* Nome da loja */}
      <div className="flex items-center justify-between mb-3">
        <h3 className="font-semibold text-smart-gray-800 text-sm">
          {item.storeName}
        </h3>
        <span className="bg-smart-red-100 text-smart-red-700 px-2 py-1 rounded-full text-xs font-medium">
          Total: {formatCurrency(item.total)}
        </span>
      </div>
      
      {/* Valores detalhados */}
      <div className="grid grid-cols-3 gap-3">
        <div className="text-center">
          <div className="text-xs text-smart-gray-500 mb-1">PDV</div>
          <div className="text-sm font-medium text-smart-gray-800">
            {formatCurrency(item.pdv)}
          </div>
        </div>
        <div className="text-center">
          <div className="text-xs text-smart-gray-500 mb-1">DANFE</div>
          <div className="text-sm font-medium text-smart-gray-800">
            {formatCurrency(item.danfe)}
          </div>
        </div>
        <div className="text-center">
          <div className="text-xs text-smart-gray-500 mb-1">Exchange</div>
          <div className="text-sm font-medium text-smart-gray-800">
            {formatCurrency(item.exchange)}
          </div>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza visualização mobile com cards
   */
  const renderMobileView = (): React.ReactNode => (
    <div className="mx-2">
      {/* Header com total */}
      {data.length > 0 && (
        <div className="bg-smart-red-50 rounded-lg p-4 mb-4 border border-smart-red-200">
          <div className="text-center">
            <div className="text-sm text-smart-red-600 mb-1">Total Geral</div>
            <div className="text-xl font-bold text-smart-red-700">
              {formatCurrency(calculateTotal())}
            </div>
            <div className="text-xs text-smart-red-500 mt-1">
              {data.length} {data.length === 1 ? 'loja' : 'lojas'}
            </div>
          </div>
        </div>
      )}
      
      {/* Cards das lojas */}
      <div className="space-y-0">
        {data.map(renderMobileCard)}
      </div>
    </div>
  );

  /**
   * Renderiza a tabela de dados (desktop)
   */
  const renderDesktopTable = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-lg border border-smart-gray-100 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full divide-y divide-smart-gray-200">
          {renderTableHeader()}
          <tbody className="bg-white divide-y divide-smart-gray-200">
            {data.map(renderTableRow)}
          </tbody>
          {data.length > 0 && renderTableFooter()}
        </table>
      </div>
    </div>
  );

  /**
   * Renderiza a visualização adequada conforme o dispositivo
   */
  const renderDataView = (): React.ReactNode => {
    if (isMobile) {
      return renderMobileView();
    }
    return renderDesktopTable();
  };

  // Estados de renderização baseados no estado atual
  if (loading) {
    return renderLoading();
  }

  if (error) {
    return renderError();
  }

  if (data.length === 0) {
    return renderEmpty();
  }

  return (
    <div className={`vendas-table ${className}`}>
      {renderDataView()}
    </div>
  );
};

export default VendasTable;
