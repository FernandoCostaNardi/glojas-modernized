import React, { useState } from 'react';
import { InputType, ChangeEventHandler } from '@/types';

/**
 * Interface para as propriedades do componente Input
 * Seguindo princípios de Clean Code com tipos bem definidos
 */
interface InputProps {
  readonly type?: InputType;
  readonly label?: string;
  readonly placeholder?: string;
  readonly value: string;
  readonly onChange: ChangeEventHandler<HTMLInputElement>;
  readonly error?: string | undefined;
  readonly required?: boolean;
  readonly disabled?: boolean;
  readonly className?: string;
  readonly autoComplete?: string;
  readonly 'aria-label'?: string;
}

/**
 * Componente Input reutilizável com TypeScript
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Input: React.FC<InputProps> = ({
  type = 'text',
  label,
  placeholder,
  value,
  onChange,
  error,
  required = false,
  disabled = false,
  className = '',
  'aria-label': ariaLabel,
  ...rest
}) => {
  const [isFocused, setIsFocused] = useState(false);

  /**
   * Obtém as classes CSS baseadas no estado do input
   * @returns Classes CSS para o input
   */
  const getInputClasses = (): string => {
    const baseClasses = 'w-full px-4 py-3 border rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-0 placeholder-smart-gray-400';
    
    if (error) {
      return `${baseClasses} border-error-500 focus:ring-error-500 focus:border-error-500`;
    }
    
    if (isFocused) {
      return `${baseClasses} border-smart-red-600 focus:ring-smart-red-500 focus:border-smart-red-600`;
    }
    
    return `${baseClasses} border-smart-gray-300 focus:ring-smart-red-500 focus:border-smart-red-600`;
  };

  /**
   * Obtém as classes CSS para o label
   * @returns {string} Classes CSS para o label
   */
  const getLabelClasses = () => {
    const baseClasses = 'block text-sm font-medium mb-2';
    
    if (error) {
      return `${baseClasses} text-error-600`;
    }
    
    return `${baseClasses} text-smart-gray-700`;
  };

  /**
   * Renderiza o ícone de erro
   * @returns {React.ReactNode} Ícone de erro
   */
  const renderErrorIcon = () => {
    if (!error) return null;
    
    return (
      <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
        <svg className="h-5 w-5 text-error-500" viewBox="0 0 20 20" fill="currentColor">
          <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
        </svg>
      </div>
    );
  };

  /**
   * Renderiza a mensagem de erro
   * @returns {React.ReactNode} Mensagem de erro
   */
  const renderErrorMessage = () => {
    if (!error) return null;
    
    return (
      <p className="mt-1 text-sm text-error-600 flex items-center">
        <svg className="h-4 w-4 mr-1" viewBox="0 0 20 20" fill="currentColor">
          <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
        </svg>
        {error}
      </p>
    );
  };

  return (
    <div className={`${className}`}>
      {label && (
        <label className={getLabelClasses()}>
          {label}
          {required && <span className="text-error-500 ml-1">*</span>}
        </label>
      )}
      
      <div className="relative">
        <input
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          disabled={disabled}
          required={required}
          className={getInputClasses()}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          {...rest}
        />
        {renderErrorIcon()}
      </div>
      
      {renderErrorMessage()}
    </div>
  );
};

// TypeScript já fornece validação de tipos em tempo de compilação

export default Input;
