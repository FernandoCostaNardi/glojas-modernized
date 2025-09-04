import React from 'react';
import { Role } from '@/types';
import { useRoleToggle } from '@/hooks/useRoleToggle';

/**
 * Props da tabela de roles
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface RolesTableProps {
  readonly roles: readonly Role[];
  readonly isLoading: boolean;
  readonly onEditRole: (role: Role) => void;
  readonly onDeleteRole: (role: Role) => void;
  readonly onToggleRoleStatus: (role: Role) => void;
}

/**
 * Tabela de roles do sistema
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const RolesTable: React.FC<RolesTableProps> = ({
  roles,
  isLoading,
  onEditRole,
  onToggleRoleStatus
}) => {
  // Hook para gerenciamento de toggle com estados granulares
  const { state: toggleState, toggleRole, canToggleRole } = useRoleToggle({
    enableRetry: true,
    maxRetries: 3,
    enableLogging: true
  });

  /**
   * Manipula o toggle de status da role
   * Integra com o hook customizado e o callback do componente pai
   */
  const handleToggleRole = async (role: Role): Promise<void> => {
    try {
      // Validar se pode alterar a role antes de executar
      if (!canToggleRole(role)) {
        console.warn(`Não é possível alterar a role "${role.name}" - role crítica do sistema`);
        return;
      }

      // Executar toggle usando o hook
      await toggleRole(role);
      
      // Notificar componente pai para atualizar dados
      onToggleRoleStatus(role);
    } catch (error) {
      console.error('Erro ao alternar status da role:', error);
    }
  };

  /**
   * Verifica se uma role específica está sendo processada
   */
  const isRoleToggling = (roleId: string): boolean => {
    return toggleState.isToggling && toggleState.togglingRoleId === roleId;
  };

  if (isLoading) {
    return (
      <div className="text-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600 mx-auto"></div>
        <p className="mt-2 text-smart-gray-600">Carregando roles...</p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      {/* Indicador de erro global */}
      {toggleState.error && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-red-800">{toggleState.error}</p>
            </div>
          </div>
        </div>
      )}

      <table className="min-w-full divide-y divide-smart-gray-200">
        <thead className="bg-smart-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Nome
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Descrição
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Permissões
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Status
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Ações
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-smart-gray-200">
          {roles.map((role) => (
            <tr key={role.id} className="hover:bg-smart-gray-50">
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="text-sm font-medium text-smart-gray-900">{role.name}</div>
              </td>
              <td className="px-6 py-4">
                <div className="text-sm text-smart-gray-900 max-w-xs truncate">
                  {role.description || 'Sem descrição'}
                </div>
              </td>
              <td className="px-6 py-4">
                <div className="flex flex-wrap gap-1">
                  {role.permissionNames.slice(0, 3).map((permission) => (
                    <span
                      key={permission}
                      className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-smart-blue-100 text-smart-blue-800"
                    >
                      {permission}
                    </span>
                  ))}
                  {role.permissionNames.length > 3 && (
                    <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-smart-gray-100 text-smart-gray-800">
                      +{role.permissionNames.length - 3}
                    </span>
                  )}
                </div>
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <span
                  className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    role.active
                      ? 'bg-green-100 text-green-800'
                      : 'bg-red-100 text-red-800'
                  }`}
                >
                  {role.active ? 'Ativa' : 'Inativa'}
                </span>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div className="flex items-center space-x-2">
                  {/* Botão Editar */}
                  <button
                    onClick={() => onEditRole(role)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                    title="Editar role"
                    aria-label={`Editar role ${role.name}`}
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  
                  {/* Botão Ativar/Desativar */}
                  <button
                    onClick={() => handleToggleRole(role)}
                    disabled={isRoleToggling(role.id)}
                    className={`transition-all duration-200 p-1 ${
                      isRoleToggling(role.id)
                        ? 'text-smart-blue-600 cursor-wait'
                        : role.active
                        ? 'text-smart-orange-600 hover:text-smart-orange-900 hover:bg-smart-orange-50'
                        : 'text-smart-green-600 hover:text-smart-green-900 hover:bg-smart-green-50'
                    }`}
                    title={
                      isRoleToggling(role.id)
                        ? 'Processando...'
                        : role.active
                        ? 'Desativar role'
                        : 'Ativar role'
                    }
                    aria-label={`${role.active ? 'Desativar' : 'Ativar'} role ${role.name}`}
                  >
                    {isRoleToggling(role.id) ? (
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current"></div>
                    ) : role.active ? (
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L5.636 5.636" />
                      </svg>
                    ) : (
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    )}
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
