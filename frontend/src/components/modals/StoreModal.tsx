import React, { useState, useEffect } from 'react';
import { storeService } from '@/services/api';
import type { 
  CreateStoreRequest, 
  UpdateStoreRequest, 
  ApiStore,
  StoreFormData,
  StoreValidationResult
} from '@/types';

/**
 * Interface para loja legacy
 * Mapeia os campos retornados pela API /legacy/stores
 */
interface LegacyStore {
  readonly id: string;
  readonly code: string;
  readonly name: string;
  readonly city: string;
  readonly status: boolean;
}

/**
 * Interface para propriedades do modal
 */
interface StoreModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: 'create' | 'edit';
  readonly store: ApiStore | null;
  readonly onSuccess?: (message: string) => void; // Callback para notificar sucesso
}

/**
 * Modal de Cadastro/Edição de Loja
 * Com select de lojas legacy para criação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StoreModal: React.FC<StoreModalProps> = ({
  isOpen,
  onClose,
  mode,
  store,
  onSuccess
}) => {
  const [formData, setFormData] = useState<StoreFormData>({
    code: '',
    name: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    phone: '',
    email: '',
    managerName: '',
    managerEmail: '',
    managerPhone: '',
    isActive: true
  });
  const [errors, setErrors] = useState<StoreValidationResult['errors']>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [selectedLegacyStore, setSelectedLegacyStore] = useState<string>('');
  const [availableLegacyStores, setAvailableLegacyStores] = useState<LegacyStore[]>([]);
  const [isLoadingLegacyStores, setIsLoadingLegacyStores] = useState<boolean>(false);

  /**
   * Carrega lojas legacy toda vez que o modal for aberto no modo 'create'
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  useEffect(() => {
    const loadLegacyStores = async (): Promise<void> => {
      if (isOpen && mode === 'create') {
        try {
          setIsLoadingLegacyStores(true);
          const legacyData = await storeService.getLegacyStores();
          // Garantir que sempre seja um array
          setAvailableLegacyStores(Array.isArray(legacyData) ? legacyData : []);
        } catch (error) {
          console.error('❌ Erro ao carregar lojas legacy:', error);
          // Em caso de erro, definir como array vazio
          setAvailableLegacyStores([]);
        } finally {
          setIsLoadingLegacyStores(false);
        }
      }
    };

    loadLegacyStores();
  }, [isOpen, mode]); // Removido availableLegacyStores.length para sempre carregar

  /**
   * Limpa as lojas legacy quando o modal fechar
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  useEffect(() => {
    if (!isOpen) {
      setAvailableLegacyStores([]);
      setSelectedLegacyStore('');
    }
  }, [isOpen]);

  /**
   * Inicializa o formulário com dados da loja (modo edição)
   */
  useEffect(() => {
    if (mode === 'edit' && store) {
      setFormData({
        code: store.code,
        name: store.name,
        address: '',
        city: store.city || '',
        state: '',
        zipCode: '',
        phone: '',
        email: '',
        managerName: '',
        managerEmail: '',
        managerPhone: '',
        isActive: store.status
      });
    } else {
      // Reset para modo criação
      setFormData({
        code: '',
        name: '',
        address: '',
        city: '',
        state: '',
        zipCode: '',
        phone: '',
        email: '',
        managerName: '',
        managerEmail: '',
        managerPhone: '',
        isActive: true
      });
      setSelectedLegacyStore('');
    }
    setErrors({});
  }, [mode, store]);

  /**
   * Manipula a seleção de loja legacy
   */
  const handleLegacyStoreChange = (legacyStoreId: string): void => {
    setSelectedLegacyStore(legacyStoreId);
    
    if (legacyStoreId) {
      const legacyStore = availableLegacyStores.find(ls => ls.id === legacyStoreId);
      if (legacyStore) {
        setFormData(prev => ({
          ...prev,
          code: legacyStore.code || legacyStore.id,
          name: legacyStore.name,
          city: legacyStore.city
        }));
      }
    }
  };


  /**
   * Manipula mudanças nos campos do formulário
   */
  const handleInputChange = (field: keyof StoreFormData, value: string | boolean): void => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Limpa erro do campo quando o usuário começa a digitar
    if (errors[field as keyof typeof errors]) {
      setErrors(prev => ({ ...prev, [field as keyof typeof errors]: undefined }));
    }
  };

  /**
   * Valida os dados do formulário
   */
  const validateForm = (): StoreValidationResult => {
    const newErrors: {
      code?: string;
      name?: string;
      city?: string;
      state?: string;
      email?: string;
      phone?: string;
      zipCode?: string;
      managerEmail?: string;
      managerPhone?: string;
    } = {};

    // Validações obrigatórias
    if (!formData.code.trim()) {
      newErrors.code = 'Código é obrigatório';
    } else if (formData.code.length < 2) {
      newErrors.code = 'Código deve ter pelo menos 2 caracteres';
    }

    if (!formData.name.trim()) {
      newErrors.name = 'Nome é obrigatório';
    } else if (formData.name.length < 2) {
      newErrors.name = 'Nome deve ter pelo menos 2 caracteres';
    }

    if (!formData.city.trim()) {
      newErrors.city = 'Cidade é obrigatória';
    }

    // Validações de email (se preenchido)
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Email deve ser válido';
    }

    if (formData.managerEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.managerEmail)) {
      newErrors.managerEmail = 'Email do gerente deve ser válido';
    }

    // Validações de telefone (se preenchido)
    if (formData.phone && !/^\(\d{2}\)\s\d{4,5}-\d{4}$/.test(formData.phone)) {
      newErrors.phone = 'Telefone deve estar no formato (XX) XXXX-XXXX';
    }

    if (formData.managerPhone && !/^\(\d{2}\)\s\d{4,5}-\d{4}$/.test(formData.managerPhone)) {
      newErrors.managerPhone = 'Telefone do gerente deve estar no formato (XX) XXXX-XXXX';
    }

    // Validação de CEP (se preenchido)
    if (formData.zipCode && !/^\d{5}-?\d{3}$/.test(formData.zipCode)) {
      newErrors.zipCode = 'CEP deve estar no formato XXXXX-XXX';
    }

    return {
      isValid: Object.keys(newErrors).length === 0,
      errors: newErrors
    };
  };

  /**
   * Manipula o envio do formulário
   */
  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    
    const validation = validateForm();
    if (!validation.isValid) {
      setErrors(validation.errors);
      return;
    }

    setIsSubmitting(true);
    try {
      if (mode === 'create') {
        const createData: CreateStoreRequest = {
          code: formData.code.trim(),
          name: formData.name.trim(),
          status: formData.isActive,
          ...(formData.address.trim() && { address: formData.address.trim() }),
          ...(formData.city.trim() && { city: formData.city.trim() }),
          ...(formData.state && { state: formData.state }),
          ...(formData.zipCode.trim() && { zipCode: formData.zipCode.trim() }),
          ...(formData.phone.trim() && { phone: formData.phone.trim() }),
          ...(formData.email.trim() && { email: formData.email.trim() }),
          ...(formData.managerName.trim() && { managerName: formData.managerName.trim() }),
          ...(formData.managerEmail.trim() && { managerEmail: formData.managerEmail.trim() }),
          ...(formData.managerPhone.trim() && { managerPhone: formData.managerPhone.trim() })
        };

        await storeService.createStore(createData);
        console.log('✅ Loja criada com sucesso');
      } else if (mode === 'edit' && store) {
        const updateData: UpdateStoreRequest = {
          code: formData.code.trim(),
          name: formData.name.trim(),
          status: formData.isActive,
          ...(formData.address.trim() && { address: formData.address.trim() }),
          ...(formData.city.trim() && { city: formData.city.trim() }),
          ...(formData.state && { state: formData.state }),
          ...(formData.zipCode.trim() && { zipCode: formData.zipCode.trim() }),
          ...(formData.phone.trim() && { phone: formData.phone.trim() }),
          ...(formData.email.trim() && { email: formData.email.trim() }),
          ...(formData.managerName.trim() && { managerName: formData.managerName.trim() }),
          ...(formData.managerEmail.trim() && { managerEmail: formData.managerEmail.trim() }),
          ...(formData.managerPhone.trim() && { managerPhone: formData.managerPhone.trim() })
        };

        await storeService.updateStore(store.id, updateData);
        console.log('✅ Loja atualizada com sucesso');
      }

      if (onSuccess) {
        onSuccess(mode === 'create' ? 'Loja criada com sucesso!' : 'Loja atualizada com sucesso!');
      }
      onClose();
    } catch (error) {
      console.error('❌ Erro ao salvar loja:', error);
      // Aqui você pode adicionar tratamento de erro mais específico
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Renderiza o formulário de loja
   */
  const renderStoreForm = (): React.ReactNode => (
    <div className="space-y-4">
      {/* Select de lojas legacy (apenas no modo criação) */}
      {mode === 'create' && (
        <div>
          <label htmlFor="legacyStore" className="block text-sm font-medium text-smart-gray-700 mb-1">
            Selecionar Loja *
          </label>
          <select
            id="legacyStore"
            value={selectedLegacyStore}
            onChange={(e) => handleLegacyStoreChange(e.target.value)}
            disabled={isLoadingLegacyStores}
            className="w-full px-3 py-2 border border-smart-gray-300 rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 disabled:bg-smart-gray-100 disabled:cursor-not-allowed"
          >
            <option value="">
              {isLoadingLegacyStores ? 'Carregando lojas...' : 'Selecione uma loja'}
            </option>
            {Array.isArray(availableLegacyStores) && availableLegacyStores.map((legacyStore) => (
              <option key={legacyStore.id} value={legacyStore.id}>
                {legacyStore.id} - {legacyStore.name}
              </option>
            ))}
          </select>
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="code" className="block text-sm font-medium text-smart-gray-700 mb-1">
            Código 
          </label>
          <input
            type="text"
            id="code"
            value={formData.code}
            onChange={(e) => handleInputChange('code', e.target.value)}
            className={`w-full px-3 py-2 border rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 ${
              errors.code ? 'border-red-500' : 'border-smart-gray-300'
            }`}
            placeholder="Ex: LOJA001"
            disabled={true}
          />
          {errors.code && <p className="mt-1 text-sm text-red-600">{errors.code}</p>}
        </div>

        <div>
          <label htmlFor="name" className="block text-sm font-medium text-smart-gray-700 mb-1">
            Nome 
          </label>
          <input
            type="text"
            id="name"
            value={formData.name}
            onChange={(e) => handleInputChange('name', e.target.value)}
            className={`w-full px-3 py-2 border rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 ${
              errors.name ? 'border-red-500' : 'border-smart-gray-300'
            }`}
            placeholder="Nome da loja"
            disabled={true}
          />
          {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name}</p>}
        </div>
      </div>

      <div>
        <label htmlFor="city" className="block text-sm font-medium text-smart-gray-700 mb-1">
          Cidade 
        </label>
        <input
          type="text"
          id="city"
          value={formData.city}
          onChange={(e) => handleInputChange('city', e.target.value)}
          className={`w-full px-3 py-2 border rounded-md focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 ${
            errors.city ? 'border-red-500' : 'border-smart-gray-300'
          }`}
          placeholder="Cidade"
          disabled={true}
        />
        {errors.city && <p className="mt-1 text-sm text-red-600">{errors.city}</p>}
      </div>

      {/* Switch para ativar/desativar loja */}
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
          Loja Ativa
        </label>
      </div>
    </div>
  );


  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-hidden">
        {/* Header do Modal */}
        <div className="bg-smart-red-600 text-white px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">
              {mode === 'create' ? 'Adicionar Loja' : 'Editar Loja'}
            </h2>
            <button
              onClick={onClose}
              className="text-white hover:text-smart-gray-200 transition-colors duration-200"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        {/* Conteúdo do Modal */}
        <form onSubmit={handleSubmit} className="p-6 max-h-[60vh] overflow-y-auto">
          {renderStoreForm()}

          {/* Botões de Ação */}
          <div className="flex justify-end space-x-3 mt-6 pt-4 border-t border-smart-gray-200">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-red-500 transition-colors duration-200"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 text-sm font-medium text-white bg-smart-red-600 border border-transparent rounded-md hover:bg-smart-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              {isSubmitting ? 'Salvando...' : (mode === 'create' ? 'Adicionar Loja' : 'Salvar Alterações')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export { StoreModal };
