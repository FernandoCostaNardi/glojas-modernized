import { useState, useCallback, useRef, useEffect } from 'react';
import { Operation, OperationKind, OperationSearchResponse, OperationFilters } from '@/types';
import { operationService, operationKindService } from '@/services/api';

/**
 * Hook customizado para gerenciamento de operações
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const useOperationManagement = () => {
  // Estado para operações
  const [operations, setOperations] = useState<Operation[]>([]);
  const [isLoadingOperations, setIsLoadingOperations] = useState<boolean>(true);
  
  // Estado para paginação
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [pageSize] = useState<number>(5); // Padrão: 5 itens por página
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string>('code');
  const [sortDir, setSortDir] = useState<string>('asc');
  
  // Estado para filtros
  const [filters, setFilters] = useState<OperationFilters>({});
  const [pendingFilters, setPendingFilters] = useState<OperationFilters>({});
  
  // Estado para totalizadores
  const [totalSell, setTotalSell] = useState<number>(0);
  const [totalExchange, setTotalExchange] = useState<number>(0);
  const [totalOperations, setTotalOperations] = useState<number>(0);
  
  // Estado para tipos de operação
  const [operationKinds, setOperationKinds] = useState<OperationKind[]>([]);
  const [isLoadingOperationKinds, setIsLoadingOperationKinds] = useState<boolean>(false);
  
  // Refs para controlar carregamento único
  const hasLoadedOperationKinds = useRef<boolean>(false);

  /**
   * Carrega as operações do sistema com paginação e filtros
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const loadOperations = useCallback(async () => {
    console.log('🔄 Carregando operações com paginação e filtros...', { 
      currentPage, 
      pageSize, 
      sortBy, 
      sortDir,
      filters
    });
    
    try {
      setIsLoadingOperations(true);
      
      const response: OperationSearchResponse = await operationService.getOperationsWithFilters(
        filters.code,
        filters.operationSource,
        currentPage,
        pageSize,
        sortBy,
        sortDir
      );
      
      setOperations([...response.operations]);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      
      // Atualizar totalizadores
      if (response.counts) {
        setTotalSell(response.counts.totalSell);
        setTotalExchange(response.counts.totalExchange);
        setTotalOperations(response.counts.totalOperations);
      }
      
      console.log('✅ Operações carregadas com sucesso:', {
        operations: response.operations.length,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.currentPage,
        totalSell: response.counts?.totalSell,
        totalExchange: response.counts?.totalExchange,
        totalOperations: response.counts?.totalOperations
      });
    } catch (error) {
      console.error('❌ Erro ao carregar operações:', error);
    } finally {
      setIsLoadingOperations(false);
    }
  }, [currentPage, pageSize, sortBy, sortDir, filters]); // Dependências para recarregar quando mudarem

  /**
   * Carrega operações automaticamente quando parâmetros de paginação mudam
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  useEffect(() => {
    console.log('🔄 useEffect: Carregando operações...', {
      currentPage,
      pageSize,
      sortBy,
      sortDir
    });
    
    loadOperations();
  }, [currentPage, pageSize, sortBy, sortDir, loadOperations]);

  /**
   * Recarrega as operações do sistema (força atualização)
   * Utilizado após operações CRUD para manter dados sincronizados
   */
  const refreshOperations = useCallback(async () => {
    console.log('🔄 refreshOperations: Forçando atualização das operações...');
    await loadOperations();
  }, [loadOperations]);

  /**
   * Atualiza os filtros pendentes (não aplicados ainda)
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const updatePendingFilters = useCallback((newFilters: OperationFilters): void => {
    console.log('🔍 Atualizando filtros pendentes:', newFilters);
    setPendingFilters(newFilters);
  }, []);

  /**
   * Aplica os filtros pendentes
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const applyFilters = useCallback((): void => {
    console.log('🔍 Aplicando filtros pendentes:', pendingFilters);
    setFilters(pendingFilters);
    setCurrentPage(0); // Reset para primeira página ao aplicar filtros
  }, [pendingFilters]);

  /**
   * Limpa todos os filtros
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const clearFilters = useCallback((): void => {
    console.log('🧹 Limpando filtros');
    setFilters({});
    setPendingFilters({});
    setCurrentPage(0); // Reset para primeira página
  }, []);

  /**
   * Altera a página atual
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const changePage = useCallback((page: number) => {
    console.log('🔄 changePage: Alterando página de', currentPage, 'para', page);
    setCurrentPage(page);
  }, [currentPage]);

  /**
   * Altera a ordenação
   */
  const handleSort = useCallback((field: string) => {
    if (sortBy === field) {
      // Se já está ordenando por este campo, inverte a direção
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      // Se é um novo campo, ordena por ele em ordem crescente
      setSortBy(field);
      setSortDir('asc');
    }
  }, [sortBy, sortDir]);

  /**
   * Carrega os tipos de operação do sistema
   * Suporta carregamento lazy (sob demanda)
   */
  const loadOperationKinds = useCallback(async (forceRefresh: boolean = false) => {
    if (!forceRefresh && hasLoadedOperationKinds.current) {
      console.log('🔄 OperationKinds já carregados, pulando carregamento');
      return;
    }
    
    console.log('🔄 Carregando operation-kinds...', { forceRefresh, hasLoaded: hasLoadedOperationKinds.current });
    
    try {
      setIsLoadingOperationKinds(true);
      hasLoadedOperationKinds.current = true;
      
      const operationKindsData = await operationKindService.getAllOperationKinds();
      setOperationKinds(operationKindsData);
      
      console.log('✅ OperationKinds carregados com sucesso:', operationKindsData.length);
    } catch (error) {
      console.error('❌ Erro ao carregar tipos de operação:', error);
      hasLoadedOperationKinds.current = false;
    } finally {
      setIsLoadingOperationKinds(false);
    }
  }, []); // Dependências vazias para evitar recriação desnecessária

  /**
   * Cria uma nova operação
   */
  const createOperation = useCallback(async (operationData: any) => {
    try {
      await operationService.createOperation(operationData);
      // Recarrega operações após criação para manter sincronização
      await refreshOperations();
    } catch (error) {
      console.error('Erro ao criar operação:', error);
      throw error;
    }
  }, [refreshOperations]);

  /**
   * Atualiza uma operação existente
   */
  const updateOperation = useCallback(async (operationId: string, operationData: any) => {
    try {
      await operationService.updateOperation(operationId, operationData);
      // Recarrega operações após atualização para manter sincronização
      await refreshOperations();
    } catch (error) {
      console.error('Erro ao atualizar operação:', error);
      throw error;
    }
  }, [refreshOperations]);

  /**
   * Busca uma operação por ID
   */
  const getOperationById = useCallback(async (operationId: string) => {
    try {
      return await operationService.getOperationById(operationId);
    } catch (error) {
      console.error('Erro ao buscar operação por ID:', error);
      throw error;
    }
  }, []);

  return {
    // Estado
    operations,
    operationKinds,
    isLoadingOperations,
    isLoadingOperationKinds,
    
    // Estado de paginação
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    sortBy,
    sortDir,
    
    // Estado de filtros
    filters,
    pendingFilters,
    
    // Estado de totalizadores
    totalSell,
    totalExchange,
    totalOperations,
    
    // Ações
    loadOperations,
    loadOperationKinds,
    refreshOperations,
    createOperation,
    updateOperation,
    getOperationById,
    
    // Ações de paginação
    changePage,
    handleSort,
    
    // Ações de filtros
    updatePendingFilters,
    applyFilters,
    clearFilters
  };
};
