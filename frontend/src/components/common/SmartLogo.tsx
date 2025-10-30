import React from 'react';

/**
 * Interface para as propriedades do SmartLogo
 */
interface SmartLogoProps {
  readonly className?: string;
  readonly size?: 'small' | 'medium' | 'large';
}

/**
 * Componente SmartLogo
 * Logo da Smart Eletron usando a imagem oficial
 * Seguindo princípios de Clean Code
 */
const SmartLogo: React.FC<SmartLogoProps> = ({ 
  className = '', 
  size = 'medium' 
}) => {
  
  /**
   * Define a altura do logo baseado na prop size
   */
  const getHeightClass = (): string => {
    switch (size) {
      case 'small':
        return 'h-8';
      case 'large':
        return 'h-16';
      case 'medium':
      default:
        return 'h-12';
    }
  };

  return (
    <img 
      src="/logoheader.png" 
      alt="Smart Eletron" 
      className={`${getHeightClass()} w-auto object-contain ${className}`}
      aria-label="Smart Eletron Logo"
    />
  );
};

export default SmartLogo;

