import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { MonthlySalesReportResponse } from '../services/vendasApi';

/**
 * Interface para as propriedades do VendasTableMonthly
 */
interface VendasTableMonthlyProps {
  readonly data: MonthlySalesReportResponse[];
  readonly loading: boolean;
  readonly error: string | null;
  readonly className?: string;
}

/**
 * Componente de tabela para exibir dados de vendas mensais.
 * Exibe dados agregados por loja com total e percentual de participação.
 * Seguindo princípios de Clean Code com responsabilidade única.
 * 
 * @param data - Array de dados de vendas mensais
 * @param loading - Estado de carregamento
 * @param error - Mensagem de erro (se houver)
 * @param className - Classes CSS adicionais
 */
const VendasTableMonthly: React.FC<VendasTableMonthlyProps> = ({ 
  data, 
  loading, 
  error, 
  className = '' 
}) => {
  const { isMobile } = useLayout();
  
  /**
   * Formata valor monetário para exibição
   * @param value - Valor a ser formatado
   * @returns String formatada em reais
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  /**
   * Formata percentual para exibição
   * @param value - Percentual a ser formatado
   * @returns String formatada com símbolo %
   */
  const formatPercentage = (value: number): string => {
    return `${value.toFixed(2)}%`;
  };

  /**
   * Renderiza o estado de carregamento
   */
  const renderLoading = (): React.ReactNode => (
    <div className="flex items-center justify-center py-12">
      <div className="flex items-center space-x-3">
        <svg className="animate-spin h-6 w-6 text-smart-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span className="text-smart-gray-600 font-medium">Carregando dados de vendas mensais...</span>
      </div>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className="flex items-center justify-center py-12">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-red-500 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Erro ao carregar dados</h3>
        <p className="text-smart-gray-600">{error}</p>
      </div>
    </div>
  );

  /**
   * Renderiza o estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className="flex items-center justify-center py-12">
      <div className="text-center">
        <svg className="mx-auto h-12 w-12 text-smart-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">Nenhum dado encontrado</h3>
        <p className="text-smart-gray-600">Não há dados de vendas mensais para o período selecionado.</p>
      </div>
    </div>
  );

  /**
   * Renderiza o cabeçalho da tabela
   */
  const renderTableHeader = (): React.ReactNode => (
    <thead className="bg-smart-gray-50">
      <tr>
        <th className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider`}>
          Loja
        </th>
        <th className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider`}>
          Total
        </th>
        <th className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-right text-xs font-medium text-smart-gray-500 uppercase tracking-wider`}>
          % do Total
        </th>
      </tr>
    </thead>
  );

  /**
   * Renderiza uma linha da tabela
   * @param item - Dados da loja
   * @param index - Índice da linha
   */
  const renderTableRow = (item: MonthlySalesReportResponse, index: number): React.ReactNode => (
    <tr key={`${item.storeName}-${index}`} className={index % 2 === 0 ? 'bg-white' : 'bg-smart-gray-50'}>
      <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-4'} whitespace-nowrap`}>
        <div className={`${isMobile ? 'text-xs' : 'text-sm'} font-medium text-smart-gray-900`}>
          {item.storeName}
        </div>
      </td>
      <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-4'} whitespace-nowrap text-right`}>
        <div className={`${isMobile ? 'text-xs' : 'text-sm'} font-medium text-smart-gray-900`}>
          {formatCurrency(item.total)}
        </div>
      </td>
      <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-4'} whitespace-nowrap text-right`}>
        <div className="flex items-center justify-end space-x-3">
          <div className="w-20 bg-smart-gray-200 rounded-full h-2">
            <div 
              className="bg-smart-blue-600 h-2 rounded-full transition-all duration-300"
              style={{ width: `${Math.min(item.percentageOfTotal, 100)}%` }}
            ></div>
          </div>
          <div className={`${isMobile ? 'text-xs' : 'text-sm'} font-medium text-smart-gray-900 min-w-[60px] text-right`}>
            {formatPercentage(item.percentageOfTotal)}
          </div>
        </div>
      </td>
    </tr>
  );

  /**
   * Renderiza o rodapé da tabela com totais
   */
  const renderTableFooter = (): React.ReactNode => {
    const grandTotal = data.reduce((sum, item) => sum + item.total, 0);
    
    return (
      <tfoot className="bg-smart-gray-100">
        <tr>
          <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-left ${isMobile ? 'text-xs' : 'text-sm'} font-bold text-smart-gray-900`}>
            Total Geral
          </td>
          <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-right ${isMobile ? 'text-xs' : 'text-sm'} font-bold text-smart-gray-900`}>
            {formatCurrency(grandTotal)}
          </td>
          <td className={`${isMobile ? 'px-3 py-2' : 'px-6 py-3'} text-right ${isMobile ? 'text-xs' : 'text-sm'} font-bold text-smart-gray-900`}>
            100.00%
          </td>
        </tr>
      </tfoot>
    );
  };

  /**
   * Renderiza o conteúdo principal da tabela
   */
  const renderTableContent = (): React.ReactNode => {
    if (loading) {
      return renderLoading();
    }

    if (error) {
      return renderError();
    }

    if (!data || data.length === 0) {
      return renderEmpty();
    }

    return (
      <div className="bg-white rounded-lg shadow-lg border border-smart-gray-100 overflow-hidden w-full max-w-full box-border">
        <div className="overflow-x-auto w-full max-w-full">
          <table className="w-full divide-y divide-smart-gray-200 min-w-full">
            {renderTableHeader()}
            <tbody className="bg-white divide-y divide-smart-gray-200">
              {data.map(renderTableRow)}
            </tbody>
            {renderTableFooter()}
          </table>
        </div>
      </div>
    );
  };

  return (
    <div className={`vendas-table-monthly w-full max-w-full box-border ${className}`}>
      <div className="mb-4">
        <h3 className={`font-medium text-smart-gray-900 ${
          isMobile ? 'text-sm' : 'text-lg'
        }`}>
          Relatório de Vendas Mensais
        </h3>
        <p className={`text-smart-gray-600 ${
          isMobile ? 'text-xs' : 'text-sm'
        }`}>
          Dados agregados por loja com percentual de participação
        </p>
      </div>
      
      {renderTableContent()}
    </div>
  );
};

export default VendasTableMonthly;
