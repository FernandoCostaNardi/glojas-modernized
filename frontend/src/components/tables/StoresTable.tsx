import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
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
 * Componente de tabela de lojas mobile-first
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
  const { isMobile } = useLayout();
  
  // Garantir que stores seja sempre um array
  const safeStores = Array.isArray(stores) ? stores : [];

  /**
   * Renderiza o ícone de ordenação
   */
  const renderSortIcon = (field: string) => {
    if (sortBy !== field || !onSort) return null;
    
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

  /**
   * Renderiza um card de loja para mobile
   */
  const renderStoreCard = (store: ApiStore): React.ReactNode => (
    <div key={store.id} className="bg-white rounded-lg shadow-sm border border-smart-gray-200 p-4 mb-3">
      {/* Header do card */}
      <div className="flex items-center justify-between mb-3">
        <div>
          <div className="text-sm font-semibold text-smart-gray-900">
            {store.name}
          </div>
          <div className="text-xs text-smart-gray-500 mt-1">
            Código: {store.code}
          </div>
        </div>
        
        {/* Status badge */}
        <span
          className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
            store.active
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          }`}
        >
          {store.active ? 'Ativa' : 'Inativa'}
        </span>
      </div>
      
      {/* Detalhes */}
      <div className="space-y-2 mb-4">
        {store.cnpj && (
          <div>
            <div className="text-xs text-smart-gray-500">CNPJ</div>
            <div className="text-sm text-smart-gray-800">{store.cnpj}</div>
          </div>
        )}
        {store.address && (
          <div>
            <div className="text-xs text-smart-gray-500">Endereço</div>
            <div className="text-sm text-smart-gray-800">{store.address}</div>
          </div>
        )}
        {store.phone && (
          <div>
            <div className="text-xs text-smart-gray-500">Telefone</div>
            <div className="text-sm text-smart-gray-800">{store.phone}</div>
          </div>
        )}
      </div>
      
      {/* Ações */}
      <div className="flex justify-end pt-3 border-t border-smart-gray-100">
        <button
          onClick={() => onEditStore(store)}
          className="flex items-center px-3 py-1.5 text-xs font-medium text-smart-blue-600 hover:text-smart-blue-800 hover:bg-smart-blue-50 rounded-md transition-colors duration-200"
        >
          <svg className="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          Editar
        </button>
      </div>
    </div>
  );

  /**
   * Renderiza visualização mobile com cards
   */
  const renderMobileView = (): React.ReactNode => (
    <div className="mx-2">
      {safeStores.map(renderStoreCard)}
    </div>
  );

  /**
   * Renderiza tabela desktop
   */
  const renderDesktopTable = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md border border-smart-gray-100 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-smart-gray-200">
          <thead className="bg-smart-gray-50">
            <tr>
              <th 
                className={`px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
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
                className={`px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
                  onSort ? 'cursor-pointer hover:bg-smart-gray-100' : ''
                }`}
                onClick={() => onSort?.('name')}
              >
                <div className="flex items-center">
                  Nome
                  {renderSortIcon('name')}
                </div>
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                CNPJ
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Endereço
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Telefone
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Ações
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-smart-gray-200">
            {safeStores.map((store) => (
              <tr key={store.id} className="hover:bg-smart-gray-50 transition-colors duration-200">
                <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-smart-gray-900">
                  {store.code}
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-smart-gray-900">
                  {store.name}
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-smart-gray-900">
                  {store.cnpj || '-'}
                </td>
                <td className="px-4 py-3 text-sm text-smart-gray-900 max-w-xs truncate">
                  {store.address || '-'}
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-smart-gray-900">
                  {store.phone || '-'}
                </td>
                <td className="px-4 py-3 whitespace-nowrap">
                  <span
                    className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      store.active
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {store.active ? 'Ativa' : 'Inativa'}
                  </span>
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm font-medium">
                  <button
                    onClick={() => onEditStore(store)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                    title="Editar loja"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  // Estados de loading
  if (isLoading) {
    return (
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
              {isMobile ? 'Carregando...' : 'Carregando lojas...'}
            </span>
          </div>
        </div>
      </div>
    );
  }

  // Estado vazio
  if (safeStores.length === 0) {
    return (
      <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 ${
        isMobile ? 'p-6 mx-2' : 'p-8 mx-4'
      }`}>
        <div className="text-center">
          <div className={`text-smart-gray-400 mb-4 ${isMobile ? 'text-4xl' : 'text-6xl'}`}>🏪</div>
          <h3 className={`font-semibold text-smart-gray-800 mb-2 ${
            isMobile ? 'text-base' : 'text-lg'
          }`}>
            Nenhuma loja encontrada
          </h3>
          <p className={`text-smart-gray-600 ${isMobile ? 'text-sm' : 'text-base'}`}>
            {isMobile 
              ? 'Sem lojas cadastradas.' 
              : 'Não há lojas cadastradas no sistema.'
            }
          </p>
        </div>
      </div>
    );
  }

  /**
   * Renderiza a visualização adequada conforme o dispositivo
   */
  if (isMobile) {
    return renderMobileView();
  }
  
  return renderDesktopTable();
};

export { StoresTable };