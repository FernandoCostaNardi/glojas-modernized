import { useState, useCallback } from 'react';
import { roleService } from '@/services/api';

/**
 * Interface para role disponível
 * Mapeia os campos exatos retornados pela API
 */
export interface AvailableRole {
  readonly id: string;
  readonly name: string;
  readonly description: string;
  readonly active: boolean; // Campo retornado pela API (corrigido para 'active')
  readonly createdAt: string;
  readonly updatedAt: string;
  readonly permissionNames: string[];
}

/**
 * Hook personalizado para gerenciar roles
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const useRoles = () => {
  const [roles, setRoles] = useState<AvailableRole[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Carrega as roles da API
   */
  const loadRoles = useCallback(async (): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      
      const apiRoles = await roleService.getActiveRoles();
      
      // Mapeia os dados da API para a interface local
      const mappedRoles: AvailableRole[] = apiRoles.map((role: any) => ({
        id: role.id,
        name: role.name,
        description: role.description,
        active: role.active, // Usa o campo correto 'active' da API
        createdAt: role.createdAt,
        updatedAt: role.updatedAt,
        permissionNames: role.permissionNames || []
      }));
      
      setRoles(mappedRoles);
      
    } catch (err) {
      console.error('❌ useRoles: Erro ao carregar roles:', err);
      setError('Erro ao carregar roles da API');
      
      // Em caso de erro, usa roles mockadas como fallback
      const fallbackRoles: AvailableRole[] = [
        {
          id: '1',
          name: 'ADMIN',
          description: 'Administrador do sistema',
          active: true, // Usa o campo correto 'active'
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          permissionNames: ['SYSTEM_ADMIN', 'USER_MANAGEMENT']
        },
        {
          id: '2',
          name: 'USER',
          description: 'Usuário padrão',
          active: true, // Usa o campo correto 'active'
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          permissionNames: ['BASIC_ACCESS']
        }
      ];
      setRoles(fallbackRoles);
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * Recarrega as roles
   */
  const refreshRoles = useCallback((): void => {
    loadRoles();
  }, [loadRoles]);

  return {
    roles,
    isLoading,
    error,
    loadRoles,
    refreshRoles
  };
};
