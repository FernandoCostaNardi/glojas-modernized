import React from 'react';
import { Store, Trophy, AlertTriangle } from 'lucide-react';

/**
 * Interface para os dados das métricas
 */
interface MetricsData {
  totalActiveStores: number;
  bestDay: {
    date: string;
    value: number;
  } | null;
  worstDay: {
    date: string;
    value: number;
  } | null;
}

/**
 * Interface para as propriedades do SalesMetricsCards
 */
interface SalesMetricsCardsProps {
  readonly data: MetricsData;
  readonly className?: string;
}

/**
 * Componente de cards de métricas para vendas
 * Exibe total de lojas ativas, melhor dia e pior dia
 * Design moderno com ícones e cores diferenciadas
 */
const SalesMetricsCards: React.FC<SalesMetricsCardsProps> = ({ 
  data, 
  className = '' 
}) => {
  /**
   * Formata valor monetário para o padrão brasileiro
   * @param value - Valor numérico a ser formatado
   * @returns String formatada em reais brasileiros
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  /**
   * Formata data para exibição brasileira
   * @param dateString - Data no formato YYYY-MM-DD
   * @returns Data formatada como DD/MM/YYYY
   */
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  return (
    <div className={`grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 ${className}`}>
      {/* Card 1: Total de Lojas Ativas */}
      <div className="bg-white rounded-lg shadow-lg p-6 animate-fade-in">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-smart-gray-600 mb-1">Total de Lojas Ativas</p>
            <p className="text-3xl font-bold text-smart-gray-900">{data.totalActiveStores}</p>
          </div>
          <Store className="h-12 w-12 text-smart-gray-400" />
        </div>
      </div>

      {/* Card 2: Melhor Dia */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-smart-orange-500 animate-fade-in">
        <div className="flex items-center space-x-3">
          <Trophy className="h-8 w-8 text-smart-orange-500" />
          <div>
            <p className="text-xs text-smart-gray-600 mb-1">Melhor Dia</p>
            {data.bestDay ? (
              <>
                <p className="text-sm font-semibold text-smart-gray-900">
                  {formatDate(data.bestDay.date)}
                </p>
                <p className="text-lg font-bold text-smart-orange-600">
                  {formatCurrency(data.bestDay.value)}
                </p>
              </>
            ) : (
              <p className="text-sm text-smart-gray-500">Nenhum dado disponível</p>
            )}
          </div>
        </div>
      </div>

      {/* Card 3: Pior Dia */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-error-500 animate-fade-in">
        <div className="flex items-center space-x-3">
          <AlertTriangle className="h-8 w-8 text-error-500" />
          <div>
            <p className="text-xs text-smart-gray-600 mb-1">Pior Dia</p>
            {data.worstDay ? (
              <>
                <p className="text-sm font-semibold text-smart-gray-900">
                  {formatDate(data.worstDay.date)}
                </p>
                <p className="text-lg font-bold text-error-600">
                  {formatCurrency(data.worstDay.value)}
                </p>
              </>
            ) : (
              <p className="text-sm text-smart-gray-500">Nenhum dado disponível</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SalesMetricsCards;
