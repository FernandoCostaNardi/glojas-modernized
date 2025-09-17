import React from 'react';
import { Operation } from '@/types';
import { formatDateToBrazilian } from '@/utils/dateUtils';

/**
 * Props da tabela de operações
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface OperationsTableProps {
  readonly operations: readonly Operation[];
  readonly isLoading: boolean;
  readonly onEditOperation: (operation: Operation) => void;
  readonly onDeleteOperation: (operation: Operation) => void;
  readonly onToggleOperationStatus: (operation: Operation) => void;
  readonly sortBy?: string;
  readonly sortDir?: string;
  readonly onSort?: (field: string) => void;
}

/**
 * Tabela de operações do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const OperationsTable: React.FC<OperationsTableProps> = ({
  operations,
  isLoading,
  onEditOperation,
  onDeleteOperation,
  onToggleOperationStatus,
  sortBy,
  sortDir,
  onSort
}) => {
  // Garantir que operations seja sempre um array para evitar erros
  const safeOperations = Array.isArray(operations) ? operations : [];

  /**
   * Renderiza o ícone de ordenação
   */
  const renderSortIcon = (field: string) => {
    if (!onSort || sortBy !== field) return null;
    
    return (
      <svg 
        className={`w-3 h-3 ml-1 ${sortDir === 'asc' ? 'rotate-180' : ''}`} 
        fill="none" 
        stroke="currentColor" 
        viewBox="0 0 24 24"
      >
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
      </svg>
    );
  };

  if (isLoading) {
    return (
      <div className="text-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600 mx-auto"></div>
        <p className="mt-2 text-smart-gray-600">Carregando operações...</p>
      </div>
    );
  }

  if (safeOperations.length === 0) {
    return (
      <div className="text-center py-8">
        <div className="text-6xl mb-4">⚙️</div>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">
          Nenhuma operação encontrada
        </h3>
        <p className="text-smart-gray-600">
          Crie sua primeira operação para começar a gerenciar os códigos do sistema.
        </p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-smart-gray-200">
        <thead className="bg-smart-gray-50">
          <tr>
            <th 
              className={`px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
                onSort ? 'cursor-pointer hover:bg-smart-gray-100' : ''
              }`}
              onClick={() => onSort?.('code')}
            >
              <div className="flex items-center">
                Código
                {renderSortIcon('code')}
              </div>
            </th>
            <th 
              className={`px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
                onSort ? 'cursor-pointer hover:bg-smart-gray-100' : ''
              }`}
              onClick={() => onSort?.('description')}
            >
              <div className="flex items-center">
                Descrição
                {renderSortIcon('description')}
              </div>
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Evento
            </th>
            <th 
              className={`px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
                onSort ? 'cursor-pointer hover:bg-smart-gray-100' : ''
              }`}
              onClick={() => onSort?.('createdAt')}
            >
              <div className="flex items-center">
                Data de Criação
                {renderSortIcon('createdAt')}
              </div>
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Ações
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-smart-gray-200">
          {safeOperations.map((operation) => (
            <tr key={operation.id} className="hover:bg-smart-gray-50">
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="text-sm font-medium text-smart-gray-900">{operation.code}</div>
              </td>
              <td className="px-6 py-4">
                <div className="text-sm text-smart-gray-900 max-w-xs">
                  {operation.description}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  operation.operationSource === 'SELL' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-blue-100 text-blue-800'
                }`}>
                  {operation.operationSource === 'SELL' ? 'Venda' : 'Troca'}
                </span>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-500">
                {formatDateToBrazilian(operation.createdAt)}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div className="flex items-center space-x-2">
                  {/* Botão Editar */}
                  <button
                    onClick={() => onEditOperation(operation)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                    title="Editar operação"
                    aria-label={`Editar operação ${operation.code}`}
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export { OperationsTable };
