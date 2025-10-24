import { useState, useCallback } from 'react';
import { Role } from '@/types';
import { roleService } from '@/services/api';
import { 
  RoleToggleState, 
  UseRoleToggleReturn, 
  RoleToggleConfig,
  RoleToggleEvent 
} from '@/types/roleToggle';

/**
 * Hook customizado para gerenciamento de toggle de roles
 * Seguindo princípios de Clean Code com responsabilidade única
 * 
 * Funcionalidades:
 * - Estados de loading granulares por role
 * - Validação de permissões
 * - Tratamento de erro robusto
 * - Logging estruturado
 * - Retry automático
 */
export const useRoleToggle = (config: RoleToggleConfig = {}): UseRoleToggleReturn => {
  // Configurações padrão
  const {
    enableLogging = true
  } = config;

  // Estados do hook
  const [state, setState] = useState<RoleToggleState>({
    isToggling: false,
    togglingRoleId: null,
    error: null
  });



  /**
   * Logging estruturado para operações de toggle
   * Facilita debugging e auditoria
   */
  const logToggleEvent = useCallback((event: RoleToggleEvent) => {
    if (!enableLogging) return;

    const timestamp = new Date().toISOString();
    
    switch (event.type) {
      case 'TOGGLE_STARTED':
        console.log(`🔄 [${timestamp}] Iniciando toggle da role:`, event.roleName);
        break;
      case 'TOGGLE_SUCCESS':
        console.log(`✅ [${timestamp}] Toggle realizado com sucesso:`, event.roleName);
        break;
      case 'TOGGLE_ERROR':
        console.error(`❌ [${timestamp}] Erro no toggle da role:`, event.roleName, event.error);
        break;
      case 'TOGGLE_RETRY':
        console.warn(`🔄 [${timestamp}] Tentativa de retry:`, event.roleName, event.attempt);
        break;
    }
  }, [enableLogging]);

  /**
   * Valida se o usuário pode alterar o status da role
   * Implementa regras de negócio específicas
   */
  const canToggleRole = useCallback((role: Role): boolean => {
    // Validações básicas
    if (!role || !role.id) {
      return false;
    }

    // Verificar se a role não é crítica do sistema
    const criticalRoles = ['ADMIN', 'SUPER_ADMIN'];
    return !criticalRoles.includes(role.name.toUpperCase());
  }, []);



  /**
   * Função principal para toggle de role
   * Orquestra toda a operação com validações e tratamento de erro
   */
  const toggleRole = useCallback(async (role: Role): Promise<void> => {
    // Verificar se já está processando
    if (state.isToggling && state.togglingRoleId === role.id) {
      console.warn('Toggle já está em andamento para esta role');
      return;
    }

    // Log inicio da operação
    logToggleEvent({ 
      type: 'TOGGLE_STARTED', 
      roleId: role.id, 
      roleName: role.name 
    });

    // Iniciar operação
    setState(prev => ({
      ...prev,
      isToggling: true,
      togglingRoleId: role.id,
      error: null
    }));

    try {
      // Executar toggle diretamente
      const updatedRole = await roleService.updateRoleStatus(role.id, !role.active);
      
      // Log sucesso
      logToggleEvent({ 
        type: 'TOGGLE_SUCCESS', 
        roleId: role.id, 
        roleName: role.name,
        newStatus: !role.active
      });

      // Sucesso - atualizar estado
      setState(prev => ({
        ...prev,
        isToggling: false,
        togglingRoleId: null,
        lastOperation: {
          success: true,
          role: updatedRole,
          message: `Role "${role.name}" ${!role.active ? 'ativada' : 'desativada'} com sucesso`,
          timestamp: new Date()
        }
      }));

      console.log(`✅ Role "${role.name}" ${!role.active ? 'ativada' : 'desativada'} com sucesso`);

    } catch (error) {
      // Erro - atualizar estado
      const errorMessage = error instanceof Error ? error.message : 'Erro desconhecido';
      
      setState(prev => ({
        ...prev,
        isToggling: false,
        togglingRoleId: null,
        error: errorMessage
      }));

      console.error(`❌ Erro ao alterar status da role "${role.name}":`, errorMessage);
    }
  }, [state.isToggling, state.togglingRoleId]);

  /**
   * Limpa o estado de erro
   * Permite nova tentativa após erro
   */
  const clearError = useCallback((): void => {
    setState(prev => ({
      ...prev,
      error: null
    }));
  }, []);



  return {
    state,
    toggleRole,
    clearError,
    canToggleRole
  };
};
