import React from 'react';
import { PurchaseAnalysisItem, PurchaseAnalysisTabType } from '@/types/purchaseAnalysis';

/**
 * Interface para as propriedades do PurchaseAnalysisCard
 */
interface PurchaseAnalysisCardProps {
  readonly item: PurchaseAnalysisItem;
  readonly activeTab: PurchaseAnalysisTabType;
}

/**
 * Componente para exibição de um item de análise de compras em formato de card mobile
 * Exibe informações do produto, vendas, estoque, custos e preços de forma otimizada para dispositivos móveis
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const PurchaseAnalysisCard: React.FC<PurchaseAnalysisCardProps> = ({ item, activeTab }) => {
  /**
   * Formata valores monetários
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  /**
   * Formata valores numéricos
   */
  const formatNumber = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  };

  /**
   * Determina a classe CSS do card baseado na criticidade
   * Apenas na aba de estoque crítico, aplica destaque visual
   */
  const getCardClassName = (): string => {
    if (activeTab !== 'estoque-critico' || item.diferenca === undefined) {
      return 'bg-white border border-smart-gray-200 rounded-lg shadow-sm p-4 mb-3';
    }
    
    // Destaque mais forte para produtos muito críticos (diferença > 50)
    if (item.diferenca > 50) {
      return 'bg-smart-red-50 border border-smart-red-200 rounded-lg shadow-sm p-4 mb-3';
    }
    
    // Destaque médio para produtos críticos (diferença > 20)
    if (item.diferenca > 20) {
      return 'bg-smart-yellow-50 border border-smart-yellow-200 rounded-lg shadow-sm p-4 mb-3';
    }
    
    return 'bg-white border border-smart-gray-200 rounded-lg shadow-sm p-4 mb-3';
  };

  return (
    <div className={getCardClassName()}>
      {/* Header do Card - Informações principais do produto */}
      <div className="mb-3 pb-3 border-b border-smart-gray-200">
        <div className="flex items-start justify-between mb-2">
          <div className="flex-1 min-w-0">
            <h3 className="text-base font-bold text-smart-gray-900 truncate">
              {item.refplu}
            </h3>
            <div className="flex flex-wrap gap-2 mt-1">
              <span className="text-xs text-smart-gray-600 bg-smart-gray-100 px-2 py-0.5 rounded">
                {item.descricaoGrupo}
              </span>
              <span className="text-xs text-smart-gray-600 bg-smart-gray-100 px-2 py-0.5 rounded">
                {item.descricaoMarca}
              </span>
              {item.codigoPartNumber && (
                <span className="text-xs text-smart-gray-600 bg-smart-gray-100 px-2 py-0.5 rounded">
                  {item.codigoPartNumber}
                </span>
              )}
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
          title={item.descricaoProduto}
        >
          {item.descricaoProduto}
        </p>
      </div>

      {/* Seção Financeira */}
      <div className="mb-3 pb-3 border-b border-smart-gray-200">
        <h4 className="text-xs font-semibold text-smart-gray-500 uppercase tracking-wide mb-2">
          Valores Financeiros
        </h4>
        <div className="grid grid-cols-2 gap-2">
          <div className="bg-smart-gray-50 border border-smart-gray-200 rounded p-2">
            <div className="text-xs text-smart-gray-600 mb-1">Custo Rep.</div>
            <div className="text-sm font-bold text-smart-gray-900">
              {formatCurrency(item.custoReposicao)}
            </div>
          </div>
          <div className="bg-smart-green-50 border border-smart-green-200 rounded p-2">
            <div className="text-xs text-smart-green-700 mb-1">Preço Venda</div>
            <div className="text-sm font-bold text-smart-green-900">
              {formatCurrency(item.precoVenda)}
            </div>
          </div>
        </div>
      </div>

      {/* Seção Vendas */}
      <div className="mb-3 pb-3 border-b border-smart-gray-200">
        <h4 className="text-xs font-semibold text-smart-gray-500 uppercase tracking-wide mb-2">
          Vendas
        </h4>
        <div className="grid grid-cols-2 gap-2">
          <div className="bg-smart-blue-50 border border-smart-blue-200 rounded p-2">
            <div className="text-xs text-smart-blue-700 mb-1">90 dias</div>
            <div className="text-sm font-bold text-smart-blue-900">
              {formatNumber(item.vendas90Dias)}
            </div>
          </div>
          <div className="bg-smart-blue-50 border border-smart-blue-200 rounded p-2">
            <div className="text-xs text-smart-blue-700 mb-1">60 dias</div>
            <div className="text-sm font-bold text-smart-blue-900">
              {formatNumber(item.vendas60Dias)}
            </div>
          </div>
          <div className="bg-smart-blue-50 border border-smart-blue-200 rounded p-2">
            <div className="text-xs text-smart-blue-700 mb-1">30 dias</div>
            <div className="text-sm font-bold text-smart-blue-900">
              {formatNumber(item.vendas30Dias)}
            </div>
          </div>
          <div className="bg-smart-green-50 border border-smart-green-200 rounded p-2">
            <div className="text-xs text-smart-green-700 mb-1">Mês Atual</div>
            <div className="text-sm font-bold text-smart-green-900">
              {formatNumber(item.vendasMesAtual)}
            </div>
          </div>
        </div>
      </div>

      {/* Seção Estoque */}
      <div className="mb-3 pb-3 border-b border-smart-gray-200">
        <h4 className="text-xs font-semibold text-smart-gray-500 uppercase tracking-wide mb-2">
          Estoque
        </h4>
        <div className="bg-smart-purple-50 border border-smart-purple-200 rounded p-2">
          <div className="text-xs text-smart-purple-700 mb-1">Quantidade</div>
          <div className="text-lg font-bold text-smart-purple-900">
            {formatNumber(item.estoque)}
          </div>
        </div>
      </div>

      {/* Seção Estoque Crítico - Apenas na aba de estoque crítico */}
      {activeTab === 'estoque-critico' && (item.mediaMensal !== undefined || item.diferenca !== undefined) && (
        <div>
          <h4 className="text-xs font-semibold text-smart-gray-500 uppercase tracking-wide mb-2">
            Análise Crítica
          </h4>
          <div className="grid grid-cols-2 gap-2">
            <div className="bg-smart-indigo-50 border border-smart-indigo-200 rounded p-2">
              <div className="text-xs text-smart-indigo-700 mb-1">Média Mensal</div>
              <div className="text-sm font-semibold text-smart-indigo-900">
                {item.mediaMensal !== undefined ? formatNumber(item.mediaMensal) : '-'}
              </div>
            </div>
            <div className={`rounded p-2 border ${
              item.diferenca !== undefined && item.diferenca > 50
                ? 'bg-smart-red-100 border-smart-red-300'
                : item.diferenca !== undefined && item.diferenca > 20
                ? 'bg-smart-yellow-100 border-smart-yellow-300'
                : 'bg-smart-red-50 border-smart-red-200'
            }`}>
              <div className={`text-xs mb-1 ${
                item.diferenca !== undefined && item.diferenca > 20
                  ? 'text-smart-red-900'
                  : 'text-smart-red-700'
              }`}>
                Diferença
              </div>
              <div className={`text-sm font-bold ${
                item.diferenca !== undefined && item.diferenca > 20
                  ? 'text-smart-red-900'
                  : 'text-smart-red-700'
              }`}>
                {item.diferenca !== undefined ? formatNumber(item.diferenca) : '-'}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PurchaseAnalysisCard;

