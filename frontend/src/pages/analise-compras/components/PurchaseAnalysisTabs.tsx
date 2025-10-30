import React from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { PurchaseAnalysisTabType, PurchaseAnalysisTab } from '@/types/purchaseAnalysis';

/**
 * Interface para as propriedades do componente
 */
interface PurchaseAnalysisTabsProps {
  readonly activeTab: PurchaseAnalysisTabType;
  readonly onTabChange: (tab: PurchaseAnalysisTabType) => void;
}

/**
 * Componente de navegação por abas para Análise de Compras
 * Permite alternar entre "Análise Geral" e "Estoque Crítico"
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const PurchaseAnalysisTabs: React.FC<PurchaseAnalysisTabsProps> = ({
  activeTab,
  onTabChange
}) => {
  const { isMobile } = useLayout();

  /**
   * Abas disponíveis
   */
  const tabs: PurchaseAnalysisTab[] = [
    {
      id: 'geral',
      label: 'Análise Geral',
      description: 'Visão completa de vendas, estoque e preços'
    },
    {
      id: 'estoque-critico',
      label: 'Estoque Crítico',
      description: 'Produtos com estoque abaixo da média de vendas'
    }
  ];

  /**
   * Verifica se uma aba está ativa
   */
  const isActiveTab = (tabId: PurchaseAnalysisTabType): boolean => {
    return activeTab === tabId;
  };

  /**
   * Manipula clique em uma aba
   */
  const handleTabClick = (tabId: PurchaseAnalysisTabType): void => {
    if (!isActiveTab(tabId)) {
      onTabChange(tabId);
    }
  };

  return (
    <div className="border-b border-smart-gray-200">
      <nav className={`flex ${isMobile ? 'flex-col space-y-1' : 'space-x-8'}`} aria-label="Tabs">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => handleTabClick(tab.id)}
            className={`
              ${isMobile ? 'w-full py-3 px-4' : 'py-4 px-1'}
              whitespace-nowrap border-b-2 font-medium text-sm transition-colors duration-200
              ${
                isActiveTab(tab.id)
                  ? 'border-smart-blue-500 text-smart-blue-600'
                  : 'border-transparent text-smart-gray-500 hover:text-smart-gray-700 hover:border-smart-gray-300'
              }
              focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-smart-blue-500
            `}
            aria-current={isActiveTab(tab.id) ? 'page' : undefined}
            title={tab.description}
          >
            <div className={`flex items-center ${isMobile ? 'justify-start' : 'justify-center'}`}>
              <span>{tab.label}</span>
              {tab.id === 'estoque-critico' && (
                <svg 
                  className="ml-2 w-4 h-4 text-smart-red-500" 
                  fill="currentColor" 
                  viewBox="0 0 20 20"
                >
                  <path 
                    fillRule="evenodd" 
                    d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" 
                    clipRule="evenodd" 
                  />
                </svg>
              )}
            </div>
            {isMobile && (
              <p className="text-xs text-smart-gray-500 mt-1">{tab.description}</p>
            )}
          </button>
        ))}
      </nav>
    </div>
  );
};

export default PurchaseAnalysisTabs;

