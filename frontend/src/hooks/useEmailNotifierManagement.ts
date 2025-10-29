import { useState, useCallback, useEffect } from 'react';
import { emailNotifierService } from '@/services/api';
import {
  EmailNotifier,
  EmailNotifierResponse,
  EmailNotifierFilters,
  CreateEmailNotifierRequest,
  UpdateEmailNotifierRequest,
  EmailNotifierModalMode,
  EMAIL_NOTIFIER_DEFAULT_PAGINATION
} from '@/types';

/**
 * Interface para o estado do hook
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface EmailNotifierManagementState {
  // Dados principais
  readonly emailNotifiers: readonly EmailNotifierResponse[];
  readonly isLoading: boolean;
  readonly error: string | null;
  
  // Filtros e busca
  readonly filters: EmailNotifierFilters;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  
  // Paginação
  readonly totalElements: number;
  readonly totalPages: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
  
  // Estados do modal
  readonly showModal: boolean;
  readonly editingEmailNotifier: EmailNotifier | null;
  readonly modalMode: EmailNotifierModalMode;
  
  // Estados de operações
  readonly isSubmitting: boolean;
  readonly isDeleting: boolean;
}

/**
 * Interface para as ações do hook
 */
interface EmailNotifierManagementActions {
  // Operações de dados
  readonly loadEmailNotifiers: () => Promise<void>;
  readonly createEmailNotifier: (data: CreateEmailNotifierRequest) => Promise<void>;
  readonly updateEmailNotifier: (id: string, data: UpdateEmailNotifierRequest) => Promise<void>;
  readonly deleteEmailNotifier: (id: string) => Promise<void>;
  
  // Filtros e paginação
  readonly setFilters: (filters: EmailNotifierFilters) => void;
  readonly clearFilters: () => void;
  readonly changePage: (page: number) => void;
  readonly changePageSize: (size: number) => void;
  readonly handleSort: (field: string) => void;
  
  // Modal
  readonly openCreateModal: () => void;
  readonly openEditModal: (emailNotifier: EmailNotifier) => void;
  readonly closeModal: () => void;
  readonly onModalSuccess: (message: string) => void;
  
  // Estados
  readonly setError: (error: string | null) => void;
}

/**
 * Hook customizado para gerenciamento de EmailNotifier
 * Centraliza toda a lógica de estado e operações CRUD
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const useEmailNotifierManagement = (): EmailNotifierManagementState & EmailNotifierManagementActions => {
  // Estados principais
  const [emailNotifiers, setEmailNotifiers] = useState<readonly EmailNotifierResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estados de filtros e paginação
  const [filters, setFilters] = useState<EmailNotifierFilters>({});
  const [currentPage, setCurrentPage] = useState<number>(EMAIL_NOTIFIER_DEFAULT_PAGINATION.page);
  const [pageSize, setPageSize] = useState<number>(EMAIL_NOTIFIER_DEFAULT_PAGINATION.size);
  const [sortBy, setSortBy] = useState<string>(EMAIL_NOTIFIER_DEFAULT_PAGINATION.sortBy);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>(EMAIL_NOTIFIER_DEFAULT_PAGINATION.sortDir);
  
  // Estados de paginação
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(false);
  const [hasPrevious, setHasPrevious] = useState<boolean>(false);
  
  // Estados do modal
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editingEmailNotifier, setEditingEmailNotifier] = useState<EmailNotifier | null>(null);
  const [modalMode, setModalMode] = useState<EmailNotifierModalMode>('create');
  
  // Estados de operações
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  /**
   * Carrega EmailNotifiers com filtros e paginação atuais
   */
  const loadEmailNotifiers = useCallback(async (): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      
      console.log('Carregando EmailNotifiers...');
      
      const response = await emailNotifierService.getAllEmailNotifiers(
        currentPage,
        pageSize,
        sortBy,
        sortDir
      );
      
      setEmailNotifiers(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setHasNext(response.hasNext);
      setHasPrevious(response.hasPrevious);
      
      console.log(`EmailNotifiers carregados: ${response.content.length} de ${response.totalElements}`);
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar EmailNotifiers';
      setError(errorMessage);
      console.error('❌ Erro ao carregar EmailNotifiers:', err);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, pageSize, sortBy, sortDir]);

  /**
   * Cria um novo EmailNotifier
   */
  const createEmailNotifier = useCallback(async (data: CreateEmailNotifierRequest): Promise<void> => {
    try {
      setIsSubmitting(true);
      setError(null);
      
      console.log('Criando EmailNotifier:', data.email);
      
      await emailNotifierService.createEmailNotifier(data);
      
      // Recarrega a lista após criação
      await loadEmailNotifiers();
      
      console.log('EmailNotifier criado com sucesso');
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao criar EmailNotifier';
      setError(errorMessage);
      console.error('❌ Erro ao criar EmailNotifier:', err);
      throw err; // Re-throw para o modal tratar
    } finally {
      setIsSubmitting(false);
    }
  }, [loadEmailNotifiers]);

  /**
   * Atualiza um EmailNotifier existente
   */
  const updateEmailNotifier = useCallback(async (id: string, data: UpdateEmailNotifierRequest): Promise<void> => {
    try {
      setIsSubmitting(true);
      setError(null);
      
      console.log('Atualizando EmailNotifier:', id);
      
      await emailNotifierService.updateEmailNotifier(id, data);
      
      // Recarrega a lista após atualização
      await loadEmailNotifiers();
      
      console.log('EmailNotifier atualizado com sucesso');
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao atualizar EmailNotifier';
      setError(errorMessage);
      console.error('❌ Erro ao atualizar EmailNotifier:', err);
      throw err; // Re-throw para o modal tratar
    } finally {
      setIsSubmitting(false);
    }
  }, [loadEmailNotifiers]);

  /**
   * Remove um EmailNotifier
   */
  const deleteEmailNotifier = useCallback(async (id: string): Promise<void> => {
    try {
      setIsDeleting(true);
      setError(null);
      
      console.log('Removendo EmailNotifier:', id);
      
      await emailNotifierService.deleteEmailNotifier(id);
      
      // Recarrega a lista após remoção
      await loadEmailNotifiers();
      
      console.log('EmailNotifier removido com sucesso');
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao remover EmailNotifier';
      setError(errorMessage);
      console.error('❌ Erro ao remover EmailNotifier:', err);
      throw err; // Re-throw para o componente tratar
    } finally {
      setIsDeleting(false);
    }
  }, [loadEmailNotifiers]);

  /**
   * Define filtros de busca
   */
  const setFiltersCallback = useCallback((newFilters: EmailNotifierFilters): void => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset para primeira página
  }, []);

  /**
   * Limpa todos os filtros
   */
  const clearFilters = useCallback((): void => {
    setFilters({});
    setCurrentPage(0);
  }, []);

  /**
   * Muda a página
   */
  const changePage = useCallback((page: number): void => {
    setCurrentPage(page);
  }, []);

  /**
   * Muda o tamanho da página
   */
  const changePageSize = useCallback((size: number): void => {
    setPageSize(size);
    setCurrentPage(0); // Reset para primeira página
  }, []);

  /**
   * Manipula ordenação
   */
  const handleSort = useCallback((field: string): void => {
    if (sortBy === field) {
      // Se já está ordenando por este campo, inverte a direção
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      // Se é um novo campo, ordena por ele em ordem crescente
      setSortBy(field);
      setSortDir('asc');
    }
    setCurrentPage(0); // Reset para primeira página
  }, [sortBy, sortDir]);

  /**
   * Abre modal para criar novo EmailNotifier
   */
  const openCreateModal = useCallback((): void => {
    setModalMode('create');
    setEditingEmailNotifier(null);
    setShowModal(true);
  }, []);

  /**
   * Abre modal para editar EmailNotifier existente
   */
  const openEditModal = useCallback((emailNotifier: EmailNotifier): void => {
    setModalMode('edit');
    setEditingEmailNotifier(emailNotifier);
    setShowModal(true);
  }, []);

  /**
   * Fecha o modal
   */
  const closeModal = useCallback((): void => {
    setShowModal(false);
    setEditingEmailNotifier(null);
    setError(null);
  }, []);

  /**
   * Callback de sucesso do modal
   */
  const onModalSuccess = useCallback((message: string): void => {
    closeModal();
    // Aqui poderia mostrar uma notificação de sucesso
    console.log('✅ Sucesso:', message);
  }, [closeModal]);

  /**
   * Recarrega dados quando filtros ou paginação mudam
   */
  useEffect(() => {
    loadEmailNotifiers();
  }, [loadEmailNotifiers]);

  // Retorna estado e ações
  return {
    // Estado
    emailNotifiers,
    isLoading,
    error,
    filters,
    currentPage,
    pageSize,
    sortBy,
    sortDir,
    totalElements,
    totalPages,
    hasNext,
    hasPrevious,
    showModal,
    editingEmailNotifier,
    modalMode,
    isSubmitting,
    isDeleting,
    
    // Ações
    loadEmailNotifiers,
    createEmailNotifier,
    updateEmailNotifier,
    deleteEmailNotifier,
    setFilters: setFiltersCallback,
    clearFilters,
    changePage,
    changePageSize,
    handleSort,
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    setError
  };
};
