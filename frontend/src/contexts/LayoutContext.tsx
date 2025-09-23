import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import { useMediaQuery } from '@/hooks/useMediaQuery';

/**
 * Interface para o estado do layout
 */
interface LayoutState {
  readonly isSidebarOpen: boolean;
  readonly isSidebarCollapsed: boolean;
  readonly isModalOpen: boolean;
}

/**
 * Interface para as ações do layout
 */
interface LayoutActions {
  readonly openSidebar: () => void;
  readonly closeSidebar: () => void;
  readonly toggleSidebar: () => void;
  readonly collapseSidebar: () => void;
  readonly expandSidebar: () => void;
  readonly toggleSidebarCollapse: () => void;
  readonly openModal: () => void;
  readonly closeModal: () => void;
}

/**
 * Interface para o contexto completo do layout
 */
interface LayoutContextValue extends LayoutState, LayoutActions {
  readonly isMobile: boolean;
  readonly isTablet: boolean;
  readonly isDesktop: boolean;
  readonly isLargeDesktop: boolean;
}

/**
 * Interface para as propriedades do Provider
 */
interface LayoutProviderProps {
  readonly children: ReactNode;
}

/**
 * Contexto do layout da aplicação
 */
const LayoutContext = createContext<LayoutContextValue | undefined>(undefined);

/**
 * Provider do contexto de layout
 * Gerencia estado do sidebar, modais e responsividade
 * Seguindo princípios de Clean Code com responsabilidade única
 */
export const LayoutProvider: React.FC<LayoutProviderProps> = ({ children }) => {
  const { isMobile, isTablet, isDesktop, isLargeDesktop } = useMediaQuery();
  
  // Estado do sidebar
  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(!isMobile);
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(false);
  
  // Estado de modais
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  /**
   * Abre o sidebar
   */
  const openSidebar = useCallback((): void => {
    setIsSidebarOpen(true);
  }, []);

  /**
   * Fecha o sidebar
   */
  const closeSidebar = useCallback((): void => {
    setIsSidebarOpen(false);
  }, []);

  /**
   * Alterna a visibilidade do sidebar
   */
  const toggleSidebar = useCallback((): void => {
    setIsSidebarOpen(prev => !prev);
  }, []);

  /**
   * Colapsa o sidebar (desktop)
   */
  const collapseSidebar = useCallback((): void => {
    setIsSidebarCollapsed(true);
  }, []);

  /**
   * Expande o sidebar (desktop)
   */
  const expandSidebar = useCallback((): void => {
    setIsSidebarCollapsed(false);
  }, []);

  /**
   * Alterna o estado de colapso do sidebar
   */
  const toggleSidebarCollapse = useCallback((): void => {
    setIsSidebarCollapsed(prev => !prev);
  }, []);

  /**
   * Abre modal
   */
  const openModal = useCallback((): void => {
    setIsModalOpen(true);
  }, []);

  /**
   * Fecha modal
   */
  const closeModal = useCallback((): void => {
    setIsModalOpen(false);
  }, []);

  /**
   * Valor do contexto
   */
  const contextValue: LayoutContextValue = {
    // Estado
    isSidebarOpen,
    isSidebarCollapsed,
    isModalOpen,
    
    // Ações
    openSidebar,
    closeSidebar,
    toggleSidebar,
    collapseSidebar,
    expandSidebar,
    toggleSidebarCollapse,
    openModal,
    closeModal,
    
    // Responsividade
    isMobile,
    isTablet,
    isDesktop,
    isLargeDesktop,
  };

  return (
    <LayoutContext.Provider value={contextValue}>
      {children}
    </LayoutContext.Provider>
  );
};

/**
 * Hook personalizado para usar o contexto de layout
 * @returns Contexto de layout com estado e ações
 * @throws Error se usado fora do LayoutProvider
 */
export const useLayout = (): LayoutContextValue => {
  const context = useContext(LayoutContext);
  
  if (context === undefined) {
    throw new Error('useLayout deve ser usado dentro de um LayoutProvider');
  }
  
  return context;
};

/**
 * Hook utilitário para gerenciar sidebar no mobile
 * @returns Funções específicas para controle do sidebar móvel
 */
export const useMobileSidebar = () => {
  const { isMobile, isSidebarOpen, openSidebar, closeSidebar, toggleSidebar } = useLayout();
  
  return {
    isMobile,
    isOpen: isSidebarOpen,
    open: openSidebar,
    close: closeSidebar,
    toggle: toggleSidebar,
  };
};

/**
 * Hook utilitário para gerenciar sidebar no desktop
 * @returns Funções específicas para controle do sidebar desktop
 */
export const useDesktopSidebar = () => {
  const { 
    isDesktop, 
    isLargeDesktop, 
    isSidebarCollapsed, 
    collapseSidebar, 
    expandSidebar, 
    toggleSidebarCollapse 
  } = useLayout();
  
  return {
    isDesktop: isDesktop || isLargeDesktop,
    isCollapsed: isSidebarCollapsed,
    collapse: collapseSidebar,
    expand: expandSidebar,
    toggle: toggleSidebarCollapse,
  };
};

export default LayoutContext;
