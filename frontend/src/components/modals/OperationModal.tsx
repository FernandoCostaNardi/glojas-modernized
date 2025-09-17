import React, { useState, useEffect } from 'react';
import { Operation, OperationKind, OperationFormData } from '@/types';
import { operationService, operationKindService } from '@/services/api';

/**
 * Props do modal de operação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface OperationModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly mode: 'create' | 'edit';
  readonly operation?: Operation | undefined;
  readonly onSuccess: (message: string) => void;
}

/**
 * Modal para criação/edição de operações
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const OperationModal: React.FC<OperationModalProps> = ({
  isOpen,
  onClose,
  mode,
  operation,
  onSuccess
}) => {
  const [formData, setFormData] = useState<OperationFormData>({
    operationKindId: '',
    operationSource: 'SELL' as const
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  
  // Estado para operation-kinds carregados no modal
  const [availableOperationKinds, setAvailableOperationKinds] = useState<OperationKind[]>([]);
  const [isLoadingOperationKinds, setIsLoadingOperationKinds] = useState<boolean>(false);

  /**
   * Carrega operation-kinds quando o modal é aberto
   */
  useEffect(() => {
    if (isOpen && availableOperationKinds.length === 0) {
      console.log('🔄 Modal aberto, carregando operation-kinds...');
      loadOperationKinds();
    }
  }, [isOpen]); // Executa apenas quando o modal abre

  /**
   * Função para carregar operation-kinds
   */
  const loadOperationKinds = async () => {
    try {
      setIsLoadingOperationKinds(true);
      const operationKindsData = await operationKindService.getAllOperationKinds();
      setAvailableOperationKinds(operationKindsData);
      console.log('✅ OperationKinds carregados no modal:', operationKindsData.length);
    } catch (error) {
      console.error('❌ Erro ao carregar operation-kinds no modal:', error);
    } finally {
      setIsLoadingOperationKinds(false);
    }
  };

  useEffect(() => {
    if (mode === 'edit' && operation) {
      console.log('🔄 Editando operação:', operation);
      
      // Para edição, precisamos encontrar o OperationKind correspondente
      // baseado no code da operação
      const matchingOperationKind = availableOperationKinds.find(
        kind => kind.id === operation.code
      );
      
      const formDataToSet = {
        operationKindId: matchingOperationKind?.id || '',
        operationSource: operation.operationSource || 'SELL' as const
      };
      
      console.log('🔄 FormData sendo definido:', formDataToSet);
      setFormData(formDataToSet);
    } else {
      console.log('🔄 Criando nova operação');
      setFormData({
        operationKindId: '',
        operationSource: 'SELL' as const
      });
    }
    setErrors({});
  }, [mode, operation]); // Removido availableOperationKinds das dependências para evitar re-renders desnecessários

  /**
   * Atualiza o valor de um campo do formulário
   */
  const handleInputChange = (field: keyof OperationFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  /**
   * Valida o formulário antes do envio
   */
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.operationKindId) {
      newErrors.operationKindId = 'Tipo de operação é obrigatório';
    }

    if (!formData.operationSource) {
      newErrors.operationSource = 'Fonte do evento é obrigatória';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Prepara os dados para envio à API
   * Mapeia o OperationKind selecionado para os dados da operação
   */
  const prepareOperationDataForApi = (formData: OperationFormData) => {
    const selectedOperationKind = availableOperationKinds.find(
      kind => kind.id === formData.operationKindId
    );
    
    if (!selectedOperationKind) {
      throw new Error('Tipo de operação selecionado não encontrado');
    }
    
    return {
      code: selectedOperationKind.id, // O ID do OperationKind vira o code da operação
      description: selectedOperationKind.description, // A description do OperationKind vira a description da operação
      operationSource: formData.operationSource // Inclui a fonte do evento
    };
  };

  /**
   * Manipula o envio do formulário
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const operationData = prepareOperationDataForApi(formData);
      
      // Log para debug
      console.log('🔄 Dados do formulário:', formData);
      console.log('🔄 Dados preparados para API:', operationData);
      
      if (mode === 'create') {
        await operationService.createOperation(operationData);
        onSuccess('Operação criada com sucesso!');
      } else if (operation) {
        await operationService.updateOperation(operation.id, operationData);
        onSuccess('Operação atualizada com sucesso!');
      }
      // Remove o handleClose() daqui - o onSuccess controlará o fechamento
    } catch (error: any) {
      console.error('Erro ao salvar operação:', error);
      
      if (error.response?.data?.message) {
        setErrors({ submit: error.response.data.message });
      } else if (error.message) {
        setErrors({ submit: error.message });
      } else {
        setErrors({ submit: 'Erro ao salvar operação. Tente novamente.' });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Manipula o fechamento do modal
   */
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
            {mode === 'create' ? 'Criar Nova Operação' : 'Editar Operação'}
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
          {/* Tipo de Operação */}
          <div>
            <label htmlFor="operationKindId" className="block text-sm font-medium text-smart-gray-700 mb-2">
              Tipo de Operação *
            </label>
            <select
              id="operationKindId"
              value={formData.operationKindId}
              onChange={(e) => handleInputChange('operationKindId', e.target.value)}
              disabled={isLoadingOperationKinds}
              className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-smart-red-500 ${
                errors.operationKindId ? 'border-red-500' : 'border-smart-gray-300'
              } ${isLoadingOperationKinds ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              <option value="">
                {isLoadingOperationKinds ? 'Carregando tipos de operação...' : 'Selecione um tipo de operação'}
              </option>
              {availableOperationKinds.map((operationKind) => (
                <option key={operationKind.id} value={operationKind.id}>
                  {operationKind.id} - {operationKind.description}
                </option>
              ))}
            </select>
            {errors.operationKindId && (
              <p className="mt-1 text-sm text-red-600">{errors.operationKindId}</p>
            )}
            <p className="mt-1 text-xs text-smart-gray-500">
              Selecione o tipo de operação que deseja criar
            </p>
          </div>

          {/* Fonte do Evento */}
          <div>
            <label htmlFor="operationSource" className="block text-sm font-medium text-smart-gray-700 mb-2">
              Fonte do Evento *
            </label>
            <select
              id="operationSource"
              value={formData.operationSource}
              onChange={(e) => handleInputChange('operationSource', e.target.value as 'SELL' | 'EXCHANGE')}
              className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-smart-red-500 ${
                errors.operationSource ? 'border-red-500' : 'border-smart-gray-300'
              }`}
            >
              <option value="SELL">Venda</option>
              <option value="EXCHANGE">Troca</option>
            </select>
            {errors.operationSource && (
              <p className="mt-1 text-sm text-red-600">{errors.operationSource}</p>
            )}
            <p className="mt-1 text-xs text-smart-gray-500">
              Selecione a fonte do evento para esta operação
            </p>
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
              {isSubmitting ? 'Salvando...' : (mode === 'create' ? 'Criar Operação' : 'Atualizar Operação')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
