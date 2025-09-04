import React, { useState } from 'react';

/**
 * Props do modal de confirmação
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface ConfirmationModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onConfirm: () => void;
  readonly title: string;
  readonly message: string;
  readonly confirmButtonText: string;
  readonly cancelButtonText: string;
}

/**
 * Modal de confirmação para alterações de status
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const ConfirmationModal: React.FC<ConfirmationModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmButtonText,
  cancelButtonText
}) => {
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setIsSubmitting(true);
    try {
      await onConfirm();
      handleClose();
    } catch (error) {
      console.error('Erro ao confirmar ação:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    setIsSubmitting(false);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 className="text-lg font-semibold text-smart-gray-800 mb-2">
          {title}
        </h3>
        <p className="text-sm text-smart-gray-600 mb-4">
          {message}
        </p>
        
        <form onSubmit={handleSubmit}>
          <div className="flex justify-end space-x-3">
            <button
              type="button"
              onClick={handleClose}
              disabled={isSubmitting}
              className="px-4 py-2 text-sm font-medium text-smart-gray-600 bg-smart-gray-100 hover:bg-smart-gray-200 rounded-md transition-colors duration-200 disabled:opacity-50"
            >
              {cancelButtonText}
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 text-sm font-medium text-white bg-smart-red-600 hover:bg-smart-red-700 rounded-md transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? 'Processando...' : confirmButtonText}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
