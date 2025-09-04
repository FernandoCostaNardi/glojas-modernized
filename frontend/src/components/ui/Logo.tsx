import React from 'react';
import { LogoSize, LogoVariant } from '@/types';

/**
 * Interface para as propriedades do componente Logo
 * Seguindo princípios de Clean Code com tipos bem definidos
 */
interface LogoProps {
  readonly size?: LogoSize;
  readonly variant?: LogoVariant;
  readonly className?: string;
  readonly 'aria-label'?: string;
}

/**
 * Componente Logo da Smart Eletron com TypeScript
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Logo: React.FC<LogoProps> = ({
  size = 'md',
  variant = 'color',
  className = '',
  'aria-label': _ariaLabel = 'Smart Eletron Logo',
  ...rest
}) => {
  /**
   * Obtém as classes CSS de tamanho para a imagem
   * @returns Classes CSS com largura e altura
   */
  const getSizeClasses = (): string => {
    switch (size) {
      case 'sm':
        return 'w-16 h-16';
      case 'md':
        return 'w-20 h-20';
      case 'lg':
        return 'w-24 h-24';
      case 'xl':
        return 'w-32 h-32';
      default:
        return 'w-20 h-20';
    }
  };

  const sizeClasses = getSizeClasses();

  return (
    <div className={`${className}`} {...rest}>
      <img
        src="/logo.png"
        alt={_ariaLabel}
        className={`${sizeClasses} object-contain`}
      />
    </div>
  );
};

// TypeScript já fornece validação de tipos em tempo de compilação

export default Logo;
