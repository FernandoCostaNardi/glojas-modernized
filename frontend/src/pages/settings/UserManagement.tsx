import { UserFilters } from '@/components/filters';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { ConfirmationModal, UserModal } from '@/components/modals';
import { Pagination } from '@/components/pagination';
import { SystemStatsCards } from '@/components/stats';
import { UsersTable } from '@/components/tables';
import { useAuth } from '@/contexts/AuthContext';
import { useLayout } from '@/contexts/LayoutContext';
import { useUserManagement } from '@/hooks/useUserManagement';
import { ApiSystemUser } from '@/types';
import React, { useEffect } from 'react';


/**
 * Página de Gerenciamento de Usuários
 * Permite cadastrar, editar, desabilitar e alterar senha de usuários
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const UserManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const { isMobile } = useLayout();
  
  // Hook customizado para lógica de negócio (Context7 - separação de responsabilidades)
  const {
    users,
    isLoading,
    filters,
    currentPage,
    pageSize,
    sortBy,
    sortDir,
    totalElements,
    totalPages,
    availableRoles,
    systemCounts,
    showModal,
    editingUser,
    modalMode,
    showStatusConfirmation,
    confirmingUser,
    confirmationAction,
    setIsLoading,
    setFilters,
    refreshUserList,
    loadAvailableRoles,
    clearFilters,
    changePage,
    handleSort,
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    openStatusConfirmation,
    closeStatusConfirmation,
    confirmStatusChange
  } = useUserManagement();


  /**
   * Verifica se o usuário tem permissão para gerenciar usuários
   */
  const canManageUsers = (): boolean => {
    return hasPermission('SYSTEM_ADMIN');
  };

  // Função loadUsers removida - agora usamos useEffect diretamente



  /**
   * Abre modal para criar novo usuário (Clean Code - delega para o hook)
   */
  const handleCreateUser = (): void => {
    openCreateModal();
  };

  /**
   * Abre modal para editar usuário existente (Clean Code - delega para o hook)
   */
  const handleEditUser = (user: ApiSystemUser): void => {
    openEditModal(user);
  };

  /**
   * Carrega dados iniciais
   */
  useEffect(() => {
    refreshUserList();
    loadAvailableRoles();
  }, [refreshUserList, loadAvailableRoles]);

  /**
   * Recarrega dados quando filtros/paginação mudam
   */
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setIsLoading(true);
        await refreshUserList();
      } catch (error) {
        console.error('❌ Erro ao carregar usuários:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUsers();
  }, [filters, currentPage, pageSize, sortBy, sortDir, refreshUserList, setIsLoading]);

  /**
   * Altera o status ativo/inativo do usuário (Clean Code - delega para o hook)
   */
  const handleToggleUserStatus = async (user: ApiSystemUser): Promise<void> => {
    openStatusConfirmation(user, 'toggleActive');
  };

  /**
   * Altera o status bloqueado/desbloqueado do usuário (Clean Code - delega para o hook)
   */
  const handleToggleUserLock = async (user: ApiSystemUser): Promise<void> => {
    openStatusConfirmation(user, 'toggleLock');
  };

  /**
   * Abre modal para alterar senha
   */
  const handleChangePassword = (_user: ApiSystemUser): void => {
    // TODO: Implementar modal específico para alteração de senha
  };

  // Função handleCloseModal removida - Clean Code: delegação direta para o hook
  // A função closeModal do hook já gerencia o fechamento e atualização da lista

  /**
   * Fecha o modal de confirmação (Clean Code - delega para o hook)
   */
  const handleCloseConfirmationModal = (): void => {
    closeStatusConfirmation();
  };

  /**
   * Confirma a ação de status do usuário (Clean Code - delega para o hook)
   */
  const handleConfirmStatusChange = async (): Promise<void> => {
    await confirmStatusChange();
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className={`mb-6 ${isMobile ? 'mb-4' : 'mb-6'}`}>
      <div className={`${
        isMobile 
          ? 'space-y-4' 
          : 'flex items-center justify-between'
      }`}>
        <div>
          <h1 className={`font-bold text-smart-gray-800 mb-1 ${
            isMobile ? 'text-xl' : 'text-2xl'
          }`}>
            Gerenciamento de Usuários 👥
          </h1>
          <p className={`text-smart-gray-600 ${
            isMobile ? 'text-xs' : 'text-sm'
          }`}>
            {isMobile 
              ? 'Gerencie usuários do sistema.' 
              : 'Cadastre, edite e gerencie usuários do sistema Smart Eletron.'
            }
          </p>
        </div>
        <button
          onClick={handleCreateUser}
          className={`bg-smart-red-600 hover:bg-smart-red-700 text-white rounded-lg font-medium transition-colors duration-200 flex items-center ${
            isMobile 
              ? 'px-4 py-3 text-sm w-full justify-center' 
              : 'px-4 py-2 text-sm'
          }`}
        >
          <svg className={`${isMobile ? 'w-5 h-5 mr-2' : 'w-4 h-4 mr-2'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Novo Usuário
        </button>
      </div>
    </div>
  );

  // Função renderUsersTable removida - Clean Code: eliminação de código morto
  // O componente UsersTable já implementa essa funcionalidade de forma modular

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 bg-smart-gray-50 overflow-x-hidden overflow-y-auto w-full max-w-full">
      <div className={`w-full max-w-full box-border ${
        isMobile ? 'p-4' : 'p-6'
      }`}>
        {renderPageHeader()}
        
        {/* Estatísticas rápidas */}
        <SystemStatsCards systemCounts={systemCounts} />

        {/* Filtros de busca */}
        <UserFilters
          filters={filters}
          availableRoles={availableRoles}
          totalElements={totalElements}
          onFiltersChange={setFilters}
          onClearFilters={clearFilters}
        />

        {/* Tabela de usuários */}
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <div className={`animate-spin rounded-full border-b-2 border-smart-red-600 mx-auto mb-4 ${
                isMobile ? 'h-8 w-8' : 'h-12 w-12'
              }`}></div>
              <p className={`text-smart-gray-600 ${
                isMobile ? 'text-sm' : 'text-base'
              }`}>
                {isMobile ? 'Carregando...' : 'Carregando usuários...'}
              </p>
            </div>
          </div>
        ) : (
          <>
            <UsersTable
              users={users}
              sortBy={sortBy}
              sortDir={sortDir}
              onSort={handleSort}
              onEditUser={handleEditUser}
              onToggleUserStatus={handleToggleUserStatus}
              onToggleUserLock={handleToggleUserLock}
              onChangePassword={handleChangePassword}
            />
            
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalElements={totalElements}
              pageSize={pageSize}
              onPageChange={changePage}
            />
          </>
        )}

        {/* Modal de confirmação de status */}
        {showStatusConfirmation && (
          <ConfirmationModal
            isOpen={showStatusConfirmation}
            onClose={handleCloseConfirmationModal}
            onConfirm={handleConfirmStatusChange}
            title={confirmationAction === 'toggleActive' ? 'Alterar Status do Usuário' : 'Alterar Status de Bloqueio'}
            message={confirmationAction === 'toggleActive' 
              ? `Tem certeza que deseja ${confirmingUser?.active ? 'desabilitar' : 'habilitar'} o usuário "${confirmingUser?.name}"?`
              : `Tem certeza que deseja ${confirmingUser?.notLocked ? 'bloquear' : 'desbloquear'} o usuário "${confirmingUser?.name}"?`
            }
            confirmButtonText={confirmationAction === 'toggleActive'
              ? (confirmingUser?.active ? 'Sim, Desabilitar' : 'Sim, Habilitar')
              : (confirmingUser?.notLocked ? 'Sim, Bloquear' : 'Sim, Desbloquear')
            }
            cancelButtonText="Cancelar"
          />
        )}

        {/* Modal de usuário (Clean Code - componente único) */}
        <UserModal
          isOpen={showModal}
          onClose={closeModal}
          mode={modalMode}
          user={editingUser}
          onSuccess={onModalSuccess}
        />
      </div>
    </main>
  );





  // Verifica permissão de acesso
  if (!canManageUsers()) {
    return (
      <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
        <Header />
        <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
          <Sidebar />
          <div className="flex-1 flex items-center justify-center overflow-y-auto">
            <div className="text-center p-4">
              <div className={`mb-4 ${isMobile ? 'text-4xl' : 'text-6xl'}`}>🚫</div>
              <h1 className={`font-bold text-smart-gray-800 mb-2 ${
                isMobile ? 'text-xl' : 'text-2xl'
              }`}>
                Acesso Negado
              </h1>
              <p className={`text-smart-gray-600 ${
                isMobile ? 'text-sm' : 'text-base'
              }`}>
                Você não tem permissão para gerenciar usuários do sistema.
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-smart-gray-50 w-full max-w-full overflow-x-hidden">
      {/* Header */}
      <Header />
      
      {/* Layout principal */}
      <div className="flex flex-1 relative w-full max-w-full overflow-x-hidden">
        {/* Sidebar */}
        <Sidebar />
        
        {/* Conteúdo principal */}
        <div className="flex-1 min-w-0 w-full max-w-full overflow-x-hidden">
          {renderMainContent()}
        </div>
      </div>
    </div>
  );
};

export default UserManagement;
