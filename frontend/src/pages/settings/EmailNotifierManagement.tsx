import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import EmailNotifierTable from '@/components/tables/EmailNotifierTable';
import EmailNotifierModal from '@/components/modals/EmailNotifierModal';
import { Pagination } from '@/components/pagination';
import { useEmailNotifierManagement } from '@/hooks/useEmailNotifierManagement';
import { EmailNotifierResponse } from '@/types';

/**
 * Página de Gerenciamento de Notificações por Email
 * Permite cadastrar, editar e gerenciar notificações por email do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EmailNotifierManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Hook customizado para lógica de negócio
  const {
    emailNotifiers,
    isLoading,
    error,
    currentPage,
    sortBy,
    sortDir,
    totalElements,
    totalPages,
    showModal,
    editingEmailNotifier,
    modalMode,
    isSubmitting,
    isDeleting,
    changePage,
    handleSort,
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    createEmailNotifier,
    updateEmailNotifier,
    deleteEmailNotifier
  } = useEmailNotifierManagement();

  /**
   * Verifica se o usuário tem permissão para gerenciar notificações por email
   */
  const canManageEmailNotifiers = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  /**
   * Abre modal para criar novo email
   */
  const handleCreateEmailNotifier = (): void => {
    openCreateModal();
  };

  /**
   * Abre modal para editar email existente
   */
  const handleEditEmailNotifier = (emailNotifier: EmailNotifierResponse): void => {
    openEditModal(emailNotifier);
  };

  /**
   * Manipula salvamento de email (criação ou edição)
   */
  const handleSaveEmailNotifier = async (data: any): Promise<void> => {
    if (modalMode === 'create') {
      await createEmailNotifier(data);
    } else if (modalMode === 'edit' && editingEmailNotifier) {
      await updateEmailNotifier(editingEmailNotifier.id, data);
    }
  };

  /**
   * Manipula mudança de página
   */
  const handlePageChange = (page: number): void => {
    changePage(page);
  };

  /**
   * Manipula ordenação
   */
  const handleSortChange = (field: string): void => {
    handleSort(field);
  };

  /**
   * Manipula remoção de email
   */
  const handleDeleteEmailNotifier = async (id: string): Promise<void> => {
    if (window.confirm('Tem certeza que deseja remover este email? Esta ação não pode ser desfeita.')) {
      try {
        await deleteEmailNotifier(id);
      } catch (error) {
        console.error('Erro ao remover email:', error);
        // Aqui poderia mostrar uma notificação de erro
      }
    }
  };

  // Verificação de permissão
  if (!canManageEmailNotifiers()) {
    return (
      <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
        <Header isSidebarCollapsed={isSidebarCollapsed} />
        <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
          <Sidebar onCollapseChange={setIsSidebarCollapsed} />
          <div className="flex-1 flex items-center justify-center overflow-y-auto">
            <div className="text-center p-4">
              <div className="text-6xl mb-4">🚫</div>
              <h1 className="text-2xl font-bold text-smart-gray-800 mb-2">
                Acesso Negado
              </h1>
              <p className="text-smart-gray-600">
                Você não tem permissão para gerenciar notificações por email.
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-4 md:mb-8">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3 md:gap-0">
        <div>
          <h1 className="text-xl md:text-3xl font-bold text-smart-gray-800 mb-2">
            Notificações por Email 📧
          </h1>
          <p className="text-sm md:text-base text-smart-gray-600">
            Gerencie emails que receberão notificações de vendas do sistema.
          </p>
        </div>
        <button
          onClick={handleCreateEmailNotifier}
          disabled={isLoading || isSubmitting}
          className="w-full md:w-auto inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-smart-red-600 hover:bg-smart-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-red-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Adicionar Email
        </button>
      </div>
    </div>
  );

  /**
   * Renderiza estatísticas da página
   */
  const renderStats = (): React.ReactNode => (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-6 mb-4 md:mb-6">
      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="text-2xl">📧</div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Total de Emails
                </dt>
                <dd className="text-lg font-medium text-smart-gray-900">
                  {totalElements}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="text-2xl">📊</div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Notificações Diárias
                </dt>
                <dd className="text-lg font-medium text-smart-gray-900">
                  {emailNotifiers.filter(e => e.dailySellNotifier).length}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="text-2xl">📈</div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Notificações Mensais
                </dt>
                <dd className="text-lg font-medium text-smart-gray-900">
                  {emailNotifiers.filter(e => e.dailyMonthNotifier).length}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza a tabela de emails
   */
  const renderEmailNotifierTable = (): React.ReactNode => (
    <div className="bg-white shadow overflow-hidden sm:rounded-md w-full max-w-full box-border">
      <div className="w-full max-w-full overflow-x-auto">
        <EmailNotifierTable
          data={emailNotifiers}
          loading={isLoading}
          error={error}
          onEdit={handleEditEmailNotifier}
          onDelete={handleDeleteEmailNotifier}
          onSort={handleSortChange}
          sortBy={sortBy}
          sortDir={sortDir}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );

  /**
   * Renderiza a paginação
   */
  const renderPagination = (): React.ReactNode => {
    if (totalPages <= 1) return null;

    return (
      <div className="mt-4 md:mt-6">
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={20}
          onPageChange={handlePageChange}
        />
      </div>
    );
  };

  /**
   * Renderiza o modal
   */
  const renderModal = (): React.ReactNode => (
    <EmailNotifierModal
      isOpen={showModal}
      onClose={closeModal}
      mode={modalMode}
      emailNotifier={editingEmailNotifier}
      onSuccess={onModalSuccess}
      onSave={handleSaveEmailNotifier}
      isSubmitting={isSubmitting}
    />
  );

  return (
    <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
      {/* Header */}
      <Header isSidebarCollapsed={isSidebarCollapsed} />
      
      {/* Layout principal */}
      <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
        {/* Sidebar */}
        <Sidebar onCollapseChange={setIsSidebarCollapsed} />
        
        {/* Conteúdo principal */}
        <div className="flex-1 min-w-0 w-full max-w-full overflow-x-hidden">
          <main className="h-full bg-smart-gray-50 overflow-x-hidden overflow-y-auto w-full max-w-full">
            <div className="p-3 md:p-6 w-full max-w-full box-border">
              {renderPageHeader()}
              {renderStats()}
              {renderEmailNotifierTable()}
              {renderPagination()}
            </div>
          </main>
        </div>
      </div>

      {/* Modal */}
      {renderModal()}
    </div>
  );
};

export default EmailNotifierManagement;
