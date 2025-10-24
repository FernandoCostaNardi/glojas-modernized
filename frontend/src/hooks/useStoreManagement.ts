import { useState, useCallback, useEffect } from 'react';
import { storeService } from '@/services/api';
import { ApiStore, CreateStoreRequest, UpdateStoreRequest } from '@/types';

/**
 * Hook customizado para gerenciamento de lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 * Context7 - separação de responsabilidades por domínio
 */
export const useStoreManagement = () => {
  // Estado para lojas
  const [stores, setStores] = useState<ApiStore[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  
  // Estado para paginação
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [pageSize] = useState<number>(5); // Padrão: 5 itens por página
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string>('code');
  const [sortDir, setSortDir] = useState<string>('asc');

  /**
   * Carrega as lojas do sistema com paginação
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const loadStores = useCallback(async (): Promise<void> => {
    console.log('🔄 Carregando lojas com paginação...', { 
      currentPage, 
      pageSize, 
      sortBy, 
      sortDir 
    });
    
    try {
      setIsLoading(true);
      
      const response = await storeService.getStoresWithPagination(
        currentPage,
        pageSize,
        sortBy,
        sortDir
      );
      
      setStores(response.stores);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      
      console.log('✅ Lojas carregadas com sucesso:', {
        stores: response.stores.length,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.currentPage
      });
    } catch (error) {
      console.error('❌ Erro ao carregar lojas:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, pageSize, sortBy, sortDir]);

  /**
   * Carrega lojas automaticamente quando parâmetros de paginação mudam
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  useEffect(() => {
    console.log('🔄 useEffect: Carregando lojas...', {
      currentPage,
      pageSize,
      sortBy,
      sortDir
    });
    
    const fetchStores = async () => {
      try {
        setIsLoading(true);
        
        const response = await storeService.getStoresWithPagination(
          currentPage,
          pageSize,
          sortBy,
          sortDir
        );
        
        setStores(response.stores);
        setTotalElements(response.totalElements);
        setTotalPages(response.totalPages);
        
        console.log('✅ Lojas carregadas com sucesso:', {
          stores: response.stores.length,
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          currentPage: response.currentPage
        });
      } catch (error) {
        console.error('❌ Erro ao carregar lojas:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStores();
  }, [currentPage, pageSize, sortBy, sortDir]); // Dependências diretas, sem função intermediária

  /**
   * Cria uma nova loja
   */
  const createStore = useCallback(async (storeData: CreateStoreRequest): Promise<void> => {
    try {
      await storeService.createStore(storeData);
      await loadStores(); // Recarrega a lista após criação
    } catch (error) {
      console.error('❌ Erro ao criar loja:', error);
      throw error;
    }
  }, [loadStores]);

  /**
   * Atualiza uma loja existente
   */
  const updateStore = useCallback(async (storeId: string, storeData: UpdateStoreRequest): Promise<void> => {
    try {
      await storeService.updateStore(storeId, storeData);
      await loadStores(); // Recarrega a lista após atualização
    } catch (error) {
      console.error('❌ Erro ao atualizar loja:', error);
      throw error;
    }
  }, [loadStores]);

  /**
   * Busca uma loja por ID
   */
  const getStoreById = useCallback(async (storeId: string): Promise<ApiStore> => {
    try {
      const response = await storeService.getStoreById(storeId);
      // Converter CreateStoreResponse para ApiStore se necessário
      if ('message' in response) {
        // É CreateStoreResponse, converter para ApiStore
        const dateObj = new Date(response.createdAt);
        const apiStore: ApiStore = {
          id: response.id,
          code: response.code,
          name: response.name,
          status: response.status,
          createdAt: [
            dateObj.getFullYear(), 
            dateObj.getMonth() + 1, 
            dateObj.getDate(),
            dateObj.getHours(),
            dateObj.getMinutes(),
            dateObj.getSeconds()
          ]
        };
        
        // Adicionar city apenas se existir
        if (response.city) {
          (apiStore as any).city = response.city;
        }
        
        return apiStore;
      }
      // Converter CreateStoreResponse para ApiStore forçadamente
      return response as unknown as ApiStore;
    } catch (error) {
      console.error('❌ Erro ao buscar loja por ID:', error);
      throw error;
    }
  }, []);

  /**
   * Recarrega a lista de lojas
   */
  const refreshStores = useCallback(async (): Promise<void> => {
    console.log('🔄 refreshStores: Forçando atualização das lojas...');
    await loadStores();
  }, [loadStores]);

  /**
   * Altera a página atual
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const changePage = useCallback((page: number): void => {
    console.log('🔄 changePage: Alterando página de', currentPage, 'para', page);
    setCurrentPage(page);
  }, [currentPage]);

  /**
   * Altera a ordenação
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const handleSort = useCallback((field: string): void => {
    console.log('🔄 handleSort: Alterando ordenação para', field);
    
    if (sortBy === field) {
      // Se já está ordenando por este campo, inverte a direção
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      // Se é um novo campo, ordena por ele em ordem crescente
      setSortBy(field);
      setSortDir('asc');
    }
    
    // Volta para a primeira página quando muda a ordenação
    setCurrentPage(0);
  }, [sortBy, sortDir]);

  return {
    // Estado
    stores,
    isLoading,
    
    // Estado de paginação
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    sortBy,
    sortDir,
    
    // Ações
    loadStores,
    createStore,
    updateStore,
    getStoreById,
    refreshStores,
    changePage,
    handleSort
  };
};