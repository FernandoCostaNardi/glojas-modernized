import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { ConfirmationModal, RoleModal } from '@/components/modals';
import { RolesTable } from '@/components/tables';
import { usePermissionManagement } from '@/hooks/usePermissionManagement';
import { Role } from '@/types';

/**
 * Página de Gerenciamento de Roles
 * Permite cadastrar, editar e gerenciar roles do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const PermissionManagement: React.FC = () => {
  const { hasPermission } = useAuth();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Hook customizado para lógica de negócio
  const {
    roles,
    permissions,
    isLoadingRoles,
    loadRoles,
    loadPermissions,
    refreshRoles,
    deleteRole,
    toggleRoleStatus
  } = usePermissionManagement();
  
  // Estado para modais
  const [showRoleModal, setShowRoleModal] = useState<boolean>(false);
  const [editingRole, setEditingRole] = useState<Role | null>(null);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  
  // Estado para modal de confirmação
  const [showConfirmation, setShowConfirmation] = useState<boolean>(false);
  const [confirmingItem, setConfirmingItem] = useState<Role | null>(null);
  const [confirmationAction, setConfirmationAction] = useState<'deleteRole' | 'toggleRoleStatus'>('deleteRole');

  /**
   * Carrega dados iniciais
   */
  useEffect(() => {
    loadRoles();
    loadPermissions();
  }, [loadRoles, loadPermissions]);

  /**
   * Verifica se o usuário tem permissão para gerenciar permissões
   */
  const canManagePermissions = (): boolean => {
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
   * Abre modal para criar nova role
   */
  const handleCreateRole = (): void => {
    setModalMode('create');
    setEditingRole(null);
    setShowRoleModal(true);
  };

  /**
   * Abre modal para editar role
   */
  const handleEditRole = (role: Role): void => {
    setModalMode('edit');
    setEditingRole(role);
    setShowRoleModal(true);
  };

  /**
   * Confirma exclusão de role
   */
  const handleDeleteRole = (role: Role): void => {
    setConfirmingItem(role);
    setConfirmationAction('deleteRole');
    setShowConfirmation(true);
  };

  /**
   * Confirma alteração de status da role
   */
  const handleToggleRoleStatus = (role: Role): void => {
    setConfirmingItem(role);
    setConfirmationAction('toggleRoleStatus');
    setShowConfirmation(true);
  };

  /**
   * Executa ação confirmada
   */
  const handleConfirmAction = async (): Promise<void> => {
    if (!confirmingItem) return;

    try {
      if (confirmationAction === 'deleteRole') {
        await deleteRole(confirmingItem.id);
        // Fechar modal de confirmação após sucesso
        setShowConfirmation(false);
        setConfirmingItem(null);
      } else if (confirmationAction === 'toggleRoleStatus') {
        await toggleRoleStatus(confirmingItem);
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
    refreshRoles();
    
    // Fecha modais
    setShowRoleModal(false);
    
    // TODO: Implementar notificação de sucesso
    console.log(message);
  };

  /**
   * Limpa o modal de confirmação
   */
  const handleClearConfirmation = (): void => {
    setShowConfirmation(false);
    setConfirmingItem(null);
    setConfirmationAction('deleteRole');
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className="mb-8">
      <h1 className="text-3xl font-bold text-smart-gray-800 mb-2">
        Gerenciamento de Roles 🔐
      </h1>
      <p className="text-smart-gray-600">
        Gerencie roles e suas permissões no sistema Smart Eletron.
      </p>
    </div>
  );

  /**
   * Renderiza a seção de roles
   */
  const renderRolesSection = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-smart-gray-800">Roles do Sistema</h2>
        <button
          onClick={handleCreateRole}
          className="px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors duration-200"
        >
          + Nova Role
        </button>
      </div>

      <RolesTable
        roles={roles}
        isLoading={isLoadingRoles}
        onEditRole={handleEditRole}
        onDeleteRole={handleDeleteRole}
        onToggleRoleStatus={handleToggleRoleStatus}
      />
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className="flex-1 p-6 bg-smart-gray-50 overflow-auto">
      {renderPageHeader()}
      {renderRolesSection()}
    </main>
  );

  // Verifica permissão de acesso
  if (!canManagePermissions()) {
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
                Você não tem permissão para acessar o gerenciamento de roles.
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
      <RoleModal
        isOpen={showRoleModal}
        onClose={() => setShowRoleModal(false)}
        mode={modalMode}
        role={editingRole || undefined}
        availablePermissions={permissions}
        onSuccess={handleSuccess}
      />

      <ConfirmationModal
        isOpen={showConfirmation}
        onClose={handleClearConfirmation}
        onConfirm={handleConfirmAction}
        title={
          confirmationAction === 'deleteRole' ? 'Excluir Role' : 'Alterar Status da Role'
        }
        message={
          confirmationAction === 'deleteRole' 
            ? `Tem certeza que deseja excluir a role "${confirmingItem?.name}"?`
            : `Tem certeza que deseja ${confirmingItem?.active ? 'desativar' : 'ativar'} a role "${confirmingItem?.name}"?`
        }
        confirmButtonText={
          confirmationAction === 'deleteRole' ? 'Excluir' : 'Confirmar'
        }
        cancelButtonText="Cancelar"
      />
    </div>
  );
};

export default PermissionManagement;
