import { useState, useCallback, useEffect } from 'react';
import { eventOriginService } from '@/services/api';
import {
  EventOrigin,
  EventOriginResponse,
  EventOriginFilters,
  EventOriginSearchParams,
  EventOriginSearchResponse,
  CreateEventOriginRequest,
  UpdateEventOriginRequest,
  EventOriginModalMode,
  DEFAULT_PAGINATION
} from '@/types';

/**
 * Interface para o estado do hook
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface EventOriginManagementState {
  // Dados principais
  readonly eventOrigins: readonly EventOriginResponse[];
  readonly isLoading: boolean;
  readonly error: string | null;
  
  // Filtros e busca
  readonly filters: EventOriginFilters;
  readonly currentPage: number;
  readonly pageSize: number;
  readonly sortBy: string;
  readonly sortDir: 'asc' | 'desc';
  
  // Paginação
  readonly totalElements: number;
  readonly totalPages: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
  
  // Totalizadores
  readonly totalPdv: number;
  readonly totalExchange: number;
  readonly totalDanfe: number;
  readonly totalEventOrigins: number;
  
  // Estados do modal
  readonly showModal: boolean;
  readonly editingEventOrigin: EventOrigin | null;
  readonly modalMode: EventOriginModalMode;
  
  // Estados de operações
  readonly isSubmitting: boolean;
  readonly isDeleting: boolean;
}

/**
 * Interface para as ações do hook
 */
interface EventOriginManagementActions {
  // Operações de dados
  readonly loadEventOrigins: () => Promise<void>;
  readonly refreshEventOrigins: () => Promise<void>;
  readonly createEventOrigin: (data: CreateEventOriginRequest) => Promise<void>;
  readonly updateEventOrigin: (id: string, data: UpdateEventOriginRequest) => Promise<void>;
  
  // Filtros e paginação
  readonly setFilters: (filters: EventOriginFilters) => void;
  readonly clearFilters: () => void;
  readonly changePage: (page: number) => void;
  readonly changePageSize: (size: number) => void;
  readonly handleSort: (field: string) => void;
  
  // Modal
  readonly openCreateModal: () => void;
  readonly openEditModal: (eventOrigin: EventOrigin) => void;
  readonly closeModal: () => void;
  readonly onModalSuccess: (message: string) => void;
  
  // Estados
  readonly setIsLoading: (loading: boolean) => void;
  readonly setError: (error: string | null) => void;
}

/**
 * Hook customizado para gerenciamento de EventOrigin
 * Centraliza toda a lógica de estado e operações CRUD
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const useEventOriginManagement = (): EventOriginManagementState & EventOriginManagementActions => {
  // Estados principais
  const [eventOrigins, setEventOrigins] = useState<readonly EventOriginResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estados de filtros e paginação
  const [filters, setFilters] = useState<EventOriginFilters>({});
  const [currentPage, setCurrentPage] = useState<number>(DEFAULT_PAGINATION.page);
  const [pageSize, setPageSize] = useState<number>(DEFAULT_PAGINATION.size);
  const [sortBy, setSortBy] = useState<string>(DEFAULT_PAGINATION.sortBy);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>(DEFAULT_PAGINATION.sortDir);
  
  // Estados de paginação
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(false);
  const [hasPrevious, setHasPrevious] = useState<boolean>(false);
  
  // Estados de totalizadores
  const [totalPdv, setTotalPdv] = useState<number>(0);
  const [totalExchange, setTotalExchange] = useState<number>(0);
  const [totalDanfe, setTotalDanfe] = useState<number>(0);
  const [totalEventOrigins, setTotalEventOrigins] = useState<number>(0);
  
  // Estados do modal
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editingEventOrigin, setEditingEventOrigin] = useState<EventOrigin | null>(null);
  const [modalMode, setModalMode] = useState<EventOriginModalMode>('create');
  
  // Estados de operações
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  /**
   * Carrega códigos de origem com filtros e paginação atuais
   */
  const loadEventOrigins = useCallback(async (): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      
      const params: EventOriginSearchParams = {
        ...(filters.eventSource && { eventSource: filters.eventSource }),
        ...(filters.sourceCode && { sourceCode: filters.sourceCode }),
        page: currentPage,
        size: pageSize,
        sortBy,
        sortDir
      };
      
      const response: EventOriginSearchResponse = await eventOriginService.getEventOrigins(params);
      
      setEventOrigins(response.eventOrigins);
      setTotalElements(response.pagination.totalElements);
      setTotalPages(response.pagination.totalPages);
      setHasNext(response.pagination.hasNext);
      setHasPrevious(response.pagination.hasPrevious);
      setTotalPdv(response.counts.totalPdv);
      setTotalExchange(response.counts.totalExchange);
      setTotalDanfe(response.counts.totalDanfe);
      setTotalEventOrigins(response.counts.totalEventOrigins);
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar códigos de origem';
      setError(errorMessage);
      console.error('❌ Erro ao carregar códigos de origem:', err);
    } finally {
      setIsLoading(false);
    }
  }, [filters, currentPage, pageSize, sortBy, sortDir]);

  /**
   * Recarrega os dados (útil após operações CRUD)
   */
  const refreshEventOrigins = useCallback(async (): Promise<void> => {
    await loadEventOrigins();
  }, [loadEventOrigins]);

  /**
   * Cria um novo código de origem
   */
  const createEventOrigin = useCallback(async (data: CreateEventOriginRequest): Promise<void> => {
    try {
      setIsSubmitting(true);
      setError(null);
      
      await eventOriginService.createEventOrigin(data);
      
      // Recarrega a lista após criação
      await refreshEventOrigins();
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao criar código de origem';
      setError(errorMessage);
      console.error('❌ Erro ao criar código de origem:', err);
      throw err; // Re-throw para o modal tratar
    } finally {
      setIsSubmitting(false);
    }
  }, [refreshEventOrigins]);

  /**
   * Atualiza um código de origem existente
   */
  const updateEventOrigin = useCallback(async (id: string, data: UpdateEventOriginRequest): Promise<void> => {
    try {
      setIsSubmitting(true);
      setError(null);
      
      await eventOriginService.updateEventOrigin(id, data);
      
      // Recarrega a lista após atualização
      await refreshEventOrigins();
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao atualizar código de origem';
      setError(errorMessage);
      console.error('❌ Erro ao atualizar código de origem:', err);
      throw err; // Re-throw para o modal tratar
    } finally {
      setIsSubmitting(false);
    }
  }, [refreshEventOrigins]);

  /**
   * Define novos filtros e recarrega dados
   */
  const handleSetFilters = useCallback((newFilters: EventOriginFilters): void => {
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
   * Muda a página atual
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
   * Abre modal para criar novo código
   */
  const openCreateModal = useCallback((): void => {
    setModalMode('create');
    setEditingEventOrigin(null);
    setShowModal(true);
  }, []);

  /**
   * Abre modal para editar código existente
   */
  const openEditModal = useCallback((eventOrigin: EventOrigin): void => {
    setModalMode('edit');
    setEditingEventOrigin(eventOrigin);
    setShowModal(true);
  }, []);

  /**
   * Fecha o modal
   */
  const closeModal = useCallback((): void => {
    setShowModal(false);
    setEditingEventOrigin(null);
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
    loadEventOrigins();
  }, [loadEventOrigins]);

  // Retorna estado e ações
  return {
    // Estado
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
    hasNext,
    hasPrevious,
    totalPdv,
    totalExchange,
    totalDanfe,
    totalEventOrigins,
    showModal,
    editingEventOrigin,
    modalMode,
    isSubmitting,
    isDeleting: false,
    
    // Ações
    loadEventOrigins,
    refreshEventOrigins,
    createEventOrigin,
    updateEventOrigin,
    setFilters: handleSetFilters,
    clearFilters,
    changePage,
    changePageSize,
    handleSort,
    openCreateModal,
    openEditModal,
    closeModal,
    onModalSuccess,
    setIsLoading,
    setError
  };
};
