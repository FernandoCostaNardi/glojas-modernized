import React, { useState, useEffect } from 'react';
import { salesTargetConfigService, storeService } from '@/services/api';
import type { 
  SalesTargetConfig,
  SalesTargetConfigRequest,
  UpdateSalesTargetConfigRequest,
  SalesTargetConfigFormData,
  SalesTargetConfigValidationResult,
  ApiStore
} from '@/types';

/**
 * Interface para propriedades do modal
 */
interface SalesTargetConfigModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: 'create' | 'edit';
  readonly config: SalesTargetConfig | null;
  readonly onSuccess?: () => void;
}

/**
 * Modal de Cadastro/Edição de Configuração de Metas e Comissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const SalesTargetConfigModal: React.FC<SalesTargetConfigModalProps> = ({
  isOpen,
  onClose,
  mode,
  config,
  onSuccess
}) => {
  const [formData, setFormData] = useState<SalesTargetConfigFormData>({
    storeCode: '',
    competenceDate: '',
    storeSalesTarget: '',
    collectiveCommissionPercentage: '',
    individualSalesTarget: '',
    individualCommissionPercentage: ''
  });
  const [errors, setErrors] = useState<SalesTargetConfigValidationResult['errors']>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [availableStores, setAvailableStores] = useState<ApiStore[]>([]);
  const [isLoadingStores, setIsLoadingStores] = useState<boolean>(false);

  /**
   * Carrega lojas toda vez que o modal for aberto
   */
  useEffect(() => {
    const loadStores = async (): Promise<void> => {
      if (isOpen) {
        try {
          setIsLoadingStores(true);
          const stores = await storeService.getAllStores();
          setAvailableStores(Array.isArray(stores) ? stores : []);
        } catch (error) {
          console.error('❌ Erro ao carregar lojas:', error);
          setAvailableStores([]);
        } finally {
          setIsLoadingStores(false);
        }
      }
    };

    loadStores();
  }, [isOpen]);

  /**
   * Inicializa o formulário com dados da configuração (modo edição)
   */
  useEffect(() => {
    if (mode === 'edit' && config) {
      setFormData({
        storeCode: config.storeCode || '',
        competenceDate: config.competenceDate || '',
        storeSalesTarget: config.storeSalesTarget?.toString() || '',
        collectiveCommissionPercentage: config.collectiveCommissionPercentage?.toString() || '',
        individualSalesTarget: config.individualSalesTarget?.toString() || '',
        individualCommissionPercentage: config.individualCommissionPercentage?.toString() || ''
      });
    } else {
      // Reset para modo criação
      setFormData({
        storeCode: '',
        competenceDate: '',
        storeSalesTarget: '',
        collectiveCommissionPercentage: '',
        individualSalesTarget: '',
        individualCommissionPercentage: ''
      });
    }
    setErrors({});
  }, [mode, config]);

  /**
   * Manipula mudanças nos campos do formulário
   */
  const handleInputChange = (field: keyof SalesTargetConfigFormData, value: string): void => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Limpa erro do campo quando o usuário começa a digitar
    if (errors[field as keyof typeof errors]) {
      setErrors(prev => ({ ...prev, [field as keyof typeof errors]: undefined }));
    }
  };

  /**
   * Aplica máscara MM/YYYY no campo de competência
   */
  const handleCompetenceDateChange = (value: string): void => {
    // Remove caracteres não numéricos
    const numbers = value.replace(/\D/g, '');
    
    // Aplica máscara MM/YYYY
    let formatted = numbers;
    if (numbers.length > 2) {
      formatted = `${numbers.slice(0, 2)}/${numbers.slice(2, 6)}`;
    } else if (numbers.length > 0) {
      formatted = numbers;
    }
    
    handleInputChange('competenceDate', formatted);
  };

  /**
   * Valida os dados do formulário
   */
  const validateForm = (): SalesTargetConfigValidationResult => {
    const newErrors: SalesTargetConfigValidationResult['errors'] = {};

    // Validações obrigatórias
    if (!formData.storeCode.trim()) {
      newErrors.storeCode = 'Código da loja é obrigatório';
    } else if (formData.storeCode.length !== 6) {
      newErrors.storeCode = 'Código da loja deve ter 6 caracteres';
    }

    if (!formData.competenceDate.trim()) {
      newErrors.competenceDate = 'Data de competência é obrigatória';
    } else if (!/^(0[1-9]|1[0-2])\/\d{4}$/.test(formData.competenceDate)) {
      newErrors.competenceDate = 'Data de competência deve estar no formato MM/YYYY (ex: 01/2024)';
    }

    if (!formData.storeSalesTarget.trim()) {
      newErrors.storeSalesTarget = 'Meta de venda da loja é obrigatória';
    } else {
      const value = parseFloat(formData.storeSalesTarget);
      if (isNaN(value) || value < 0) {
        newErrors.storeSalesTarget = 'Meta de venda da loja deve ser um número maior ou igual a zero';
      }
    }

    if (!formData.collectiveCommissionPercentage.trim()) {
      newErrors.collectiveCommissionPercentage = 'Percentual de comissão coletiva é obrigatório';
    } else {
      const value = parseFloat(formData.collectiveCommissionPercentage);
      if (isNaN(value) || value < 0) {
        newErrors.collectiveCommissionPercentage = 'Percentual de comissão coletiva deve ser um número maior ou igual a zero';
      }
    }

    if (!formData.individualSalesTarget.trim()) {
      newErrors.individualSalesTarget = 'Meta de venda individual é obrigatória';
    } else {
      const value = parseFloat(formData.individualSalesTarget);
      if (isNaN(value) || value < 0) {
        newErrors.individualSalesTarget = 'Meta de venda individual deve ser um número maior ou igual a zero';
      }
    }

    if (!formData.individualCommissionPercentage.trim()) {
      newErrors.individualCommissionPercentage = 'Percentual de comissão individual é obrigatório';
    } else {
      const value = parseFloat(formData.individualCommissionPercentage);
      if (isNaN(value) || value < 0) {
        newErrors.individualCommissionPercentage = 'Percentual de comissão individual deve ser um número maior ou igual a zero';
      }
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
      const requestData: SalesTargetConfigRequest | UpdateSalesTargetConfigRequest = {
        storeCode: formData.storeCode.trim(),
        competenceDate: formData.competenceDate.trim(),
        storeSalesTarget: parseFloat(formData.storeSalesTarget),
        collectiveCommissionPercentage: parseFloat(formData.collectiveCommissionPercentage),
        individualSalesTarget: parseFloat(formData.individualSalesTarget),
        individualCommissionPercentage: parseFloat(formData.individualCommissionPercentage)
      };

      if (mode === 'create') {
        await salesTargetConfigService.createSalesTargetConfig(requestData);
        console.log('✅ Configuração criada com sucesso');
      } else if (mode === 'edit' && config) {
        await salesTargetConfigService.updateSalesTargetConfig(config.id, requestData);
        console.log('✅ Configuração atualizada com sucesso');
      }

      if (onSuccess) {
        onSuccess();
      }
      onClose();
    } catch (error) {
      console.error('❌ Erro ao salvar configuração:', error);
      // TODO: Adicionar tratamento de erro mais específico
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="px-6 py-4 border-b border-smart-gray-200">
          <h2 className="text-xl font-semibold text-smart-gray-800">
            {mode === 'create' ? 'Nova Configuração' : 'Editar Configuração'}
          </h2>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="px-6 py-4">
          <div className="space-y-4">
            {/* Loja */}
            <div>
              <label htmlFor="storeCode" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Loja <span className="text-red-500">*</span>
              </label>
              {isLoadingStores ? (
                <div className="text-sm text-smart-gray-500">Carregando lojas...</div>
              ) : (
                <select
                  id="storeCode"
                  value={formData.storeCode}
                  onChange={(e) => handleInputChange('storeCode', e.target.value)}
                  className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                    errors.storeCode ? 'border-red-500' : 'border-smart-gray-300'
                  }`}
                  disabled={isSubmitting}
                >
                  <option value="">Selecione uma loja</option>
                  {availableStores.map((store) => (
                    <option key={store.id} value={store.code}>
                      {store.code} - {store.name}
                    </option>
                  ))}
                </select>
              )}
              {errors.storeCode && (
                <p className="mt-1 text-sm text-red-600">{errors.storeCode}</p>
              )}
            </div>

            {/* Competência */}
            <div>
              <label htmlFor="competenceDate" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Competência (MM/YYYY) <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                id="competenceDate"
                value={formData.competenceDate}
                onChange={(e) => handleCompetenceDateChange(e.target.value)}
                placeholder="01/2024"
                maxLength={7}
                className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                  errors.competenceDate ? 'border-red-500' : 'border-smart-gray-300'
                }`}
                disabled={isSubmitting}
              />
              {errors.competenceDate && (
                <p className="mt-1 text-sm text-red-600">{errors.competenceDate}</p>
              )}
            </div>

            {/* Meta de Venda da Loja */}
            <div>
              <label htmlFor="storeSalesTarget" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Meta de Venda da Loja (R$) <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="storeSalesTarget"
                value={formData.storeSalesTarget}
                onChange={(e) => handleInputChange('storeSalesTarget', e.target.value)}
                step="0.01"
                min="0"
                className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                  errors.storeSalesTarget ? 'border-red-500' : 'border-smart-gray-300'
                }`}
                disabled={isSubmitting}
              />
              {errors.storeSalesTarget && (
                <p className="mt-1 text-sm text-red-600">{errors.storeSalesTarget}</p>
              )}
            </div>

            {/* % Comissão Coletiva */}
            <div>
              <label htmlFor="collectiveCommissionPercentage" className="block text-sm font-medium text-smart-gray-700 mb-1">
                % Comissão Coletiva <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="collectiveCommissionPercentage"
                value={formData.collectiveCommissionPercentage}
                onChange={(e) => handleInputChange('collectiveCommissionPercentage', e.target.value)}
                step="0.01"
                min="0"
                className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                  errors.collectiveCommissionPercentage ? 'border-red-500' : 'border-smart-gray-300'
                }`}
                disabled={isSubmitting}
              />
              {errors.collectiveCommissionPercentage && (
                <p className="mt-1 text-sm text-red-600">{errors.collectiveCommissionPercentage}</p>
              )}
            </div>

            {/* Meta de Venda Individual */}
            <div>
              <label htmlFor="individualSalesTarget" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Meta de Venda Individual (R$) <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="individualSalesTarget"
                value={formData.individualSalesTarget}
                onChange={(e) => handleInputChange('individualSalesTarget', e.target.value)}
                step="0.01"
                min="0"
                className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                  errors.individualSalesTarget ? 'border-red-500' : 'border-smart-gray-300'
                }`}
                disabled={isSubmitting}
              />
              {errors.individualSalesTarget && (
                <p className="mt-1 text-sm text-red-600">{errors.individualSalesTarget}</p>
              )}
            </div>

            {/* % Comissão Individual */}
            <div>
              <label htmlFor="individualCommissionPercentage" className="block text-sm font-medium text-smart-gray-700 mb-1">
                % Comissão Individual <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="individualCommissionPercentage"
                value={formData.individualCommissionPercentage}
                onChange={(e) => handleInputChange('individualCommissionPercentage', e.target.value)}
                step="0.01"
                min="0"
                className={`w-full px-3 py-2 border rounded-md focus:ring-smart-blue-500 focus:border-smart-blue-500 ${
                  errors.individualCommissionPercentage ? 'border-red-500' : 'border-smart-gray-300'
                }`}
                disabled={isSubmitting}
              />
              {errors.individualCommissionPercentage && (
                <p className="mt-1 text-sm text-red-600">{errors.individualCommissionPercentage}</p>
              )}
            </div>
          </div>

          {/* Footer */}
          <div className="mt-6 flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 border border-smart-gray-300 text-smart-gray-700 rounded-md hover:bg-smart-gray-50 transition-colors disabled:opacity-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 bg-smart-red-600 text-white rounded-md hover:bg-smart-red-700 transition-colors disabled:opacity-50"
            >
              {isSubmitting ? 'Salvando...' : mode === 'create' ? 'Criar' : 'Salvar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SalesTargetConfigModal;

