import React from 'react';
import { ApiSystemUser } from '@/types';

/**
 * Props da tabela de usuários
 */
interface UsersTableProps {
  readonly users: readonly ApiSystemUser[];
  readonly sortBy: string;
  readonly sortDir: string;
  readonly onSort: (field: string) => void;
  readonly onEditUser: (user: ApiSystemUser) => void;
  readonly onToggleUserStatus: (user: ApiSystemUser) => void;
  readonly onToggleUserLock: (user: ApiSystemUser) => void;
  readonly onChangePassword: (user: ApiSystemUser) => void;
}

/**
 * Componente de tabela de usuários
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const UsersTable: React.FC<UsersTableProps> = ({
  users,
  sortBy,
  sortDir,
  onSort,
  onEditUser,
  onToggleUserStatus,
  onToggleUserLock,
  onChangePassword
}) => {
  /**
   * Renderiza o ícone de ordenação
   */
  const renderSortIcon = (field: string) => {
    if (sortBy !== field) return null;
    
    return (
      <svg 
        className={`w-3 h-3 ml-1 ${sortDir === 'asc' ? 'rotate-180' : ''}`} 
        fill="none" 
        stroke="currentColor" 
        viewBox="0 0 24 24"
      >
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
      </svg>
    );
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-smart-gray-100 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-smart-gray-200">
          <thead className="bg-smart-gray-50">
            <tr>
              <th 
                className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider cursor-pointer hover:bg-smart-gray-100"
                onClick={() => onSort('name')}
              >
                <div className="flex items-center">
                  Usuário
                  {renderSortIcon('name')}
                </div>
              </th>
              <th 
                className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider cursor-pointer hover:bg-smart-gray-100"
                onClick={() => onSort('email')}
              >
                <div className="flex items-center">
                  Email
                  {renderSortIcon('email')}
                </div>
              </th>
              <th className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Roles
              </th>
              <th className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th 
                className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider cursor-pointer hover:bg-smart-gray-100"
                onClick={() => onSort('lastLoginDate')}
              >
                <div className="flex items-center">
                  Último Login
                  {renderSortIcon('lastLoginDate')}
                </div>
              </th>
              <th className="px-4 py-2 text-left text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
                Ações
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-smart-gray-200">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-smart-gray-50">
                <td className="px-4 py-3 whitespace-nowrap">
                  <div className="flex items-center">
                    <div className="w-8 h-8 bg-smart-red-100 rounded-full flex items-center justify-center">
                      <span className="text-smart-red-600 font-medium text-xs">
                        {user.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    <div className="ml-3">
                      <div className="text-sm font-medium text-smart-gray-900">
                        {user.name}
                      </div>
                      <div className="text-xs text-smart-gray-500">
                        @{user.username}
                      </div>
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-smart-gray-900">
                  {user.email}
                </td>
                <td className="px-4 py-3 whitespace-nowrap">
                  <div className="flex flex-wrap gap-1">
                    {user.roles.map((role, index) => (
                      <span
                        key={index}
                        className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-smart-red-100 text-smart-red-800"
                      >
                        {role}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="px-4 py-3 whitespace-nowrap">
                  <div className="flex flex-col gap-1">
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                      user.active 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {user.active ? 'Ativo' : 'Inativo'}
                    </span>
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                      user.notLocked 
                        ? 'bg-blue-100 text-blue-800' 
                        : 'bg-orange-100 text-orange-800'
                    }`}>
                      {user.notLocked ? 'Desbloqueado' : 'Bloqueado'}
                    </span>
                  </div>
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-xs text-smart-gray-500">
                  {user.lastLoginDate 
                    ? new Date(user.lastLoginDate).toLocaleDateString('pt-BR')
                    : 'Nunca'
                  }
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm font-medium">
                  <div className="flex items-center space-x-1">
                    {/* Botão Editar */}
                    <button
                      onClick={() => onEditUser(user)}
                      className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                      title="Editar usuário"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    
                    {/* Botão Ativar/Desativar */}
                    <button
                      onClick={() => onToggleUserStatus(user)}
                      className={`transition-colors duration-200 p-1 ${
                        user.active 
                          ? 'text-red-600 hover:text-red-900' 
                          : 'text-green-600 hover:text-green-900'
                      }`}
                      title={user.active ? 'Desabilitar usuário' : 'Habilitar usuário'}
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        {user.active ? (
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L5.636 5.636" />
                        ) : (
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        )}
                      </svg>
                    </button>
                    
                    {/* Botão Bloquear/Desbloquear */}
                    <button
                      onClick={() => onToggleUserLock(user)}
                      className={`transition-colors duration-200 p-1 ${
                        user.notLocked 
                          ? 'text-orange-600 hover:text-orange-900' 
                          : 'text-blue-600 hover:text-blue-900'
                      }`}
                      title={user.notLocked ? 'Bloquear usuário' : 'Desbloquear usuário'}
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        {user.notLocked ? (
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                        ) : (
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 11V7a4 4 0 118 0m-4 8v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2 2v6a2 2 0 002 2z" />
                        )}
                      </svg>
                    </button>
                    
                    {/* Botão Alterar Senha */}
                    <button
                      onClick={() => onChangePassword(user)}
                      className="text-smart-purple-600 hover:text-smart-purple-900 transition-colors duration-200 p-1"
                      title="Alterar senha"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
