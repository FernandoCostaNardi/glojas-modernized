import React from 'react';
import { StockItem } from '@/types/stock';

/**
 * Interface para as propriedades do StockCard
 */
interface StockCardProps {
  readonly item: StockItem;
  readonly storeNames: Map<number, string>;
}

/**
 * Componente para exibição de um item de estoque em formato de card mobile
 * Exibe informações do produto e estoque por loja de forma otimizada para dispositivos móveis
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const StockCard: React.FC<StockCardProps> = ({ item, storeNames }) => {
  /**
   * Obtém o nome da loja baseado no número
   * @param storeNumber Número da loja
   * @returns Nome da loja ou nome genérico se não encontrado
   */
  const getStoreName = (storeNumber: number): string => {
    return storeNames.get(storeNumber) || `Loja ${storeNumber}`;
  };

  /**
   * Obtém lista de números de lojas ordenados
   * @returns Array de números de lojas ordenados
   */
  const getSortedStoreNumbers = (): number[] => {
    return Array.from(storeNames.keys()).sort((a, b) => a - b);
  };

  /**
   * Obtém o valor de estoque de uma loja específica do item
   * @param storeNumber Número da loja
   * @returns Valor do estoque ou null
   */
  const getStoreQuantity = (storeNumber: number): number | null => {
    // Mapear número da loja para propriedade do item (loj1, loj2, etc.)
    const propertyMap: { [key: number]: keyof StockItem } = {
      1: 'loj1', 2: 'loj2', 3: 'loj3', 4: 'loj4', 5: 'loj5',
      6: 'loj6', 7: 'loj7', 8: 'loj8', 9: 'loj9', 10: 'loj10',
      11: 'loj11', 12: 'loj12', 13: 'loj13', 14: 'loj14'
    };
    
    const property = propertyMap[storeNumber];
    if (property) {
      const value = item[property];
      return typeof value === 'number' ? value : null;
    }
    return null;
  };

  /**
   * Formata quantidade para exibição
   * @param quantity Quantidade a ser formatada
   * @returns String formatada ou "-" se zero/null
   */
  const formatQuantity = (quantity: number | null | undefined): string => {
    if (quantity == null || quantity === 0) {
      return '-';
    }
    return quantity.toLocaleString('pt-BR');
  };

  /**
   * Verifica se a loja tem estoque
   * @param quantity Quantidade da loja
   * @returns true se tem estoque, false caso contrário
   */
  const hasStock = (quantity: number | null | undefined): boolean => {
    return quantity != null && quantity > 0;
  };

  const sortedStoreNumbers = getSortedStoreNumbers();
  const total = item.total ?? 0;

  return (
    <div className="bg-white border border-smart-gray-200 rounded-lg shadow-sm p-4 mb-3">
      {/* Header do Card - Informações principais do produto */}
      <div className="mb-3 pb-3 border-b border-smart-gray-200">
        <div className="flex items-start justify-between mb-2">
          <div className="flex-1 min-w-0">
            <h3 className="text-base font-bold text-smart-gray-900 truncate">
              {item.refplu}
            </h3>
            <p className="text-sm text-smart-gray-600 mt-1 truncate">
              {item.marca}
            </p>
          </div>
          {/* Badge de Total destacado */}
          <div className="ml-3 flex-shrink-0">
            <div className="bg-smart-blue-600 text-white px-3 py-1 rounded-md text-sm font-bold">
              Total: {formatQuantity(total)}
            </div>
          </div>
        </div>
        <p 
          className="text-sm text-smart-gray-700 overflow-hidden" 
          style={{ 
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            maxHeight: '2.5rem'
          }}
          title={item.descricao}
        >
          {item.descricao}
        </p>
      </div>

      {/* Grid de Lojas */}
      <div className="space-y-2">
        <h4 className="text-xs font-semibold text-smart-gray-500 uppercase tracking-wide mb-2">
          Estoque por Loja
        </h4>
        <div className="grid grid-cols-2 gap-2">
          {sortedStoreNumbers.map((storeNumber) => {
            const quantity = getStoreQuantity(storeNumber);
            const storeName = getStoreName(storeNumber);
            const hasStockValue = hasStock(quantity);

            return (
              <div
                key={storeNumber}
                className={`p-2 rounded border ${
                  hasStockValue
                    ? 'bg-smart-blue-50 border-smart-blue-200'
                    : 'bg-smart-gray-50 border-smart-gray-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span
                    className={`text-xs font-medium truncate flex-1 ${
                      hasStockValue
                        ? 'text-smart-blue-900'
                        : 'text-smart-gray-500'
                    }`}
                    title={storeName}
                  >
                    {storeName}
                  </span>
                  <span
                    className={`ml-2 text-sm font-bold flex-shrink-0 ${
                      hasStockValue
                        ? 'text-smart-blue-700'
                        : 'text-smart-gray-400'
                    }`}
                  >
                    {formatQuantity(quantity)}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default StockCard;

