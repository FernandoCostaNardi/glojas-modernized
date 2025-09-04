import React, { useState, useEffect } from 'react';
import { Role, Permission, RoleFormData } from '@/types';
import { roleService } from '@/services/api';

/**
 * Props do modal de role
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface RoleModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: 'create' | 'edit';
  readonly role?: Role | undefined;
  readonly availablePermissions: readonly Permission[];
  readonly onSuccess: (message: string) => void;
}

/**
 * Modal para criação/edição de roles
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const RoleModal: React.FC<RoleModalProps> = ({
  isOpen,
  onClose,
  mode,
  role,
  availablePermissions,
  onSuccess
}) => {
  const [formData, setFormData] = useState<RoleFormData>({
    name: '',
    description: '',
    isActive: true,
    selectedPermissions: []
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  useEffect(() => {
    if (mode === 'edit' && role) {
      console.log('🔄 Editando role:', role);
      console.log('🔄 Campo active da API (raw):', role.active);
      console.log('🔄 Campo active da API (tipo):', typeof role.active);
      console.log('🔄 Campo active da API (convertido):', Boolean(role.active));
      
      const formDataToSet = {
        name: role.name,
        description: role.description || '',
        isActive: Boolean(role.active), // Usa o campo correto 'active' da API
        selectedPermissions: role.permissionNames
      };
      
      console.log('🔄 FormData sendo definido:', formDataToSet);
      setFormData(formDataToSet);
    } else {
      console.log('🔄 Criando nova role');
      setFormData({
        name: '',
        description: '',
        isActive: true,
        selectedPermissions: []
      });
    }
    setErrors({});
  }, [mode, role]);

  /**
   * Atualiza o valor de um campo do formulário
   * Garante que valores booleanos sejam sempre booleanos válidos
   */
  const handleInputChange = (field: keyof RoleFormData, value: string | boolean | string[]) => {
    // Garante que valores booleanos sejam sempre booleanos válidos
    const processedValue = field === 'isActive' ? Boolean(value) : value;
    
    setFormData(prev => ({ ...prev, [field]: processedValue }));
    
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Nome da role é obrigatório';
    } else if (formData.name.trim().length < 2) {
      newErrors.name = 'Nome da role deve ter pelo menos 2 caracteres';
    } else if (/[a-z]/.test(formData.name)) {
      newErrors.name = 'Nome da role deve conter apenas letras maiúsculas';
    }

    if (formData.description.trim().length < 5) {
      newErrors.description = 'Descrição deve ter pelo menos 5 caracteres';
    } else if (formData.description.trim().length > 255) {
      newErrors.description = 'Descrição deve ter no máximo 255 caracteres';
    }

    if (formData.selectedPermissions.length === 0) {
      newErrors.permissions = 'Pelo menos uma permissão deve ser selecionada';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Mapeia os nomes das permissões selecionadas para seus IDs
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const mapPermissionsToIds = (permissionNames: readonly string[]): string[] => {
    return permissionNames
      .map(name => availablePermissions.find(p => p.name === name)?.id)
      .filter(id => id !== undefined) as string[];
  };

  /**
   * Prepara os dados para envio à API
   * Garante que todos os campos tenham valores válidos
   * Mapeia o campo isActive para active (nome esperado pela API)
   */
  const prepareRoleDataForApi = (formData: RoleFormData) => {
    return {
      name: formData.name.trim(),
      description: formData.description.trim(),
      active: Boolean(formData.isActive), // Mapeia para 'active' (nome esperado pela API)
      permissionIds: mapPermissionsToIds(formData.selectedPermissions)
    };
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const roleData = prepareRoleDataForApi(formData);
      
      // Log para debug - verificar se o valor active está correto
      console.log('🔄 Dados do formulário:', formData);
      console.log('🔄 Dados preparados para API:', roleData);
      console.log('🔄 Campo active sendo enviado:', roleData.active);
      
      if (mode === 'create') {
        await roleService.createRole(roleData);
        onSuccess('Role criada com sucesso!');
      } else if (role) {
        await roleService.updateRole(role.id, roleData);
        onSuccess('Role atualizada com sucesso!');
      }
      // Remove o handleClose() daqui - o onSuccess controlará o fechamento
    } catch (error: any) {
      console.error('Erro ao salvar role:', error);
      
      if (error.response?.data?.message) {
        setErrors({ submit: error.response.data.message });
      } else if (error.message) {
        setErrors({ submit: error.message });
      } else {
        setErrors({ submit: 'Erro ao salvar role. Tente novamente.' });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    setIsSubmitting(false);
    setErrors({});
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6 bg-smart-red-600 -m-6 p-6 rounded-t-lg">
          <h3 className="text-xl font-semibold text-white">
            {mode === 'create' ? 'Criar Nova Role' : 'Editar Role'}
          </h3>
          <button
            onClick={handleClose}
            className="text-white hover:text-smart-gray-200 transition-colors duration-200"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Nome da Role */}
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-smart-gray-700 mb-2">
              Nome da Role *
            </label>
            <input
              type="text"
              id="name"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value.toUpperCase())}
              className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-smart-red-500 ${
                errors.name ? 'border-red-500' : 'border-smart-gray-300'
              }`}
              placeholder="Ex: ADMIN, USER, MANAGER"
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-600">{errors.name}</p>
            )}
          </div>

          {/* Descrição */}
          <div>
            <label htmlFor="description" className="block text-sm font-medium text-smart-gray-700 mb-2">
              Descrição
            </label>
            <textarea
              id="description"
              value={formData.description}
              onChange={(e) => handleInputChange('description', e.target.value)}
              rows={3}
              className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-smart-red-500 ${
                errors.description ? 'border-red-500' : 'border-smart-gray-300'
              }`}
              placeholder="Descrição da role e suas responsabilidades"
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description}</p>
            )}
          </div>

          {/* Status Ativo */}
          <div className="flex items-center space-x-3">
            <button
              type="button"
              onClick={() => {
                const newValue = !formData.isActive;
                console.log('🔄 Alternando isActive de', formData.isActive, 'para', newValue);
                handleInputChange('isActive', newValue);
              }}
              className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 ${
                formData.isActive ? 'bg-smart-red-600' : 'bg-smart-gray-300'
              }`}
              role="switch"
              aria-checked={formData.isActive}
              aria-label={`${formData.isActive ? 'Desativar' : 'Ativar'} role ativa`}
            >
              <span
                className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200 ${
                  formData.isActive ? 'translate-x-5' : 'translate-x-0.5'
                }`}
              />
            </button>
            <label className="text-sm font-medium text-smart-gray-700">
              Role ativa: {formData.isActive ? 'Sim' : 'Não'}
            </label>
          </div>

          {/* Permissões */}
          <div>
            <label className="block text-sm font-medium text-smart-gray-700 mb-2">
              Permissões
            </label>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 max-h-60 overflow-y-auto border border-smart-gray-200 rounded-md p-3">
              {availablePermissions.map((permission) => (
                <div key={permission.id} className="flex items-center space-x-3">
                  <button
                    type="button"
                    onClick={() => {
                      if (formData.selectedPermissions.includes(permission.name)) {
                        handleInputChange('selectedPermissions', formData.selectedPermissions.filter(p => p !== permission.name));
                      } else {
                        handleInputChange('selectedPermissions', [...formData.selectedPermissions, permission.name]);
                      }
                    }}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 ${
                      formData.selectedPermissions.includes(permission.name)
                        ? 'bg-smart-red-600'
                        : 'bg-smart-gray-200'
                    }`}
                    role="switch"
                    aria-checked={formData.selectedPermissions.includes(permission.name)}
                    aria-label={`${formData.selectedPermissions.includes(permission.name) ? 'Desativar' : 'Ativar'} permissão ${permission.name}`}
                  >
                    <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200 ${
                        formData.selectedPermissions.includes(permission.name)
                          ? 'translate-x-6'
                          : 'translate-x-1'
                      }`}
                    />
                  </button>
                  <span className="text-sm text-smart-gray-700">
                    {permission.name}
                  </span>
                </div>
              ))}
            </div>
            <p className="mt-1 text-xs text-smart-gray-500">
              Selecione as permissões que esta role deve ter
            </p>
            {errors.permissions && (
              <p className="mt-1 text-sm text-red-600">{errors.permissions}</p>
            )}
          </div>

          {/* Erro de submissão */}
          {errors.submit && (
            <p className="mt-1 text-sm text-red-600">{errors.submit}</p>
          )}

          {/* Botões */}
          <div className="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={handleClose}
              disabled={isSubmitting}
              className="px-4 py-2 text-sm font-medium text-smart-gray-600 bg-smart-gray-100 hover:bg-smart-gray-200 rounded-md transition-colors duration-200 disabled:opacity-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 text-sm font-medium text-white bg-smart-red-600 hover:bg-smart-red-700 rounded-md transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? 'Salvando...' : (mode === 'create' ? 'Criar Role' : 'Atualizar Role')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
