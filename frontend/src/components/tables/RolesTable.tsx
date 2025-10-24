import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
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
 * Tabela de roles do sistema mobile-first
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const RolesTable: React.FC<RolesTableProps> = ({
  roles,
  onEditRole,
  onToggleRoleStatus
}) => {
  const { isMobile } = useLayout();
  
  // Hook para gerenciamento de toggle com estados granulares
  const { state: toggleState, toggleRole, canToggleRole } = useRoleToggle({
    enableRetry: true,
    maxRetries: 3,
    enableLogging: true
  });

  // Estado padrão seguro para evitar erros
  const safeToggleState = {
    ...toggleState,
    error: toggleState.error || null
  };

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
      console.error('Erro ao alterar status da role:', error);
    }
  };

  /**
   * Verifica se uma role específica está sendo alterada
   */
  const isRoleToggling = (roleId: string): boolean => {
    return safeToggleState.isToggling && safeToggleState.togglingRoleId === roleId;
  };

  /**
   * Renderiza um card de role para mobile
   */
  const renderRoleCard = (role: Role): React.ReactNode => (
    <div key={role.id} className="bg-white rounded-lg shadow-sm border border-smart-gray-200 p-4 mb-3">
      {/* Header do card */}
      <div className="flex items-center justify-between mb-3">
        <div>
          <div className="text-sm font-semibold text-smart-gray-900">
            {role.name}
          </div>
          <div className="text-xs text-smart-gray-500 mt-1">
            {role.description || 'Sem descrição'}
          </div>
        </div>
        
        {/* Status badge */}
        <span
          className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
            role.active
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          }`}
        >
          {role.active ? 'Ativa' : 'Inativa'}
        </span>
      </div>
      
      {/* Permissões */}
      <div className="mb-4">
        <div className="text-xs text-smart-gray-500 mb-2">Permissões ({role.permissionNames.length})</div>
        <div className="flex flex-wrap gap-1">
          {role.permissionNames.slice(0, 4).map((permission) => (
            <span
              key={permission}
              className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-smart-blue-100 text-smart-blue-800"
            >
              {permission}
            </span>
          ))}
          {role.permissionNames.length > 4 && (
            <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-smart-gray-100 text-smart-gray-800">
              +{role.permissionNames.length - 4} mais
            </span>
          )}
        </div>
      </div>
      
      {/* Ações */}
      <div className="flex justify-end space-x-2 pt-3 border-t border-smart-gray-100">
        <button
          onClick={() => onEditRole(role)}
          className="flex items-center px-3 py-1.5 text-xs font-medium text-smart-blue-600 hover:text-smart-blue-800 hover:bg-smart-blue-50 rounded-md transition-colors duration-200"
        >
          <svg className="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          Editar
        </button>
        
        <button
          onClick={() => handleToggleRole(role)}
          disabled={isRoleToggling(role.id)}
          className={`flex items-center px-3 py-1.5 text-xs font-medium rounded-md transition-colors duration-200 ${
            isRoleToggling(role.id)
              ? 'text-smart-blue-600 bg-smart-blue-50 cursor-wait'
              : role.active
              ? 'text-orange-600 hover:text-orange-800 hover:bg-orange-50'
              : 'text-green-600 hover:text-green-800 hover:bg-green-50'
          }`}
        >
          {isRoleToggling(role.id) ? (
            <>
              <div className="animate-spin rounded-full h-3 w-3 border-b-2 border-current mr-1"></div>
              Processando...
            </>
          ) : (
            <>
              <svg className="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {role.active ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L5.636 5.636" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                )}
              </svg>
              {role.active ? 'Desativar' : 'Ativar'}
            </>
          )}
        </button>
      </div>
    </div>
  );

  /**
   * Renderiza visualização mobile com cards
   */
  const renderMobileView = (): React.ReactNode => (
    <div className="mx-2">
      {roles.map(renderRoleCard)}
    </div>
  );

  /**
   * Renderiza tabela desktop
   */
  const renderDesktopTable = (): React.ReactNode => (
    <div className="bg-white rounded-lg shadow-smart-md border border-smart-gray-100 overflow-hidden">
      {/* Error state */}
      {safeToggleState.error && (
        <div className="bg-red-50 border-l-4 border-red-400 p-4">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-red-800">{safeToggleState.error}</p>
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

  /**
   * Renderiza a visualização adequada conforme o dispositivo
   */
  if (isMobile) {
    return renderMobileView();
  }
  
  return renderDesktopTable();
};

export { RolesTable };