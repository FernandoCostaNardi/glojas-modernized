import React from 'react';
import SmartLogo from '@/components/common/SmartLogo';

/**
 * Interface para as propriedades do LogoArea
 */
interface LogoAreaProps {
  readonly isCollapsed?: boolean;
  readonly isMobile?: boolean;
}

/**
 * Componente LogoArea
 * Área dedicada para exibir o logo da Smart Eletron no sidebar
 * Responsivo e adaptável ao estado colapsado/expandido
 * Seguindo princípios de Clean Code
 */
const LogoArea: React.FC<LogoAreaProps> = ({ 
  isCollapsed = false, 
  isMobile = false 
}) => {
  
  /**
   * Manipula o clique no logo
   * Redireciona para a página inicial
   */
  const handleLogoClick = (): void => {
    window.location.href = '/dashboard';
  };

  /**
   * Determina o tamanho do logo baseado no estado
   */
  const getLogoSize = (): 'small' | 'medium' | 'large' => {
    if (isCollapsed && !isMobile) {
      return 'small';
    }
    return 'large';
  };

  /**
   * Renderiza a versão colapsada (apenas logo)
   */
  const renderCollapsed = (): React.ReactNode => (
    <button
      onClick={handleLogoClick}
      className="flex items-center justify-center p-4 transition-all duration-300 hover:scale-110 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 rounded-lg"
      title="Smart Eletron - Ir para Home"
      aria-label="Logo Smart Eletron - Ir para Home"
    >
      <SmartLogo size={getLogoSize()} />
    </button>
  );

  /**
   * Renderiza a versão expandida (logo + texto)
   */
  const renderExpanded = (): React.ReactNode => (
    <button
      onClick={handleLogoClick}
      className="flex flex-col items-center justify-center p-6 space-y-3 transition-all duration-300 hover:scale-105 focus:outline-none focus:ring-2 focus:ring-smart-red-500 focus:ring-offset-2 rounded-lg group w-full"
      aria-label="Logo Smart Eletron - Ir para Home"
    >
      {/* Logo com efeito de hover */}
      <div className="transform transition-transform duration-300 group-hover:rotate-6">
        <SmartLogo size={getLogoSize()} />
      </div>
      
      {/* Nome da empresa */}
      <div className="text-center space-y-1">
        <h1 className="text-xl font-bold text-smart-gray-800 group-hover:text-smart-red-600 transition-colors duration-300">
          Smart Eletron
        </h1>
        <p className="text-xs text-smart-gray-500 font-medium">
          Sistema de Gestão
        </p>
      </div>
      
      {/* Indicador visual de clique */}
      <div className="h-1 w-0 bg-smart-red-500 rounded-full transition-all duration-300 group-hover:w-16"></div>
    </button>
  );

  /**
   * Container principal com background
   */
  return (
    <div className="relative bg-gradient-to-br from-smart-gray-50 to-white border-b border-smart-gray-200">
      {/* Barra decorativa superior */}
      <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-smart-red-500 via-smart-red-600 to-smart-red-500"></div>
      
      {/* Conteúdo */}
      {isCollapsed && !isMobile ? renderCollapsed() : renderExpanded()}
      
      {/* Barra decorativa inferior */}
      <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-smart-red-300 to-transparent opacity-50"></div>
    </div>
  );
};

export default LogoArea;

