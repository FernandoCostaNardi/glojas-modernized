import axios from 'axios';
import { getToken } from '@/utils/token';
import {
  CreateUserRequest,
  CreateUserResponse,
  UpdateUserRequest,
  UpdateUserResponse,
  UserSearchFilters,
  UserPageResponse
} from '@/types';

/**
 * Configuração base do axios para comunicação com a API
 */
const api = axios.create({
  baseURL: 'http://localhost:8082/api/business',
  timeout: 10000,
});

/**
 * Interceptor para adicionar token de autenticação em todas as requisições
 */
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Interceptor para tratamento de erros de resposta
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado ou inválido
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

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

export default api;
