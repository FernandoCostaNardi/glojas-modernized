import axios from 'axios';
import { API_CONFIG } from '@/config/api.config';
import { getToken } from '@/utils/token';
import {
  CreateUserRequest,
  CreateUserResponse,
  UpdateUserRequest,
  UpdateUserResponse,
  UserSearchFilters,
  UserPageResponse,
  CreateEventOriginRequest,
  UpdateEventOriginRequest,
  EventOriginResponse,
  EventOriginSearchResponse,
  EventOriginSearchParams,
  Operation,
  OperationKind,
  OperationSearchResponse,
  ApiStore,
  CreateStoreRequest,
  CreateStoreResponse,
  UpdateStoreRequest,
  UpdateStoreResponse,
  CreateEmailNotifierRequest,
  EmailNotifierResponse,
  EmailNotifierPageResponse,
  UpdateEmailNotifierRequest
} from '@/types';
import { StockPageResponse, StockFilters } from '@/types/stock';
import { PurchaseAnalysisPageResponse, PurchaseAnalysisFilters } from '@/types/purchaseAnalysis';

/**
 * Clientes Axios centralizados
 */
export const businessApi = axios.create({
  baseURL: API_CONFIG.BUSINESS_API_BASE_URL,
  timeout: 30000
});

export const legacyApi = axios.create({
  baseURL: API_CONFIG.LEGACY_API_BASE_URL,
  timeout: 30000
});

// Alias para compatibilidade retroativa com código existente
const api = businessApi;

/**
 * Interceptor para adicionar token de autenticação em todas as requisições
 */
// Interceptor de autenticação aplicado aos dois clientes
[businessApi, legacyApi].forEach((client) => {
  client.interceptors.request.use(
    (config) => {
      const token = getToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );
});

/**
 * Interceptor para tratamento de erros de resposta
 */
// Interceptor de resposta padronizado (401) em ambos os clientes
[businessApi, legacyApi].forEach((client) => {
  client.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
});

/**
 * Serviço para gerenciamento de roles
 */
export const roleService = {
  /**
   * Busca todas as roles ativas do sistema
   * @returns Promise com lista de roles ativas
   */
  getActiveRoles: async () => {
    try {
      const response = await api.get('/roles/active');
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar roles ativas:', error);
      throw error;
    }
  },

  /**
   * Busca todas as roles do sistema
   * @returns Promise com lista de todas as roles
   */
  getAllRoles: async () => {
    try {
      const response = await api.get('/roles');
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar todas as roles:', error);
      throw error;
    }
  },

  /**
   * Cria uma nova role no sistema
   * @param roleData Dados da role a ser criada
   * @returns Promise com dados da role criada
   */
  createRole: async (roleData: any) => {
    try {
      const response = await api.post('/roles/create', roleData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar role:', error);
      throw error;
    }
  },

  /**
   * Atualiza uma role existente
   * @param id ID da role
   * @param roleData Dados atualizados da role
   * @returns Promise com dados da role atualizada
   */
  updateRole: async (id: string, roleData: any) => {
    try {
      const response = await api.put(`/roles/${id}`, roleData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar role:', error);
      throw error;
    }
  },

  /**
   * Remove uma role do sistema
   * @param id ID da role a ser removida
   * @returns Promise de confirmação
   */
  deleteRole: async (id: string) => {
    try {
      const response = await api.delete(`/roles/${id}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao remover role:', error);
      throw error;
    }
  },

  /**
   * Altera o status ativo/inativo de uma role
   * @param roleId ID da role
   * @param isActive Novo status ativo
   * @returns Promise com resposta da API
   */
  updateRoleStatus: async (roleId: string, isActive: boolean) => {
    try {
      const response = await api.patch(`/roles/${roleId}/status`, { active: isActive });
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao alterar status da role:', error);
      throw error;
    }
  }
};

/**
 * Serviço para gerenciamento de permissões
 */
export const permissionService = {
  /**
   * Busca todas as permissões do sistema
   * @returns Promise com lista de todas as permissões
   */
  getAllPermissions: async () => {
    try {
      const response = await api.get('/permissions');
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar permissões:', error);
      throw error;
    }
  },

  /**
   * Cria uma nova permissão no sistema
   * @param permissionData Dados da permissão a ser criada
   * @returns Promise com dados da permissão criada
   */
  createPermission: async (permissionData: any) => {
    try {
      const response = await api.post('/permissions', permissionData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar permissão:', error);
      throw error;
    }
  },

  /**
   * Atualiza uma permissão existente
   * @param id ID da permissão
   * @param permissionData Dados atualizados da permissão
   * @returns Promise com dados da permissão atualizada
   */
  updatePermission: async (id: string, permissionData: any) => {
    try {
      const response = await api.put(`/permissions/${id}`, permissionData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar permissão:', error);
      throw error;
    }
  },

  /**
   * Remove uma permissão do sistema
   * @param id ID da permissão a ser removida
   * @returns Promise de confirmação
   */
  deletePermission: async (id: string) => {
    try {
      const response = await api.delete(`/permissions/${id}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao remover permissão:', error);
      throw error;
    }
  }
};

/**
 * Serviço para operações de usuários
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const userService = {
  /**
   * Cria um novo usuário no sistema
   * @param userData Dados do usuário a ser criado
   * @returns Resposta da API com dados do usuário criado
   */
  createUser: async (userData: CreateUserRequest): Promise<CreateUserResponse> => {
    try {
      const response = await api.post('/users/create', userData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar usuário:', error);
      throw error;
    }
  },

  /**
   * Atualiza um usuário existente no sistema
   * @param userId ID do usuário a ser atualizado
   * @param userData Dados do usuário a ser atualizado
   * @returns Resposta da API com dados do usuário atualizado
   */
  updateUser: async (userId: string, userData: UpdateUserRequest): Promise<UpdateUserResponse> => {
    try {
      const response = await api.put(`/users/${userId}`, userData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar usuário:', error);
      throw error;
    }
  },

  /**
   * Busca usuários com filtros e paginação
   * @param filters Filtros de busca
   * @param page Número da página (0-based)
   * @param size Tamanho da página
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação (asc/desc)
   * @returns Página de usuários com metadados de paginação
   */
  getUsersWithFilters: async (
    filters: UserSearchFilters,
    page: number = 0,
    size: number = 20,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Promise<UserPageResponse> => {
    try {
      const params = new URLSearchParams();
      
      // Parâmetros de paginação
      params.append('page', page.toString());
      params.append('size', size.toString());
      params.append('sortBy', sortBy);
      params.append('sortDir', sortDir);
      
      // Filtros
      if (filters.name) params.append('name', filters.name);
      if (filters.roles && filters.roles.length > 0) {
        filters.roles.forEach((role: string) => params.append('roles', role));
      }
      if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString());
      if (filters.isNotLocked !== undefined) params.append('isNotLocked', filters.isNotLocked.toString());
      
      const response = await api.get(`/users?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar usuários com filtros:', error);
      throw error;
    }
  },

  /**
   * Altera o status ativo/inativo de um usuário
   * @param userId ID do usuário
   * @param isActive Novo status ativo
   * @param comment Comentário obrigatório para auditoria
   * @returns Promise com resposta da API
   */
  toggleUserStatus: async (userId: string, isActive: boolean, comment: string): Promise<any> => {
    try {
      const response = await api.patch(`/users/${userId}/status`, {
        isActive,
        comment
      });
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao alterar status do usuário:', error);
      throw error;
    }
  },

  /**
   * Altera o status de bloqueio de um usuário
   * @param userId ID do usuário
   * @param isNotLocked Novo status de bloqueio
   * @param comment Comentário obrigatório para auditoria
   * @returns Promise com resposta da API
   */
  toggleUserLock: async (userId: string, isNotLocked: boolean, comment: string): Promise<any> => {
    try {
      const response = await api.patch(`/users/${userId}/lock`, {
        isNotLocked,
        comment
      });
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao alterar status de bloqueio do usuário:', error);
      throw error;
    }
  }
};

/**
 * Serviço para operações de EventOrigin (Códigos de Origem)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const eventOriginService = {
  /**
   * Cria um novo código de origem no sistema
   * @param data Dados do código de origem a ser criado
   * @returns Resposta da API com dados do código criado
   */
  createEventOrigin: async (data: CreateEventOriginRequest): Promise<EventOriginResponse> => {
    try {
      const response = await api.post('/event-origins', data);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar código de origem:', error);
      throw error;
    }
  },

  /**
   * Atualiza um código de origem existente no sistema
   * @param id ID do código de origem a ser atualizado
   * @param data Dados do código de origem a ser atualizado
   * @returns Resposta da API com dados do código atualizado
   */
  updateEventOrigin: async (id: string, data: UpdateEventOriginRequest): Promise<EventOriginResponse> => {
    try {
      const response = await api.put(`/event-origins/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar código de origem:', error);
      throw error;
    }
  },

  /**
   * Busca um código de origem por ID
   * @param id ID do código de origem
   * @returns Dados do código de origem
   */
  getEventOriginById: async (id: string): Promise<EventOriginResponse> => {
    try {
      const response = await api.get(`/event-origins/${id}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar código de origem por ID:', error);
      throw error;
    }
  },

  /**
   * Busca códigos de origem com filtros e paginação
   * @param params Parâmetros de busca (filtros, paginação, ordenação)
   * @returns Página de códigos de origem com metadados de paginação
   */
  getEventOrigins: async (params: EventOriginSearchParams): Promise<EventOriginSearchResponse> => {
    try {
      const response = await api.get('/event-origins', { params });
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar códigos de origem:', error);
      throw error;
    }
  }
};

/**
 * Serviço para operações de Operation (Códigos de Operação)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const operationService = {
  /**
   * Busca operações com filtros e paginação
   * @param code Filtro por código (opcional)
   * @param operationSource Filtro por fonte da operação (opcional)
   * @param page Número da página (0-based)
   * @param size Tamanho da página
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação (asc/desc)
   * @returns Promise com resposta paginada
   */
  getOperationsWithFilters: async (
    code?: string,
    operationSource?: string,
    page: number = 0,
    size: number = 5,
    sortBy: string = 'code',
    sortDir: string = 'asc'
  ): Promise<OperationSearchResponse> => {
    try {
      console.log('🌐 API: Fazendo requisição GET /operations com paginação');
      
      const params = new URLSearchParams();
      if (code) params.append('code', code);
      if (operationSource) params.append('operationSource', operationSource);
      params.append('page', page.toString());
      params.append('size', size.toString());
      params.append('sortBy', sortBy);
      params.append('sortDir', sortDir);
      
      const response = await api.get(`/operations?${params.toString()}`);
      console.log('🌐 API: Resposta recebida para /operations:', response.data);
      
      // Debug: verificar formato das datas
      if (response.data.operations && response.data.operations.length > 0) {
        console.log('🌐 API: Exemplo de data recebida:', response.data.operations[0].createdAt);
        console.log('🌐 API: Tipo da data:', typeof response.data.operations[0].createdAt);
      }
      
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar operações:', error);
      throw error;
    }
  },

  /**
   * Busca todas as operações do sistema (método legacy)
   * @returns Promise com lista de todas as operações
   */
  getAllOperations: async (): Promise<Operation[]> => {
    try {
      const response = await operationService.getOperationsWithFilters(undefined, undefined, 0, 1000);
      return [...response.operations];
    } catch (error) {
      console.error('❌ Erro ao buscar operações:', error);
      throw error;
    }
  },

  /**
   * Busca uma operação específica por ID
   * @param operationId ID da operação
   * @returns Promise com dados da operação
   */
  getOperationById: async (operationId: string): Promise<Operation> => {
    try {
      const response = await api.get(`/operations/${operationId}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar operação por ID:', error);
      throw error;
    }
  },

  /**
   * Cria uma nova operação no sistema
   * @param operationData Dados da operação a ser criada
   * @returns Promise com dados da operação criada
   */
  createOperation: async (operationData: any): Promise<Operation> => {
    try {
      const response = await api.post('/operations', operationData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar operação:', error);
      throw error;
    }
  },

  /**
   * Atualiza uma operação existente
   * @param operationId ID da operação
   * @param operationData Dados atualizados da operação
   * @returns Promise com dados da operação atualizada
   */
  updateOperation: async (operationId: string, operationData: any): Promise<Operation> => {
    try {
      const response = await api.put(`/operations/${operationId}`, operationData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar operação:', error);
      throw error;
    }
  }
};

/**
 * Serviço para tipos de operação (OperationKind)
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const operationKindService = {
  /**
   * Busca todos os tipos de operação disponíveis
   * @returns Promise com lista de tipos de operação
   */
  getAllOperationKinds: async (): Promise<OperationKind[]> => {
    try {
      console.log('🌐 API: Fazendo requisição GET /operation-kinds');
      const response = await api.get('/operation-kinds');
      console.log('🌐 API: Resposta recebida para /operation-kinds:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar tipos de operação:', error);
      throw error;
    }
  }
};

/**
 * Serviço para operações de lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const storeService = {
  /**
   * Busca todas as lojas cadastradas
   * @returns Promise com lista de todas as lojas
   */
  getAllStores: async (): Promise<ApiStore[]> => {
    try {
      const response = await api.get('/stores');
      // A API retorna um objeto Page, precisamos extrair o content
      if (response.data && response.data.content) {
        return response.data.content;
      }
      // Fallback: se não for um objeto Page, retorna os dados diretamente
      return Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      console.error('❌ Erro ao buscar lojas:', error);
      throw error;
    }
  },

  /**
   * Busca lojas com paginação e filtros
   * @param page Número da página (baseado em 0)
   * @param size Tamanho da página
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação (asc ou desc)
   * @returns Promise com resposta paginada
   */
  getStoresWithPagination: async (
    page: number = 0,
    size: number = 5,
    sortBy: string = 'code',
    sortDir: string = 'asc'
  ): Promise<{
    stores: ApiStore[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    pageSize: number;
    hasNext: boolean;
    hasPrevious: boolean;
  }> => {
    try {
      const response = await api.get('/stores', {
        params: {
          page,
          size,
          sortBy,
          sortDir
        }
      });

      // A API retorna um objeto Page do Spring
      const pageData = response.data;
      
      return {
        stores: pageData.content || [],
        totalElements: pageData.totalElements || 0,
        totalPages: pageData.totalPages || 0,
        currentPage: pageData.number || 0,
        pageSize: pageData.size || size,
        hasNext: pageData.last === false,
        hasPrevious: pageData.first === false
      };
    } catch (error) {
      console.error('❌ Erro ao buscar lojas com paginação:', error);
      throw error;
    }
  },

  /**
   * Cria uma nova loja no sistema
   * @param storeData Dados da loja a ser criada
   * @returns Resposta da API com dados da loja criada
   */
  createStore: async (storeData: CreateStoreRequest): Promise<CreateStoreResponse> => {
    try {
      const response = await api.post('/stores', storeData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar loja:', error);
      throw error;
    }
  },

  /**
   * Atualiza uma loja existente no sistema
   * @param storeId ID da loja a ser atualizada
   * @param storeData Dados da loja a ser atualizada
   * @returns Resposta da API com dados da loja atualizada
   */
  updateStore: async (storeId: string, storeData: UpdateStoreRequest): Promise<UpdateStoreResponse> => {
    try {
      const response = await api.put(`/stores/${storeId}`, storeData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar loja:', error);
      throw error;
    }
  },

  /**
   * Busca uma loja específica por ID
   * @param storeId ID da loja
   * @returns Promise com dados da loja
   */
  getStoreById: async (storeId: string): Promise<CreateStoreResponse> => {
    try {
      const response = await api.get(`/stores/${storeId}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar loja por ID:', error);
      throw error;
    }
  },

  /**
   * Busca lojas legacy para seleção no modal
   * @returns Promise com lista de lojas legacy
   */
  getLegacyStores: async (): Promise<any[]> => {
    try {
      const response = await api.get('/legacy/stores');
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar lojas legacy:', error);
      throw error;
    }
  }
};

/**
 * Serviço para gerenciamento de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const emailNotifierService = {
  /**
   * Cria um novo EmailNotifier
   * @param emailNotifierData Dados do EmailNotifier a ser criado
   * @returns Promise com dados do EmailNotifier criado
   */
  createEmailNotifier: async (emailNotifierData: CreateEmailNotifierRequest): Promise<EmailNotifierResponse> => {
    try {
      const response = await api.post('/email-notifiers', emailNotifierData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao criar EmailNotifier:', error);
      throw error;
    }
  },

  /**
   * Busca todos os EmailNotifiers com paginação
   * @param page Número da página (0-based)
   * @param size Tamanho da página
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação (asc/desc)
   * @returns Promise com página de EmailNotifiers
   */
  getAllEmailNotifiers: async (
    page: number = 0,
    size: number = 20,
    sortBy: string = 'email',
    sortDir: string = 'asc'
  ): Promise<EmailNotifierPageResponse> => {
    try {
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('size', size.toString());
      params.append('sortBy', sortBy);
      params.append('sortDir', sortDir);

      const response = await api.get(`/email-notifiers?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar EmailNotifiers:', error);
      throw error;
    }
  },

  /**
   * Busca um EmailNotifier específico por ID
   * @param id ID do EmailNotifier
   * @returns Promise com dados do EmailNotifier
   */
  getEmailNotifierById: async (id: string): Promise<EmailNotifierResponse> => {
    try {
      const response = await api.get(`/email-notifiers/${id}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar EmailNotifier por ID:', error);
      throw error;
    }
  },

  /**
   * Atualiza um EmailNotifier existente
   * @param id ID do EmailNotifier a ser atualizado
   * @param emailNotifierData Dados do EmailNotifier a ser atualizado
   * @returns Promise com dados do EmailNotifier atualizado
   */
  updateEmailNotifier: async (id: string, emailNotifierData: UpdateEmailNotifierRequest): Promise<EmailNotifierResponse> => {
    try {
      const response = await api.put(`/email-notifiers/${id}`, emailNotifierData);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao atualizar EmailNotifier:', error);
      throw error;
    }
  },

  /**
   * Remove um EmailNotifier
   * @param id ID do EmailNotifier a ser removido
   * @returns Promise de confirmação
   */
  deleteEmailNotifier: async (id: string): Promise<void> => {
    try {
      await api.delete(`/email-notifiers/${id}`);
    } catch (error) {
      console.error('❌ Erro ao remover EmailNotifier:', error);
      throw error;
    }
  }
};

/**
 * Serviço para operações de estoque
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const stockService = {
  /**
   * Busca estoque com filtros, paginação e ordenação
   * @param filters Filtros de busca
   * @returns Promise com resposta paginada de estoque
   */
  getStocks: async (filters: StockFilters): Promise<StockPageResponse> => {
    try {
      const params = new URLSearchParams();
      
      // Adicionar filtros opcionais
      if (filters.refplu) {
        params.append('refplu', filters.refplu);
      }
      if (filters.marca) {
        params.append('marca', filters.marca);
      }
      if (filters.descricao) {
        params.append('descricao', filters.descricao);
      }
      if (filters.hasStock !== undefined) {
        params.append('hasStock', filters.hasStock.toString());
      }
      
      // Adicionar parâmetros de paginação e ordenação
      params.append('page', filters.page.toString());
      params.append('size', filters.size.toString());
      params.append('sortBy', filters.sortBy);
      params.append('sortDir', filters.sortDir);

      const response = await api.get(`/stocks?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar estoque:', error);
      throw error;
    }
  },

  /**
   * Testa conectividade com o serviço de estoque
   * @returns Promise com status da conexão
   */
  testConnection: async (): Promise<{ status: string; service: string; timestamp: string; version: string }> => {
    try {
      const response = await api.get('/stocks/health');
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao testar conexão com estoque:', error);
      throw error;
    }
  }
};

/**
 * Serviço para análise de compras
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const purchaseAnalysisService = {
  /**
   * Busca análise de compras com filtros, paginação e ordenação
   * @param filters Filtros de busca
   * @returns Promise com resposta paginada de análise de compras
   */
  getPurchaseAnalysis: async (filters: PurchaseAnalysisFilters): Promise<PurchaseAnalysisPageResponse> => {
    try {
      const params = new URLSearchParams();
      
      // Adicionar filtros opcionais
      if (filters.refplu) {
        params.append('refplu', filters.refplu);
      }
      
      // Adicionar filtro de produtos sem vendas (padrão true)
      params.append('hideNoSales', (filters.hideNoSales ?? true).toString());
      
      // Adicionar parâmetros de paginação e ordenação
      params.append('page', filters.page.toString());
      params.append('size', filters.size.toString());
      params.append('sortBy', filters.sortBy);
      params.append('sortDir', filters.sortDir);

      const response = await api.get(`/api/v1/purchase-analysis?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar análise de compras:', error);
      throw error;
    }
  }
};

/**
 * Serviço para estoque crítico
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const criticalStockService = {
  /**
   * Busca produtos com estoque crítico (estoque < média mensal de vendas)
   * @param filters Filtros de busca (sem hideNoSales)
   * @returns Promise com resposta paginada de estoque crítico
   */
  getCriticalStock: async (filters: Omit<PurchaseAnalysisFilters, 'hideNoSales'>): Promise<PurchaseAnalysisPageResponse> => {
    try {
      const params = new URLSearchParams();

      // Adicionar filtro opcional
      if (filters.refplu) {
        params.append('refplu', filters.refplu);
      }

      // Adicionar parâmetros de paginação e ordenação
      params.append('page', filters.page.toString());
      params.append('size', filters.size.toString());
      params.append('sortBy', filters.sortBy);
      params.append('sortDir', filters.sortDir);

      const response = await api.get(`/api/v1/critical-stock?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('❌ Erro ao buscar estoque crítico:', error);
      throw error;
    }
  }
};

export default api;
