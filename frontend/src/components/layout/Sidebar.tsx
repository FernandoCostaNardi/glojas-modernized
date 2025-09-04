import React, { useState } from 'react';

/**
 * Interface para as propriedades do Sidebar
 */
interface SidebarProps {
  readonly className?: string;
  readonly onCollapseChange?: (isCollapsed: boolean) => void;
}

/**
 * Interface para itens do menu
 */
interface MenuItem {
  readonly id: string;
  readonly label: string;
  readonly icon: React.ReactNode;
  readonly href: string;
  readonly isActive?: boolean;
}

/**
 * Componente Sidebar
 * Menu lateral de navegação seguindo princípios de Clean Code
 */
const Sidebar: React.FC<SidebarProps> = ({ className = '', onCollapseChange }) => {
  const [isCollapsed, setIsCollapsed] = useState<boolean>(false);

  /**
   * Items do menu de navegação
   */
  const menuItems: MenuItem[] = [
    {
      id: 'home',
      label: 'Home',
      href: '/dashboard',
      isActive: true,
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
  ];

  /**
   * Alterna o estado de colapso do sidebar
   */
  const toggleCollapse = (): void => {
    const newState = !isCollapsed;
    setIsCollapsed(newState);
    
    // Notifica mudança de estado para o componente pai
    if (onCollapseChange) {
      onCollapseChange(newState);
    }
  };

  /**
   * Renderiza um item do menu
   * @param item - Item do menu a ser renderizado
   */
  const renderMenuItem = (item: MenuItem): React.ReactNode => {
    const baseClasses = 'flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200 group';
    const activeClasses = item.isActive 
      ? 'bg-smart-red-100 text-smart-red-700 border-r-2 border-smart-red-600' 
      : 'text-smart-gray-600 hover:bg-smart-gray-100 hover:text-smart-gray-900';

    return (
      <a
        key={item.id}
        href={item.href}
        className={`${baseClasses} ${activeClasses} ${
          isCollapsed ? 'justify-center px-2' : ''
        }`}
        title={isCollapsed ? item.label : undefined}
      >
        <span className="flex-shrink-0">
          {item.icon}
        </span>
        {!isCollapsed && (
          <span className="ml-3">
            {item.label}
          </span>
        )}
      </a>
    );
  };

  /**
   * Renderiza o botão de toggle
   */
  const renderToggleButton = (): React.ReactNode => (
    <button
      onClick={toggleCollapse}
      className="p-2 rounded-lg text-smart-gray-500 hover:bg-smart-gray-100 hover:text-smart-gray-700 transition-colors duration-200"
      title={isCollapsed ? 'Expandir menu' : 'Recolher menu'}
    >
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        {isCollapsed ? (
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
        ) : (
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        )}
      </svg>
    </button>
  );

  /**
   * Classes CSS do sidebar baseadas no estado
   */
  const sidebarClasses = `
    ${isCollapsed ? 'w-16' : 'w-64'}
    bg-white border-r border-smart-gray-200 transition-all duration-300 ease-in-out
    flex flex-col h-full relative z-30
    ${className}
  `.trim();

  return (
    <aside className={sidebarClasses}>
      {/* Espaço para o logo sobreposto - dinâmico baseado no estado */}
      {!isCollapsed && <div className="h-40 flex-shrink-0"></div>}
      
      {/* Header do Sidebar */}
      <div className="flex items-center justify-between p-4 border-b border-smart-gray-200">
        {!isCollapsed && (
          <h2 className="text-lg font-semibold text-smart-gray-800">
            Menu
          </h2>
        )}
        {renderToggleButton()}
      </div>

      {/* Navegação */}
      <nav className="flex-1 p-4 space-y-1">
        {menuItems.map(renderMenuItem)}
      </nav>

      {/* Footer do Sidebar */}
      <div className="p-4 border-t border-smart-gray-200">
        {!isCollapsed && (
          <div className="text-xs text-smart-gray-500 text-center">
            Smart Eletron
          </div>
        )}
      </div>
    </aside>
  );
};

export default Sidebar;
