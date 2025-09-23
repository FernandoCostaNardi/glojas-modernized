import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useLayout } from '@/contexts/LayoutContext';

/**
 * Interface para as propriedades do Header
 */
interface HeaderProps {
  readonly className?: string;
}

/**
 * Componente Header
 * Cabeçalho da aplicação mobile-first com hamburger menu, logo responsivo e menu de usuário
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Header: React.FC<HeaderProps> = ({ className = '' }) => {
  const { user, logout, hasPermission } = useAuth();
  const { isMobile, isDesktop, isSidebarCollapsed, toggleSidebar } = useLayout();
  const [showUserMenu, setShowUserMenu] = useState<boolean>(false);

  /**
   * Alterna a visibilidade do menu do usuário
   */
  const toggleUserMenu = (): void => {
    setShowUserMenu(!showUserMenu);
  };

  /**
   * Fecha o menu do usuário
   */
  const closeUserMenu = (): void => {
    setShowUserMenu(false);
  };

  /**
   * Manipula o logout do usuário
   */
  const handleLogout = (): void => {
    closeUserMenu();
    logout();
  };

  /**
   * Manipula abertura das configurações
   */
  const handleSettings = (): void => {
    closeUserMenu();
    // Navega para a página de configurações
    window.location.href = '/settings';
  };

  /**
   * Renderiza o botão hamburger (apenas mobile)
   */
  const renderHamburgerButton = (): React.ReactNode => {
    if (!isMobile) return null;

    return (
      <button
        onClick={toggleSidebar}
        className="p-2 rounded-lg text-white hover:bg-smart-red-700 transition-colors duration-200 mr-3"
        title="Abrir menu"
        aria-label="Abrir menu de navegação"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    );
  };

  /**
   * Renderiza o ícone de configurações (apenas para SYSTEM_ADMIN)
   */
  const renderSettingsIcon = (): React.ReactNode => {
    if (!hasPermission('SYSTEM_ADMIN')) {
      return null;
    }

    return (
      <button
        onClick={handleSettings}
        className="p-2 rounded-lg text-white hover:bg-smart-red-700 hover:text-white transition-colors duration-200"
        title="Configurações do sistema"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" 
          />
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" 
          />
        </svg>
      </button>
    );
  };

  /**
   * Renderiza o ícone do usuário e menu dropdown
   */
  const renderUserMenu = (): React.ReactNode => (
    <div className="relative">
      {/* Botão do usuário */}
      <button
        onClick={toggleUserMenu}
        className="flex items-center space-x-2 p-2 rounded-lg text-white hover:bg-smart-red-700 transition-colors duration-200"
        title={`Usuário: ${user?.name || user?.username}`}
        aria-label="Menu do usuário"
      >
        <div className="w-8 h-8 bg-white rounded-full flex items-center justify-center text-smart-red-600 text-sm font-medium">
          {user?.name?.charAt(0).toUpperCase() || user?.username?.charAt(0).toUpperCase() || 'U'}
        </div>
        {!isMobile && (
          <div className="text-left">
            <div className="text-sm font-medium text-white">
              {user?.name || user?.username}
            </div>
            <div className="text-xs text-smart-red-100">
              {user?.roles?.join(', ')}
            </div>
          </div>
        )}
        <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* Menu dropdown */}
      {showUserMenu && (
        <>
          {/* Overlay para fechar o menu */}
          <div 
            className="fixed inset-0 z-10" 
            onClick={closeUserMenu}
          />
          
          {/* Menu */}
          <div className={`absolute right-0 mt-2 ${isMobile ? 'w-56' : 'w-48'} bg-white rounded-lg shadow-smart-lg border border-smart-gray-200 z-20`}>
            <div className="py-1">
              {/* Informações do usuário */}
              <div className="px-4 py-2 border-b border-smart-gray-100">
                <div className="text-sm font-medium text-smart-gray-800">
                  {user?.name}
                </div>
                <div className="text-xs text-smart-gray-500">
                  @{user?.username}
                </div>
              </div>
              
              {/* Opções do menu */}
              <button
                onClick={handleLogout}
                className="w-full text-left px-4 py-2 text-sm text-smart-gray-700 hover:bg-smart-gray-100 transition-colors duration-200"
              >
                <div className="flex items-center">
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Sair
                </div>
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );

  /**
   * Classes CSS do header responsivo
   */
  const headerClasses = `
    bg-smart-red-600 border-b border-smart-red-700 h-16
    flex items-center justify-between
    sticky top-0 z-40 relative
    ${isMobile ? 'px-4' : 'px-6'}
    ${className}
  `.trim();

  return (
    <header className={headerClasses}>
      {/* Lado esquerdo: Hamburger + Logo */}
      <div className="flex items-center">
        {renderHamburgerButton()}
        
        <div className="flex items-center">
          <img 
            src="/logoheader.png" 
            alt="Smart Eletron" 
            className={`w-auto object-contain ${
              isMobile 
                ? 'h-10' 
                : isDesktop && !isSidebarCollapsed 
                  ? 'h-12' 
                  : 'h-10'
            }`}
          />
        </div>
      </div>

      {/* Lado direito: Configurações + Menu usuário */}
      <div className="flex items-center space-x-2">
        {renderSettingsIcon()}
        {renderUserMenu()}
      </div>
    </header>
  );
};

export default Header;
