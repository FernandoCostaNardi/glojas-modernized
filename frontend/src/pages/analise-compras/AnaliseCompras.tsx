import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { purchaseAnalysisService, criticalStockService } from '@/services/api';
import { PurchaseAnalysisPageResponse, PurchaseAnalysisFilters, PurchaseAnalysisTabType } from '@/types/purchaseAnalysis';
import PurchaseAnalysisFiltersComponent from './components/PurchaseAnalysisFilters';
import PurchaseAnalysisTable from './components/PurchaseAnalysisTable';
import PurchaseAnalysisTabs from './components/PurchaseAnalysisTabs';

/**
 * Interface para as propriedades do componente
 */
interface AnaliseComprasProps {
  readonly className?: string;
}

/**
 * Página principal de Análise de Compras
 * Layout responsivo com sidebar, header e sistema de filtros
 * Mostra dados de vendas (90, 60, 30 dias e mês atual), estoque, custos e preços
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const AnaliseCompras: React.FC<AnaliseComprasProps> = () => {
  const { isMobile } = useLayout();
  
  // Estado para aba ativa
  const [activeTab, setActiveTab] = useState<PurchaseAnalysisTabType>('geral');
  
  // Estados para dados e controle da interface
  const [data, setData] = useState<PurchaseAnalysisPageResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Estados para filtros e paginação
  const [filters, setFilters] = useState<PurchaseAnalysisFilters>({
    hideNoSales: true, // Ocultar produtos sem vendas por padrão
    page: 0,
    size: 20,
    sortBy: activeTab === 'estoque-critico' ? 'diferenca' : 'refplu',
    sortDir: activeTab === 'estoque-critico' ? 'desc' : 'asc'
  });

  /**
   * Carrega dados conforme a aba ativa
   */
  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      
      console.log(`Carregando dados da aba ${activeTab} com filtros:`, filters);
      
      let response: PurchaseAnalysisPageResponse;
      
      if (activeTab === 'estoque-critico') {
        // Aba de estoque crítico - sem hideNoSales
        const { hideNoSales, ...criticalFilters } = filters;
        response = await criticalStockService.getCriticalStock(criticalFilters);
      } else {
        // Aba geral - com hideNoSales
        response = await purchaseAnalysisService.getPurchaseAnalysis(filters);
      }
      
      setData(response);
      
      console.log(`Dados carregados: ${response.content.length} itens de ${response.pagination.totalElements} total`);
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar dados';
      setError(errorMessage);
      console.error('❌ Erro ao carregar dados:', err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Carrega dados na montagem do componente e quando a aba mudar
   */
  useEffect(() => {
    loadData();
  }, [activeTab]);

  /**
   * Manipula mudança de aba
   */
  const handleTabChange = (tab: PurchaseAnalysisTabType): void => {
    setActiveTab(tab);
    // Resetar filtros e ordenação conforme a aba
    const newFilters: PurchaseAnalysisFilters = {
      page: 0,
      size: 20,
      sortBy: tab === 'estoque-critico' ? 'diferenca' : 'refplu',
      sortDir: tab === 'estoque-critico' ? 'desc' : 'asc'
    };
    
    if (tab === 'geral') {
      newFilters.hideNoSales = true;
    }
    
    setFilters(newFilters);
    setData(null); // Limpar dados anteriores
  };

  /**
   * Manipula mudança nos filtros
   * @param newFilters Novos filtros
   */
  const handleFiltersChange = (newFilters: PurchaseAnalysisFilters): void => {
    setFilters(newFilters);
  };

  /**
   * Executa busca com os filtros atuais
   */
  const handleSearch = (): void => {
    setFilters(prev => ({ ...prev, page: 0 })); // Reset para primeira página
    loadData();
  };

  /**
   * Limpa todos os filtros e recarrega dados
   */
  const handleClear = (): void => {
    const clearedFilters: PurchaseAnalysisFilters = {
      page: 0,
      size: filters.size,
      sortBy: filters.sortBy,
      sortDir: filters.sortDir
    };
    
    if (activeTab === 'geral') {
      clearedFilters.hideNoSales = true;
    }
    
    setFilters(clearedFilters);
    loadData();
  };

  /**
   * Manipula mudança de página
   * @param page Nova página
   */
  const handlePageChange = (page: number): void => {
    const newFilters = { ...filters, page };
    setFilters(newFilters);
    loadData();
  };

  /**
   * Manipula mudança de tamanho da página
   * @param size Novo tamanho
   */
  const handleSizeChange = (size: number): void => {
    const newFilters = { ...filters, size, page: 0 }; // Reset para primeira página
    setFilters(newFilters);
    loadData();
  };

  /**
   * Manipula mudança de ordenação
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação
   */
  const handleSortChange = (sortBy: string, sortDir: 'asc' | 'desc'): void => {
    const newFilters = { ...filters, sortBy, sortDir, page: 0 }; // Reset para primeira página
    setFilters(newFilters);
    loadData();
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className={`mb-4 ${isMobile ? 'mb-3' : 'mb-4'}`}>
      <h1 className={`font-bold text-smart-gray-800 mb-1 ${
        isMobile ? 'text-lg' : 'text-xl'
      }`}>
        Análise de Compras 🛒
      </h1>
      <p className={`text-smart-gray-600 ${
        isMobile ? 'text-sm' : 'text-base'
      }`}>
        Consulte dados de vendas em múltiplos períodos, estoque, custos e preços para análise de compras
      </p>
    </div>
  );

  /**
   * Renderiza o conteúdo principal da página
   */
  const renderMainContent = (): React.ReactNode => (
    <main className={`bg-white rounded-lg shadow-sm border border-smart-gray-200 ${
      isMobile ? 'p-2' : 'p-4'
    }`}>
      {renderPageHeader()}
      
      {/* Abas de navegação */}
      <div className="mb-4">
        <PurchaseAnalysisTabs
          activeTab={activeTab}
          onTabChange={handleTabChange}
        />
      </div>
      
      {/* Filtros de busca - Aplicar em ambas as abas */}
      <PurchaseAnalysisFiltersComponent
        filters={filters}
        onFiltersChange={handleFiltersChange}
        onSearch={handleSearch}
        onClear={handleClear}
        loading={loading}
        activeTab={activeTab}
      />

      {/* Separador */}
      <div className="my-4 border-t border-smart-gray-200"></div>

      {/* Tabela de dados */}
      <PurchaseAnalysisTable
        data={data}
        loading={loading}
        error={error}
        onPageChange={handlePageChange}
        onSizeChange={handleSizeChange}
        onSortChange={handleSortChange}
        currentPage={filters.page}
        pageSize={filters.size}
        sortBy={filters.sortBy}
        sortDir={filters.sortDir}
        activeTab={activeTab}
      />
    </main>
  );

  return (
    <div className="min-h-screen flex flex-col bg-white">
      {/* Header */}
      <Header />
      
      {/* Layout principal */}
      <div className="flex flex-1 relative">
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

export default AnaliseCompras;

