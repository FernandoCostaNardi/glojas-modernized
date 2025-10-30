import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { stockService, storeService } from '@/services/api';
import { StockPageResponse, StockFilters } from '@/types/stock';
import StockFiltersComponent from './components/StockFilters';
import StockTable from './components/StockTable';

/**
 * Interface para as propriedades do Estoque
 */
interface EstoqueProps {
  readonly className?: string;
}

/**
 * Página principal de estoque
 * Layout responsivo com sidebar, header e sistema de filtros para consulta de estoque
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const Estoque: React.FC<EstoqueProps> = () => {
  const { isMobile } = useLayout();
  // Estados para dados e controle da interface
  const [data, setData] = useState<StockPageResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estado para armazenar o mapeamento de números de lojas para nomes
  const [stores, setStores] = useState<Map<number, string>>(new Map());

  // Estado para controle do filtro de estoque
  const [showOnlyWithStock, setShowOnlyWithStock] = useState<boolean>(true);

  // Estados para filtros e paginação
  const [filters, setFilters] = useState<StockFilters>({
    hasStock: true,
    page: 0,
    size: 15,
    sortBy: 'refplu',
    sortDir: 'asc'
  });

  /**
   * Carrega os nomes das lojas ativas do sistema
   * Cria um mapeamento de número da loja (1-14) para nome da loja
   */
  const loadStores = async (): Promise<void> => {
    try {
      console.log('🏪 Carregando lojas ativas...');
      const allStores = await storeService.getAllStores();
      
      // Filtrar apenas lojas ativas
      const activeStores = allStores.filter((store: any) => store.status === true);
      
      // Criar mapeamento: número da loja → nome da loja
      const storeMap = new Map<number, string>();
      activeStores.forEach((store: any) => {
        // Converter código "000001" para número 1
        const storeNumber = parseInt(store.code, 10);
        storeMap.set(storeNumber, store.name);
      });
      
      setStores(storeMap);
      console.log(`✅ Lojas carregadas: ${storeMap.size}`);
      
    } catch (err) {
      console.error('❌ Erro ao carregar lojas:', err);
      // Não falhar a página se não conseguir carregar lojas
      // Continua com os nomes genéricos "Loja 1", "Loja 2", etc.
    }
  };

  /**
   * Carrega dados de estoque com os filtros atuais
   */
  const loadStocks = async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      
      console.log('Carregando estoque com filtros:', filters);
      
      const response = await stockService.getStocks(filters);
      setData(response);
      
      console.log(`Estoque carregado: ${response.content.length} itens de ${response.pagination.totalElements} total`);
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar estoque';
      setError(errorMessage);
      console.error('❌ Erro ao carregar estoque:', err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Carrega lojas e dados de estoque na montagem do componente
   */
  useEffect(() => {
    loadStores();
    loadStocks();
  }, []);

  /**
   * Recarrega dados quando o filtro de estoque mudar
   */
  useEffect(() => {
    if (data !== null) { // Não executar na montagem inicial
      loadStocks();
    }
  }, [showOnlyWithStock]);

  /**
   * Manipula mudança nos filtros
   * @param newFilters Novos filtros
   */
  const handleFiltersChange = (newFilters: StockFilters): void => {
    setFilters(newFilters);
  };

  /**
   * Executa busca com os filtros atuais
   */
  const handleSearch = (): void => {
    setFilters(prev => ({ ...prev, page: 0 })); // Reset para primeira página
    loadStocks();
  };

  /**
   * Limpa todos os filtros e recarrega dados
   */
  const handleClear = (): void => {
    const clearedFilters: StockFilters = {
      hasStock: true,
      page: 0,
      size: filters.size,
      sortBy: filters.sortBy,
      sortDir: filters.sortDir
    };
    setFilters(clearedFilters);
    setShowOnlyWithStock(true);
    loadStocks();
  };

  /**
   * Manipula mudança no filtro de estoque
   * @param checked Se deve filtrar apenas produtos com estoque
   */
  const handleStockFilterChange = (checked: boolean): void => {
    setShowOnlyWithStock(checked);
    const newFilters = { ...filters, hasStock: checked, page: 0 }; // Reset para primeira página
    setFilters(newFilters);
  };

  /**
   * Manipula mudança de página
   * @param page Nova página
   */
  const handlePageChange = (page: number): void => {
    const newFilters = { ...filters, page };
    setFilters(newFilters);
    loadStocks();
  };

  /**
   * Manipula mudança de tamanho da página
   * @param size Novo tamanho
   */
  const handleSizeChange = (size: number): void => {
    const newFilters = { ...filters, size, page: 0 }; // Reset para primeira página
    setFilters(newFilters);
    loadStocks();
  };

  /**
   * Manipula mudança de ordenação
   * @param sortBy Campo para ordenação
   * @param sortDir Direção da ordenação
   */
  const handleSortChange = (sortBy: string, sortDir: 'asc' | 'desc'): void => {
    const newFilters = { ...filters, sortBy, sortDir, page: 0 }; // Reset para primeira página
    setFilters(newFilters);
    loadStocks();
  };

  /**
   * Renderiza o cabeçalho da página
   */
  const renderPageHeader = (): React.ReactNode => (
    <div className={`mb-4 ${isMobile ? 'mb-3' : 'mb-4'}`}>
      <h1 className={`font-bold text-smart-gray-800 mb-1 ${
        isMobile ? 'text-lg' : 'text-xl'
      }`}>
        Estoque 📦
      </h1>
      <p className={`text-smart-gray-600 ${
        isMobile ? 'text-sm' : 'text-base'
      }`}>
        Consulte o estoque de produtos por loja com filtros e paginação
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
      
      {/* Filtros de busca */}
      <StockFiltersComponent
        filters={filters}
        onFiltersChange={handleFiltersChange}
        onSearch={handleSearch}
        onClear={handleClear}
        loading={loading}
      />

      {/* Switch para filtrar apenas produtos com estoque */}
      <div className="flex items-center justify-end mb-4 mt-2">
        <label className="flex items-center cursor-pointer">
          <span className="mr-3 text-sm font-medium text-smart-gray-700">
            Apenas com estoque
          </span>
          <div className="relative">
            <input
              type="checkbox"
              checked={showOnlyWithStock}
              onChange={(e) => handleStockFilterChange(e.target.checked)}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-smart-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-smart-blue-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-smart-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-smart-blue-600"></div>
          </div>
        </label>
      </div>

      {/* Tabela de dados */}
      <StockTable
        data={data}
        loading={loading}
        error={error}
        storeNames={stores}
        onPageChange={handlePageChange}
        onSizeChange={handleSizeChange}
        onSortChange={handleSortChange}
        currentPage={filters.page}
        pageSize={filters.size}
        sortBy={filters.sortBy}
        sortDir={filters.sortDir}
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

export default Estoque;
