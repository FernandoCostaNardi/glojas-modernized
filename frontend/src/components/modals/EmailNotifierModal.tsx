import React, { useState, useEffect } from 'react';
import {
  EmailNotifierModalProps,
  EmailNotifierFormState,
  EmailNotifierFormErrorsState,
  CreateEmailNotifierRequest,
  UpdateEmailNotifierRequest,
  EMAIL_NOTIFIER_VALIDATION_RULES
} from '@/types';

/**
 * Modal de Cadastro/Edição de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EmailNotifierModal: React.FC<EmailNotifierModalProps> = ({
  isOpen,
  onClose,
  mode,
  emailNotifier,
  onSuccess,
  onSave,
  isSubmitting: externalIsSubmitting = false
}) => {
  // Estados do formulário
  const [formData, setFormData] = useState<EmailNotifierFormState>({
    email: '',
    dailySellNotifier: false,
    dailyMonthNotifier: false,
    monthYearNotifier: false
  });
  const [errors, setErrors] = useState<EmailNotifierFormErrorsState>({});

  /**
   * Reseta o formulário para o estado inicial
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const resetForm = (): void => {
    setFormData({
      email: '',
      dailySellNotifier: false,
      dailyMonthNotifier: false,
      monthYearNotifier: false
    });
    setErrors({});
  };

  /**
   * Inicializa o formulário com dados do EmailNotifier (modo edição)
   * ou reseta para modo criação
   */
  useEffect(() => {
    if (mode === 'edit' && emailNotifier) {
      setFormData({
        email: emailNotifier.email,
        dailySellNotifier: emailNotifier.dailySellNotifier,
        dailyMonthNotifier: emailNotifier.dailyMonthNotifier,
        monthYearNotifier: emailNotifier.monthYearNotifier
      });
    } else {
      // Reset para modo criação
      resetForm();
    }
    setErrors({});
  }, [mode, emailNotifier, isOpen]);

  /**
   * Valida os dados do formulário
   */
  const validateForm = (): boolean => {
    const newErrors: EmailNotifierFormErrorsState = {};

    // Validação do email
    if (!formData.email || formData.email.trim().length === 0) {
      newErrors.email = 'Email é obrigatório';
    } else if (!EMAIL_NOTIFIER_VALIDATION_RULES.email.pattern.test(formData.email)) {
      newErrors.email = 'Email deve ter um formato válido';
    } else if (formData.email.length > EMAIL_NOTIFIER_VALIDATION_RULES.email.maxLength) {
      newErrors.email = `Email deve ter no máximo ${EMAIL_NOTIFIER_VALIDATION_RULES.email.maxLength} caracteres`;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Manipula mudança nos campos do formulário
   */
  const handleInputChange = (field: keyof EmailNotifierFormState, value: string | boolean): void => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));

    // Limpa erro do campo quando usuário começa a digitar
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: undefined
      }));
    }
  };

  /**
   * Manipula submissão do formulário
   */
  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setErrors({});

    try {
      // Prepara os dados para envio
      const requestData: CreateEmailNotifierRequest | UpdateEmailNotifierRequest = mode === 'create'
        ? {
            email: formData.email.trim().toLowerCase(),
            dailySellNotifier: formData.dailySellNotifier,
            dailyMonthNotifier: formData.dailyMonthNotifier,
            monthYearNotifier: formData.monthYearNotifier
          }
        : {
            dailySellNotifier: formData.dailySellNotifier,
            dailyMonthNotifier: formData.dailyMonthNotifier,
            monthYearNotifier: formData.monthYearNotifier
          };

      // Chama a função de salvamento (vem do hook)
      await onSave(requestData);

      // Se chegou até aqui, foi sucesso
      const message = mode === 'create' 
        ? `Email "${formData.email}" cadastrado com sucesso!`
        : `Email "${formData.email}" atualizado com sucesso!`;

      // Limpa o formulário após sucesso
      if (mode === 'create') {
        resetForm();
      }

      onSuccess(message);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro ao salvar EmailNotifier';
      setErrors({ general: errorMessage });
    }
  };

  /**
   * Manipula fechamento do modal
   */
  const handleClose = (): void => {
    if (!externalIsSubmitting) {
      // Limpa o formulário ao fechar o modal
      resetForm();
      onClose();
    }
  };

  /**
   * Renderiza o título do modal
   */
  const renderModalTitle = (): React.ReactNode => (
    <h3 className="text-lg font-semibold text-smart-gray-900">
      {mode === 'create' ? 'Cadastrar Email' : 'Editar Email'}
    </h3>
  );

  /**
   * Renderiza o campo de email
   */
  const renderEmailField = (): React.ReactNode => (
    <div className="space-y-2">
      <label htmlFor="email" className="block text-sm font-medium text-smart-gray-700">
        Email *
      </label>
      <input
        type="email"
        id="email"
        value={formData.email}
        onChange={(e) => handleInputChange('email', e.target.value)}
        disabled={externalIsSubmitting || mode === 'edit'}
        className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:border-smart-red-500 ${
          errors.email 
            ? 'border-red-300 focus:ring-red-500 focus:border-red-500' 
            : 'border-smart-gray-300'
        } ${mode === 'edit' ? 'bg-smart-gray-100 cursor-not-allowed' : ''}`}
        placeholder="exemplo@empresa.com"
      />
      {errors.email && (
        <p className="text-sm text-red-600">{errors.email}</p>
      )}
    </div>
  );

  /**
   * Renderiza os checkboxes de tipos de notificação
   */
  const renderNotificationTypes = (): React.ReactNode => (
    <div className="space-y-4">
      <h4 className="text-sm font-medium text-smart-gray-700">
        Tipos de Notificação
      </h4>
      
      <div className="space-y-3">
        {/* Vendas Diárias */}
        <div className="flex items-start space-x-3">
          <input
            type="checkbox"
            id="dailySellNotifier"
            checked={formData.dailySellNotifier}
            onChange={(e) => handleInputChange('dailySellNotifier', e.target.checked)}
            disabled={externalIsSubmitting}
            className="mt-1 h-4 w-4 text-smart-red-600 focus:ring-smart-red-500 border-smart-gray-300 rounded"
          />
          <div className="flex-1">
            <label htmlFor="dailySellNotifier" className="text-sm font-medium text-smart-gray-700">
              Email com diário de vendas do dia anterior
            </label>
            <p className="text-xs text-smart-gray-500">
              Recebe relatório diário das vendas do dia anterior
            </p>
          </div>
        </div>

        {/* Vendas Mensais */}
        <div className="flex items-start space-x-3">
          <input
            type="checkbox"
            id="dailyMonthNotifier"
            checked={formData.dailyMonthNotifier}
            onChange={(e) => handleInputChange('dailyMonthNotifier', e.target.checked)}
            disabled={externalIsSubmitting}
            className="mt-1 h-4 w-4 text-smart-red-600 focus:ring-smart-red-500 border-smart-gray-300 rounded"
          />
          <div className="flex-1">
            <label htmlFor="dailyMonthNotifier" className="text-sm font-medium text-smart-gray-700">
              Diário com atualização do valor de vendas mensais
            </label>
            <p className="text-xs text-smart-gray-500">
              Recebe atualizações diárias do progresso das vendas mensais
            </p>
          </div>
        </div>

        {/* Vendas Anuais */}
        <div className="flex items-start space-x-3">
          <input
            type="checkbox"
            id="monthYearNotifier"
            checked={formData.monthYearNotifier}
            onChange={(e) => handleInputChange('monthYearNotifier', e.target.checked)}
            disabled={externalIsSubmitting}
            className="mt-1 h-4 w-4 text-smart-red-600 focus:ring-smart-red-500 border-smart-gray-300 rounded"
          />
          <div className="flex-1">
            <label htmlFor="monthYearNotifier" className="text-sm font-medium text-smart-gray-700">
              Mensal com valor atualizado das vendas do ano
            </label>
            <p className="text-xs text-smart-gray-500">
              Recebe relatório mensal com progresso das vendas anuais
            </p>
          </div>
        </div>
      </div>
    </div>
  );

  /**
   * Renderiza mensagem de erro geral
   */
  const renderGeneralError = (): React.ReactNode => {
    if (!errors.general) return null;
    
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-3">
        <p className="text-sm text-red-600">{errors.general}</p>
      </div>
    );
  };

  /**
   * Renderiza os botões do modal
   */
  const renderModalButtons = (): React.ReactNode => (
    <div className="flex justify-end space-x-3 pt-4">
      <button
        type="button"
        onClick={handleClose}
        disabled={externalIsSubmitting}
        className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-smart-red-500 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        Cancelar
      </button>
      <button
        type="submit"
        disabled={externalIsSubmitting}
        className="px-4 py-2 text-sm font-medium text-white bg-smart-red-600 border border-transparent rounded-md hover:bg-smart-red-700 focus:outline-none focus:ring-2 focus:ring-smart-red-500 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {externalIsSubmitting ? 'Salvando...' : (mode === 'create' ? 'Cadastrar' : 'Atualizar')}
      </button>
    </div>
  );

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Overlay */}
      <div className="fixed inset-0 bg-smart-gray-500 bg-opacity-75 transition-opacity" onClick={handleClose} />
      
      {/* Modal */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full">
          {/* Header */}
          <div className="px-6 py-4 border-b border-smart-gray-200">
            {renderModalTitle()}
          </div>

          {/* Body */}
          <form onSubmit={handleSubmit} className="px-6 py-4 space-y-6">
            {renderEmailField()}
            {renderNotificationTypes()}
            {renderGeneralError()}
            {renderModalButtons()}
          </form>
        </div>
      </div>
    </div>
  );
};

export default EmailNotifierModal;
