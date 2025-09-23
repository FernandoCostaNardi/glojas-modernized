import React from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useLayout } from '@/contexts/LayoutContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import VendasTabs from './components/VendasTabs';

/**
 * Interface para as propriedades do VendasPage
 */
interface VendasPageProps {
  readonly className?: string;
}

/**
 * Página principal de vendas
 * Layout responsivo com sidebar, header e sistema de abas para relatórios de vendas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const VendasPage: React.FC<VendasPageProps> = ({ className = '' }) => {
  const { user } = useAuth();
  const { isMobile } = useLayout();

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className={`mb-4 ${isMobile ? 'mb-3' : 'mb-4'}`}>
      <h1 className={`font-bold text-smart-gray-800 mb-1 ${
        isMobile ? 'text-lg' : 'text-xl'
      }`}>
        Relatórios de Vendas 📊
      </h1>
      <p className={`text-smart-gray-600 ${
        isMobile ? 'text-xs' : 'text-sm'
      }`}>
        Acompanhe o desempenho de vendas por período e loja.
      </p>
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className={`flex-1 bg-white overflow-auto ${
      isMobile ? 'p-2' : 'p-4'
    }`}>
      {renderPageHeader()}
      <VendasTabs />
    </main>
  );

  return (
    <div className="h-screen flex flex-col bg-white">
      {/* Header */}
      <Header />
      
      {/* Layout principal */}
      <div className="flex flex-1 overflow-hidden relative">
        {/* Sidebar */}
        <Sidebar />
        
        {/* Conteúdo principal */}
        <div className="flex-1">
          {renderMainContent()}
        </div>
      </div>
    </div>
  );
};

export default VendasPage;
