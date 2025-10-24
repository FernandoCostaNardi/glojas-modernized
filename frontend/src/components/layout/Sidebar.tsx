import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';

/**
 * Interface para as propriedades do Sidebar
 */
interface SidebarProps {
  readonly className?: string;
  readonly onCollapseChange?: (collapsed: boolean) => void;
}

/**
 * Interface para itens do menu
 */
interface MenuItem {
  readonly id: string;
  readonly label: string;
  readonly icon: React.ReactNode;
  readonly href: string;
}

/**
 * Componente Sidebar
 * Menu lateral responsivo: drawer no mobile, sidebar no desktop
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Sidebar: React.FC<SidebarProps> = ({ className = '' }) => {
  const { 
    isMobile, 
    isDesktop, 
    isSidebarOpen, 
    isSidebarCollapsed, 
    closeSidebar, 
    toggleSidebarCollapse 
  } = useLayout();

  /**
   * Determina se um item do menu está ativo baseado na rota atual
   * @param href - URL do item do menu
   * @returns true se o item estiver ativo
   */
  const isMenuItemActive = (href: string): boolean => {
    const currentPath = window.location.pathname;
    
    // Para a rota home/dashboard, considera ativo se estiver na raiz ou dashboard
    if (href === '/dashboard') {
      return currentPath === '/' || currentPath === '/dashboard' || currentPath === '';
    }
    
    // Para outras rotas, verifica se a rota atual começa com o href
    return currentPath.startsWith(href);
  };

  /**
   * Items do menu de navegação
   */
  const menuItems: MenuItem[] = [
    {
      id: 'home',
      label: 'Home',
      href: '/dashboard',
      icon: (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" 
          />
        </svg>
      ),
    },
    {
      id: 'vendas',
      label: 'Vendas',
      href: '/vendas',
      icon: (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a1 1 0 01-2 2h-2a2 2 0 01-2-2z" 
          />
        </svg>
      ),
    },
  ];

  /**
   * Manipula clique em item do menu
   * No mobile, fecha o sidebar automaticamente
   */
  const handleMenuItemClick = (): void => {
    if (isMobile) {
      closeSidebar();
    }
  };

  /**
   * Renderiza um item do menu
   * @param item - Item do menu a ser renderizado
   */
  const renderMenuItem = (item: MenuItem): React.ReactNode => {
    const isActive = isMenuItemActive(item.href);
    const baseClasses = 'flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200 group';
    const activeClasses = isActive 
      ? 'bg-smart-red-100 text-smart-red-700 border-r-2 border-smart-red-600' 
      : 'text-smart-gray-600 hover:bg-smart-gray-100 hover:text-smart-gray-900';

    const isCollapsedDesktop = isDesktop && isSidebarCollapsed;

    return (
      <a
        key={item.id}
        href={item.href}
        onClick={handleMenuItemClick}
        className={`${baseClasses} ${activeClasses} ${
          isCollapsedDesktop ? 'justify-center px-2' : ''
        }`}
        title={isCollapsedDesktop ? item.label : undefined}
      >
        <span className="flex-shrink-0">
          {item.icon}
        </span>
        {(!isCollapsedDesktop || isMobile) && (
          <span className="ml-3">
            {item.label}
          </span>
        )}
      </a>
    );
  };

  /**
   * Renderiza o botão de toggle (apenas desktop)
   */
  const renderToggleButton = (): React.ReactNode => {
    if (isMobile) return null;

    return (
      <button
        onClick={toggleSidebarCollapse}
        className="p-2 rounded-lg text-smart-gray-500 hover:bg-smart-gray-100 hover:text-smart-gray-700 transition-colors duration-200"
        title={isSidebarCollapsed ? 'Expandir menu' : 'Recolher menu'}
        aria-label={isSidebarCollapsed ? 'Expandir menu' : 'Recolher menu'}
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          {isSidebarCollapsed ? (
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          ) : (
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          )}
        </svg>
      </button>
    );
  };

  // Não renderiza nada se estiver no mobile e fechado
  if (isMobile && !isSidebarOpen) {
    return null;
  }

  /**
   * Classes CSS do sidebar baseadas no estado e dispositivo
   */
  const sidebarClasses = `
    bg-white border-r border-smart-gray-200 transition-all duration-300 ease-in-out
    flex flex-col h-full relative z-30
    ${isMobile 
      ? 'fixed inset-y-0 left-0 w-64 z-50' 
      : isDesktop && isSidebarCollapsed 
        ? 'w-16' 
        : 'w-64'
    }
    ${className}
  `.trim();

  /**
   * Renderiza o conteúdo do sidebar
   */
  const renderSidebarContent = (): React.ReactNode => (
    <aside className={sidebarClasses}>
      {/* Header do Sidebar */}
      <div className="flex items-center justify-between p-4 border-b border-smart-gray-200">
        {isMobile && (
          <h2 className="text-lg font-semibold text-smart-gray-800">
            Menu
          </h2>
        )}
        {!isMobile && !isSidebarCollapsed && (
          <h2 className="text-lg font-semibold text-smart-gray-800">
            Menu
          </h2>
        )}
        
        {/* Botão de fechar no mobile */}
        {isMobile && (
          <button
            onClick={closeSidebar}
            className="p-2 rounded-lg text-smart-gray-500 hover:bg-smart-gray-100 hover:text-smart-gray-700 transition-colors duration-200"
            aria-label="Fechar menu"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        )}
        
        {/* Botão de toggle no desktop */}
        {renderToggleButton()}
      </div>

      {/* Navegação */}
      <nav className="flex-1 p-4 space-y-1">
        {menuItems.map(renderMenuItem)}
      </nav>

      {/* Footer do Sidebar */}
      <div className="p-4 border-t border-smart-gray-200">
        {(!isSidebarCollapsed || isMobile) && (
          <div className="text-xs text-smart-gray-500 text-center">
            Smart Eletron
          </div>
        )}
      </div>
    </aside>
  );

  // No mobile, renderiza com overlay
  if (isMobile) {
    return (
      <>
        {/* Overlay */}
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40"
          onClick={closeSidebar}
          aria-hidden="true"
        />
        {renderSidebarContent()}
      </>
    );
  }

  // No desktop, renderiza normalmente
  return renderSidebarContent();
};

export default Sidebar;
