import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { EventOriginFilters } from '@/components/filters';
import { EventOriginTable } from '@/components/tables';
import { Pagination } from '@/components/pagination';
import EventOriginModal from '@/components/modals/EventOriginModal';
import { useEventOriginManagement } from '@/hooks/useEventOriginManagement';
import { EventOriginResponse } from '@/types';

/**
 * Página de Gerenciamento de Códigos de Origem
 * Permite cadastrar, editar e gerenciar códigos de origem do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EventOriginManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Hook customizado para lógica de negócio
  const {
    eventOrigins,
    isLoading,
    error,
    filters,
    currentPage,
    pageSize,
    sortBy,
    sortDir,
    totalElements,
    totalPages,
    totalPdv,
    totalExchange,
    totalDanfe,
    totalEventOrigins,
    showModal,
    editingEventOrigin,
    modalMode,
    isSubmitting,
    setFilters,
    clearFilters,
    changePage,
    handleSort,
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    createEventOrigin,
    updateEventOrigin
  } = useEventOriginManagement();

  /**
   * Verifica se o usuário tem permissão para gerenciar códigos de origem
   */
  const canManageEventOrigins = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  /**
   * Abre modal para criar novo código de origem
   */
  const handleCreateEventOrigin = (): void => {
    openCreateModal();
  };

  /**
   * Abre modal para editar código de origem existente
   */
  const handleEditEventOrigin = (eventOrigin: EventOriginResponse): void => {
    openEditModal(eventOrigin);
  };

  /**
   * Manipula salvamento de código de origem (criação ou edição)
   */
  const handleSaveEventOrigin = async (data: any): Promise<void> => {
    if (modalMode === 'create') {
      await createEventOrigin(data);
    } else if (modalMode === 'edit' && editingEventOrigin) {
      await updateEventOrigin(editingEventOrigin.id, data);
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

  // Verificação de permissão
  if (!canManageEventOrigins()) {
    return (
      <div className="min-h-screen bg-smart-gray-50">
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <div className="mx-auto h-12 w-12 text-smart-gray-400">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <h3 className="mt-2 text-sm font-medium text-smart-gray-900">Acesso Negado</h3>
            <p className="mt-1 text-sm text-smart-gray-500">
              Você não tem permissão para acessar esta página.
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col bg-smart-gray-50">
      {/* Header */}
      <Header isSidebarCollapsed={isSidebarCollapsed} />
      
      {/* Layout principal */}
      <div className="flex flex-1 overflow-hidden relative">
        {/* Sidebar */}
        <Sidebar onCollapseChange={setIsSidebarCollapsed} />
        
        {/* Conteúdo principal */}
        <div className="flex-1 overflow-auto">
          <div className="p-6">
            {/* Page Header */}
            <div className="mb-6">
              <div className="flex items-center justify-between">
                <div>
                  <h1 className="text-2xl font-bold text-smart-gray-900">
                    Códigos de Origem
                  </h1>
                  <p className="mt-1 text-sm text-smart-gray-600">
                    Gerencie os códigos de origem para produtos e operações do sistema
                  </p>
                </div>
                <button
                  onClick={handleCreateEventOrigin}
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-smart-blue-600 hover:bg-smart-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-blue-500 transition-colors duration-200"
                >
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                  Novo Código
                </button>
              </div>
            </div>

            {/* Error Display */}
            {error && (
              <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
                <div className="flex">
                  <div className="flex-shrink-0">
                    <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="ml-3">
                    <h3 className="text-sm font-medium text-red-800">Erro</h3>
                    <p className="mt-1 text-sm text-red-700">{error}</p>
                  </div>
                </div>
              </div>
            )}

            {/* Statistics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
              <div className="bg-white overflow-hidden shadow rounded-lg">
                <div className="p-5">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                        <span className="text-white text-sm font-medium">PDV</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-smart-gray-500 truncate">
                          Códigos de PDV
                        </dt>
                        <dd className="text-lg font-medium text-smart-gray-900">
                          {totalPdv}
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
                      <div className="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                        <span className="text-white text-sm font-medium">TR</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-smart-gray-500 truncate">
                          Códigos de Troca
                        </dt>
                        <dd className="text-lg font-medium text-smart-gray-900">
                          {totalExchange}
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
                      <div className="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                        <span className="text-white text-sm font-medium">DF</span>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-smart-gray-500 truncate">
                          Códigos de DANFE
                        </dt>
                        <dd className="text-lg font-medium text-smart-gray-900">
                          {totalDanfe}
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
                      <div className="w-8 h-8 bg-smart-gray-500 rounded-md flex items-center justify-center">
                        <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      </div>
                    </div>
                    <div className="ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-sm font-medium text-smart-gray-500 truncate">
                          Total de Códigos
                        </dt>
                        <dd className="text-lg font-medium text-smart-gray-900">
                          {totalEventOrigins}
                        </dd>
                      </dl>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Filters */}
            <div className="mb-6">
              <EventOriginFilters
                filters={filters}
                onFiltersChange={setFilters}
                onClearFilters={clearFilters}
                isLoading={isLoading}
                totalElements={totalElements}
              />
            </div>

            {/* Table */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-6 py-4 border-b border-smart-gray-200">
                <h3 className="text-lg font-medium text-smart-gray-900">
                  Códigos de Origem
                </h3>
              </div>
              <div className="p-6">
                <EventOriginTable
                  eventOrigins={eventOrigins}
                  isLoading={isLoading}
                  onEditEventOrigin={handleEditEventOrigin}
                  sortBy={sortBy}
                  sortDir={sortDir}
                  onSort={handleSortChange}
                />
              </div>
            </div>

            {/* Pagination */}
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalElements={totalElements}
              pageSize={pageSize}
              onPageChange={handlePageChange}
              isLoading={isLoading}
            />
          </div>
        </div>
      </div>

      {/* Modal */}
      <EventOriginModal
        isOpen={showModal}
        onClose={closeModal}
        mode={modalMode}
        eventOrigin={editingEventOrigin}
        onSuccess={onModalSuccess}
        onSave={handleSaveEventOrigin}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default EventOriginManagement;
