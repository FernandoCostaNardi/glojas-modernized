import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { storeService } from '@/services/api';
import { StoreOption } from '@/types/sales';

/**
 * Interface para as propriedades do StoreSelector
 */
interface StoreSelectorProps {
  readonly selectedStoreCode: string | null;
  readonly onStoreChange: (storeCode: string | null) => void;
  readonly disabled?: boolean;
  readonly className?: string;
}

/**
 * Componente para seleção de loja para o gráfico de vendas
 * Permite selecionar uma loja específica ou todas as lojas
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StoreSelector: React.FC<StoreSelectorProps> = ({
  selectedStoreCode,
  onStoreChange,
  disabled = false,
  className = ''
}) => {
  const { isMobile } = useLayout();
  const [stores, setStores] = useState<StoreOption[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  /**
   * Carrega as lojas disponíveis para o usuário
   */
  useEffect(() => {
    const loadStores = async (): Promise<void> => {
      try {
        setLoading(true);
        setError(null);
        
        console.log('Carregando lojas para o seletor do gráfico...');
        
        // Buscar todas as lojas ativas do usuário
        const response = await storeService.getAllStores();
        
        // Converter para formato do seletor
        const storeOptions: StoreOption[] = response.map(store => ({
          code: store.code,
          name: store.name
        }));
        
        setStores(storeOptions);
        
        console.log(`Lojas carregadas: ${storeOptions.length} lojas disponíveis`);
        
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar lojas';
        setError(errorMessage);
        console.error('❌ Erro ao carregar lojas para o seletor:', err);
      } finally {
        setLoading(false);
      }
    };

    loadStores();
  }, []);

  /**
   * Manipula mudança na seleção da loja
   * @param event - Evento de mudança do select
   */
  const handleStoreChange = (event: React.ChangeEvent<HTMLSelectElement>): void => {
    const value = event.target.value;
    const storeCode = value === '' ? null : value;
    onStoreChange(storeCode);
  };

  /**
   * Renderiza o estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className={`flex items-center justify-center ${isMobile ? 'py-2' : 'py-1'}`}>
      <svg className="animate-spin h-4 w-4 text-smart-gray-500 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <span className={`text-smart-gray-600 ${isMobile ? 'text-xs' : 'text-xs'}`}>
        Carregando lojas...
      </span>
    </div>
  );

  /**
   * Renderiza o estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className={`flex items-center justify-center ${isMobile ? 'py-2' : 'py-1'}`}>
      <svg className="h-4 w-4 text-red-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"></path>
      </svg>
      <span className={`text-red-600 ${isMobile ? 'text-xs' : 'text-xs'}`}>
        Erro ao carregar lojas
      </span>
    </div>
  );

  /**
   * Renderiza o seletor de lojas
   */
  const renderSelector = (): React.ReactNode => (
    <div className="space-y-2">
      <label htmlFor="store-selector" className={`block font-medium text-smart-gray-700 ${
        isMobile ? 'text-xs' : 'text-xs'
      }`}>
        Filtrar por Loja
      </label>
      
      <select
        id="store-selector"
        value={selectedStoreCode || ''}
        onChange={handleStoreChange}
        disabled={disabled || loading}
        className={`w-full px-2 py-1 text-xs border border-smart-gray-300 rounded focus:ring-1 focus:ring-smart-red-500 focus:border-smart-red-500 transition-colors duration-200 ${
          disabled || loading ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'
        }`}
      >
        <option value="">
          Todas as Lojas
        </option>
        {stores.map((store) => (
          <option key={store.code} value={store.code}>
            {store.name}
          </option>
        ))}
      </select>
      
      {selectedStoreCode && (
        <div className={`flex items-center text-xs text-smart-gray-600 ${
          isMobile ? 'justify-center' : ''
        }`}>
          <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"></path>
          </svg>
          <span>Filtro ativo: {stores.find(s => s.code === selectedStoreCode)?.name}</span>
        </div>
      )}
    </div>
  );

  return (
    <div className={`store-selector ${className}`}>
      {loading ? renderLoading() : error ? renderError() : renderSelector()}
    </div>
  );
};

export default StoreSelector;
