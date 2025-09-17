import React from 'react';
import { EventOriginResponse, EventSource, getEventSourceCompactName } from '@/types';

/**
 * Props da tabela de códigos de origem
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface EventOriginTableProps {
  readonly eventOrigins: readonly EventOriginResponse[];
  readonly isLoading: boolean;
  readonly onEditEventOrigin: (eventOrigin: EventOriginResponse) => void;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly onSort: (field: string) => void;
}

/**
 * Componente para renderizar badge de EventSource
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EventSourceBadge: React.FC<{ eventSource: EventSource }> = ({ eventSource }) => {
  const getBadgeStyles = (source: EventSource): string => {
    switch (source) {
      case 'PDV':
        return 'bg-blue-100 text-blue-800';
      case 'EXCHANGE':
        return 'bg-green-100 text-green-800';
      case 'DANFE':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getDisplayName = (source: EventSource): string => {
    return getEventSourceCompactName(source);
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getBadgeStyles(eventSource)}`}>
      {getDisplayName(eventSource)}
    </span>
  );
};

/**
 * Componente para renderizar cabeçalho da tabela com ordenação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const SortableHeader: React.FC<{
  field: string;
  label: string;
  currentSortBy: string;
  currentSortDir: 'asc' | 'desc';
  onSort: (field: string) => void;
}> = ({ field, label, currentSortBy, currentSortDir, onSort }) => {
  const isActive = currentSortBy === field;
  
  return (
    <th 
      className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider cursor-pointer hover:bg-smart-gray-100 transition-colors duration-200"
      onClick={() => onSort(field)}
    >
      <div className="flex items-center space-x-1">
        <span>{label}</span>
        {isActive && (
          <svg 
            className={`w-4 h-4 ${currentSortDir === 'asc' ? 'transform rotate-180' : ''}`} 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
          </svg>
        )}
        {!isActive && (
          <svg className="w-4 h-4 opacity-30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
          </svg>
        )}
      </div>
    </th>
  );
};

/**
 * Tabela de códigos de origem do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EventOriginTable: React.FC<EventOriginTableProps> = ({
  eventOrigins,
  isLoading,
  onEditEventOrigin,
  sortBy,
  sortDir,
  onSort
}) => {
  /**
   * Renderiza o estado de carregamento
   */
  if (isLoading) {
    return (
      <div className="text-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600 mx-auto"></div>
        <p className="mt-2 text-smart-gray-600">Carregando códigos de origem...</p>
      </div>
    );
  }

  /**
   * Renderiza estado vazio
   */
  if (eventOrigins.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="mx-auto h-12 w-12 text-smart-gray-400">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <h3 className="mt-2 text-sm font-medium text-smart-gray-900">Nenhum código de origem encontrado</h3>
        <p className="mt-1 text-sm text-smart-gray-500">
          Tente ajustar os filtros ou criar um novo código de origem.
        </p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-smart-gray-200">
        <thead className="bg-smart-gray-50">
          <tr>
            <SortableHeader
              field="eventSource"
              label="Fonte do Evento"
              currentSortBy={sortBy}
              currentSortDir={sortDir}
              onSort={onSort}
            />
            <SortableHeader
              field="sourceCode"
              label="Código da Fonte"
              currentSortBy={sortBy}
              currentSortDir={sortDir}
              onSort={onSort}
            />
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Ações
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-smart-gray-200">
          {eventOrigins.map((eventOrigin) => (
            <tr key={eventOrigin.id} className="hover:bg-smart-gray-50 transition-colors duration-200">
              <td className="px-6 py-4 whitespace-nowrap">
                <EventSourceBadge eventSource={eventOrigin.eventSource} />
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="text-sm font-medium text-smart-gray-900">
                  {eventOrigin.sourceCode}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div className="flex items-center space-x-2">
                  {/* Botão Editar */}
                  <button
                    onClick={() => onEditEventOrigin(eventOrigin)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1 rounded-md hover:bg-smart-blue-50"
                    title="Editar código de origem"
                    aria-label={`Editar código de origem ${eventOrigin.sourceCode}`}
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

export { EventOriginTable };
