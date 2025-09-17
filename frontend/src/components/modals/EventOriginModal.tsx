import React, { useState, useEffect } from 'react';
import {
  EventOriginModalProps,
  EventOriginFormState,
  EventOriginFormErrorsState,
  EventSource,
  EVENT_SOURCE_OPTIONS,
  VALIDATION_RULES,
  CreateEventOriginRequest,
  UpdateEventOriginRequest
} from '@/types';

/**
 * Modal de Cadastro/Edição de Código de Origem
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const EventOriginModal: React.FC<EventOriginModalProps> = ({
  isOpen,
  onClose,
  mode,
  eventOrigin,
  onSuccess,
  onSave,
  isSubmitting: externalIsSubmitting = false
}) => {
  // Estados do formulário
  const [formData, setFormData] = useState<EventOriginFormState>({
    eventSource: 'PDV' as EventSource,
    sourceCode: ''
  });
  const [errors, setErrors] = useState<EventOriginFormErrorsState>({});

  /**
   * Reseta o formulário para o estado inicial
   * Seguindo princípios de Clean Code com responsabilidade única
   */
  const resetForm = (): void => {
    setFormData({
      eventSource: 'PDV' as EventSource,
      sourceCode: ''
    });
    setErrors({});
  };

  /**
   * Inicializa o formulário com dados do código de origem (modo edição)
   * ou reseta para modo criação
   */
  useEffect(() => {
    if (mode === 'edit' && eventOrigin) {
      setFormData({
        eventSource: eventOrigin.eventSource,
        sourceCode: eventOrigin.sourceCode
      });
    } else {
      // Reset para modo criação
      resetForm();
    }
    setErrors({});
  }, [mode, eventOrigin, isOpen]);

  /**
   * Valida os dados do formulário
   */
  const validateForm = (): boolean => {
    const newErrors: EventOriginFormErrorsState = {};

    // Validação do EventSource
    if (!formData.eventSource) {
      newErrors.eventSource = 'Fonte do evento é obrigatória';
    }

    // Validação do SourceCode
    if (!formData.sourceCode || formData.sourceCode.trim().length === 0) {
      newErrors.sourceCode = 'Código da fonte é obrigatório';
    } else if (formData.sourceCode.length > VALIDATION_RULES.sourceCode.maxLength) {
      newErrors.sourceCode = `Código da fonte deve ter no máximo ${VALIDATION_RULES.sourceCode.maxLength} caracteres`;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Manipula mudança nos campos do formulário
   */
  const handleInputChange = (field: keyof EventOriginFormState, value: string): void => {
    setFormData(prev => ({
      ...prev,
      [field]: field === 'eventSource' ? value as EventSource : value
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
      const requestData: CreateEventOriginRequest | UpdateEventOriginRequest = {
        eventSource: formData.eventSource,
        sourceCode: formData.sourceCode
      };

      // Chama a função de salvamento (vem do hook)
      await onSave(requestData);

      // Se chegou até aqui, foi sucesso
      const message = mode === 'create' 
        ? `Código de origem "${formData.sourceCode}" criado com sucesso!`
        : `Código de origem "${formData.sourceCode}" atualizado com sucesso!`;

      // Limpa o formulário após sucesso
      if (mode === 'create') {
        resetForm();
      }

      onSuccess(message);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro ao salvar código de origem';
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
   * Manipula tecla Escape
   */
  const handleKeyDown = (e: React.KeyboardEvent): void => {
    if (e.key === 'Escape' && !externalIsSubmitting) {
      handleClose();
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Overlay */}
      <div 
        className="fixed inset-0 bg-smart-gray-500 bg-opacity-75 transition-opacity"
        onClick={handleClose}
      />

      {/* Modal */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div 
          className="relative bg-white rounded-lg shadow-xl max-w-md w-full"
          onKeyDown={handleKeyDown}
        >
          {/* Header */}
          <div className="flex items-center justify-between p-6 bg-red-600 text-white rounded-t-lg">
            <h3 className="text-lg font-medium">
              {mode === 'create' ? 'Novo Código de Origem' : 'Editar Código de Origem'}
            </h3>
            <button
              type="button"
              onClick={handleClose}
              disabled={externalIsSubmitting}
              className="text-white hover:text-gray-200 transition-colors duration-200 disabled:opacity-50"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            {/* Erro geral */}
            {errors.general && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-md">
                <div className="flex">
                  <div className="flex-shrink-0">
                    <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="ml-3">
                    <p className="text-sm text-red-800">{errors.general}</p>
                  </div>
                </div>
              </div>
            )}

            {/* Campo EventSource */}
            <div>
              <label htmlFor="eventSource" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Fonte do Evento *
              </label>
              <select
                id="eventSource"
                value={formData.eventSource}
                onChange={(e) => handleInputChange('eventSource', e.target.value)}
                disabled={externalIsSubmitting}
                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-50 disabled:text-smart-gray-500 ${
                  errors.eventSource ? 'border-red-300' : 'border-smart-gray-300'
                }`}
              >
                {EVENT_SOURCE_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
              {errors.eventSource && (
                <p className="mt-1 text-sm text-red-600">{errors.eventSource}</p>
              )}
            </div>

            {/* Campo SourceCode */}
            <div>
              <label htmlFor="sourceCode" className="block text-sm font-medium text-smart-gray-700 mb-1">
                Código da Fonte *
              </label>
              <input
                type="text"
                id="sourceCode"
                value={formData.sourceCode}
                onChange={(e) => handleInputChange('sourceCode', e.target.value)}
                disabled={externalIsSubmitting}
                placeholder="Digite o código da fonte..."
                maxLength={VALIDATION_RULES.sourceCode.maxLength}
                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:bg-smart-gray-50 disabled:text-smart-gray-500 ${
                  errors.sourceCode ? 'border-red-300' : 'border-smart-gray-300'
                }`}
              />
              {errors.sourceCode && (
                <p className="mt-1 text-sm text-red-600">{errors.sourceCode}</p>
              )}
              <p className="mt-1 text-xs text-smart-gray-500">
                {formData.sourceCode.length}/{VALIDATION_RULES.sourceCode.maxLength} caracteres
              </p>
            </div>

            {/* Botões */}
            <div className="flex items-center justify-end space-x-3 pt-4 border-t border-smart-gray-200">
              <button
                type="button"
                onClick={handleClose}
                disabled={externalIsSubmitting}
                className="px-4 py-2 text-sm font-medium text-smart-gray-700 bg-white border border-smart-gray-300 rounded-md shadow-sm hover:bg-smart-gray-50 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:border-smart-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={externalIsSubmitting}
                className="px-4 py-2 text-sm font-medium text-white bg-smart-blue-600 border border-transparent rounded-md shadow-sm hover:bg-smart-blue-700 focus:outline-none focus:ring-2 focus:ring-smart-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                {externalIsSubmitting ? (
                  <div className="flex items-center space-x-2">
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Salvando...</span>
                  </div>
                ) : (
                  mode === 'create' ? 'Criar' : 'Salvar'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EventOriginModal;
