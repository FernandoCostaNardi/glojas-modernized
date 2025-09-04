import React from 'react';
import type { ButtonVariant, ButtonSize, ClickEventHandler } from '@/types';

/**
 * Interface para as propriedades do componente Button
 * Seguindo princípios de Clean Code com tipos bem definidos
 */
interface ButtonProps {
  readonly variant?: ButtonVariant;
  readonly size?: ButtonSize;
  readonly disabled?: boolean;
  readonly loading?: boolean;
  readonly children: React.ReactNode;
  readonly onClick?: ClickEventHandler<HTMLButtonElement>;
  readonly className?: string;
  readonly type?: 'button' | 'submit' | 'reset';
  readonly 'aria-label'?: string;
}

/**
 * Componente Button reutilizável com TypeScript
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  disabled = false,
  loading = false,
  children,
  onClick,
  className = '',
  type = 'button',
  'aria-label': ariaLabel,
  ...rest
}) => {
  /**
   * Obtém as classes CSS baseadas na variante
   * @returns {string} Classes CSS para a variante
   */
  const getVariantClasses = (): string => {
    const baseClasses = 'font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';
    
    switch (variant) {
      case 'primary':
        return `${baseClasses} bg-smart-red-600 hover:bg-smart-red-700 text-white focus:ring-smart-red-500`;
      case 'secondary':
        return `${baseClasses} bg-white hover:bg-smart-gray-50 text-smart-gray-700 border border-smart-gray-300 focus:ring-smart-red-500`;
      case 'danger':
        return `${baseClasses} bg-error-600 hover:bg-error-700 text-white focus:ring-error-500`;
      case 'ghost':
        return `${baseClasses} bg-transparent hover:bg-smart-gray-100 text-smart-gray-700 focus:ring-smart-red-500`;
      default:
        return `${baseClasses} bg-smart-red-600 hover:bg-smart-red-700 text-white focus:ring-smart-red-500`;
    }
  };

  /**
   * Obtém as classes CSS baseadas no tamanho
   * @returns {string} Classes CSS para o tamanho
   */
  const getSizeClasses = (): string => {
    switch (size) {
      case 'sm':
        return 'px-3 py-2 text-sm';
      case 'md':
        return 'px-4 py-2.5 text-sm';
      case 'lg':
        return 'px-6 py-3 text-base';
      case 'xl':
        return 'px-8 py-4 text-lg';
      default:
        return 'px-4 py-2.5 text-sm';
    }
  };

  /**
   * Renderiza o conteúdo do botão
   * @returns {React.ReactNode} Conteúdo renderizado
   */
  const renderContent = (): React.ReactNode => {
    if (loading) {
      return (
        <div className="flex items-center justify-center space-x-2">
          <div className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
          <span>Carregando...</span>
        </div>
      );
    }
    
    return children;
  };

  const buttonClasses = `
    ${getVariantClasses()}
    ${getSizeClasses()}
    ${className}
  `.trim();

  return (
    <button
      className={buttonClasses}
      disabled={disabled || loading}
      onClick={onClick}
      type={type}
      aria-label={ariaLabel}
      {...rest}
    >
      {renderContent()}
    </button>
  );
};

export default Button;