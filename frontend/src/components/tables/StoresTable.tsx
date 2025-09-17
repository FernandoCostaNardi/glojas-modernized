import React from 'react';
import { ApiStore } from '@/types';

/**
 * Props da tabela de lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface StoresTableProps {
  readonly stores: readonly ApiStore[];
  readonly isLoading: boolean;
  readonly onEditStore: (store: ApiStore) => void;
  readonly sortBy?: string;
  readonly sortDir?: string;
  readonly onSort?: (field: string) => void;
}

/**
 * Componente de tabela de lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StoresTable: React.FC<StoresTableProps> = ({
  stores,
  isLoading,
  onEditStore,
  sortBy = 'code',
  sortDir = 'asc',
  onSort
}) => {
  // Garantir que stores seja sempre um array
  const safeStores = Array.isArray(stores) ? stores : [];

  /**
   * Formata a data retornada pela API (array de números) para string legível
   * @param dateArray Array de números [ano, mês, dia, hora, minuto, segundo, nanosegundo]
   * @returns String formatada da data
   */
  const formatApiDate = (dateArray: number[]): string => {
    if (!dateArray || dateArray.length < 6) return 'Data não disponível';
    
    const [year, month, day, hour, minute, second] = dateArray;
    
    // Validação de valores
    if (year === undefined || month === undefined || day === undefined || 
        hour === undefined || minute === undefined || second === undefined) {
      return 'Data não disponível';
    }
    
    const date = new Date(year, month - 1, day, hour, minute, second);
    
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  /**
   * Renderiza cabeçalho clicável para ordenação
   * @param field Campo para ordenação
   * @param label Texto do cabeçalho
   * @returns Elemento JSX do cabeçalho
   */
  const renderSortableHeader = (field: string, label: string): React.ReactNode => {
    const isActive = sortBy === field;
    const isAsc = sortDir === 'asc';
    
    return (
      <th 
        className={`px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
          onSort ? 'cursor-pointer hover:bg-smart-gray-100 select-none' : ''
        }`}
        onClick={() => onSort?.(field)}
      >
        <div className="flex items-center space-x-1">
          <span>{label}</span>
          {onSort && (
            <div className="flex flex-col">
              <svg 
                className={`w-3 h-3 ${isActive && isAsc ? 'text-smart-red-600' : 'text-smart-gray-400'}`}
                fill="currentColor" 
                viewBox="0 0 20 20"
              >
                <path fillRule="evenodd" d="M14.707 12.707a1 1 0 01-1.414 0L10 9.414l-3.293 3.293a1 1 0 01-1.414-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 010 1.414z" clipRule="evenodd" />
              </svg>
              <svg 
                className={`w-3 h-3 ${isActive && !isAsc ? 'text-smart-red-600' : 'text-smart-gray-400'}`}
                fill="currentColor" 
                viewBox="0 0 20 20"
              >
                <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
              </svg>
            </div>
          )}
        </div>
      </th>
    );
  };

  if (isLoading) {
    return (
      <div className="text-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600 mx-auto"></div>
        <p className="mt-2 text-smart-gray-600">Carregando lojas...</p>
      </div>
    );
  }

  // Se não há lojas para exibir
  if (safeStores.length === 0) {
    return (
      <div className="text-center py-8">
        <div className="text-6xl mb-4">🏪</div>
        <h3 className="text-lg font-medium text-smart-gray-900 mb-2">
          Nenhuma loja encontrada
        </h3>
        <p className="text-smart-gray-500">
          Não há lojas cadastradas no sistema ainda.
        </p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-smart-gray-200">
        <thead className="bg-smart-gray-50">
          <tr>
            {renderSortableHeader('code', 'Código')}
            {renderSortableHeader('name', 'Nome')}
            {renderSortableHeader('city', 'Cidade')}
            {renderSortableHeader('createdAt', 'Data de Criação')}
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Status
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Ações
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-smart-gray-200">
          {safeStores.map((store) => (
            <tr key={store.id} className="hover:bg-smart-gray-50">
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="flex items-center">
                  <div className="w-8 h-8 bg-smart-red-100 rounded-full flex items-center justify-center">
                    <span className="text-smart-red-600 font-medium text-xs">
                      🏪
                    </span>
                  </div>
                  <div className="ml-3">
                    <div className="text-sm font-medium text-smart-gray-900">
                      {store.code}
                    </div>
                  </div>
                </div>
              </td>
              <td className="px-6 py-4">
                <div className="text-sm font-medium text-smart-gray-900">
                  {store.name}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="text-sm text-smart-gray-900">
                  {store.city || 'Não informado'}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="text-sm text-smart-gray-900">
                  {formatApiDate(store.createdAt)}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  store.status
                    ? 'bg-green-100 text-green-800'
                    : 'bg-red-100 text-red-800'
                }`}>
                  {store.status ? 'Ativa' : 'Inativa'}
                </span>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div className="flex items-center space-x-2">
                  {/* Botão Editar */}
                  <button
                    onClick={() => onEditStore(store)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                    title="Editar loja"
                    aria-label={`Editar loja ${store.name}`}
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

export { StoresTable };
