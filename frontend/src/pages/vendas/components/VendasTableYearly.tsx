import React from 'react';
import { YearlySalesReportResponse } from '../services/vendasApi';
import { formatCurrency, formatPercentage } from '@/utils/formatters';

/**
 * Interface para as propriedades do VendasTableYearly
 */
interface VendasTableYearlyProps {
  readonly data: YearlySalesReportResponse[];
  readonly loading: boolean;
  readonly error: string | null;
}

/**
 * Componente de tabela para exibir dados de vendas anuais
 * Exibe loja, total e percentual de participação com progress bar
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasTableYearly: React.FC<VendasTableYearlyProps> = ({ data, loading, error }) => {

  /**
   * Renderiza o corpo da tabela baseado no estado
   */
  const renderTableBody = (): React.ReactNode => {
    if (loading) {
      return renderLoadingState();
    }

    if (error) {
      return renderErrorState(error);
    }

    if (data.length === 0) {
      return renderEmptyState();
    }

    return (
      <tbody className="bg-white divide-y divide-smart-gray-200">
        {data.map((item, index) => renderTableRow(item, index))}
      </tbody>
    );
  };

  /**
   * Renderiza uma linha da tabela
   */
  const renderTableRow = (item: YearlySalesReportResponse, index: number): React.ReactNode => (
    <tr key={index} className="hover:bg-smart-gray-50">
      <td className="px-6 py-4 whitespace-nowrap">
        <div className="text-sm font-medium text-smart-gray-900">{item.storeName}</div>
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-right">
        <div className="text-sm font-medium text-smart-gray-900">
          {formatCurrency(item.total)}
        </div>
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-right">
        <div className="flex items-center justify-end space-x-3">
          <div className="w-20 bg-smart-gray-200 rounded-full h-2">
            <div
              className="bg-smart-blue-600 h-2 rounded-full transition-all duration-300"
              style={{ width: `${Math.min(item.percentageOfTotal, 100)}%` }}
            ></div>
          </div>
          <div className="text-sm font-medium text-smart-gray-900 min-w-[60px] text-right">
            {formatPercentage(item.percentageOfTotal)}
          </div>
        </div>
      </td>
    </tr>
  );

  /**
   * Renderiza o rodapé da tabela com total geral
   */
  const renderTableFooter = (): React.ReactNode => {
    if (data.length === 0 || loading || error) {
      return null;
    }

    const totalSum = data.reduce((sum, item) => sum + item.total, 0);

    return (
      <tfoot className="bg-smart-gray-100">
        <tr>
          <td className="px-6 py-3 text-left text-xs font-bold text-smart-gray-700 uppercase tracking-wider">
            Total Geral
          </td>
          <td className="px-6 py-3 text-right text-sm font-bold text-smart-gray-900">
            {formatCurrency(totalSum)}
          </td>
          <td className="px-6 py-3 text-right text-sm font-bold text-smart-gray-900">
            {formatPercentage(100)}
          </td>
        </tr>
      </tfoot>
    );
  };

  /**
   * Renderiza estado de loading
   */
  const renderLoadingState = (): React.ReactNode => (
    <tbody>
      <tr>
        <td colSpan={3} className="text-center py-8 text-smart-gray-500">
          <div className="flex items-center justify-center space-x-2">
            <svg className="animate-spin h-5 w-5 text-smart-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>Carregando dados...</span>
          </div>
        </td>
      </tr>
    </tbody>
  );

  /**
   * Renderiza estado de erro
   */
  const renderErrorState = (errorMessage: string): React.ReactNode => (
    <tbody>
      <tr>
        <td colSpan={3} className="text-center py-8 text-red-600">
          <div className="flex items-center justify-center space-x-2">
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <span>Erro: {errorMessage}</span>
          </div>
        </td>
      </tr>
    </tbody>
  );

  /**
   * Renderiza estado vazio
   */
  const renderEmptyState = (): React.ReactNode => (
    <tbody>
      <tr>
        <td colSpan={3} className="text-center py-8 text-smart-gray-500">
          <div className="flex items-center justify-center space-x-2">
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-9a1 1 0 00-2 0v4a1 1 0 102 0V9zm3-1a1 1 0 10-2 0v5a1 1 0 102 0V8z" clipRule="evenodd" />
            </svg>
            <span>Nenhum dado de vendas anual encontrado para o período.</span>
          </div>
        </td>
      </tr>
    </tbody>
  );

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-smart-gray-200">
          <thead className="bg-smart-gray-50">
            <tr>
              <th
                scope="col"
                className="px-6 py-3 text-left text-xs font-bold text-smart-gray-700 uppercase tracking-wider"
              >
                Loja
              </th>
              <th
                scope="col"
                className="px-6 py-3 text-right text-xs font-bold text-smart-gray-700 uppercase tracking-wider"
              >
                Total
              </th>
              <th
                scope="col"
                className="px-6 py-3 text-right text-xs font-bold text-smart-gray-700 uppercase tracking-wider"
              >
                % do Total
              </th>
            </tr>
          </thead>
          {renderTableBody()}
          {renderTableFooter()}
        </table>
      </div>
    </div>
  );
};

export default VendasTableYearly;
