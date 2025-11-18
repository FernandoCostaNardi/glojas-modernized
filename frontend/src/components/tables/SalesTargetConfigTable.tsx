import React, { useState, useEffect } from 'react';
import { useLayout } from '@/contexts/LayoutContext';
import { SalesTargetConfig } from '@/types';
import { storeService } from '@/services/api';
import { ApiStore } from '@/types';

/**
 * Props da tabela de configurações de metas e comissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
interface SalesTargetConfigTableProps {
  readonly configs: readonly SalesTargetConfig[];
  readonly isLoading: boolean;
  readonly error: string | null;
  readonly onEditConfig: (config: SalesTargetConfig) => void;
  readonly onDeleteConfig: (config: SalesTargetConfig) => void;
}

/**
 * Componente de tabela de configurações de metas e comissões
 * Seguindo princípios de Clean Code com responsabilidade única
 */
const SalesTargetConfigTable: React.FC<SalesTargetConfigTableProps> = ({
  configs,
  isLoading,
  error,
  onEditConfig,
  onDeleteConfig
}) => {
  const { isMobile } = useLayout();
  const [stores, setStores] = useState<Map<string, string>>(new Map());

  /**
   * Carrega nomes das lojas para exibição
   */
  useEffect(() => {
    const loadStores = async (): Promise<void> => {
      try {
        const allStores = await storeService.getAllStores();
        const storeMap = new Map<string, string>();
        allStores.forEach((store: ApiStore) => {
          storeMap.set(store.code, store.name);
        });
        setStores(storeMap);
      } catch (err) {
        console.error('❌ Erro ao carregar lojas:', err);
      }
    };

    loadStores();
  }, []);

  /**
   * Formata valor monetário
   */
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  /**
   * Formata percentual
   */
  const formatPercentage = (value: number): string => {
    return `${value.toFixed(2)}%`;
  };

  /**
   * Obtém nome da loja pelo código
   */
  const getStoreName = (code: string): string => {
    return stores.get(code) || code;
  };

  // Garantir que configs seja sempre um array
  const safeConfigs = Array.isArray(configs) ? configs : [];

  /**
   * Renderiza estado de loading
   */
  const renderLoading = (): React.ReactNode => (
    <div className="text-center py-12">
      <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-smart-red-600"></div>
      <p className="mt-4 text-smart-gray-600">Carregando configurações...</p>
    </div>
  );

  /**
   * Renderiza estado de erro
   */
  const renderError = (): React.ReactNode => (
    <div className="text-center py-12">
      <div className="text-red-600 mb-2">❌</div>
      <p className="text-smart-gray-800 font-medium">Erro ao carregar configurações</p>
      <p className="text-sm text-smart-gray-600 mt-1">{error}</p>
    </div>
  );

  /**
   * Renderiza estado vazio
   */
  const renderEmpty = (): React.ReactNode => (
    <div className="text-center py-12">
      <div className="text-4xl mb-4">🎯</div>
      <p className="text-smart-gray-800 font-medium">Nenhuma configuração encontrada</p>
      <p className="text-sm text-smart-gray-600 mt-1">
        Clique em "Nova Configuração" para criar a primeira.
      </p>
    </div>
  );

  /**
   * Renderiza um card de configuração para mobile
   */
  const renderConfigCard = (config: SalesTargetConfig): React.ReactNode => (
    <div key={config.id} className="bg-white rounded-lg shadow-sm border border-smart-gray-200 p-4 mb-3">
      {/* Header do card */}
      <div className="mb-3">
        <div className="text-sm font-semibold text-smart-gray-900">
          {config.storeCode} - {getStoreName(config.storeCode)}
        </div>
        <div className="text-xs text-smart-gray-500 mt-1">
          Competência: {config.competenceDate}
        </div>
      </div>
      
      {/* Detalhes */}
      <div className="space-y-2 mb-4 text-sm">
        <div className="flex justify-between">
          <span className="text-smart-gray-600">Meta Loja:</span>
          <span className="font-medium text-smart-gray-800">{formatCurrency(config.storeSalesTarget)}</span>
        </div>
        <div className="flex justify-between">
          <span className="text-smart-gray-600">% Comissão Coletiva:</span>
          <span className="font-medium text-smart-gray-800">{formatPercentage(config.collectiveCommissionPercentage)}</span>
        </div>
        <div className="flex justify-between">
          <span className="text-smart-gray-600">Meta Individual:</span>
          <span className="font-medium text-smart-gray-800">{formatCurrency(config.individualSalesTarget)}</span>
        </div>
        <div className="flex justify-between">
          <span className="text-smart-gray-600">% Comissão Individual:</span>
          <span className="font-medium text-smart-gray-800">{formatPercentage(config.individualCommissionPercentage)}</span>
        </div>
      </div>
      
      {/* Ações */}
      <div className="flex justify-end gap-2 pt-3 border-t border-smart-gray-100">
        <button
          onClick={() => onEditConfig(config)}
          className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
          title="Editar configuração"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
        </button>
        <button
          onClick={() => onDeleteConfig(config)}
          className="text-red-600 hover:text-red-900 transition-colors duration-200 p-1"
          title="Deletar configuração"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        </button>
      </div>
    </div>
  );

  /**
   * Renderiza visualização mobile com cards
   */
  const renderMobileView = (): React.ReactNode => (
    <div className="mx-2">
      {safeConfigs.map(renderConfigCard)}
    </div>
  );

  /**
   * Renderiza tabela desktop
   */
  const renderDesktopTable = (): React.ReactNode => (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-smart-gray-200">
        <thead className="bg-smart-gray-50">
          <tr>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Loja
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Competência
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Meta Loja
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              % Comissão Coletiva
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Meta Individual
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              % Comissão Individual
            </th>
            <th className="px-4 py-3 text-center text-xs font-medium text-smart-gray-500 uppercase tracking-wider">
              Ações
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-smart-gray-200">
          {safeConfigs.map((config) => (
            <tr key={config.id} className="hover:bg-smart-gray-50">
              <td className="px-4 py-4 whitespace-nowrap text-center">
                <div className="text-sm font-medium text-smart-gray-900">
                  {config.storeCode}
                </div>
                <div className="text-sm text-smart-gray-500">
                  {getStoreName(config.storeCode)}
                </div>
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-center">
                {config.competenceDate}
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-center">
                {formatCurrency(config.storeSalesTarget)}
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-center">
                {formatPercentage(config.collectiveCommissionPercentage)}
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-center">
                {formatCurrency(config.individualSalesTarget)}
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm text-smart-gray-900 text-center">
                {formatPercentage(config.individualCommissionPercentage)}
              </td>
              <td className="px-4 py-4 whitespace-nowrap text-sm font-medium text-center">
                <div className="flex items-center justify-center space-x-2">
                  <button
                    onClick={() => onEditConfig(config)}
                    className="text-smart-blue-600 hover:text-smart-blue-900 transition-colors duration-200 p-1"
                    title="Editar configuração"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button
                    onClick={() => onDeleteConfig(config)}
                    className="text-red-600 hover:text-red-900 transition-colors duration-200 p-1"
                    title="Deletar configuração"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  // Renderizar estados especiais
  if (isLoading) return renderLoading();
  if (error) return renderError();
  if (safeConfigs.length === 0) return renderEmpty();

  return isMobile ? renderMobileView() : renderDesktopTable();
};

export default SalesTargetConfigTable;

