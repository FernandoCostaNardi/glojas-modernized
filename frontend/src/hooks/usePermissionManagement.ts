import { useState, useCallback, useRef } from 'react';
import { Role, Permission } from '@/types';
import { roleService, permissionService } from '@/services/api';

/**
 * Hook customizado para gerenciamento de roles e permissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const usePermissionManagement = () => {
  // Estado para roles
  const [roles, setRoles] = useState<Role[]>([]);
  const [isLoadingRoles, setIsLoadingRoles] = useState<boolean>(true);
  
  // Estado para permissões
  const [permissions, setPermissions] = useState<Permission[]>([]);
  
  // Refs para controlar carregamento único
  const hasLoadedRoles = useRef<boolean>(false);
  const hasLoadedPermissions = useRef<boolean>(false);

  /**
   * Carrega as roles do sistema
   * Permite recarregamento forçado quando necessário
   */
  const loadRoles = useCallback(async (forceRefresh: boolean = false) => {
    // Se não for refresh forçado e já carregou, não recarrega
    if (!forceRefresh && hasLoadedRoles.current) {
      return;
    }
    
    try {
      setIsLoadingRoles(true);
      
      const rolesData = await roleService.getAllRoles();
      setRoles(rolesData);
      
      // Marca como carregado apenas se não for refresh forçado
      if (!forceRefresh) {
        hasLoadedRoles.current = true;
      }
    } catch (error) {
      console.error('Erro ao carregar roles:', error);
      // Em caso de erro, permite nova tentativa
      hasLoadedRoles.current = false;
    } finally {
      setIsLoadingRoles(false);
    }
  }, []);

  /**
   * Recarrega as roles do sistema (força atualização)
   * Utilizado após operações CRUD para manter dados sincronizados
   */
  const refreshRoles = useCallback(async () => {
    await loadRoles(true);
  }, [loadRoles]);

  /**
   * Carrega as permissões do sistema
   */
  const loadPermissions = useCallback(async () => {
    if (hasLoadedPermissions.current) {
      return;
    }
    
    try {
      hasLoadedPermissions.current = true;
      
      const permissionsData = await permissionService.getAllPermissions();
      setPermissions(permissionsData);
    } catch (error) {
      console.error('Erro ao carregar permissões:', error);
      hasLoadedPermissions.current = false;
    }
  }, []);

  /**
   * Deleta uma role
   */
  const deleteRole = useCallback(async (roleId: string) => {
    try {
      await roleService.deleteRole(roleId);
      // Recarrega roles após exclusão para manter sincronização
      await refreshRoles();
    } catch (error) {
      console.error('Erro ao deletar role:', error);
      throw error;
    }
  }, [refreshRoles]);

  /**
   * Altera o status de uma role
   */
  const toggleRoleStatus = useCallback(async (role: Role) => {
    try {
      await roleService.updateRoleStatus(role.id, !role.active);
      // Recarrega roles após alteração para manter sincronização
      await refreshRoles();
    } catch (error) {
      console.error('Erro ao alterar status da role:', error);
      throw error;
    }
  }, [refreshRoles]);

  return {
    // Estado
    roles,
    permissions,
    isLoadingRoles,
    
    // Ações
    loadRoles,
    loadPermissions,
    refreshRoles,
    deleteRole,
    toggleRoleStatus
  };
};
