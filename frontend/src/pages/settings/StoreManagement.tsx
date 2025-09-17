import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { StoreModal } from '@/components/modals';
import { Pagination } from '@/components/pagination';
import { StoresTable } from '@/components/tables';
import { useStoreManagement } from '@/hooks/useStoreManagement';
import { ApiStore } from '@/types';

/**
 * Página de Gerenciamento de Lojas
 * Permite cadastrar, editar e gerenciar lojas do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StoreManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Hook customizado para lógica de negócio
  const {
    stores,
    isLoading,
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    sortBy,
    sortDir,
    refreshStores,
    changePage,
    handleSort
  } = useStoreManagement();

  // Estado para modais
  const [showStoreModal, setShowStoreModal] = useState<boolean>(false);
  const [editingStore, setEditingStore] = useState<ApiStore | null>(null);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');

  /**
   * Verifica se o usuário tem permissão para gerenciar lojas
   */
  const canManageStores = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  /**
   * Abre modal para criar nova loja
   */
  const handleCreateStore = (): void => {
    setModalMode('create');
    setEditingStore(null);
    setShowStoreModal(true);
  };

  /**
   * Abre modal para editar loja
   */
  const handleEditStore = (store: ApiStore): void => {
    setModalMode('edit');
    setEditingStore(store);
    setShowStoreModal(true);
  };

  // Funções de exclusão e toggle removidas - não mais necessárias

  /**
   * Manipula sucesso de operação
   */
  const handleSuccess = (message: string): void => {
    // Recarrega dados com refresh forçado
    refreshStores();
    
    // Fecha modais
    setShowStoreModal(false);
    
    // TODO: Implementar notificação de sucesso
    console.log(message);
  };

  // Função de limpeza de confirmação removida - não mais necessária

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-8">
      <h1 className="text-3xl font-bold text-smart-gray-800 mb-2">
        Gerenciamento de Lojas 🏪
      </h1>
      <p className="text-smart-gray-600">
        Gerencie lojas do sistema Smart Eletron.
      </p>
    </div>
  );

  /**
   * Renderiza a seção de lojas
   */
  const renderStoresSection = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-smart-gray-800">Lojas do Sistema</h2>
        <button
          onClick={handleCreateStore}
          className="px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors duration-200"
        >
          + Nova Loja
        </button>
      </div>

      <StoresTable
        stores={stores}
        isLoading={isLoading}
        onEditStore={handleEditStore}
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
        isLoading={isLoading}
      />
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 p-6 bg-smart-gray-50 overflow-auto">
      {renderPageHeader()}
      {renderStoresSection()}
    </main>
  );

  // Verifica permissão de acesso
  if (!canManageStores()) {
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
                Você não tem permissão para acessar o gerenciamento de lojas.
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
      <StoreModal
        isOpen={showStoreModal}
        onClose={() => setShowStoreModal(false)}
        mode={modalMode}
        store={editingStore}
        onSuccess={handleSuccess}
      />

      {/* Modal de confirmação removido - não mais necessário */}
    </div>
  );
};

export default StoreManagement;
