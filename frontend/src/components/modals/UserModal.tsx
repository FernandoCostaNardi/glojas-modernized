import React, { useState, useEffect } from 'react';
import { useRoles } from '@/hooks/useRoles';
import { userService } from '@/services/api';
import type { CreateUserRequest, CreateUserResponse, UpdateUserRequest, UpdateUserResponse, ApiSystemUser } from '@/types';

/**
 * Interface para dados do formulário
 */
interface UserFormData {
  name: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  isActive: boolean;
  isNotLocked: boolean;
  selectedRoles: string[];
}

/**
 * Interface para propriedades do modal
 */
interface UserModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: 'create' | 'edit';
  readonly user: ApiSystemUser | null;
  readonly onSuccess?: (message: string) => void; // Callback para notificar sucesso
}

/**
 * Modal de Cadastro/Edição de Usuário
 * Com abas separadas para dados básicos e permissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const UserModal: React.FC<UserModalProps> = ({
  isOpen,
  onClose,
  mode,
  user,
  onSuccess
}) => {
  const [activeTab, setActiveTab] = useState<'basic' | 'permissions'>('basic');
  const [formData, setFormData] = useState<UserFormData>({
    name: '',
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    isActive: true,
    isNotLocked: true,
    selectedRoles: []
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  
  // Hook para gerenciar roles
  const { roles: localAvailableRoles, isLoading: rolesLoading, loadRoles } = useRoles();

  /**
   * Inicializa o formulário com dados do usuário (modo edição)
   */
  useEffect(() => {
    if (mode === 'edit' && user) {
      setFormData({
        name: user.name,
        username: user.username,
        email: user.email,
        password: '',
        confirmPassword: '',
        isActive: user.active,
        isNotLocked: user.notLocked,
        selectedRoles: user.roles
      });
    } else {
      // Reset para modo criação
      setFormData({
        name: '',
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        isActive: true,
        isNotLocked: true,
        selectedRoles: []
      });
    }
    setErrors({});
  }, [mode, user]);

  /**
   * Manipula a mudança de aba
   */
  const handleTabChange = (tab: 'basic' | 'permissions'): void => {
    setActiveTab(tab);

    // Sempre carrega as roles da API quando a aba de permissões for selecionada
    if (tab === 'permissions') {
      loadRoles();
    }
  };

  /**
   * Valida os dados do formulário
   */
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    // Validações básicas
    if (!formData.name.trim()) {
      newErrors.name = 'Nome é obrigatório';
    }

    if (!formData.username.trim()) {
      newErrors.username = 'Username é obrigatório';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Username deve ter pelo menos 3 caracteres';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email é obrigatório';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Email deve ser válido';
    }

    // Validações de senha (apenas para criação ou se fornecida na edição)
    if (mode === 'create' || formData.password) {
      if (!formData.password) {
        newErrors.password = 'Senha é obrigatória';
      } else if (formData.password.length < 8) {
        newErrors.password = 'Senha deve ter pelo menos 8 caracteres';
      }

      if (formData.password !== formData.confirmPassword) {
        newErrors.confirmPassword = 'Senhas não coincidem';
      }
    }

    // Validação de roles - OBRIGATÓRIA para criação e edição
    if (formData.selectedRoles.length === 0) {
      newErrors.roles = 'Pelo menos uma role deve ser selecionada';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Manipula mudanças nos campos do formulário
   */
  const handleInputChange = (field: keyof UserFormData, value: string | boolean | string[]): void => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Limpa erro do campo quando usuário começa a digitar
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  /**
   * Manipula seleção/desseleção de roles
   */
  const handleRoleToggle = (roleName: string): void => {
    setFormData(prev => ({
      ...prev,
      selectedRoles: prev.selectedRoles.includes(roleName)
        ? prev.selectedRoles.filter(r => r !== roleName)
        : [...prev.selectedRoles, roleName]
    }));
    
    if (errors.roles) {
      setErrors(prev => ({ ...prev, roles: '' }));
    }
  };

  /**
   * Submete o formulário
   */
  const handleSubmit = async (): Promise<void> => {
    // Validação obrigatória antes da submissão
    if (!validateForm()) {
      // Se houver erro de roles, força a mudança para a aba de permissões
      if (errors.roles && activeTab !== 'permissions') {
        setActiveTab('permissions');
        // Scroll para o topo da aba de permissões
        setTimeout(() => {
          const permissionsTab = document.querySelector('[data-tab="permissions"]');
          if (permissionsTab) {
            permissionsTab.scrollIntoView({ behavior: 'smooth', block: 'start' });
          }
        }, 100);
      }
      return;
    }

    try {
      setIsSubmitting(true);
      
      if (mode === 'create') {
        // Preparar dados para criação de usuário
        const createUserData: CreateUserRequest = {
          name: formData.name.trim(),
          username: formData.username.trim(),
          email: formData.email.trim(),
          password: formData.password,
          roles: formData.selectedRoles
        };

        // Chamada para API de criação
        const response: CreateUserResponse = await userService.createUser(createUserData);
        
        console.log('✅ Usuário criado com sucesso:', response);
        
        // Fecha modal e notifica sucesso
        onClose();
        
        // Notifica sucesso para o componente pai
        if (onSuccess) {
          onSuccess(`Usuário "${response.name}" criado com sucesso!`);
        }
        
      } else if (mode === 'edit' && user) {
        // Preparar dados para atualização de usuário
        const updateUserData: UpdateUserRequest = {
          name: formData.name.trim(),
          email: formData.email.trim(),
          roles: formData.selectedRoles,
          isActive: formData.isActive,
          isNotLocked: formData.isNotLocked
        };

        // Chamada para API de atualização
        const response: UpdateUserResponse = await userService.updateUser(user.id, updateUserData);
        
        console.log('✅ Usuário atualizado com sucesso:', response);
        
        // Fecha modal e notifica sucesso
        onClose();
        
        // Notifica sucesso para o componente pai
        if (onSuccess) {
          onSuccess(`Usuário "${response.name}" atualizado com sucesso!`);
        }
      }

    } catch (error: any) {
      console.error('❌ Erro ao salvar usuário:', error);
      
      // Tratamento específico de erros da API
      let errorMessage = 'Erro ao salvar usuário. Tente novamente.';
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.status === 400) {
        errorMessage = 'Dados inválidos. Verifique as informações fornecidas.';
      } else if (error.response?.status === 409) {
        errorMessage = 'Username ou email já existem no sistema.';
      } else if (error.response?.status === 403) {
        errorMessage = 'Sem permissão para realizar esta operação.';
      }
      
      setErrors({ submit: errorMessage });
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Renderiza a aba de dados básicos
   */
  const renderBasicTab = (): React.ReactNode => (
    <div className="space-y-6">
      {/* Nome */}
      <div>
        <label htmlFor="name" className="block text-sm font-medium text-smart-gray-700 mb-2">
          Nome Completo *
        </label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => handleInputChange('name', e.target.value)}
          className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
            errors.name ? 'border-red-500' : 'border-smart-gray-300'
          }`}
          placeholder="Digite o nome completo"
        />
        {errors.name && (
          <p className="mt-1 text-sm text-red-600">{errors.name}</p>
        )}
      </div>

      {/* Username */}
      <div>
        <label htmlFor="username" className="block text-sm font-medium text-smart-gray-700 mb-2">
          Username *
        </label>
        <input
          type="text"
          id="username"
          value={formData.username}
          onChange={(e) => handleInputChange('username', e.target.value)}
          className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
            errors.username ? 'border-red-500' : 'border-smart-gray-300'
          }`}
          placeholder="Digite o username"
          disabled={mode === 'edit'} // Username não pode ser alterado na edição
        />
        {errors.username && (
          <p className="mt-1 text-sm text-red-600">{errors.username}</p>
        )}
        {mode === 'edit' && (
          <p className="mt-1 text-sm text-smart-gray-500">
            Username não pode ser alterado após criação
          </p>
        )}
      </div>

      {/* Email */}
      <div>
        <label htmlFor="email" className="block text-sm font-medium text-smart-gray-700 mb-2">
          Email *
        </label>
        <input
          type="email"
          id="email"
          value={formData.email}
          onChange={(e) => handleInputChange('email', e.target.value)}
          className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
            errors.email ? 'border-red-500' : 'border-smart-gray-300'
          }`}
          placeholder="Digite o email"
        />
        {errors.email && (
          <p className="mt-1 text-sm text-red-600">{errors.email}</p>
        )}
      </div>

      {/* Senha */}
      <div>
        <label htmlFor="password" className="block text-sm font-medium text-smart-gray-700 mb-2">
          Senha {mode === 'create' ? '*' : ''}
        </label>
        <input
          type="password"
          id="password"
          value={formData.password}
          onChange={(e) => handleInputChange('password', e.target.value)}
          className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
            errors.password ? 'border-red-500' : 'border-smart-gray-300'
          }`}
          placeholder={mode === 'create' ? 'Digite a senha' : 'Deixe em branco para manter'}
        />
        {errors.password && (
          <p className="mt-1 text-sm text-red-600">{errors.password}</p>
        )}
        {mode === 'edit' && (
          <p className="mt-1 text-sm text-smart-gray-500">
            Deixe em branco para manter a senha atual
          </p>
        )}
      </div>

      {/* Confirmação de Senha */}
      {(mode === 'create' || formData.password) && (
        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-smart-gray-700 mb-2">
            Confirmar Senha *
          </label>
          <input
            type="password"
            id="confirmPassword"
            value={formData.confirmPassword}
            onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
              errors.confirmPassword ? 'border-red-500' : 'border-smart-gray-300'
            }`}
            placeholder="Confirme a senha"
          />
          {errors.confirmPassword && (
            <p className="mt-1 text-sm text-red-600">{errors.confirmPassword}</p>
          )}
        </div>
      )}

      {/* Status do Usuário */}
      <div className="grid grid-cols-2 gap-4">
        <div className="flex items-center space-x-3">
          <button
            type="button"
            onClick={() => handleInputChange('isActive', !formData.isActive)}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 ${
              formData.isActive ? 'bg-smart-red-600' : 'bg-smart-gray-300'
            }`}
          >
            <span
              className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200 ${
                formData.isActive ? 'translate-x-6' : 'translate-x-1'
              }`}
            />
          </button>
          <label htmlFor="isActive" className="text-sm font-medium text-smart-gray-700">
            Usuário Ativo
          </label>
        </div>
        <div className="flex items-center space-x-3">
          <button
            type="button"
            onClick={() => handleInputChange('isNotLocked', !formData.isNotLocked)}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 ${
              formData.isNotLocked ? 'bg-smart-red-600' : 'bg-smart-gray-300'
            }`}
          >
            <span
              className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200 ${
                formData.isNotLocked ? 'translate-x-6' : 'translate-x-1'
              }`}
            />
          </button>
          <label htmlFor="isNotLocked" className="text-sm font-medium text-smart-gray-700">
            Conta Desbloqueada
          </label>
        </div>
      </div>

      {/* Alerta de roles não selecionadas - visível em ambas as abas */}
      {errors.roles && (
        <div className="mt-4 p-3 bg-amber-50 border border-amber-200 rounded-lg">
          <div className="flex items-center">
            <svg className="w-5 h-5 text-amber-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
            </svg>
            <div>
              <p className="text-sm text-amber-700 font-medium">Atenção: Roles não selecionadas</p>
              <p className="text-xs text-amber-600 mt-1">
                Para continuar, vá para a aba "Roles e Permissões" e selecione pelo menos uma role.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  /**
   * Renderiza a aba de permissões
   */
  const renderPermissionsTab = (): React.ReactNode => (
    <div className="space-y-6" data-tab="permissions">
      <div>
        <h3 className="text-lg font-medium text-smart-gray-800 mb-4">
          Selecionar Roles e Permissões
        </h3>
        <p className="text-sm text-smart-gray-600 mb-6">
          Selecione as roles que serão atribuídas ao usuário. As permissões serão herdadas automaticamente.
        </p>
      </div>

      {/* Estado de carregamento */}
      {rolesLoading && (
        <div className="flex items-center justify-center py-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600 mx-auto mb-3"></div>
            <p className="text-sm text-smart-gray-600">Carregando roles...</p>
          </div>
        </div>
      )}

      {/* Lista de Roles Disponíveis */}
      {!rolesLoading && (
        <div className="space-y-3">
          {localAvailableRoles.length > 0 ? (
            localAvailableRoles.map((role) => (
              <div key={role.id} className="flex items-center p-4 border border-smart-gray-200 rounded-lg hover:bg-smart-gray-50">
                <button
                  type="button"
                  onClick={() => handleRoleToggle(role.name)}
                  className={`relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 ${
                    formData.selectedRoles.includes(role.name) ? 'bg-smart-red-600' : 'bg-smart-gray-300'
                  }`}
                >
                  <span
                    className={`inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-200 ${
                      formData.selectedRoles.includes(role.name) ? 'translate-x-5' : 'translate-x-1'
                    }`}
                  />
                </button>
                <div className="ml-3 flex-1">
                  <span className="text-sm font-medium text-smart-gray-900">
                    {role.name}
                  </span>
                  <p className="text-sm text-smart-gray-500">{role.description}</p>
                  {role.permissionNames && role.permissionNames.length > 0 && (
                    <div className="mt-2">
                      <p className="text-xs text-smart-gray-600 mb-1">Permissões:</p>
                      <div className="flex flex-wrap gap-1">
                        {role.permissionNames.map((permission, index) => (
                          <span
                            key={index}
                            className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-smart-gray-100 text-smart-gray-700"
                          >
                            {permission}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
                <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                  role.active 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {role.active ? 'Ativo' : 'Inativo'}
                </span>
              </div>
            ))
          ) : (
            <div className="text-center py-8">
              <div className="text-4xl mb-3">📋</div>
              <p className="text-sm text-smart-gray-600">
                Nenhuma role encontrada. Verifique a conexão com a API.
              </p>
            </div>
          )}
        </div>
      )}

      {/* Exibição de erro de roles */}
      {errors.roles && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
          <div className="flex items-center">
            <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <p className="text-sm text-red-600 font-medium">{errors.roles}</p>
          </div>
        </div>
      )}

      {/* Resumo das Permissões */}
      {formData.selectedRoles.length > 0 && (
        <div className="bg-smart-gray-50 p-4 rounded-lg">
          <h4 className="text-sm font-medium text-smart-gray-800 mb-2">
            Roles Selecionadas ({formData.selectedRoles.length})
          </h4>
          <div className="flex flex-wrap gap-2 mb-3">
            {formData.selectedRoles.map((roleName) => (
              <span
                key={roleName}
                className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-smart-red-100 text-smart-red-800"
              >
                {roleName}
              </span>
            ))}
          </div>
          
          {/* Permissões das roles selecionadas */}
          <div>
            <h5 className="text-sm font-medium text-smart-gray-700 mb-2">
              Permissões das Roles:
            </h5>
            <div className="flex flex-wrap gap-1">
              {formData.selectedRoles.flatMap(roleName => {
                const role = localAvailableRoles.find(r => r.name === roleName);
                return role?.permissionNames || [];
              }).map((permission, index) => (
                <span
                  key={index}
                  className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-smart-blue-100 text-smart-blue-800"
                >
                  {permission}
                </span>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );

  /**
   * Renderiza as abas do modal
   */
  const renderTabs = (): React.ReactNode => (
    <div className="border-b border-smart-gray-200 mb-6">
      <nav className="-mb-px flex space-x-8">
        <button
          onClick={() => handleTabChange('basic')}
          className={`py-2 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'basic'
              ? 'border-smart-red-500 text-smart-red-600'
              : 'border-transparent text-smart-gray-500 hover:text-smart-gray-700 hover:border-smart-gray-300'
          }`}
        >
          Dados Básicos
        </button>
        <button
          onClick={() => handleTabChange('permissions')}
          className={`py-2 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'permissions'
              ? 'border-smart-red-500 text-smart-red-600'
              : 'border-transparent text-smart-gray-500 hover:text-smart-gray-700 hover:border-smart-gray-300'
          }`}
        >
          Roles e Permissões
        </button>
      </nav>
    </div>
  );

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Overlay */}
      <div className="fixed inset-0 bg-black bg-opacity-50 transition-opacity z-50" onClick={onClose} />
      
      {/* Modal */}
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
          {/* Header */}
          <div className="px-6 py-4 border-b border-smart-red-200 bg-smart-red-600">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold text-white">
                {mode === 'create' ? 'Novo Usuário' : 'Editar Usuário'}
              </h2>
              <button
                onClick={onClose}
                className="text-white hover:text-smart-red-100 transition-colors duration-200"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          {/* Conteúdo */}
          <div className="px-6 py-4 max-h-[calc(90vh-200px)] overflow-y-auto">
            {renderTabs()}
            
            {activeTab === 'basic' ? renderBasicTab() : renderPermissionsTab()}
          </div>

          {/* Footer */}
          <div className="px-6 py-4 border-t border-smart-gray-200 bg-smart-gray-50">
            <div className="flex items-center justify-end space-x-3">
              <button
                onClick={onClose}
                className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-lg hover:bg-smart-gray-50 transition-colors duration-200"
              >
                Cancelar
              </button>
              <button
                onClick={handleSubmit}
                disabled={isSubmitting}
                className="px-4 py-2 text-sm font-medium text-white bg-smart-red-600 border border-transparent rounded-lg hover:bg-smart-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                {isSubmitting ? (
                  <div className="flex items-center">
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Salvando...
                  </div>
                ) : (
                  mode === 'create' ? 'Criar Usuário' : 'Salvar Alterações'
                )}
              </button>
            </div>
            
            {errors.submit && (
              <p className="mt-3 text-sm text-red-600 text-center">{errors.submit}</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserModal;
