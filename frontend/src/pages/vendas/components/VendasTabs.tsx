import React, { useState } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import VendasDiarias from './VendasDiarias';

/**
 * Interface para as propriedades do VendasTabs
 */
interface VendasTabsProps {
  readonly className?: string;
}

/**
 * Interface para as abas de vendas
 */
interface TabItem {
  readonly id: string;
  readonly label: string;
  readonly icon: React.ReactNode;
  readonly isActive: boolean;
}

/**
 * Componente de sistema de abas para relatórios de vendas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasTabs: React.FC<VendasTabsProps> = ({ className = '' }) => {
  const [activeTab, setActiveTab] = useState<string>('diarias');
  const { isMobile } = useLayout();

  /**
   * Configuração das abas disponíveis
   */
  const tabs: TabItem[] = [
    {
      id: 'diarias',
      label: 'Vendas Diárias',
      icon: (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" 
          />
        </svg>
      ),
      isActive: activeTab === 'diarias',
    },
    {
      id: 'mensais',
      label: 'Vendas Mensais',
      icon: (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" 
          />
        </svg>
      ),
      isActive: activeTab === 'mensais',
    },
    {
      id: 'anuais',
      label: 'Vendas Anuais',
      icon: (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" 
          />
        </svg>
      ),
      isActive: activeTab === 'anuais',
    },
  ];

  /**
   * Altera a aba ativa
   * @param tabId - ID da aba a ser ativada
   */
  const handleTabChange = (tabId: string): void => {
    setActiveTab(tabId);
  };

  /**
   * Renderiza uma aba individual
   * @param tab - Dados da aba a ser renderizada
   */
  const renderTab = (tab: TabItem): React.ReactNode => {
    const baseClasses = `flex items-center transition-all duration-200 cursor-pointer rounded ${
      isMobile ? 'px-2 py-2 text-xs' : 'px-3 py-2 text-xs'
    } font-medium`;
    const activeClasses = tab.isActive 
      ? 'bg-smart-red-100 text-smart-red-700 border-r-2 border-smart-red-600' 
      : 'text-smart-gray-600 hover:bg-smart-gray-100 hover:text-smart-gray-900';

    return (
      <button
        key={tab.id}
        onClick={() => handleTabChange(tab.id)}
        className={`${baseClasses} ${activeClasses}`}
      >
        <span className={`flex-shrink-0 ${isMobile ? 'mr-1' : 'mr-2'}`}>
          {tab.icon}
        </span>
        <span className={isMobile ? 'text-xs' : ''}>{tab.label}</span>
      </button>
    );
  };

  /**
   * Renderiza o conteúdo da aba ativa
   */
  const renderTabContent = (): React.ReactNode => {
    switch (activeTab) {
      case 'diarias':
        return <VendasDiarias />;
      case 'mensais':
        return (
          <div className="bg-white rounded-lg shadow-smart-md p-8 border border-smart-gray-100 text-center">
            <div className="text-smart-gray-500 text-lg">
              🚧 Vendas Mensais - Em desenvolvimento
            </div>
            <p className="text-smart-gray-400 mt-2">
              Esta funcionalidade estará disponível em breve.
            </p>
          </div>
        );
      case 'anuais':
        return (
          <div className="bg-white rounded-lg shadow-smart-md p-8 border border-smart-gray-100 text-center">
            <div className="text-smart-gray-500 text-lg">
              🚧 Vendas Anuais - Em desenvolvimento
            </div>
            <p className="text-smart-gray-400 mt-2">
              Esta funcionalidade estará disponível em breve.
            </p>
          </div>
        );
      default:
        return <VendasDiarias />;
    }
  };

  return (
    <div className={`vendas-tabs ${className}`}>
      {/* Navegação das abas */}
      <div className={`bg-white rounded-lg shadow-smart-md border border-smart-gray-100 mb-4 ${
        isMobile ? 'mx-2' : 'mx-4'
      }`}>
        <div className="flex border-b border-smart-gray-200">
          {tabs.map(renderTab)}
        </div>
      </div>

      {/* Conteúdo da aba ativa */}
      <div className={`tab-content ${isMobile ? 'mx-2' : 'mx-4'}`}>
        {renderTabContent()}
      </div>
    </div>
  );
};

export default VendasTabs;
