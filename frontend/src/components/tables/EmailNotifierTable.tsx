import React from 'react';
import { EmailNotifierResponse, EMAIL_NOTIFIER_TABLE_COLUMNS } from '@/types';

/**
 * Interface para as propriedades da tabela
 */
interface EmailNotifierTableProps {
  readonly data: readonly EmailNotifierResponse[];
  readonly loading: boolean;
  readonly error: string | null;
  readonly onEdit: (emailNotifier: EmailNotifierResponse) => void;
  readonly onDelete: (id: string) => void;
  readonly onSort: (field: string) => void;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  readonly isDeleting?: boolean;
  readonly className?: string;
}

/**
 * Componente de tabela para EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EmailNotifierTable: React.FC<EmailNotifierTableProps> = ({
  data,
  loading,
  error,
  onEdit,
  onDelete,
  onSort,
  sortBy,
  sortDir,
  isDeleting = false,
  className = ''
}) => {
  /**
   * Renderiza o estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <tr>
      <td colSpan={EMAIL_NOTIFIER_TABLE_COLUMNS.length} className="px-6 py-8">
        <div className="flex items-center justify-center">
          <div className="flex items-center space-x-2">
            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-smart-red-600"></div>
            <span className="text-smart-gray-600">Carregando emails...</span>
          </div>
        </div>
      </td>
    </tr>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <tr>
      <td colSpan={EMAIL_NOTIFIER_TABLE_COLUMNS.length} className="px-6 py-8">
        <div className="flex items-center justify-center">
          <div className="text-center">
            <div className="text-red-500 text-4xl mb-2">⚠️</div>
            <p className="text-red-600 font-medium">Erro ao carregar emails</p>
            <p className="text-smart-gray-500 text-sm mt-1">{error}</p>
          </div>
        </div>
      </td>
    </tr>
  );

  /**
   * Renderiza o estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <tr>
      <td colSpan={EMAIL_NOTIFIER_TABLE_COLUMNS.length} className="px-6 py-8">
        <div className="flex items-center justify-center">
          <div className="text-center">
            <div className="text-smart-gray-400 text-4xl mb-2">📧</div>
            <p className="text-smart-gray-600 font-medium">Nenhum email cadastrado</p>
            <p className="text-smart-gray-500 text-sm mt-1">
              Clique em "Adicionar Email" para cadastrar o primeiro email
            </p>
          </div>
        </div>
      </td>
    </tr>
  );

  /**
   * Renderiza o ícone de ordenação
   */
  const renderSortIcon = (field: string): React.ReactNode => {
    if (sortBy !== field) {
      return (
        <svg className="w-4 h-4 text-smart-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
        </svg>
      );
    }

    return sortDir === 'asc' ? (
      <svg className="w-4 h-4 text-smart-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
      </svg>
    ) : (
      <svg className="w-4 h-4 text-smart-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
      </svg>
    );
  };

  /**
   * Renderiza o cabeçalho da tabela
   */
  const renderTableHeader = (): React.ReactNode => (
    <thead className="bg-smart-gray-50">
      <tr>
        {EMAIL_NOTIFIER_TABLE_COLUMNS.map((column) => (
          <th
            key={column.key}
            className={`px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider ${
              column.sortable ? 'cursor-pointer hover:bg-smart-gray-100' : ''
            }`}
            onClick={() => column.sortable && onSort(column.key)}
            style={{ width: column.width }}
          >
            <div className="flex items-center space-x-1">
              <span>{column.label}</span>
              {column.sortable && renderSortIcon(column.key)}
            </div>
          </th>
        ))}
      </tr>
    </thead>
  );

  /**
   * Renderiza o status de notificação (checkmark ou X)
   */
  const renderNotificationStatus = (enabled: boolean): React.ReactNode => (
    <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
      enabled 
        ? 'bg-green-100 text-green-800' 
        : 'bg-red-100 text-red-800'
    }`}>
      {enabled ? '✓' : '✗'}
    </span>
  );

  /**
   * Renderiza as ações da linha
   */
  const renderRowActions = (emailNotifier: EmailNotifierResponse): React.ReactNode => (
    <div className="flex items-center space-x-2">
      <button
        onClick={() => onEdit(emailNotifier)}
        disabled={isDeleting}
        className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1 disabled:opacity-50 disabled:cursor-not-allowed"
        title="Editar email"
      >
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
      </button>
      <button
        onClick={() => onDelete(emailNotifier.id)}
        disabled={isDeleting}
        className="text-red-600 hover:text-red-900 transition-colors duration-200 p-1 disabled:opacity-50 disabled:cursor-not-allowed"
        title={isDeleting ? 'Removendo...' : 'Remover email'}
      >
        {isDeleting ? (
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current"></div>
        ) : (
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        )}
      </button>
    </div>
  );

  /**
   * Renderiza uma linha da tabela
   */
  const renderTableRow = (emailNotifier: EmailNotifierResponse): React.ReactNode => (
    <tr key={emailNotifier.id} className="hover:bg-smart-gray-50">
      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-smart-gray-900">
        {emailNotifier.email}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-500">
        {renderNotificationStatus(emailNotifier.dailySellNotifier)}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-500">
        {renderNotificationStatus(emailNotifier.dailyMonthNotifier)}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-500">
        {renderNotificationStatus(emailNotifier.monthYearNotifier)}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-smart-gray-500">
        {renderRowActions(emailNotifier)}
      </td>
    </tr>
  );

  /**
   * Renderiza o corpo da tabela
   */
  const renderTableBody = (): React.ReactNode => {
    if (loading) return renderLoading();
    if (error) return renderError();
    if (data.length === 0) return renderEmpty();

    return (
      <tbody className="bg-white divide-y divide-smart-gray-200">
        {data.map(renderTableRow)}
      </tbody>
    );
  };

  /**
   * Renderiza a tabela completa
   */
  const renderTable = (): React.ReactNode => (
    <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
      <table className="min-w-full divide-y divide-smart-gray-200">
        {renderTableHeader()}
        {renderTableBody()}
      </table>
    </div>
  );

  return (
    <div className={`email-notifier-table ${className}`}>
      {renderTable()}
    </div>
  );
};

export default EmailNotifierTable;
