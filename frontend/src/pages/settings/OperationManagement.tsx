import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { ConfirmationModal, OperationModal } from '@/components/modals';
import { Pagination } from '@/components/pagination';
import { OperationsTable } from '@/components/tables';
import OperationFilters from '@/components/filters/OperationFilters';
import { useOperationManagement } from '@/hooks/useOperationManagement';
import { Operation } from '@/types';

/**
 * Página de Gerenciamento de Operações
 * Permite cadastrar, editar e gerenciar operações do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const OperationManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Hook customizado para lógica de negócio
  const {
    operations,
    isLoadingOperations,
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    sortBy,
    sortDir,
    filters,
    pendingFilters,
    totalSell,
    totalExchange,
    totalOperations,
    refreshOperations,
    getOperationById,
    changePage,
    handleSort,
    updatePendingFilters,
    applyFilters,
    clearFilters
  } = useOperationManagement();
  
  // Estado para modais
  const [showOperationModal, setShowOperationModal] = useState<boolean>(false);
  const [editingOperation, setEditingOperation] = useState<Operation | null>(null);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  
  // Estado para modal de confirmação
  const [showConfirmation, setShowConfirmation] = useState<boolean>(false);
  const [confirmingItem, setConfirmingItem] = useState<Operation | null>(null);
  const [confirmationAction, setConfirmationAction] = useState<'deleteOperation'>('deleteOperation');

  /**
   * Carrega dados iniciais
   * O hook useOperationManagement já gerencia o carregamento automático
   */
  useEffect(() => {
    console.log('🚀 Inicializando página OperationManagement...');
    // O carregamento das operações é gerenciado automaticamente pelo hook
    // através do useEffect que monitora mudanças nos parâmetros de paginação
  }, []); // Dependências vazias para executar apenas uma vez

  /**
   * Verifica se o usuário tem permissão para gerenciar operações
   */
  const canManageOperations = (): boolean => {
    const hasUserCreate = hasPermission('user:create');
    const hasUserUpdate = hasPermission('user:update');
    const hasPermissionRead = hasPermission('permission:read');
    
    console.log('🔐 Verificando permissões do usuário:');
    console.log('🔐 user:create:', hasUserCreate);
    console.log('🔐 user:update:', hasUserUpdate);
    console.log('🔐 permission:read:', hasPermissionRead);
    
    return hasUserCreate && hasUserUpdate;
  };

  /**
   * Abre modal para criar nova operação
   */
  const handleCreateOperation = (): void => {
    setModalMode('create');
    setEditingOperation(null);
    setShowOperationModal(true);
  };

  /**
   * Abre modal para editar operação
   */
  const handleEditOperation = async (operation: Operation): Promise<void> => {
    try {
      // Busca dados atualizados da operação
      const updatedOperation = await getOperationById(operation.id);
      setModalMode('edit');
      setEditingOperation(updatedOperation);
      setShowOperationModal(true);
    } catch (error) {
      console.error('Erro ao buscar operação para edição:', error);
      // Em caso de erro, usa os dados que já temos
      setModalMode('edit');
      setEditingOperation(operation);
      setShowOperationModal(true);
    }
  };

  /**
   * Confirma exclusão de operação
   */
  // const handleDeleteOperation = (operation: Operation): void => {
  //   setConfirmingItem(operation);
  //   setConfirmationAction('deleteOperation');
  //   setShowConfirmation(true);
  // };


  /**
   * Executa ação confirmada
   */
  const handleConfirmAction = async (): Promise<void> => {
    if (!confirmingItem) return;

    try {
      if (confirmationAction === 'deleteOperation') {
        // TODO: Implementar exclusão de operação quando a API estiver disponível
        console.log('Exclusão de operação não implementada ainda');
        // await deleteOperation(confirmingItem.id);
        // Fechar modal de confirmação após sucesso
        setShowConfirmation(false);
        setConfirmingItem(null);
      }
    } catch (error) {
      console.error('Erro ao executar ação:', error);
      // Em caso de erro, manter modal aberto para nova tentativa
    }
  };

  /**
   * Manipula sucesso de operação
   */
  const handleSuccess = (message: string): void => {
    // Recarrega dados com refresh forçado
    refreshOperations();
    
    // Fecha modais
    setShowOperationModal(false);
    
    // TODO: Implementar notificação de sucesso
    console.log(message);
  };

  /**
   * Limpa o modal de confirmação
   */
  const handleClearConfirmation = (): void => {
    setShowConfirmation(false);
    setConfirmingItem(null);
    setConfirmationAction('deleteOperation');
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-8">
      <h1 className="text-3xl font-bold text-smart-gray-800 mb-2">
        Gerenciamento de Operações ⚙️
      </h1>
      <p className="text-smart-gray-600">
        Gerencie códigos de operação do sistema Smart Eletron.
      </p>
    </div>
  );

  /**
   * Renderiza os cards de totalizadores
   */
  const renderStatisticsCards = (): React.ReactNode => (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div className="bg-white overflow-hidden shadow rounded-lg">
        <div className="p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <span className="text-white text-sm font-medium">V</span>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Operações de Venda
                </dt>
                <dd className="text-lg font-medium text-smart-gray-900">
                  {totalSell}
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
              <div className="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <span className="text-white text-sm font-medium">T</span>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Operações de Troca
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
              <div className="w-8 h-8 bg-smart-gray-500 rounded-md flex items-center justify-center">
                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-smart-gray-500 truncate">
                  Total de Operações
                </dt>
                <dd className="text-lg font-medium text-smart-gray-900">
                  {totalOperations}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza a seção de filtros
   */
  const renderFiltersSection = (): React.ReactNode => (
    <div className="mb-6">
      <OperationFilters
        filters={filters}
        pendingFilters={pendingFilters}
        onPendingFiltersChange={updatePendingFilters}
        onApplyFilters={applyFilters}
        onClearFilters={clearFilters}
        isLoading={isLoadingOperations}
        totalElements={totalElements}
      />
    </div>
  );

  /**
   * Renderiza a seção de operações
   */
  const renderOperationsSection = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-smart-gray-800">Operações do Sistema</h2>
        <button
          onClick={handleCreateOperation}
          className="px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors duration-200"
        >
          + Nova Operação
        </button>
      </div>

      <OperationsTable
        operations={operations}
        isLoading={isLoadingOperations}
        onEditOperation={handleEditOperation}
        sortBy={sortBy}
        sortDir={sortDir}
        onSort={handleSort}
      />
      
      {/* Paginação */}
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={changePage}
        isLoading={isLoadingOperations}
      />
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 p-6 bg-smart-gray-50 overflow-auto">
      {renderPageHeader()}
      {renderStatisticsCards()}
      {renderFiltersSection()}
      {renderOperationsSection()}
    </main>
  );

  // Verifica permissão de acesso
  if (!canManageOperations()) {
    return (
      <div className="h-screen flex flex-col bg-smart-gray-50">
        <Header isSidebarCollapsed={isSidebarCollapsed} />
        <div className="flex flex-1 overflow-hidden relative">
          <Sidebar onCollapseChange={setIsSidebarCollapsed} />
          <div className="flex-1 flex items-center justify-center">
            <div className="text-center">
              <div className="text-6xl mb-4">🚫</div>
              <h1 className="text-2xl font-bold text-smart-gray-800 mb-2">
                Acesso Negado
              </h1>
              <p className="text-smart-gray-600">
                Você não tem permissão para acessar o gerenciamento de operações.
              </p>
            </div>
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
        <div className="flex-1">
          {renderMainContent()}
        </div>
      </div>

      {/* Modais */}
      <OperationModal
        isOpen={showOperationModal}
        onClose={() => setShowOperationModal(false)}
        mode={modalMode}
        operation={editingOperation || undefined}
        onSuccess={handleSuccess}
      />

      <ConfirmationModal
        isOpen={showConfirmation}
        onClose={handleClearConfirmation}
        onConfirm={handleConfirmAction}
        title="Excluir Operação"
        message={`Tem certeza que deseja excluir a operação "${confirmingItem?.code}"?`}
        confirmButtonText="Excluir"
        cancelButtonText="Cancelar"
      />
    </div>
  );
};

export default OperationManagement;
