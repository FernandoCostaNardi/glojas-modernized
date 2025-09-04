import { useState, useCallback, useRef } from 'react';
import { ApiSystemUser, UserSearchFilters, UserPageResponse, AvailableRole } from '@/types';
import { userService, roleService } from '@/services/api';

/**
 * Hook customizado para gerenciamento de usuários
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const useUserManagement = () => {
  // Estado para usuários
  const [users, setUsers] = useState<ApiSystemUser[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  
  // Estado para filtros e paginação
  const [filters, setFilters] = useState<UserSearchFilters>({});
  const [currentPage, setCurrentPage] = useState<number>(0);
  const pageSize = 5; // Padrão: 5 itens por página
  const [sortBy, setSortBy] = useState<string>('id');
  const [sortDir, setSortDir] = useState<string>('asc');
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  
  // Estado para roles ativas
  const [availableRoles, setAvailableRoles] = useState<AvailableRole[]>([]);
  
  // Ref para controlar carregamento único de roles
  const hasLoadedRoles = useRef<boolean>(false);
  
  // Estado para totalizadores do sistema
  const [systemCounts, setSystemCounts] = useState({
    totalActive: 0,
    totalInactive: 0,
    totalBlocked: 0,
    totalUsers: 0
  });

  // Estado para modais (Context7 - separação de responsabilidades)
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editingUser, setEditingUser] = useState<ApiSystemUser | null>(null);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');

  // Estado para modal de confirmação (Context7 - separação de responsabilidades)
  const [showStatusConfirmation, setShowStatusConfirmation] = useState<boolean>(false);
  const [confirmingUser, setConfirmingUser] = useState<ApiSystemUser | null>(null);
  const [confirmationAction, setConfirmationAction] = useState<'toggleActive' | 'toggleLock'>('toggleActive');

  /**
   * Atualiza a lista de usuários e totalizadores
   */
  const refreshUserList = useCallback(async (): Promise<void> => {
    try {
      const response: UserPageResponse = await userService.getUsersWithFilters(
        filters,
        currentPage,
        pageSize,
        sortBy,
        sortDir
      );
      
      setUsers(response.users);
      setTotalElements(response.pagination.totalElements);
      setTotalPages(response.pagination.totalPages);
      setSystemCounts(response.counts);
    } catch (error) {
      console.error('❌ Erro ao atualizar lista de usuários:', error);
    }
  }, [filters, currentPage, pageSize, sortBy, sortDir]);

  /**
   * Carrega roles ativas
   */
  const loadAvailableRoles = useCallback(async (): Promise<void> => {
    if (hasLoadedRoles.current) {
      return;
    }
    
    try {
      hasLoadedRoles.current = true;
      
      const roles = await roleService.getActiveRoles();
      setAvailableRoles(roles);
    } catch (error) {
      console.error('❌ Erro ao carregar roles ativas:', error);
      hasLoadedRoles.current = false;
    }
  }, []);

  /**
   * Altera o status ativo/inativo do usuário
   */
  const toggleUserStatus = useCallback(async (userId: string, isActive: boolean, reason: string): Promise<void> => {
    try {
      await userService.toggleUserStatus(userId, isActive, reason);
      await refreshUserList();
    } catch (error) {
      console.error('❌ Erro ao alterar status do usuário:', error);
      throw error;
    }
  }, [refreshUserList]);

  /**
   * Altera o status bloqueado/desbloqueado do usuário
   */
  const toggleUserLock = useCallback(async (userId: string, isNotLocked: boolean, reason: string): Promise<void> => {
    try {
      await userService.toggleUserLock(userId, isNotLocked, reason);
      await refreshUserList();
    } catch (error) {
      console.error('❌ Erro ao alterar bloqueio do usuário:', error);
      throw error;
    }
  }, [refreshUserList]);

  /**
   * Limpa todos os filtros
   */
  const clearFilters = useCallback((): void => {
    setFilters({});
    setCurrentPage(0);
    setSortBy('id');
    setSortDir('asc');
  }, []);

  /**
   * Manipula mudança de página
   */
  const changePage = useCallback((page: number): void => {
    setCurrentPage(page);
  }, []);

  /**
   * Manipula ordenação
   */
  const handleSort = useCallback((field: string): void => {
    if (sortBy === field) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortDir('asc');
    }
  }, [sortBy, sortDir]);

  /**
   * Abre modal para criar novo usuário (Clean Code - função específica)
   */
  const openCreateModal = useCallback((): void => {
    setModalMode('create');
    setEditingUser(null);
    setShowModal(true);
  }, []);

  /**
   * Abre modal para editar usuário existente (Clean Code - função específica)
   */
  const openEditModal = useCallback((user: ApiSystemUser): void => {
    setModalMode('edit');
    setEditingUser(user);
    setShowModal(true);
  }, []);

  /**
   * Fecha o modal e limpa o estado (Clean Code - responsabilidade única)
   */
  const closeModal = useCallback((): void => {
    setShowModal(false);
    setEditingUser(null);
    setModalMode('create');
  }, []);

  /**
   * Callback executado após sucesso na operação do modal
   */
  const onModalSuccess = useCallback((message: string): void => {
    closeModal();
    refreshUserList();
    console.log('✅', message);
  }, [closeModal, refreshUserList]);

  /**
   * Abre modal de confirmação para alterar status do usuário (Clean Code - função específica)
   */
  const openStatusConfirmation = useCallback((user: ApiSystemUser, action: 'toggleActive' | 'toggleLock'): void => {
    setConfirmingUser(user);
    setConfirmationAction(action);
    setShowStatusConfirmation(true);
  }, []);

  /**
   * Fecha modal de confirmação (Clean Code - responsabilidade única)
   */
  const closeStatusConfirmation = useCallback((): void => {
    setShowStatusConfirmation(false);
    setConfirmingUser(null);
    setConfirmationAction('toggleActive');
  }, []);

  /**
   * Confirma ação de status do usuário (Clean Code - função específica)
   */
  const confirmStatusChange = useCallback(async (): Promise<void> => {
    if (!confirmingUser) return;

    try {
      if (confirmationAction === 'toggleActive') {
        await toggleUserStatus(confirmingUser.id, !confirmingUser.active, 'Alteração automática de status');
      } else if (confirmationAction === 'toggleLock') {
        await toggleUserLock(confirmingUser.id, !confirmingUser.notLocked, 'Alteração automática de bloqueio');
      }
    } catch (error) {
      console.error('❌ Erro ao confirmar ação de status:', error);
      throw error;
    } finally {
      closeStatusConfirmation();
    }
  }, [confirmingUser, confirmationAction, toggleUserStatus, toggleUserLock, closeStatusConfirmation]);

  return {
    // Estado dos usuários
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
    
    // Estado do modal de usuário
    showModal,
    editingUser,
    modalMode,
    
    // Estado do modal de confirmação
    showStatusConfirmation,
    confirmingUser,
    confirmationAction,
    
    // Ações dos usuários
    setIsLoading,
    setFilters,
    refreshUserList,
    loadAvailableRoles,
    toggleUserStatus,
    toggleUserLock,
    clearFilters,
    changePage,
    handleSort,
    
    // Ações do modal de usuário
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    
    // Ações do modal de confirmação
    openStatusConfirmation,
    closeStatusConfirmation,
    confirmStatusChange
  };
};
